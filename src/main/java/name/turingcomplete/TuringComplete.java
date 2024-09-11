package name.turingcomplete;


import name.turingcomplete.init.blockEntityTypeInit;
import name.turingcomplete.init.blockInit;
import name.turingcomplete.init.itemInit;
import net.fabricmc.api.ModInitializer;

import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TuringComplete implements ModInitializer {
	public static final String MOD_ID = "turingcomplete";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Loading...");
		itemInit.load();
		LOGGER.info("Items initialized...");
		blockInit.load();
		LOGGER.info("Blocks initialized...");
		blockEntityTypeInit.load();
		LOGGER.info("Block Entities initialized...");

	}

	public static Identifier id(String path){
		return Identifier.of(MOD_ID, path);
	}
}