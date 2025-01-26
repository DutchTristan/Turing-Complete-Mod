package kosmek.turing_complete;

import kosmek.turing_complete.data.provider.TuringCompleteLootTableProvider;
import kosmek.turing_complete.data.provider.TuringCompleteRecipeProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.registry.RegistryBuilder;

public class TuringCompleteUnofficialDataGenerator implements DataGeneratorEntrypoint {
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
