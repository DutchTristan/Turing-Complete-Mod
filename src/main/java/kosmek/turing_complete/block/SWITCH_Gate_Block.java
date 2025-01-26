package kosmek.turing_complete.block;

import kosmek.turing_complete.AbstractEnableGate;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class SWITCH_Gate_Block extends AbstractEnableGate {

    public SWITCH_Gate_Block(Settings settings) {
        super(settings);
    }

    @Override
    public boolean gateConditionsMet(BlockState thisBlockState, World world, BlockPos pos) {
        if (hasEnable(world, pos, thisBlockState)){
            boolean back = getFrontInputLevel(thisBlockState, world, pos) > 0;
            return back;
        }
        return false;

    }

    @Override
    public boolean hasEnable(World world, BlockPos pos, BlockState state){
        boolean left = !state.get(SWAPPED_DIR);
        int leftParameter = 0;
        if (left){
            leftParameter = 1;
        }
        if (getSideInputLevel(state, world, pos, leftParameter) > 0){
            world.setBlockState(pos, state.with(ENABLED, true));
            return true;
        }
        world.setBlockState(pos, state.with(ENABLED, false));
        return false;
    }

    //@Override
    //public void appendTooltip(ItemStack itemStack, Item.TooltipContext context, List<Text> tooltip, TooltipType options) {
    //    tooltip.add(Text.translatable("block.turingcomplete.switch_gate_block.tooltip").formatted(Formatting.RED).formatted(Formatting.ITALIC));
    //}
}