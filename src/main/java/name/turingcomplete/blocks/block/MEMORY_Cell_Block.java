package name.turingcomplete.blocks.block;

import name.turingcomplete.blocks.AbstractSimpleGate;
import name.turingcomplete.blocks.RelativeSide;
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

public class MEMORY_Cell_Block extends AbstractSimpleGate {
    public static final BooleanProperty ENABLED = Properties.ENABLED;

    public MEMORY_Cell_Block(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState()
                .with(ENABLED,false)
        );
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(ENABLED);
    }

    @Override
    protected boolean evaluateGate(World world, BlockPos gatePos, BlockState gateState) {
        boolean signal = getInputActive(world, gatePos, gateState,RelativeSide.BACK);
        boolean enable = getInputActive(world, gatePos, gateState,getEnabledSide(gateState));

        if (enable) {
            return signal;
        }
        else {
            return gateState.get(POWERED);
        }
    }

    @Override 
    public boolean canMirror(){
        return true;
    }

    @Override
    protected void onNeighborUpdate(World world, BlockPos gatePos, BlockState gateState){
        super.onNeighborUpdate(world, gatePos, gateState);
        world.setBlockState(gatePos, gateState.with(
            ENABLED,
            getInputActive(world, gatePos, gateState, getEnabledSide(gateState))));
    }

    @Override
    public Boolean dustConnectsToThis(BlockState gateState, Direction direction){
        if (direction.getAxis() == gateState.get(FACING).getAxis()) {
            return true;
        }
        Direction facing = gateState.get(FACING);
        return direction == getEnabledSide(gateState).onDirection(facing);
    }

    private RelativeSide getEnabledSide(BlockState state){
        return state.get(MIRRORED) ? RelativeSide.RIGHT : RelativeSide.LEFT;
    }

    @Override
    public void appendTooltip(ItemStack itemStack, Item.TooltipContext context, List<Text> tooltip, TooltipType options) {
        tooltip.add(Text.translatable("block.turingcomplete.memory_cell_gate.tooltip").formatted(Formatting.RED).formatted(Formatting.ITALIC));
    }
}