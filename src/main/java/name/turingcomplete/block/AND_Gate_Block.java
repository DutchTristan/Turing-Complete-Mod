package name.turingcomplete.block;

import name.turingcomplete.AbstractLogicGate;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;


public class AND_Gate_Block extends AbstractLogicGate {

    public AND_Gate_Block(Settings settings) {
        super(settings);
    }

    @Override
    public boolean gateConditionsMet(BlockState thisBlockState, World world, BlockPos pos)
    {
        boolean left = getSideInputLevel(thisBlockState, world, pos,0) > 0;
        boolean right = getSideInputLevel(thisBlockState, world, pos, 1) > 0;
        return (left && right);
    }

    @Override
    public boolean supportsSideDirection() {
        return true;
    }

    @Override
    public boolean supportsBackDirection() {
        return false;
    }
}