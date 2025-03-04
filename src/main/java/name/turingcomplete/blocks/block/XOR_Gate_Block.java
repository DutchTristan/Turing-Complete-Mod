package name.turingcomplete.blocks.block;

import name.turingcomplete.blocks.AbstractSimpleGate;
import name.turingcomplete.blocks.RelativeSide;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;


public class XOR_Gate_Block extends AbstractSimpleGate {

    public XOR_Gate_Block(Settings settings) {
        super(settings);
    }

    @Override
    protected boolean evaluateGate(World world, BlockPos gatePos, BlockState gateState) {
        boolean left = getInputActive(world, gatePos, gateState,RelativeSide.LEFT);
        boolean right = getInputActive(world, gatePos, gateState,RelativeSide.RIGHT);
        return (left ^ right);
    }
}