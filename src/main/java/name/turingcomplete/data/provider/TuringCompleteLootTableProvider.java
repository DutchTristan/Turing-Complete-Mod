package name.turingcomplete.data.provider;

import name.turingcomplete.init.blockInit;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class TuringCompleteLootTableProvider extends FabricBlockLootTableProvider {
    public TuringCompleteLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(dataOutput,completableFuture);
    }

    @Override
    public void generate() {
        addDrop(blockInit.NAND_GATE, drops(blockInit.NAND_GATE));
        addDrop(blockInit.AND_GATE, drops(blockInit.AND_GATE));
        addDrop(blockInit.LOGIC_BASE_PLATE_BLOCK, drops(blockInit.LOGIC_BASE_PLATE_BLOCK));
        addDrop(blockInit.NOR_GATE, drops(blockInit.NOR_GATE));
        addDrop(blockInit.NOT_GATE, drops(blockInit.NOT_GATE));
        addDrop(blockInit.OR_GATE, drops(blockInit.OR_GATE));
        addDrop(blockInit.REDSTONE_BRIDGE_BLOCK, drops(blockInit.REDSTONE_BRIDGE_BLOCK));
        addDrop(blockInit.SWITCH_GATE, drops(blockInit.SWITCH_GATE));
        addDrop(blockInit.THREE_AND_GATE, drops(blockInit.THREE_AND_GATE));
        addDrop(blockInit.THREE_OR_GATE, drops(blockInit.THREE_OR_GATE));
        addDrop(blockInit.XNOR_GATE, drops(blockInit.XNOR_GATE));
        addDrop(blockInit.XOR_GATE, drops(blockInit.XOR_GATE));
        addDrop(blockInit.FULL_ADDER, drops(blockInit.FULL_ADDER));
        addDrop(blockInit.HALF_ADDER, drops(blockInit.HALF_ADDER));
        addDrop(blockInit.SR_LATCH_BLOCK, drops(blockInit.SR_LATCH_BLOCK));
    }
}

