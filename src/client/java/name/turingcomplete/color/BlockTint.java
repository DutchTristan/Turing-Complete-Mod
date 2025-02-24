package name.turingcomplete.color;

import name.turingcomplete.blocks.block.*;
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
        registerer.register((state, world, pos, tintIndex) -> NOT_Gate_Block.getBlockColor(state, tintIndex), blockInit.NOT_GATE);
        registerer.register((state, world, pos, tintIndex) -> AND_Gate_Block.getBlockColor(state, tintIndex), blockInit.AND_GATE);
        registerer.register((state, world, pos, tintIndex) -> NAND_Gate_Block.getBlockColor(state, tintIndex), blockInit.NAND_GATE);
        registerer.register((state, world, pos, tintIndex) -> THREE_AND_Gate_Block.getBlockColor(state, tintIndex), blockInit.THREE_AND_GATE);
        registerer.register((state, world, pos, tintIndex) -> OR_Gate_Block.getBlockColor(state, tintIndex), blockInit.OR_GATE);
        registerer.register((state, world, pos, tintIndex) -> NOR_Gate_Block.getBlockColor(state, tintIndex), blockInit.NOR_GATE);
        registerer.register((state, world, pos, tintIndex) -> THREE_OR_Gate_Block.getBlockColor(state, tintIndex), blockInit.THREE_OR_GATE);
        registerer.register((state, world, pos, tintIndex) -> XOR_Gate_Block.getBlockColor(state, tintIndex), blockInit.XOR_GATE);
        registerer.register((state, world, pos, tintIndex) -> XNOR_Gate_Block.getBlockColor(state, tintIndex), blockInit.XNOR_GATE);
    }
}
