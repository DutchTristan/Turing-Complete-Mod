package name.turingcomplete.mixin;

import name.turingcomplete.AbstractEnableGate;
import name.turingcomplete.AbstractLogicGate;
import net.minecraft.block.RedstoneWireBlock;
import org.spongepowered.asm.mixin.Mixin;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.Direction;

@Mixin(RedstoneWireBlock.class)
public class RedstoneWireBlockMixin {
    // ==================================================
    @Inject(method = "connectsTo(Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/Direction;)Z",
            at = @At("HEAD"), cancellable = true, remap = true)
    private static void connectsTo_tcd_mixin(BlockState state, Direction dir, CallbackInfoReturnable<Boolean> e)
    {
        //check for block type
        if (!(state.getBlock() instanceof AbstractLogicGate) && !(state.getBlock() instanceof AbstractEnableGate))
            return;

        //check for null direction
        if(dir == null)
        {
            e.setReturnValue(false);
            e.cancel();
            return;
        }
        // use dustConnectsToThis(state, dir) to determine whether redstone dust should or should not
        // connect to that side of the Logic Gate.
        if(state.getBlock() instanceof AbstractLogicGate)
        {
            AbstractLogicGate algb = (AbstractLogicGate) state.getBlock();
            e.setReturnValue(algb.dustConnectsToThis(state, dir));
            e.cancel();
            return;
        }
        if(state.getBlock() instanceof AbstractEnableGate)
        {
            AbstractEnableGate aegb = (AbstractEnableGate) state.getBlock();
            e.setReturnValue(aegb.dustConnectsToThis(state, dir));
            e.cancel();
            return;
        }
    }
}
