package name.turingcomplete.client;

import name.turingcomplete.init.ScreenHandlerInit;
import name.turingcomplete.client.screen.TruthTableScreen;
import name.turingcomplete.client.color.BlockTint;
import name.turingcomplete.init.BlockInit;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;

public class TuringCompleteClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// To make some parts of the block transparent (like glass, saplings and doors):
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(),
			BlockInit.NOT_GATE,
			BlockInit.NAND_GATE,
			BlockInit.NOR_GATE,
			BlockInit.XNOR_GATE,
			BlockInit.PULSE_EXTENDER_BLOCK,
			BlockInit.LOGIC_BASE_PLATE_BLOCK
		);

		BlockRenderLayerMap.INSTANCE.putBlock(BlockInit.OMNI_DIRECTIONAL_REDSTONE_BRIDGE_BLOCK,RenderLayer.getTranslucent());

		BlockTint.create();

		HandledScreens.register(ScreenHandlerInit.TRUTH_TABLE, TruthTableScreen::new);
	}
}