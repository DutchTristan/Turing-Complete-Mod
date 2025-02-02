package name.turingcomplete.blocks.block;

import name.turingcomplete.blocks.AbstractGate;
import name.turingcomplete.blocks.AbstractSimpleLogicGate;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;


public class THREE_OR_Gate_Block extends AbstractSimpleLogicGate {

    public THREE_OR_Gate_Block(Settings settings) {
        super(settings);
    }

    @Override
    public boolean gateConditionMet(World world, BlockPos pos, BlockState thisBlockState) {
        boolean left = isInputPowered(world, thisBlockState, pos, AbstractGate.InputDirection.LEFT) ;
        boolean right = isInputPowered(world, thisBlockState, pos, AbstractGate.InputDirection.RIGHT);
        boolean back = isInputPowered(world, thisBlockState, pos, InputDirection.BACK);
        return left || right || back;
    }

    public boolean supportsSideDirection(BlockState state, Direction direction)
    {return true;}
    public boolean supportsBackDirection()
    {return true;}
}