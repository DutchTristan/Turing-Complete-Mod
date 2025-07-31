package name.turingcomplete;

import name.turingcomplete.init.screenHandlerInit;
import name.turingcomplete.client.screen.TruthTableScreen;
import name.turingcomplete.color.BlockTint;
import name.turingcomplete.init.blockInit;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;

public class TuringCompleteClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// To make some parts of the block transparent (like glass, saplings and doors):
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(),
				blockInit.NOT_GATE,blockInit.NAND_GATE,
				blockInit.NOR_GATE,blockInit.XNOR_GATE
		);

		BlockRenderLayerMap.INSTANCE.putBlock(blockInit.OMNI_DIRECTIONAL_REDSTONE_BRIDGE_BLOCK,RenderLayer.getTranslucent());

		BlockTint.create();

		HandledScreens.register(screenHandlerInit.TRUTH_TABLE, TruthTableScreen::new);
	}
}