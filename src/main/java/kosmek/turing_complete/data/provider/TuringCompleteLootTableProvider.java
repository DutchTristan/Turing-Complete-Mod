package kosmek.turing_complete.data.provider;

import kosmek.turing_complete.init.BlockInit;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class TuringCompleteLootTableProvider extends FabricBlockLootTableProvider {
    public TuringCompleteLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(dataOutput);
    }

    @Override
    public void generate() {
        addDrop(BlockInit.NAND_GATE, drops(BlockInit.NAND_GATE));
        addDrop(BlockInit.AND_GATE, drops(BlockInit.AND_GATE));
        addDrop(BlockInit.LOGIC_BASE_PLATE_BLOCK, drops(BlockInit.LOGIC_BASE_PLATE_BLOCK));
        addDrop(BlockInit.NOR_GATE, drops(BlockInit.NOR_GATE));
        addDrop(BlockInit.NOT_GATE, drops(BlockInit.NOT_GATE));
        addDrop(BlockInit.OR_GATE, drops(BlockInit.OR_GATE));
        addDrop(BlockInit.REDSTONE_BRIDGE_BLOCK, drops(BlockInit.REDSTONE_BRIDGE_BLOCK));
        addDrop(BlockInit.SWITCH_GATE, drops(BlockInit.SWITCH_GATE));
        addDrop(BlockInit.THREE_AND_GATE, drops(BlockInit.THREE_AND_GATE));
        addDrop(BlockInit.THREE_OR_GATE, drops(BlockInit.THREE_OR_GATE));
        addDrop(BlockInit.XNOR_GATE, drops(BlockInit.XNOR_GATE));
        addDrop(BlockInit.XOR_GATE, drops(BlockInit.XOR_GATE));
        addDrop(BlockInit.FULL_ADDER, drops(BlockInit.FULL_ADDER));
        addDrop(BlockInit.HALF_ADDER, drops(BlockInit.HALF_ADDER));
    }
}

