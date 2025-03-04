package name.turingcomplete.blocks;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.tick.TickPriority;

//any single-block logic unit with one binary output opposite its FACING direction
//todo: why opposite? why north-facing gate not outputing northward?
public abstract class AbstractSimpleGate extends AbstractLogicBlock{
    protected static final BooleanProperty POWERED = Properties.POWERED;
    private static final VoxelShape SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);

    protected AbstractSimpleGate(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(POWERED,false));
    }
    
    protected abstract boolean evaluateGate(World world, BlockPos gatePos, BlockState gateState);
    protected int getOutputDelay(BlockState gateState) {
        return 2;
    }

    //allow immediate block state changes in response to input, such as highlighting active inputs
    protected void onNeighborUpdate(World world, BlockPos gatePos, BlockState gateState){}

    //allow for block state changes after output is calculated, such as highlighting output if active
    //redstone output is handled by AbstractSimpleGate
    protected void onOutputChange(World world, BlockPos gatePos, BlockState gateState){}

    //position and state are not relevant
    @Override
    public boolean canMirrorHere(BlockPos mainPos, BlockState mainState){
        return canMirror();
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!canMirror()) {
            return ActionResult.PASS;
        }

        world.setBlockState(pos, state.with(MIRRORED, !state.get(MIRRORED)));
        if(state.get(MIRRORED)) {
            world.playSound(player, pos, SoundEvents.BLOCK_COMPARATOR_CLICK, SoundCategory.BLOCKS, 0.3F, 0.5F);
        }
        else {
            world.playSound(player, pos, SoundEvents.BLOCK_COMPARATOR_CLICK, SoundCategory.BLOCKS, 0.3F, 0.55F);
        }
        world.scheduleBlockTick(pos,this, getOutputDelay(state), TickPriority.VERY_HIGH);

        return ActionResult.SUCCESS_NO_ITEM_USED;
    }

    //default gate arrangement: output on the front, and inputs on the left and right
    @Override
    public Boolean dustConnectsToThis(BlockState gateState, Direction direction){
        //front output
        Direction facing = gateState.get(FACING);
        if (direction == facing) {
            return true;
        }
        //side inputs
        if (direction.getAxis() != facing.getAxis() && direction.getAxis() != Axis.Y) {
            return true;
        }
        return false;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(POWERED);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        return SHAPE;
    }

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        boolean gateEval = evaluateGate(world, pos, state);
        if (gateEval != state.get(POWERED)) {
            //use separate variable to make sure onOutputChange can add to state
            state = state.with(POWERED,gateEval);
            world.setBlockState(pos, state);
            onOutputChange(world, pos, state);
            updateOutputBlock(world,pos,state);
        }
    }
    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
        onNeighborUpdate(world,pos,state);
        handleInputChange(world,pos,state);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        //redstone dust hasn't been redirected yet, so must schedule unconditionally
        world.scheduleBlockTick(pos,this, getOutputDelay(state), TickPriority.VERY_HIGH);
    }

    @Override
    protected final int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction)
    {
        return super.getStrongRedstonePower(state, world, pos, direction);
    }

    @Override
    protected final int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction)
    {
        return (direction == state.get(FACING) && state.get(POWERED)) ? 15 : 0;
    }

    protected final void handleInputChange(World world, BlockPos gatePos, BlockState gateState){
        if (evaluateGate(world, gatePos, gateState) != gateState.get(POWERED)) {
            world.scheduleBlockTick(gatePos,this, getOutputDelay(gateState), TickPriority.VERY_HIGH);
        }
    }

    protected final void updateOutputBlock(World world, BlockPos gatePos, BlockState gateState){
        Direction facing = gateState.get(FACING);
        BlockPos targetPos = gatePos.offset(facing.getOpposite());

        //I don't understand this part
        world.updateNeighbor(targetPos, this, gatePos);
        world.updateNeighborsExcept(targetPos, this, facing);
    }
}
