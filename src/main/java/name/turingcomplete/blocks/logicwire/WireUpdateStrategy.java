package name.turingcomplete.blocks.logicwire;

import java.util.Optional;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class WireUpdateStrategy {
    public abstract void onNeighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify);

    //TODO: use a registry so blocks from other mods can integrate
    protected static Optional<LogicWireAdapter<? extends Block>> getAdapter(Block block){
        if(block instanceof AbstractLogicWire){
            return Optional.of(LogicWireAdapter.LOGIC_WIRE);
        }
        else if(block == Blocks.REDSTONE_WIRE){
            return Optional.of(LogicWireAdapter.REDSTONE);
        }
        return Optional.empty();
    }

    protected static boolean isWireBlock(Block block){
        return block instanceof AbstractLogicWire || block.equals(Blocks.REDSTONE_WIRE);
    }
}
