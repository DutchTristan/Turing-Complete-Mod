package name.turingcomplete.init;

import name.turingcomplete.TuringComplete;
import name.turingcomplete.block.NAND_Gate_Block;
import name.turingcomplete.block.NOT_Gate_Block;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import static net.minecraft.block.AbstractBlock.*;

public class blockInit {
    public static final NAND_Gate_Block NAND_GATE_BLOCK = registerWithItem("nand_gate_block",
            new NAND_Gate_Block(Settings.copy(Blocks.COMPARATOR)));


    public static final NOT_Gate_Block NOT_GATE = registerWithItem("not_gate_block",
            new NOT_Gate_Block(AbstractBlock.Settings.copy(Blocks.COMPARATOR)));

    public static <T extends Block> T register(String name, T block){
        return Registry.register(Registries.BLOCK, TuringComplete.id(name), block);
    }

    public static <T extends Block> T registerWithItem(String name, T block, Item.Settings settings){
        T registered = register(name, block);
        itemInit.register(name, new BlockItem(registered, settings));
        return registered;
    }

    public static <T extends Block> T registerWithItem(String name, T block){
        return registerWithItem(name, block, new Item.Settings());
    }

    public static void load(){}
}
