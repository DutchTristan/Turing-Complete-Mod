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

public class JK_LATCH_Block extends AbstractSimpleLogicGate {
    private static final BooleanProperty SWAP = propertyInit.SWAPPED_DIR;
    private static final BooleanProperty SET_ENABLED = propertyInit.POWERED_X;
    private static final BooleanProperty RESET_ENABLED = propertyInit.POWERED_Z;


    public JK_LATCH_Block(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState()
                .with(SWAP,false)
                .with(SET_ENABLED,false)
                .with(RESET_ENABLED,false)
        );
    }

    @Override
    protected void properties(StateManager.Builder<Block, BlockState> builder)
    {super.properties(builder);     builder.add(SWAP,SET_ENABLED,RESET_ENABLED);}

    /*
         On Set (Left) : True
         On Reset (Right) (Dominant) : False
         On No Power In : SET Property Value
     */

    @Override
    public boolean gateConditionMet( World world, BlockPos pos, BlockState state) {
        boolean set = isInputPowered(world,state,pos, getSetDirection(state));
        boolean reset = isInputPowered(world,state,pos,getSetDirection(state).getOpposite());

        if(!set && reset)
            return false;
        if(set && !reset)
            return true;
        if(set)
            return !state.get(POWERED);

        return state.get(POWERED);
    }

    @Override
    protected boolean shouldUpdate(World world, BlockState state, BlockPos pos) {
        return (isInputPowered(world,state,pos, getSetDirection(state)) && !state.get(SET_ENABLED) ||
                isInputPowered(world,state,pos,getSetDirection(state).getOpposite()) && !state.get(RESET_ENABLED)) &&
                gateConditionMet(world, pos, state) != state.get(POWERED) &&
                !world.getBlockTickScheduler().isTicking(pos, this);
    }

    @Override
    protected void updateImmediate(World world, BlockPos pos, BlockState state) {
        boolean set = isInputPowered(world,state,pos, getSetDirection(state));
        boolean reset = isInputPowered(world,state,pos,getSetDirection(state).getOpposite());

        world.setBlockState(pos,state.with(SET_ENABLED,set).with(RESET_ENABLED, reset));
    }

    @Override
    protected boolean shouldUpdateImmediate(World world, BlockState state, BlockPos pos) {
        return isInputPowered(world,state,pos, getSetDirection(state)) != state.get(SET_ENABLED) ||
               isInputPowered(world,state,pos,getSetDirection(state).getOpposite()) != state.get(RESET_ENABLED);
    }

    @Override
    public boolean supportsSideDirection(BlockState state, Direction direction) {return true;}
    @Override
    public boolean supportsBackDirection() {return false;}

    private InputDirection getSetDirection(BlockState state){
        return state.get(SWAP) ? InputDirection.LEFT : InputDirection.RIGHT;
    }
}
