package name.turingcomplete.block;

import name.turingcomplete.AbstractEnableGate;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
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
        boolean back = getFrontInputLevel(thisBlockState, world, pos) > 0;
        return back;
    }

    @Override
    public void appendTooltip(ItemStack itemStack, Item.TooltipContext context, List<Text> tooltip, TooltipType options) {
        tooltip.add(Text.translatable("block.turingcomplete.switch_gate_block.tooltip").formatted(Formatting.RED).formatted(Formatting.ITALIC));
    }
}