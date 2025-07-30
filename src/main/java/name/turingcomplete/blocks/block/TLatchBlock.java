package name.turingcomplete.blocks.block;

import name.turingcomplete.blocks.AbstractSimpleGate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class TLatchBlock extends AbstractSimpleGate {
    protected static final BooleanProperty WAS_TOGGLED = BooleanProperty.of("was_toggled");

    public TLatchBlock(Settings settings) {
        super(settings);
    }

    @Override
    public boolean evaluateGate(World world, BlockPos gatePos, BlockState gateState) {
        boolean toggle = getInputActive(world, gatePos, gateState,RelativeSide.BACK);
        boolean powered = gateState.get(POWERED);
        boolean was_toggled = gateState.get(WAS_TOGGLED);
        if(was_toggled) return powered;

        if (toggle) return !powered;

        return powered;
    }

    @Override
    protected void onOutputChange(World world, BlockPos gatePos, BlockState gateState){
        //don't notify neighbors, because they should not care about this
        //"listeners" includes server -> client communication, so is probably still needed
        world.setBlockState(gatePos, gateState.with(WAS_TOGGLED, true),Block.NOTIFY_LISTENERS);
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
        boolean toggle = getInputActive(world, pos, state,RelativeSide.BACK);
        if (!toggle) {
            //don't notify neighbors, because they should not care about this
            //"listeners" includes server -> client communication, so is probably still needed
            world.setBlockState(pos, state.with(WAS_TOGGLED, false),Block.NOTIFY_LISTENERS);
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(WAS_TOGGLED);
    }

    @Override
    public Boolean dustConnectsToThis(BlockState gateState, Direction direction){
        return direction.getAxis() == gateState.get(FACING).getAxis();
    }
}
