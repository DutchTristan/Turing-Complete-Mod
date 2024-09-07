package name.turingcomplete.block.entity;

import name.turingcomplete.init.blockEntityTypeInit;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

public class ExampleBE extends BlockEntity {


    public ExampleBE(BlockPos pos, BlockState state) {
        super(blockEntityTypeInit.NOT_GATE, pos, state);
    }


}
