package name.turingcomplete;

import name.turingcomplete.data.provider.TuringCompleteRecipeProvider;
import name.turingcomplete.data.provider.TuringCompleteLootTableProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryKeys;

import static net.minecraft.data.server.loottable.BlockLootTableGenerator.drops;

public class TuringCompleteDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

		pack.addProvider(TuringCompleteRecipeProvider::new);
		pack.addProvider(TuringCompleteLootTableProvider::new);
	}

	@Override
	public void buildRegistry (RegistryBuilder registryBuilder){

	}
}
