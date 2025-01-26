package kosmek.turing_complete.block;

import kosmek.turing_complete.AbstractEnableGate;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import static kosmek.turing_complete.AbstractEnableGate.SWAPPED_DIR;
import static net.minecraft.block.HorizontalFacingBlock.FACING;

public class Redstone_Bridge_Block extends AbstractEnableGate {

    public Redstone_Bridge_Block(AbstractBlock.Settings settings) {
        super(settings);
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        Direction dir = null;
        if (state.get(SWAPPED_DIR)){
            dir = state.get(FACING).rotateYCounterclockwise();
        }
        else {
            dir = state.get(FACING).rotateYClockwise();
        }

        if (!(Boolean) state.get(POWERED) && !state.get(ENABLED)){
            return 0;
        }
        else if ((Boolean) state.get(POWERED) && !state.get(ENABLED)){
            return state.get(FACING) == direction ? this.getOutputLevel(world, pos, state) : 0;
        }
        else if (!(Boolean) state.get(POWERED) && state.get(ENABLED)){
            return dir == direction ? this.getOutputLevel(world, pos, state) : 0;
        }
        else{
            return (dir == direction || state.get(FACING) == direction)
                    ? this.getOutputLevel(world,pos,state) : 0;
        }
    }

    @Override
    public boolean hasEnable(World world, BlockPos pos, BlockState state){
        boolean left = !state.get(SWAPPED_DIR);
        int leftParameter = 0;
        if (left){
            leftParameter = 1;
        }
        if (getSideInputLevel(state, world, pos, leftParameter) > 0){
            world.setBlockState(pos, state.with(ENABLED, true));
            return true;
        }
        world.setBlockState(pos, state.with(ENABLED, false));
        return false;
    }

    // used by RedstoneWireBlockMixin to determine where redstone should connect
    @Override
    public Boolean dustConnectsToThis(BlockState state, Direction dir) {
        return true;
    }

    @Override
    public boolean gateConditionsMet(BlockState state, World world, BlockPos pos) {
        hasEnable(world,pos,state);
        return getFrontInputLevel(state, world, pos) > 0;
    }
}
