package name.turingcomplete.block;

import name.turingcomplete.AbstractLogicGate;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;


public class NOT_Gate_Block extends AbstractLogicGate {

    public NOT_Gate_Block(Settings settings) {
        super(settings);
    }

    @Override
    public boolean gateConditionsMet(BlockState thisBlockState, World world, BlockPos pos)
    {
        boolean input = getFrontInputLevel(thisBlockState, world, pos) > 0;
        return !input;
    }
}