package name.turingcomplete.blocks;

import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.tick.TickPriority;

//any single-block logic unit with one binary output opposite its FACING direction
//FACING is backwards for save compatibility
public abstract class AbstractSimpleGate extends AbstractSimpleLogicBlock{
    protected static final BooleanProperty POWERED = Properties.POWERED;

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
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        boolean gateEval = evaluateGate(world, pos, state);
        if (gateEval != state.get(POWERED)) {
            //use separate variable to make sure onOutputChange can add to state
            state = state.with(POWERED,gateEval);
            //updateOutputBlock will update the correct neighbors
            world.setBlockState(pos, state, Block.NOTIFY_LISTENERS);
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
    protected void onInputChange(World world, BlockPos gatePos, BlockState gateState){
        if (evaluateGate(world, gatePos, gateState) != gateState.get(POWERED)) {
            world.scheduleBlockTick(gatePos,this, getOutputDelay(gateState), TickPriority.VERY_HIGH);
        }
    }

    @Override
    @MustBeInvokedByOverriders
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack){
        super.onPlaced(world, pos, state, placer, itemStack);

        if(state.get(POWERED)) {
            updateOutputBlock(world, pos, state);
        }
    }

    @Override
    @MustBeInvokedByOverriders
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if(state.get(POWERED)) {
            //need to change blockstate so that getWeakRedstonePower returns 0
            world.setBlockState(pos, state.with(POWERED,false));
            updateOutputBlock(world, pos, state);
        }
        return super.onBreak(world, pos, state, player);
    }

    protected final void updateOutputBlock(World world, BlockPos gatePos, BlockState gateState){
        Direction facing = gateState.get(FACING);

        super.updateOutputBlock(world,gatePos,facing.getOpposite());
    }
}
