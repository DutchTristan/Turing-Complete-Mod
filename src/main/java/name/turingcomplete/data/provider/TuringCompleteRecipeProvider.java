package name.turingcomplete.data.provider;

import name.turingcomplete.init.blockInit;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class TuringCompleteRecipeProvider extends FabricRecipeProvider {
    public TuringCompleteRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, blockInit.LOGIC_BASE_PLATE_BLOCK, 12)
                .input('E', Blocks.SMOOTH_STONE_SLAB)
                .pattern("   ")
                .pattern("   ")
                .pattern("EEE")
                .criterion(hasItem(Blocks.SMOOTH_STONE_SLAB),conditionsFromItem(Blocks.SMOOTH_STONE_SLAB))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, blockInit.NAND_GATE, 3)
                .input('E', blockInit.LOGIC_BASE_PLATE_BLOCK)
                .input('T', Blocks.REDSTONE_TORCH)
                .input('R', Blocks.REDSTONE_WIRE)
                .pattern(" R ")
                .pattern("TRT")
                .pattern("EEE")
                .criterion(hasItem(blockInit.LOGIC_BASE_PLATE_BLOCK),conditionsFromItem(blockInit.LOGIC_BASE_PLATE_BLOCK))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, blockInit.NOT_GATE, 2)
                .input('N', blockInit.NAND_GATE)
                .input('R', Blocks.REDSTONE_WIRE)
                .pattern("RR ")
                .pattern("RN ")
                .pattern("RR ")
                .criterion(hasItem(blockInit.NAND_GATE),conditionsFromItem(blockInit.NAND_GATE))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, blockInit.AND_GATE, 1)
                .input('N', blockInit.NAND_GATE)
                .input('R', Blocks.REDSTONE_WIRE)
                .input('G', blockInit.NOT_GATE)
                .pattern(" R ")
                .pattern(" NG")
                .pattern(" R ")
                .criterion(hasItem(blockInit.NAND_GATE),conditionsFromItem(blockInit.NAND_GATE))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, blockInit.OR_GATE, 1)
                .input('N', blockInit.NAND_GATE)
                .input('R', Blocks.REDSTONE_WIRE)
                .input('G', blockInit.NOT_GATE)
                .pattern("GR ")
                .pattern(" N ")
                .pattern("GR ")
                .criterion(hasItem(blockInit.NAND_GATE),conditionsFromItem(blockInit.NAND_GATE))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, blockInit.NOR_GATE, 1)
                .input('N', blockInit.NAND_GATE)
                .input('R', Blocks.REDSTONE_WIRE)
                .input('G', blockInit.NOT_GATE)
                .pattern("GR ")
                .pattern(" NG")
                .pattern("GR ")
                .criterion(hasItem(blockInit.NAND_GATE),conditionsFromItem(blockInit.NAND_GATE))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, blockInit.XOR_GATE, 1)
                .input('N', blockInit.NAND_GATE)
                .input('R', Blocks.REDSTONE_WIRE)
                .pattern(" N ")
                .pattern("NRN")
                .pattern(" N ")
                .criterion(hasItem(blockInit.NAND_GATE),conditionsFromItem(blockInit.NAND_GATE))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, blockInit.XNOR_GATE, 1)
                .input('X', blockInit.XOR_GATE)
                .input('N', blockInit.NOT_GATE)
                .pattern("   ")
                .pattern(" XN")
                .pattern("   ")
                .criterion(hasItem(blockInit.XOR_GATE),conditionsFromItem(blockInit.XOR_GATE))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, blockInit.THREE_AND_GATE, 1)
                .input('A', blockInit.AND_GATE)
                .input('R', Blocks.REDSTONE_WIRE)
                .pattern("RRA")
                .pattern(" AR")
                .pattern("   ")
                .criterion(hasItem(blockInit.AND_GATE),conditionsFromItem(blockInit.AND_GATE))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, blockInit.THREE_OR_GATE, 1)
                .input('O', blockInit.OR_GATE)
                .input('R', Blocks.REDSTONE_WIRE)
                .pattern("RRO")
                .pattern(" OR")
                .pattern("   ")
                .criterion(hasItem(blockInit.OR_GATE),conditionsFromItem(blockInit.OR_GATE))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, blockInit.SWITCH_GATE, 1)
                .input('C', Blocks.COMPARATOR)
                .input('R', Blocks.REDSTONE_WIRE)
                .input('T', Blocks.REDSTONE_TORCH)
                .pattern("TR ")
                .pattern("RCR")
                .pattern("   ")
                .criterion(hasItem(Blocks.COMPARATOR),conditionsFromItem(Blocks.COMPARATOR))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, blockInit.MEMORY_CELL, 1)
                .input('R', Blocks.REPEATER)
                .input('W', Blocks.REDSTONE_WIRE)
                .pattern(" W ")
                .pattern(" R ")
                .pattern("WRW")
                .criterion(hasItem(Blocks.REPEATER),conditionsFromItem(Blocks.REPEATER))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, blockInit.HALF_ADDER, 1)
                .input('X', blockInit.XOR_GATE)
                .input('A', blockInit.AND_GATE)
                .input('W', Blocks.REDSTONE_WIRE)
                .pattern("WW ")
                .pattern(" WX")
                .pattern("WA ")
                .criterion(hasItem(blockInit.XOR_GATE),conditionsFromItem(blockInit.XOR_GATE))
                .criterion(hasItem(blockInit.AND_GATE),conditionsFromItem(blockInit.AND_GATE))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, blockInit.FULL_ADDER, 1)
                .input('H', blockInit.HALF_ADDER)
                .input('O', blockInit.OR_GATE)
                .input('W', Blocks.REDSTONE_WIRE)
                .pattern("HW ")
                .pattern(" HW")
                .pattern(" WO")
                .criterion(hasItem(blockInit.HALF_ADDER),conditionsFromItem(blockInit.HALF_ADDER))
                .criterion(hasItem(blockInit.OR_GATE),conditionsFromItem(blockInit.OR_GATE))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, blockInit.BI_DIRECTIONAL_REDSTONE_BRIDGE_BLOCK, 1)
                .input('R', Blocks.REPEATER)
                .input('W', Blocks.REDSTONE_WIRE)
                .pattern(" W ")
                .pattern("WRW")
                .pattern(" W ")
                .criterion(hasItem(Blocks.REPEATER),conditionsFromItem(Blocks.REPEATER))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, blockInit.OMNI_DIRECTIONAL_REDSTONE_BRIDGE_BLOCK, 1)
                .input('R', blockInit.LOGIC_BASE_PLATE_BLOCK)
                .input('W', Blocks.REDSTONE_WIRE)
                .pattern(" W ")
                .pattern("WRW")
                .pattern(" W ")
                .criterion(hasItem(blockInit.OMNI_DIRECTIONAL_REDSTONE_BRIDGE_BLOCK),conditionsFromItem(blockInit.OMNI_DIRECTIONAL_REDSTONE_BRIDGE_BLOCK))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, blockInit.SR_LATCH_BLOCK, 1)
                .input('R', Blocks.REDSTONE_WIRE)
                .input('N', blockInit.NOR_GATE)
                .pattern("RNR")
                .pattern(" R ")
                .pattern("RNR")
                .criterion(hasItem(blockInit.NOR_GATE),conditionsFromItem(blockInit.NOR_GATE))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, blockInit.JK_LATCH_BLOCK, 1)
                .input('S', blockInit.SR_LATCH_BLOCK)
                .input('W', Blocks.REDSTONE_WIRE)
                .input('A', blockInit.AND_GATE)
                .pattern("AW ")
                .pattern(" S ")
                .pattern("AW ")
                .criterion(hasItem(blockInit.SR_LATCH_BLOCK),conditionsFromItem(blockInit.SR_LATCH_BLOCK))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, blockInit.T_LATCH_BLOCK, 1)
                .input('W', Blocks.REDSTONE_WIRE)
                .input('J', blockInit.JK_LATCH_BLOCK)
                .pattern("WW ")
                .pattern("WJ ")
                .pattern("WW ")
                .criterion(hasItem(blockInit.JK_LATCH_BLOCK),conditionsFromItem(blockInit.JK_LATCH_BLOCK))
                .offerTo(exporter);
    }

}
