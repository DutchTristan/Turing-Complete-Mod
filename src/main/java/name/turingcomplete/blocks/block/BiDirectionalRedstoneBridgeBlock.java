package name.turingcomplete.blocks.block;

import name.turingcomplete.blocks.AbstractLogicBlock;
import name.turingcomplete.blocks.RelativeSide;
import name.turingcomplete.init.propertyInit;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.tick.TickPriority;

public class BiDirectionalRedstoneBridgeBlock extends AbstractLogicBlock {
    private static final BooleanProperty POWERED_X = propertyInit.POWERED_X;
    private static final BooleanProperty POWERED_Z = propertyInit.POWERED_Z;

    private static final int gate_delay = 2;

    public BiDirectionalRedstoneBridgeBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState()
                .with(POWERED_X,false)
                .with(POWERED_Z, false)
        );
    }

    //position and state are not relevant
    @Override
    public boolean canMirrorHere(BlockPos mainPos, BlockState mainState){
        return canMirror();
    }

    @Override
    public boolean canMirror(){
        return true;
    }

    @Override
    public Boolean dustConnectsToThis(BlockState gateState, Direction direction){
        return true;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        super.appendProperties(builder);
        builder.add(POWERED_X,POWERED_Z);
    }

    @Override
    protected int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        Direction SideInputDirection = getCrossInputSide(state).onDirection(state.get(FACING)).getOpposite();
        Direction ForwardInputDirection = state.get(FACING);

        if (direction == ForwardInputDirection)
            return state.get(POWERED_Z) ? 15 : 0;
        else if (direction == SideInputDirection)
            return state.get(POWERED_X) ? 15 : 0;
        else
            return 0;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        //redstone dust hasn't been redirected yet, so must schedule unconditionally
        world.scheduleBlockTick(pos,this, gate_delay, TickPriority.VERY_HIGH);
    }

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        boolean side_input_active = getCrossInputActive(world,pos,state);
        boolean back_input_active = getInputActive(world, pos, state, RelativeSide.BACK);

        boolean will_change_side_out = side_input_active != state.get(POWERED_X);
        boolean will_change_front_out = back_input_active != state.get(POWERED_Z);

        if(will_change_side_out || will_change_front_out) {
            world.setBlockState(
                pos,
                state
                    .with(POWERED_X,side_input_active)
                    .with(POWERED_Z,back_input_active));
            if(will_change_side_out) {
                updateSideBlock(world, pos, state);
            }
            if(will_change_front_out) {
                updateFrontBlock(world, pos, state);
            }
        }
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
        boolean side_input_active = getCrossInputActive(world,pos,state);
        boolean back_input_active = getInputActive(world, pos, state, RelativeSide.BACK);


        if (
            side_input_active != state.get(POWERED_X) ||
            back_input_active != state.get(POWERED_Z)) {

            world.scheduleBlockTick(pos, this, gate_delay, TickPriority.VERY_HIGH);
        }
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!canMirror()) {
            return ActionResult.PASS;
        }

        state = state
            .with(MIRRORED, !state.get(MIRRORED))
            .with(POWERED_X,false);
        world.setBlockState(pos, state);
        if(state.get(MIRRORED)) {
            world.playSound(player, pos, SoundEvents.BLOCK_COMPARATOR_CLICK, SoundCategory.BLOCKS, 0.3F, 0.5F);
        }
        else {
            world.playSound(player, pos, SoundEvents.BLOCK_COMPARATOR_CLICK, SoundCategory.BLOCKS, 0.3F, 0.55F);
        }
        world.scheduleBlockTick(pos,this, gate_delay, TickPriority.VERY_HIGH);

        return ActionResult.SUCCESS_NO_ITEM_USED;
    }

    protected final void updateFrontBlock(World world, BlockPos gatePos, BlockState gateState){
        Direction facing = gateState.get(FACING);
        BlockPos targetPos = gatePos.offset(facing.getOpposite());

        //I don't understand this part
        world.updateNeighbor(targetPos, this, gatePos);
        world.updateNeighborsExcept(targetPos, this, facing);
    }

    protected final void updateSideBlock(World world, BlockPos gatePos, BlockState gateState){
        Direction targetDirection = getCrossInputSide(gateState).onDirection(gateState.get(FACING)).getOpposite();
        BlockPos targetPos = gatePos.offset(targetDirection);

        //I don't understand this part
        world.updateNeighbor(targetPos, this, gatePos);
        world.updateNeighborsExcept(targetPos, this, targetDirection);
    }

    private RelativeSide getCrossInputSide(BlockState gateState){
        return gateState.get(MIRRORED) ? RelativeSide.RIGHT : RelativeSide.LEFT;
    }

    private boolean getCrossInputActive(World world, BlockPos gatePos, BlockState gateState) {
        return getInputActive(world, gatePos, world.getBlockState(gatePos),getCrossInputSide(gateState));
    }
}
