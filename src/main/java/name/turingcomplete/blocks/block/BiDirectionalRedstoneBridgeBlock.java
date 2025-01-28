package name.turingcomplete.blocks.block;

import name.turingcomplete.blocks.Abstract2WayGate;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class BiDirectionalRedstoneBridgeBlock extends Abstract2WayGate {

    public BiDirectionalRedstoneBridgeBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.cuboid(0, 0, 0, 1, 0.125, 1);
    }

    //=============================================

    @Override
    public void update(World world, BlockPos pos, BlockState state) {
        Direction side_direction = state.get(SWAPPED_DIR) ?
                state.get(FACING).rotateYCounterclockwise() :
                state.get(FACING).rotateYClockwise();

        boolean should_power_z = isInputPowered(world,pos, state.get(FACING).getOpposite());
        boolean should_power_x = isInputPowered(world,pos, side_direction.getOpposite());

        BlockState newState = state;

        if(state.get(POWERED_X) != should_power_x)
            newState = newState.with(POWERED_X, should_power_x);
        if(state.get(POWERED_Z) != should_power_z)
            newState = newState.with(POWERED_Z, should_power_z);

        world.setBlockState(pos, newState);

        world.updateNeighborsAlways(pos.offset(state.get(FACING)), this);
        world.updateNeighborsAlways(pos.offset(side_direction), this);
    }

    @Override
    protected void checkForUpdate(World world, BlockPos pos, BlockState state) {
        boolean should_power_z = isInputPowered(world,pos, state.get(FACING).getOpposite());
        boolean should_power_x = isInputPowered(world,pos, state.get(SWAPPED_DIR) ?
                state.get(FACING).rotateYClockwise() :
                state.get(FACING).rotateYCounterclockwise()
        );

        BlockState newState = state;

        if(state.get(POWERED_X) != should_power_x)
            newState = newState.with(POWERED_X, should_power_x);
        if(state.get(POWERED_Z) != should_power_z)
            newState = newState.with(POWERED_Z, should_power_z);

        if(newState != state)
            world.scheduleBlockTick(pos,this, this.getUpdateDelayInternal(newState));
    }

    @Override
    public Boolean dustConnectsToThis(BlockState state, Direction di) {
        return true;
    }
}
