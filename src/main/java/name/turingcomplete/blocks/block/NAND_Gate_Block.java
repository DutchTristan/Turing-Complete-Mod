package name.turingcomplete.blocks.block;

import name.turingcomplete.blocks.AbstractSimpleLogicGate;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;


public class NAND_Gate_Block extends AbstractSimpleLogicGate {

    public NAND_Gate_Block(Settings settings) {
        super(settings);
    }

    @Override
    public boolean gateConditionMet(World world, BlockPos pos, BlockState thisBlockState) {
        boolean left = isInputPowered(world, thisBlockState, pos,InputDirection.LEFT) ;
        boolean right = isInputPowered(world, thisBlockState, pos, InputDirection.RIGHT);
        return !(left && right);
    }

    public boolean supportsSideDirection(BlockState state, Direction direction)
    {return true;}
    public boolean supportsBackDirection()
    {return false;}
}