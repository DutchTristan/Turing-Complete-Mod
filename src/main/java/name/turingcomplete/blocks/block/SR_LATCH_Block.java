package name.turingcomplete.blocks.block;

import name.turingcomplete.blocks.AbstractSimpleLogicGate;
import name.turingcomplete.init.propertyInit;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class SR_LATCH_Block extends AbstractSimpleLogicGate {
    private static final BooleanProperty SWAP = propertyInit.SWAPPED_DIR;

    public SR_LATCH_Block(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState()
                .with(SWAP,false)
        );
    }

    @Override
    protected void properties(StateManager.Builder<Block, BlockState> builder)
    {super.properties(builder);     builder.add(SWAP);}

    /*
         On Set (Left) : True
         On Reset (Right) (Dominant) : False
         On No Power In : SET Property Value
     */

    @Override
    public boolean gateConditionMet( World world, BlockPos pos, BlockState state) {
        boolean set = isInputPowered(world,state,pos,getSetDirection(state));
        boolean reset = isInputPowered(world,state,pos,getSetDirection(state).getOpposite());

        if(!set && reset)
            return false;
        if(set && !reset)
            return true;
        if(reset)
            return false;

        return state.get(POWERED);
    }


    @Override
    public boolean supportsSideDirection(BlockState state, Direction direction) {return true;}
    @Override
    public boolean supportsBackDirection() {return false;}

    private InputDirection getSetDirection(BlockState state){
        return state.get(SWAP) ? InputDirection.LEFT : InputDirection.RIGHT;
    }
}
