package name.turingcomplete.blocks.block;

import name.turingcomplete.blocks.AbstractLogicBlock;
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
        Direction toSideOutputDirection = getCrossInputSide(state).withBackDirection(state.get(FACING));
        Direction toFrontOutputDirection = state.get(FACING);

        if (direction == toFrontOutputDirection)
            return state.get(POWERED_Z) ? 15 : 0;
        else if (direction == toSideOutputDirection)
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
            //skip notifying neighbors, since outputs are explicitly updated
            world.setBlockState(
                pos,
                state
                    .with(POWERED_X,side_input_active)
                    .with(POWERED_Z,back_input_active),
                Block.NOTIFY_LISTENERS);
            if(will_change_side_out) {
                updateSideOutput(world, pos, state);
            }
            if(will_change_front_out) {
                updateFrontOutput(world, pos, state);
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
        //front input doesn't change, old side input is explicitly updated, new side input does not need to be updated until shceduled tick
        world.setBlockState(pos, state, Block.NOTIFY_LISTENERS);
        if(state.get(MIRRORED)) {
            world.playSound(player, pos, SoundEvents.BLOCK_COMPARATOR_CLICK, SoundCategory.BLOCKS, 0.3F, 0.5F);
        }
        else {
            world.playSound(player, pos, SoundEvents.BLOCK_COMPARATOR_CLICK, SoundCategory.BLOCKS, 0.3F, 0.55F);
        }
        world.scheduleBlockTick(pos,this, gate_delay, TickPriority.VERY_HIGH);

        //prevent stuck strong-power when mirroring
        updateOldSideBlock(world,pos,state);

        return ActionResult.SUCCESS_NO_ITEM_USED;
    }

    protected final void updateFrontOutput(World world, BlockPos gatePos, BlockState gateState){
        updateOutputBlock(world, gatePos,gateState.get(FACING).getOpposite());
    }

    protected final void updateSideOutput(World world, BlockPos gatePos, BlockState gateState){
        //output side is the opposite of the input side
        updateOutputBlock(world, gatePos, getCrossInputSide(gateState).getOpposite().withBackDirection(gateState.get(FACING)));
    }

    protected final void updateOldSideBlock(World world, BlockPos gatePos, BlockState gateState){
        //old output side is the new input side
        updateOutputBlock(world, gatePos, getCrossInputSide(gateState).withBackDirection(gateState.get(FACING)));
    }

    private RelativeSide getCrossInputSide(BlockState gateState){
        return gateState.get(MIRRORED) ? RelativeSide.RIGHT : RelativeSide.LEFT;
    }

    private boolean getCrossInputActive(World world, BlockPos gatePos, BlockState gateState) {
        return getInputActive(world, gatePos, gateState,getCrossInputSide(gateState));
    }
}
