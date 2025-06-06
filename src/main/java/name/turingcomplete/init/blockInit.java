package name.turingcomplete.init;

import name.turingcomplete.TuringComplete;
import name.turingcomplete.blocks.block.*;
import name.turingcomplete.blocks.multiblock.Adder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;

public class blockInit {
    public static final NAND_Gate_Block NAND_GATE = registerWithItem("nand_gate_block",
            new NAND_Gate_Block(AbstractBlock.Settings.create()
                    .breakInstantly()
                    .sounds(BlockSoundGroup.STONE)
                    .pistonBehavior(PistonBehavior.DESTROY)));


    public static final NOT_Gate_Block NOT_GATE = registerWithItem("not_gate_block",
            new NOT_Gate_Block(AbstractBlock.Settings.create()
                    .breakInstantly()
                    .sounds(BlockSoundGroup.STONE)
                    .pistonBehavior(PistonBehavior.DESTROY)));

    public static final AND_Gate_Block AND_GATE = registerWithItem("and_gate_block",
            new AND_Gate_Block(AbstractBlock.Settings.create()
                    .breakInstantly()
                    .sounds(BlockSoundGroup.STONE)
                    .pistonBehavior(PistonBehavior.DESTROY)));

    public static final Logic_Base_Plate_Block LOGIC_BASE_PLATE_BLOCK = registerWithItem("logic_base_plate_block",
            new Logic_Base_Plate_Block(Block.Settings.create()
                    .breakInstantly()
                    .sounds(BlockSoundGroup.STONE)
                    .pistonBehavior(PistonBehavior.DESTROY)
            ));

    public static final OR_Gate_Block OR_GATE = registerWithItem("or_gate_block",
            new OR_Gate_Block(AbstractBlock.Settings.create()
                    .breakInstantly()
                    .sounds(BlockSoundGroup.STONE)
                    .pistonBehavior(PistonBehavior.DESTROY)));

    public static final NOR_Gate_Block NOR_GATE = registerWithItem("nor_gate_block",
            new NOR_Gate_Block(AbstractBlock.Settings.create()
                    .breakInstantly()
                    .sounds(BlockSoundGroup.STONE)
                    .pistonBehavior(PistonBehavior.DESTROY)));

    public static final XNOR_Gate_Block XNOR_GATE = registerWithItem("xnor_gate_block",
            new XNOR_Gate_Block(AbstractBlock.Settings.create()
                    .breakInstantly()
                    .sounds(BlockSoundGroup.STONE)
                    .pistonBehavior(PistonBehavior.DESTROY)));

    public static final XOR_Gate_Block XOR_GATE = registerWithItem("xor_gate_block",
            new XOR_Gate_Block(AbstractBlock.Settings.create()
                    .breakInstantly()
                    .sounds(BlockSoundGroup.STONE)
                    .pistonBehavior(PistonBehavior.DESTROY)));

    //===============================================================================================

    public static final THREE_AND_Gate_Block THREE_AND_GATE = registerWithItem("3and_gate_block",
            new THREE_AND_Gate_Block(AbstractBlock.Settings.create()
                    .breakInstantly()
                    .sounds(BlockSoundGroup.STONE)
                    .pistonBehavior(PistonBehavior.DESTROY)));

    public static final THREE_OR_Gate_Block THREE_OR_GATE = registerWithItem("3or_gate_block",
            new THREE_OR_Gate_Block(AbstractBlock.Settings.create()
                    .breakInstantly()
                    .sounds(BlockSoundGroup.STONE)
                    .pistonBehavior(PistonBehavior.DESTROY)));

    //===============================================================================================

    public static final SWITCH_Gate_Block SWITCH_GATE = registerWithItem("switch_gate_block",
            new SWITCH_Gate_Block(AbstractBlock.Settings.create()
                    .breakInstantly()
                    .sounds(BlockSoundGroup.STONE)
                    .pistonBehavior(PistonBehavior.DESTROY)));

    public static final MEMORY_Cell_Block MEMORY_CELL = registerWithItem("memory_cell_gate",
            new MEMORY_Cell_Block(AbstractBlock.Settings.create()
                    .breakInstantly()
                    .sounds(BlockSoundGroup.STONE)
                    .pistonBehavior(PistonBehavior.DESTROY)));

    //===============================================================================================

    public static final Adder HALF_ADDER = registerWithItem("half_adder",
            new Adder(AbstractBlock.Settings.create()
                    .breakInstantly()
                    .sounds(BlockSoundGroup.STONE)
                    .pistonBehavior(PistonBehavior.DESTROY),
                    false));

    public static final Adder FULL_ADDER = registerWithItem("full_adder",
            new Adder(AbstractBlock.Settings.create()
                    .breakInstantly()
                    .sounds(BlockSoundGroup.STONE)
                    .pistonBehavior(PistonBehavior.DESTROY),
                    true));

    public static final BiDirectionalRedstoneBridgeBlock BI_DIRECTIONAL_REDSTONE_BRIDGE_BLOCK = registerWithItem("bi_directional_redstone_bridge_block",
            new BiDirectionalRedstoneBridgeBlock(AbstractBlock.Settings.create()
                    .breakInstantly()
                    .sounds(BlockSoundGroup.STONE)
                    .pistonBehavior(PistonBehavior.DESTROY)));

    //===============================================================================================

    public static final OmniDirectionalRedstoneBridgeBlock OMNI_DIRECTIONAL_REDSTONE_BRIDGE_BLOCK = registerWithItem("omni_directional_redstone_bridge_block",
            new OmniDirectionalRedstoneBridgeBlock(AbstractBlock.Settings.create()
                    .breakInstantly()
                    .sounds(BlockSoundGroup.STONE)
                    .pistonBehavior(PistonBehavior.DESTROY)));

    public static final SR_LATCH_Block SR_LATCH_BLOCK = registerWithItem("sr_latch_block",
            new SR_LATCH_Block(AbstractBlock.Settings.create()
                    .breakInstantly()
                    .sounds(BlockSoundGroup.STONE)
                    .pistonBehavior(PistonBehavior.DESTROY)
            )
    );

    public static final JK_LATCH_Block JK_LATCH_BLOCK = registerWithItem("jk_latch_block",
            new JK_LATCH_Block(AbstractBlock.Settings.create()
                    .breakInstantly()
                    .sounds(BlockSoundGroup.STONE)
                    .pistonBehavior(PistonBehavior.DESTROY)
            )
    );

    public static final T_LATCH_Block T_LATCH_BLOCK = registerWithItem("t_latch_block",
            new T_LATCH_Block(AbstractBlock.Settings.create()
                    .breakInstantly()
                    .sounds(BlockSoundGroup.STONE)
                    .pistonBehavior(PistonBehavior.DESTROY)
            )
    );


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
