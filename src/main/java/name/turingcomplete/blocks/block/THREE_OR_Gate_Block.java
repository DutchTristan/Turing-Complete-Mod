package name.turingcomplete.blocks.block;

import name.turingcomplete.blocks.AbstractLogicGate;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;


public class THREE_OR_Gate_Block extends AbstractLogicGate {

    public THREE_OR_Gate_Block(Settings settings) {
        super(settings);
    }

    @Override
    public boolean gateConditionsMet(BlockState thisBlockState, World world, BlockPos pos)
    {
        boolean left = getSideInputLevel(thisBlockState, world, pos,0) > 0;
        boolean right = getSideInputLevel(thisBlockState, world, pos, 1) > 0;
        boolean back = getFrontInputLevel(thisBlockState, world, pos) > 0;
        return (left || right || back);
    }

    @Override
    public boolean supportsSideDirection() {
        return true;
    }
}