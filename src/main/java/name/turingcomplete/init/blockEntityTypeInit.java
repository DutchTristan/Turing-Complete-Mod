package name.turingcomplete.init;

import name.turingcomplete.TuringComplete;
import name.turingcomplete.block.entity.ExampleBE;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class blockEntityTypeInit {
    public static final BlockEntityType<ExampleBE> NOT_GATE = register("not_gate_block",
            BlockEntityType.Builder.create(ExampleBE::new, blockInit.NOT_GATE).build());

    public static final BlockEntityType<ExampleBE> NAND_GATE = register("nand_gate_block",
            BlockEntityType.Builder.create(ExampleBE::new, blockInit.NAND_GATE).build());

    public static <T extends BlockEntity> BlockEntityType<T> register(String name, BlockEntityType<T> type){
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, TuringComplete.id(name), type);
    }

    public static void load(){}
}
