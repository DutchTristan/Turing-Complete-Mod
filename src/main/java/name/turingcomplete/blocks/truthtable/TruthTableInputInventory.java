package name.turingcomplete.blocks.truthtable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeMatcher;
import net.minecraft.util.collection.DefaultedList;

import java.util.List;

public class TruthTableInputInventory implements RecipeInputInventory {

    private final DefaultedList<ItemStack> stacks = DefaultedList.ofSize(5,ItemStack.EMPTY);
    @Override
    public int getWidth() {
        return 5;
    }

    @Override
    public int getHeight() {
        return 1;
    }

    @Override
    public List<ItemStack> getHeldStacks() {
        return stacks;
    }

    @Override
    public int size() {
        return 5;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : stacks){
            if (!stack.isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getStack(int slot) {
        return stacks.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack stack = getStack(slot);
        if (stack.isEmpty()) return ItemStack.EMPTY;

        ItemStack result;
        if (stack.getCount() <= amount){
            result = stack;
            stacks.set(slot, ItemStack.EMPTY);
        }
        else{
            result = stack.split(amount);
        }
        markDirty();
        return result;
    }

    @Override
    public ItemStack removeStack(int slot) {
        ItemStack result = stacks.get(slot);
        stacks.set(slot, ItemStack.EMPTY);
        markDirty();
        return result;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        stacks.set(slot, stack);
        markDirty();
    }

    @Override
    public void markDirty() {

    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void provideRecipeInputs(RecipeMatcher finder) {
        for (ItemStack stack : stacks){
            finder.addInput(stack);
        }
    }

    @Override
    public void clear() {
        stacks.clear();
    }
}
