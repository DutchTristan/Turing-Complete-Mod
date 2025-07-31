package name.turingcomplete.blocks.truthtable;

import name.turingcomplete.init.screenHandlerInit;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.CraftingResultSlot;

public class TruthTableScreenHandler extends ScreenHandler {
    private final RecipeInputInventory input;
    private final Inventory result;

    public static ScreenHandlerType<TruthTableScreenHandler> TYPE;

    public TruthTableScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new TruthTableInputInventory(), new SimpleInventory(1));
    }

    public TruthTableScreenHandler(int syncId, PlayerInventory playerInventory, RecipeInputInventory input, Inventory result) {
        super(screenHandlerInit.TRUTH_TABLE, syncId);
        this.input = input;
        this.result = result;

        for(int i = 0; i < 5; i++){
            this.addSlot(new Slot(input,i,20 + i * 18,20));
        }

        this.addSlot(new CraftingResultSlot(playerInventory.player, input, result, 0, 124, 35));

        for (int row = 0; row < 3; ++row){
            for (int col = 0; col < 9; ++col){
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        for (int col = 0; col < 9; ++col){
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);

        // Only do this on the server side
        if (!player.getWorld().isClient) {
            // Loop through your input slots
            for (int i = 0; i < input.size(); i++) {
                ItemStack stack = input.removeStack(i);
                if (!stack.isEmpty()) {
                    // Drop it at the player's feet
                    player.dropItem(stack, false);
                }
            }
        }
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        ItemStack moved = ItemStack.EMPTY;
        Slot selectedSlot = this.slots.get(slot);
        if (!selectedSlot.hasStack()) return moved;

        ItemStack original = selectedSlot.getStack();
        moved = original.copy();

        final int INPUT_START = 0;
        final int INPUT_END   = 5;   // exclusive: slots 0–4 are inputs
        final int RESULT_SLOT = 5;   // slot 5 is the crafting result
        final int PLAYER_START= 6;   // slot 6 is first player-inventory slot
        final int PLAYER_END  = PLAYER_START + 27 + 9; // 27 inv + 9 hotbar = 42 total

        if (slot == RESULT_SLOT) {
            // shift-clicking the result: move into player inventory
            if (!insertItem(original, PLAYER_START, PLAYER_END, true)) {
                return ItemStack.EMPTY;
            }
            selectedSlot.onQuickTransfer(original, moved);
        }
        else if (slot >= PLAYER_START) {
            // shift-clicking from the player inv/hotbar: try to put into the 5 input slots
            if (!insertItem(original, INPUT_START, INPUT_END, false)) {
                return ItemStack.EMPTY;
            }
        }
        else {
            // shift-clicking from one of the 5 inputs: move back to player inv
            if (!insertItem(original, PLAYER_START, PLAYER_END, false)) {
                return ItemStack.EMPTY;
            }
        }

        // cleanup empty slots
        if (original.isEmpty()) {
            selectedSlot.setStack(ItemStack.EMPTY);
        } else {
            selectedSlot.markDirty();
        }

        return moved;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    public Inventory getInput(){
        return input;
    }

    public Inventory getResult(){
        return result;
    }
}
