package name.turingcomplete.blocks.block;

import name.turingcomplete.blocks.AbstractSimpleGate;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;


public class ThreeANDGateBlock extends AbstractSimpleGate {

    public ThreeANDGateBlock(Settings settings) {
        super(settings);
    }
    
    @Override
    protected boolean evaluateGate(World world, BlockPos gatePos, BlockState gateState) {
        boolean left = getInputActive(world, gatePos, gateState,RelativeSide.LEFT);
        boolean right = getInputActive(world, gatePos, gateState,RelativeSide.RIGHT);
        boolean back = getInputActive(world, gatePos, gateState,RelativeSide.BACK);
        return (left && right && back);
    }

    @Override
    public Boolean dustConnectsToThis(BlockState gateState, Direction direction){
        return true;
    }
}