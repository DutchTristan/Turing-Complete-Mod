package name.turingcomplete.color;

import name.turingcomplete.blocks.block.OmniDirectionalRedstoneBridgeBlock;
import name.turingcomplete.init.blockInit;
import name.turingcomplete.init.propertyInit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.block.*;
import net.minecraft.client.color.block.BlockColorProvider;

@Environment(EnvType.CLIENT)
public class BlockTint {
    private static final int NO_COLOR = -1;

    public BlockTint() {}

    public static void create() {
        ColorProviderRegistry<Block, BlockColorProvider> registerer = ColorProviderRegistry.BLOCK;
        registerer.register((state, world, pos, tintIndex) ->
                OmniDirectionalRedstoneBridgeBlock.getWireColor(state,tintIndex == 0 ? propertyInit.POWER_X : propertyInit.POWER_Z),
                blockInit.OMNI_DIRECTIONAL_REDSTONE_BRIDGE_BLOCK
        );
    }
}
