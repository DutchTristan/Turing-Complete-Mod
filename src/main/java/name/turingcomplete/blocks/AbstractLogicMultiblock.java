package name.turingcomplete.blocks;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import name.turingcomplete.TuringComplete;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.tick.TickPriority;

public abstract class AbstractLogicMultiblock extends AbstractLogicBlock {

    protected AbstractLogicMultiblock(Settings settings) {
        super(settings);
    }
    
    //returns null if the main block cannot be found, which always implies the multiblock is invalid
    @Nullable
    public abstract BlockPos getMainPos(World world, BlockState partState, BlockPos partPos);

    //don't include the main position itself
    public abstract List<BlockPos> getPartPosses(World world, BlockPos mainPos, BlockState mainState);

    public abstract boolean isMultiblockValid(World world, BlockPos mainPos, BlockState mainState);

    public final void breakAll(World world, BlockPos mainPos, BlockState mainState, @Nullable PlayerEntity player){
        boolean shouldDrop = player == null || !player.isCreative();
        for (BlockPos partPos : getPartPosses(world, mainPos, mainState)){
            world.breakBlock(partPos, false);

            for (Direction direction : DIRECTIONS)
                world.updateNeighborsAlways(partPos.offset(direction), this);
        }
        world.breakBlock(mainPos, shouldDrop);

        for (Direction direction : DIRECTIONS)
            world.updateNeighborsAlways(mainPos.offset(direction), this);
    }

    protected int getLeastOutputDelay(BlockPos mainPos, BlockState mainState) {
        return 2;
    }

    //create and destroy blocks, set non-input part properties
    //mirror property handled by AbstractLogicMultiblock
    //returns new mainPos, since it can change
    protected BlockPos mirrorBlockParts(World world, BlockPos mainPos, BlockState mainState){
        return mainPos;
    }

    //to update strong-power neighbors of old inputs
    protected void afterMirror(World world, BlockPos mainPos, BlockState mainState){}

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!canMirror()) {
            return ActionResult.PASS;
        }

        BlockPos mainPos = getMainPos(world, state, pos);
        BlockState mainState = world.getBlockState(mainPos);

        if(!isMultiblockValid(world, mainPos, mainState)){
            TuringComplete.LOGGER.warn("invalid multiblock at "+mainPos+"in AbstractLogicMultiblock onUse");
            return ActionResult.PASS;
        }

        if(!canMirrorHere(mainPos, mainState)){
            return ActionResult.PASS;
        }

        boolean wasMirrored = mainState.get(MIRRORED);

        mainPos = mirrorBlockParts(world, mainPos, mainState);
        mainState = world.getBlockState(mainPos);

        
        //TODO: can we avoid some of these neighbor updates when updating state?
        for (BlockPos partPos : getPartPosses(world, mainPos, mainState)) {
            world.setBlockState(partPos, world.getBlockState(partPos).with(MIRRORED,!wasMirrored),Block.NOTIFY_ALL);
            world.updateNeighbor(partPos, null, mainPos);
        }
        mainState = mainState.with(MIRRORED, !wasMirrored);
        world.setBlockState(mainPos, mainState,Block.NOTIFY_ALL);
        if (!isMultiblockValid(world, mainPos, mainState)) {
            TuringComplete.LOGGER.warn("Invalid multiblock at "+mainPos+"in AbstractLogicMultiblock onUse after mirror");
        }
        else {
            //update inputs, because they have moved
            onInputChange(world,mainPos,mainState);
            for (BlockPos partPos : getPartPosses(world, mainPos, mainState)) {
                onInputChange(world,partPos,world.getBlockState(partPos));
            }
        }

        if(!wasMirrored) {
            world.playSound(player, pos, SoundEvents.BLOCK_COMPARATOR_CLICK, SoundCategory.BLOCKS, 0.3F, 0.5F);
        }
        else {
            world.playSound(player, pos, SoundEvents.BLOCK_COMPARATOR_CLICK, SoundCategory.BLOCKS, 0.3F, 0.55F);
        }
        afterMirror(world,mainPos,mainState);
        world.scheduleBlockTick(pos,this, getLeastOutputDelay(mainPos,mainState), TickPriority.VERY_HIGH);

        return ActionResult.SUCCESS_NO_ITEM_USED;
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (!state.canPlaceAt(world, pos)) {
            BlockPos mainPos = getMainPos(world, state, pos);
            //invalid multiblock
            if (mainPos == null) {
                world.removeBlock(pos, false);
            }
            else{
                breakAll(world, mainPos, world.getBlockState(mainPos), null);
            }            
        }
        else {
            onInputChange(world,pos,state);
        }
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player){
        BlockPos mainPos = getMainPos(world, state, pos);
        //skip multi-break if multiblock is invalid, to avoid deleting unrelated blocks
        if (mainPos != null) {
            BlockState mainState = world.getBlockState(mainPos);
            if (mainState.getBlock() instanceof AbstractLogicMultiblock) {
                if (isMultiblockValid(world, mainPos, mainState)) {
                    breakAll(world, mainPos, mainState, player);
                }
            }
        }

        return super.onBreak(world, pos, state, player);
    }
}
