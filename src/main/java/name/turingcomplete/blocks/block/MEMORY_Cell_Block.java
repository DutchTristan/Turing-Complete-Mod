package name.turingcomplete.blocks.block;

import name.turingcomplete.blocks.AbstractEnableGate;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MEMORY_Cell_Block extends AbstractEnableGate {

    public MEMORY_Cell_Block(Settings settings) {
        super(settings);
    }

    @Override
    public boolean gateConditionsMet(BlockState thisBlockState, World world, BlockPos pos) {
        boolean back = getFrontInputLevel(thisBlockState, world, pos) > 0;
        if (hasEnable(world, pos, thisBlockState)){
            return back;
        }
        return thisBlockState.get(POWERED);
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
}