package name.turingcomplete.blocks;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

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

    public final void breakAll(World world, BlockPos mainPos, BlockState mainState, PlayerEntity player){
        boolean shouldDrop = !player.isCreative();
        for (BlockPos partPos : getPartPosses(world, mainPos, mainState)){
            world.breakBlock(partPos, false);

            for (Direction direction : DIRECTIONS)
                world.updateNeighborsAlways(partPos.offset(direction), this);
        }
        world.breakBlock(mainPos, shouldDrop);

        for (Direction direction : DIRECTIONS)
            world.updateNeighborsAlways(mainPos.offset(direction), this);
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
        
        if (!canPlaceAt(state,world, pos)) {
            BlockPos mainPos = getMainPos(world, state, pos);
            //invalid multiblock
            if (mainPos == null) {
                world.removeBlock(pos, false);
            }
            else{
                breakAll(world, mainPos, world.getBlockState(mainPos), null);
            }            
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
