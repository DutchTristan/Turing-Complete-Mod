package name.turingcomplete.blocks;

import org.jetbrains.annotations.MustBeInvokedByOverriders;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
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
public abstract class AbstractSimpleGate extends AbstractSimpleLogicBlock{
    protected static final BooleanProperty POWERED = Properties.POWERED;
    private static final VoxelShape SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);

    protected AbstractSimpleGate(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(POWERED,false));
    }
    
    protected abstract boolean evaluateGate(World world, BlockPos gatePos, BlockState gateState);

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
    @MustBeInvokedByOverriders
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
    protected final int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction)
    {
        return super.getStrongRedstonePower(state, world, pos, direction);
    }

    @Override
    protected final int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction)
    {
        return (direction == state.get(FACING) && state.get(POWERED)) ? 15 : 0;
    }

    @Override
    @MustBeInvokedByOverriders
    protected void onNeighborUpdate(World world, BlockPos gatePos, BlockState gateState){
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
