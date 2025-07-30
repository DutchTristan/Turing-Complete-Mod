package name.turingcomplete.init;

import name.turingcomplete.TuringComplete;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;

public class itemGroupInit {

    private static final Text TITLE = Text.translatable("itemGroup." + TuringComplete.MOD_ID + ".group");

    public static final ItemGroup TURING_GROUP = register("turing_group", FabricItemGroup.builder()
            .icon(() -> new ItemStack(blockInit.AND_GATE))
            .displayName(TITLE)
            .entries((context, entries) -> {
                entries.add(blockInit.LOGIC_BASE_PLATE_BLOCK);
                entries.add(blockInit.NAND_GATE);
                entries.add(blockInit.NOT_GATE);
                entries.add(blockInit.OR_GATE);
                entries.add(blockInit.AND_GATE);
                entries.add(blockInit.NOR_GATE);
                entries.add(blockInit.XOR_GATE);
                entries.add(blockInit.XNOR_GATE);
                entries.add(blockInit.THREE_AND_GATE);
                entries.add(blockInit.THREE_OR_GATE);
                entries.add(blockInit.SWITCH_GATE);
                entries.add(blockInit.MEMORY_CELL);
                entries.add(blockInit.HALF_ADDER);
                entries.add(blockInit.FULL_ADDER);
                entries.add(blockInit.BI_DIRECTIONAL_REDSTONE_BRIDGE_BLOCK);
                entries.add(blockInit.OMNI_DIRECTIONAL_REDSTONE_BRIDGE_BLOCK);
                entries.add(blockInit.SR_LATCH_BLOCK);
                entries.add(blockInit.JK_LATCH_BLOCK);
                entries.add(blockInit.T_LATCH_BLOCK);
                entries.add(blockInit.TRUTH_TABLE);
            })
            .build());


    public static <T extends ItemGroup> T register(String name, T itemGroup){
        return  Registry.register(Registries.ITEM_GROUP, TuringComplete.id(name), itemGroup);
    }

    public static void load(){}
}
