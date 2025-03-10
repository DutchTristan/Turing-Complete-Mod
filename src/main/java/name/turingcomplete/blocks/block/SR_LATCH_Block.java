package name.turingcomplete.blocks.block;

import name.turingcomplete.blocks.AbstractSimpleGate;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SR_LATCH_Block extends AbstractSimpleGate {

    public SR_LATCH_Block(Settings settings) {
        super(settings);
    }

    @Override
    public boolean canMirror(){
        return true;
    }

    /*
         On Set (Left) : True
         On Reset (Right) (Dominant) : False
         On No Power In : POWERED Property Value
     */

    @Override
    public boolean evaluateGate( World world, BlockPos gatePos, BlockState gateState) {
        boolean set = getInputActive(world,gatePos,gateState,getSetDirection(gateState));
        boolean reset = getInputActive(world,gatePos,gateState,getResetDirection(gateState));

        if(!set && reset)
            return false;
        if(set && !reset)
            return true;
        if(reset) //&& set
            //the "illegal" state, defined as false
            return false;

        return gateState.get(POWERED);
    }


    protected RelativeSide getSetDirection(BlockState state){
        return state.get(MIRRORED) ? RelativeSide.LEFT : RelativeSide.RIGHT;
    }

    protected RelativeSide getResetDirection(BlockState state){
        return state.get(MIRRORED) ? RelativeSide.RIGHT : RelativeSide.LEFT;
    }
}
