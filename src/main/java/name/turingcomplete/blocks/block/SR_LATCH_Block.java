package name.turingcomplete.blocks.block;

import name.turingcomplete.blocks.AbstractLatchBlock;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.tick.TickPriority;

public class SR_LATCH_Block extends AbstractLatchBlock {
    public SR_LATCH_Block(Settings settings) {super(settings);}

    /*
     On Set (Left) : True
     On Reset (Right) (Dominant) : False
     On No Power In : SET Property Value
     */
    @Override
    public boolean latchConditionsMet(BlockState state, World world, BlockPos pos) {
        boolean left = getSideInputLevel(state,world,pos,0) > 0;
        boolean right = getSideInputLevel(state,world,pos,1) > 0;

        if(!right && left)
            return false;
        if(!left && right)
            return true;
        if(right)
            return false;

        return state.get(SET);
    }


    @Override
    public boolean supportsSideDirection() {return true;}
    @Override
    public boolean supportsBackDirection() {return false;}
}
