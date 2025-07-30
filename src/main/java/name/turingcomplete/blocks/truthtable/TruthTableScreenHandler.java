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
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return ItemStack.EMPTY;
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
