package name.turingcomplete.init;

import name.turingcomplete.TuringComplete;
import name.turingcomplete.blocks.TruthTable;
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
    public static final NANDGateBlock NAND_GATE = registerWithItem("nand_gate_block",
            new NANDGateBlock(AbstractBlock.Settings.create()
                    .breakInstantly()
                    .sounds(BlockSoundGroup.STONE)
                    .pistonBehavior(PistonBehavior.DESTROY)));


    public static final NOTGateBlock NOT_GATE = registerWithItem("not_gate_block",
            new NOTGateBlock(AbstractBlock.Settings.create()
                    .breakInstantly()
                    .sounds(BlockSoundGroup.STONE)
                    .pistonBehavior(PistonBehavior.DESTROY)));

    public static final ANDGateBlock AND_GATE = registerWithItem("and_gate_block",
            new ANDGateBlock(AbstractBlock.Settings.create()
                    .breakInstantly()
                    .sounds(BlockSoundGroup.STONE)
                    .pistonBehavior(PistonBehavior.DESTROY)));

    public static final LogicBasePlateBlock LOGIC_BASE_PLATE_BLOCK = registerWithItem("logic_base_plate_block",
            new LogicBasePlateBlock(Block.Settings.create()
                    .breakInstantly()
                    .sounds(BlockSoundGroup.STONE)
                    .pistonBehavior(PistonBehavior.DESTROY)
            ));

    public static final ORGateBlock OR_GATE = registerWithItem("or_gate_block",
            new ORGateBlock(AbstractBlock.Settings.create()
                    .breakInstantly()
                    .sounds(BlockSoundGroup.STONE)
                    .pistonBehavior(PistonBehavior.DESTROY)));

    public static final NORGateBlock NOR_GATE = registerWithItem("nor_gate_block",
            new NORGateBlock(AbstractBlock.Settings.create()
                    .breakInstantly()
                    .sounds(BlockSoundGroup.STONE)
                    .pistonBehavior(PistonBehavior.DESTROY)));

    public static final XNORGateBlock XNOR_GATE = registerWithItem("xnor_gate_block",
            new XNORGateBlock(AbstractBlock.Settings.create()
                    .breakInstantly()
                    .sounds(BlockSoundGroup.STONE)
                    .pistonBehavior(PistonBehavior.DESTROY)));

    public static final XORGateBlock XOR_GATE = registerWithItem("xor_gate_block",
            new XORGateBlock(AbstractBlock.Settings.create()
                    .breakInstantly()
                    .sounds(BlockSoundGroup.STONE)
                    .pistonBehavior(PistonBehavior.DESTROY)));

    //===============================================================================================

    public static final ThreeANDGateBlock THREE_AND_GATE = registerWithItem("3and_gate_block",
            new ThreeANDGateBlock(AbstractBlock.Settings.create()
                    .breakInstantly()
                    .sounds(BlockSoundGroup.STONE)
                    .pistonBehavior(PistonBehavior.DESTROY)));

    public static final ThreeORGateBlock THREE_OR_GATE = registerWithItem("3or_gate_block",
            new ThreeORGateBlock(AbstractBlock.Settings.create()
                    .breakInstantly()
                    .sounds(BlockSoundGroup.STONE)
                    .pistonBehavior(PistonBehavior.DESTROY)));

    //===============================================================================================

    public static final SwitchGateBlock SWITCH_GATE = registerWithItem("switch_gate_block",
            new SwitchGateBlock(AbstractBlock.Settings.create()
                    .breakInstantly()
                    .sounds(BlockSoundGroup.STONE)
                    .pistonBehavior(PistonBehavior.DESTROY)));

    public static final MemoryCellBlock MEMORY_CELL = registerWithItem("memory_cell_gate",
            new MemoryCellBlock(AbstractBlock.Settings.create()
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

    public static final SRLatchBlock SR_LATCH_BLOCK = registerWithItem("sr_latch_block",
            new SRLatchBlock(AbstractBlock.Settings.create()
                    .breakInstantly()
                    .sounds(BlockSoundGroup.STONE)
                    .pistonBehavior(PistonBehavior.DESTROY)
            )
    );

    public static final JKLatchBlock JK_LATCH_BLOCK = registerWithItem("jk_latch_block",
            new JKLatchBlock(AbstractBlock.Settings.create()
                    .breakInstantly()
                    .sounds(BlockSoundGroup.STONE)
                    .pistonBehavior(PistonBehavior.DESTROY)
            )
    );

    public static final TLatchBlock T_LATCH_BLOCK = registerWithItem("t_latch_block",
            new TLatchBlock(AbstractBlock.Settings.create()
                    .breakInstantly()
                    .sounds(BlockSoundGroup.STONE)
                    .pistonBehavior(PistonBehavior.DESTROY)
            )
    );


    public static final TruthTable TRUTH_TABLE = registerWithItem("truth_table_block",
            new TruthTable(Block.Settings.create()
                    .breakInstantly()
                    .sounds(BlockSoundGroup.STONE)
                    .pistonBehavior(PistonBehavior.DESTROY)
            ));


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
