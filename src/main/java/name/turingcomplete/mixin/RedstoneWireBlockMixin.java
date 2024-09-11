package name.turingcomplete.mixin;

import name.turingcomplete.block.NOT_Gate_Block;
import name.turingcomplete.block.NAND_Gate_Block;
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
        if(!(state.getBlock() instanceof NOT_Gate_Block) && !(state.getBlock() instanceof NAND_Gate_Block))
            return;

        //check for null direction
        if(dir == null)
        {
            e.setReturnValue(false);
            e.cancel();
            return;
        }

        if(state.getBlock() instanceof NOT_Gate_Block)
        {
            NOT_Gate_Block algb = (NOT_Gate_Block)state.getBlock();
            e.setReturnValue(algb.dustConnectsToThis(state, dir));
            e.cancel();
            return;
        }
        if(state.getBlock() instanceof NAND_Gate_Block)
        {
            NAND_Gate_Block algb = (NAND_Gate_Block)state.getBlock();
            e.setReturnValue(algb.dustConnectsToThis(state, dir));
            e.cancel();
            return;
        }
    }
}
