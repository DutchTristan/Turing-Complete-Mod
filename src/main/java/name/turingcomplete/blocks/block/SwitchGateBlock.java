package name.turingcomplete.blocks.block;

import name.turingcomplete.blocks.AbstractSimpleGate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;

public class SwitchGateBlock extends AbstractSimpleGate {
    public static final BooleanProperty ENABLED = Properties.ENABLED;
    public SwitchGateBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(ENABLED,false));
    }

    @Override
    protected boolean evaluateGate(World world, BlockPos gatePos, BlockState gateState) {
        boolean signal = getInputActive(world, gatePos, gateState,RelativeSide.BACK);
        boolean enable = getInputActive(world, gatePos, gateState,getEnabledSide(gateState));
        return (signal && enable);
    }

    @Override
    protected void onInputChange(World world, BlockPos gatePos, BlockState gateState){
        super.onInputChange(world, gatePos, gateState);
        //don't notify neighbors, because they should not care about this
        //"listeners" includes server -> client communication, so is probably still needed
        world.setBlockState(gatePos,
            gateState.with(
                ENABLED,
                getInputActive(world, gatePos, gateState, getEnabledSide(gateState))),
            Block.NOTIFY_LISTENERS); 
    }

    @Override 
    public boolean canMirror(){
        return true;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(ENABLED);
    }

    @Override
    public Boolean dustConnectsToThis(BlockState gateState, Direction direction){
        if (direction.getAxis() == gateState.get(FACING).getAxis()) {
            return true;
        }
        Direction facing = gateState.get(FACING);
        return direction == getEnabledSide(gateState).withBackDirection(facing).getOpposite();
    }

    private RelativeSide getEnabledSide(BlockState state){
        return state.get(MIRRORED) ? RelativeSide.RIGHT : RelativeSide.LEFT;
    }

    @Override
    public void appendTooltip(ItemStack itemStack, Item.TooltipContext context, List<Text> tooltip, TooltipType options) {
        tooltip.add(Text.translatable("block.turingcomplete.switch_gate_block.tooltip").formatted(Formatting.RED).formatted(Formatting.ITALIC));
    }
}