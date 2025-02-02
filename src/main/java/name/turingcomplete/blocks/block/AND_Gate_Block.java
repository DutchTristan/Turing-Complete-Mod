package name.turingcomplete.blocks.block;

import name.turingcomplete.blocks.AbstractSimpleLogicGate;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;


public class AND_Gate_Block extends AbstractSimpleLogicGate {

    public AND_Gate_Block(Settings settings)
    {super(settings);}

    @Override
    protected boolean gateConditionMet(World world, BlockPos pos, BlockState state) {
        boolean left = isInputPowered(world, state, pos,InputDirection.LEFT);
        boolean right = isInputPowered(world, state, pos,InputDirection.RIGHT);
        return (left && right);
    }

    public boolean supportsSideDirection(BlockState state, Direction direction)
    {return true;}
    public boolean supportsBackDirection()
    {return false;}

}