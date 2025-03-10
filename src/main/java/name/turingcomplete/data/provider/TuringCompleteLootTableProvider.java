package name.turingcomplete.data.provider;

import name.turingcomplete.blocks.multiblock.Adder;
import name.turingcomplete.init.blockInit;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Block;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.state.property.Property;
import net.minecraft.util.StringIdentifiable;

import java.util.concurrent.CompletableFuture;

public class TuringCompleteLootTableProvider extends FabricBlockLootTableProvider {
    public TuringCompleteLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(dataOutput,completableFuture);
    }

    @Override
    public void generate() {
        addDrop(blockInit.LOGIC_BASE_PLATE_BLOCK, drops(blockInit.LOGIC_BASE_PLATE_BLOCK));

        addDrop(blockInit.NAND_GATE, drops(blockInit.NAND_GATE));
        addDrop(blockInit.AND_GATE, drops(blockInit.AND_GATE));
        addDrop(blockInit.NOR_GATE, drops(blockInit.NOR_GATE));
        addDrop(blockInit.NOT_GATE, drops(blockInit.NOT_GATE));
        addDrop(blockInit.OR_GATE, drops(blockInit.OR_GATE));
        addDrop(blockInit.THREE_AND_GATE, drops(blockInit.THREE_AND_GATE));
        addDrop(blockInit.THREE_OR_GATE, drops(blockInit.THREE_OR_GATE));
        addDrop(blockInit.XNOR_GATE, drops(blockInit.XNOR_GATE));
        addDrop(blockInit.XOR_GATE, drops(blockInit.XOR_GATE));

        addDrop(blockInit.BI_DIRECTIONAL_REDSTONE_BRIDGE_BLOCK, drops(blockInit.BI_DIRECTIONAL_REDSTONE_BRIDGE_BLOCK));
        addDrop(blockInit.OMNI_DIRECTIONAL_REDSTONE_BRIDGE_BLOCK, drops(blockInit.OMNI_DIRECTIONAL_REDSTONE_BRIDGE_BLOCK));

        addDrop(blockInit.SWITCH_GATE, drops(blockInit.SWITCH_GATE));
        addDrop(blockInit.SR_LATCH_BLOCK, drops(blockInit.SR_LATCH_BLOCK));
        addDrop(blockInit.JK_LATCH_BLOCK, drops(blockInit.JK_LATCH_BLOCK));
        addDrop(blockInit.T_LATCH_BLOCK, drops(blockInit.T_LATCH_BLOCK));
        addDrop(blockInit.MEMORY_CELL, drops(blockInit.MEMORY_CELL));

        addDrop(blockInit.HALF_ADDER, drop_if_property(blockInit.HALF_ADDER,Adder.PART,Adder.ADDER_PART.MIDDLE));
        addDrop(blockInit.FULL_ADDER, drop_if_property(blockInit.FULL_ADDER,Adder.PART,Adder.ADDER_PART.MIDDLE));
    }

    private <T extends Comparable<T> & StringIdentifiable> LootTable.Builder drop_if_property(Block block, Property<T> property, T value ){
         LootCondition.Builder condition = new BlockStatePropertyLootCondition.Builder(block)
                .properties(StatePredicate.Builder.create().exactMatch(property,value));
        LootPoolEntry.Builder<?> entry = ItemEntry.builder(block);

        return LootTable.builder().pool(
                addSurvivesExplosionCondition(block,LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0F)))
                        .conditionally(condition)
                        .with(entry)
        );
    }
}

