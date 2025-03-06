package name.turingcomplete.blocks;

import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.tick.TickPriority;

//a logic block which is not a multiblock, and does not have multiple different delay times
public abstract class AbstractSimpleLogicBlock extends AbstractLogicBlock{

    protected AbstractSimpleLogicBlock(Settings settings) {
        super(settings);
    }

    //position and state are not relevant
    @Override
    public boolean canMirrorHere(BlockPos mainPos, BlockState mainState){
        return canMirror();
    }

    //allow immediate block state changes in response to input, such as highlighting active inputs
    protected void onNeighborUpdate(World world, BlockPos gatePos, BlockState gateState){}

    //allow for block state changes after output is calculated, such as highlighting output if active
    //redstone output is handled by AbstractSimpleGate
    protected void onOutputChange(World world, BlockPos gatePos, BlockState gateState){}

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!canMirror()) {
            return ActionResult.PASS;
        }

        state = state.with(MIRRORED, !state.get(MIRRORED));
        world.setBlockState(pos, state);
        if(state.get(MIRRORED)) {
            world.playSound(player, pos, SoundEvents.BLOCK_COMPARATOR_CLICK, SoundCategory.BLOCKS, 0.3F, 0.5F);
        }
        else {
            world.playSound(player, pos, SoundEvents.BLOCK_COMPARATOR_CLICK, SoundCategory.BLOCKS, 0.3F, 0.55F);
        }
        world.scheduleBlockTick(pos,this, getOutputDelay(state), TickPriority.VERY_HIGH);
        //update input state, becuase inputs have moved
        onNeighborUpdate(world,pos,state);

        return ActionResult.SUCCESS_NO_ITEM_USED;
    }

    protected int getOutputDelay(BlockState gateState) {
        return 2;
    }

    @Override
    @MustBeInvokedByOverriders
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
        onNeighborUpdate(world,pos,state);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        //redstone dust hasn't been redirected yet, so must schedule unconditionally
        world.scheduleBlockTick(pos,this, getOutputDelay(state), TickPriority.VERY_HIGH);
    }

}
