package name.turingcomplete.blocks.truthtable;

import name.turingcomplete.init.blockEntityInit;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class TruthTableBlockEntity extends BlockEntity implements NamedScreenHandlerFactory {
    public TruthTableBlockEntity(BlockPos pos, BlockState state) {
        super(blockEntityInit.TRUTH_TABLE_BLOCK_ENTITY, pos, state);
    }

    @Override
    public Text getDisplayName() {
        return Text.literal("Truth Table");
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new TruthTableScreenHandler(syncId, playerInventory);
    }
}
