package name.turingcomplete.client.color;

import name.turingcomplete.blocks.block.LogicBasePlateBlock;
import name.turingcomplete.blocks.block.OmniDirectionalRedstoneBridgeBlock;
import name.turingcomplete.init.BlockInit;
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
                OmniDirectionalRedstoneBridgeBlock.getWireColor(state,tintIndex),
                BlockInit.OMNI_DIRECTIONAL_REDSTONE_BRIDGE_BLOCK
        );
        registerer.register((state,world,pos,tintIndex)->LogicBasePlateBlock.getWireColor(state.getBlock(),state),
        BlockInit.LOGIC_BASE_PLATE_BLOCK);
    }
}
