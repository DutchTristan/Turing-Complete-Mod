package name.turingcomplete;

import name.turingcomplete.color.BlockTint;
import name.turingcomplete.init.blockInit;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;

public class TuringCompleteClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// To make some parts of the block transparent (like glass, saplings and doors):
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(),
				blockInit.NOT_GATE,
				blockInit.AND_GATE,blockInit.NAND_GATE,blockInit.THREE_AND_GATE,
				blockInit.OR_GATE,blockInit.NOR_GATE,blockInit.THREE_OR_GATE,
				blockInit.XOR_GATE,blockInit.XNOR_GATE
		);

		BlockRenderLayerMap.INSTANCE.putBlock(blockInit.OMNI_DIRECTIONAL_REDSTONE_BRIDGE_BLOCK,RenderLayer.getTranslucent());

		BlockTint.create();
	}
}