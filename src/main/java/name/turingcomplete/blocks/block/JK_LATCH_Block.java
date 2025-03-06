package name.turingcomplete.blocks.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class JK_LATCH_Block extends SR_LATCH_Block {
    protected static final BooleanProperty WAS_TOGGLED = BooleanProperty.of("was_toggled");

    public JK_LATCH_Block(Settings settings) {
        super(settings);
    }

    /*
         On Set (Left) : True
         On Reset (Right) : False
         On No Power In : POWERED Property Value
     */

     @Override
     public boolean evaluateGate( World world, BlockPos gatePos, BlockState gateState) {
         boolean set = getInputActive(world,gatePos,gateState,getSetDirection(gateState));
         boolean reset = getInputActive(world,gatePos,gateState,getResetDirection(gateState));
         boolean was_toggled = gateState.get(WAS_TOGGLED);

        if (set && reset) {
            if(was_toggled) return gateState.get(POWERED);
            return !gateState.get(POWERED);
        }
        
        return super.evaluateGate(world, gatePos, gateState);
    }

    @Override
    protected void onOutputChange(World world, BlockPos gatePos, BlockState gateState){
        boolean set = getInputActive(world,gatePos,gateState,getSetDirection(gateState));
        boolean reset = getInputActive(world,gatePos,gateState,getResetDirection(gateState));
        if (set && reset) {
            world.setBlockState(gatePos, gateState.with(WAS_TOGGLED, true));
        }
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
        boolean set = getInputActive(world,pos,state,getSetDirection(state));
        boolean reset = getInputActive(world,pos,state,getResetDirection(state));
        if (!set || !reset) {
            world.setBlockState(pos, state.with(WAS_TOGGLED, false));
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(WAS_TOGGLED);
    }
}
