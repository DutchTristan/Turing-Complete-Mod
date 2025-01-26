package kosmek.turing_complete.init;

import kosmek.turing_complete.TuringCompleteUnofficial;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ItemGroupInit {

    private static final Text TITLE = Text.translatable("itemGroup." + TuringCompleteUnofficial.MOD_ID + ".group");

    public static final ItemGroup TURING_GROUP = register("turing_group", FabricItemGroup.builder()
            .icon(() -> new ItemStack(BlockInit.AND_GATE))
            .displayName(TITLE)
            .entries((context, entries) -> {
                entries.add(BlockInit.LOGIC_BASE_PLATE_BLOCK);
                entries.add(BlockInit.NAND_GATE);
                entries.add(BlockInit.NOT_GATE);
                entries.add(BlockInit.OR_GATE);
                entries.add(BlockInit.AND_GATE);
                entries.add(BlockInit.NOR_GATE);
                entries.add(BlockInit.XOR_GATE);
                entries.add(BlockInit.XNOR_GATE);
                entries.add(BlockInit.THREE_AND_GATE);
                entries.add(BlockInit.THREE_OR_GATE);
                entries.add(BlockInit.SWITCH_GATE);
                //entries.add(BlockInit.MEMORY_CELL);
                entries.add(BlockInit.HALF_ADDER);
                entries.add(BlockInit.FULL_ADDER);
                entries.add(BlockInit.REDSTONE_BRIDGE_BLOCK);
            })
            .build());


    public static <T extends ItemGroup> T register(String name, T itemGroup){
        return  Registry.register(Registries.ITEM_GROUP, new Identifier(TuringCompleteUnofficial.MOD_ID,name), itemGroup);
    }

    public static void load(){}
}
