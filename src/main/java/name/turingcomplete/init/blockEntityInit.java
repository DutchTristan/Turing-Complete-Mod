package name.turingcomplete.init;

import name.turingcomplete.TuringComplete;
import name.turingcomplete.blocks.truthtable.TruthTableBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;


public class blockEntityInit {
    public static BlockEntityType<TruthTableBlockEntity> TRUTH_TABLE_BLOCK_ENTITY;

    public static void register() {
        TRUTH_TABLE_BLOCK_ENTITY = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                Identifier.of(TuringComplete.MOD_ID, "truth_table"),
                BlockEntityType.Builder.create(TruthTableBlockEntity::new, blockInit.TRUTH_TABLE).build()
        );
    }

    public static void load(){}
}