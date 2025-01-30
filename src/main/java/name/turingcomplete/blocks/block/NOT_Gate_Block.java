package name.turingcomplete.blocks.block;

import name.turingcomplete.blocks.AbstractSimpleLogicGate;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;


public class NOT_Gate_Block extends AbstractSimpleLogicGate {

    public NOT_Gate_Block(Settings settings) {
        super(settings);
    }

    @Override
    public boolean gateConditionMet(World world, BlockPos pos, BlockState thisBlockState) {
        boolean back = isInputPowered(world, thisBlockState, pos,InputDirection.BACK) ;
        return !back;
    }

    public boolean supportsSideDirection(BlockState state, Direction direction)
    {return false;}
    public boolean supportsBackDirection()
    {return true;}
}