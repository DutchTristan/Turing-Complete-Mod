package name.turingcomplete.blocks.logicwire;
import java.util.Optional;

import com.mojang.logging.LogUtils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class VanillaWireUpdateStrategy extends WireUpdateStrategy{
    public void onNeighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        LogUtils.getLogger().warn("wire updated at "+pos+" from "+sourcePos);
        Block block = state.getBlock();
        Optional<LogicWireAdapter<? extends Block>> maybeAdapter = getAdapter(block);
        if(maybeAdapter.isEmpty()){
            LogUtils.getLogger().error("attempt to use VanillaWireUpdateStrategy on non-wire block "+block+" (at "+pos+")");
            return;
        }
        LogicWireAdapter<? extends Block> adapter = maybeAdapter.get();

        for(int signalIndex = 0; signalIndex < adapter.getSignalCount(state); signalIndex++){
            int oldStrength = state.get(adapter.getSignalProperty(world,state,signalIndex));
            int newStrength = 0;
            for(BlockPos connectedPos: adapter.getConnectedBlocks(world, pos, state, signalIndex)){
                BlockState connectedState = world.getBlockState(connectedPos);
                Block connectedBlock = connectedState.getBlock();
                if(isWireBlock(connectedState.getBlock())){
                    LogicWireAdapter<? extends Block> connectedAdapter = getAdapter(connectedBlock).orElseThrow();
                    newStrength = Math.max(newStrength,connectedAdapter.getConnectedSignalStrength(world, connectedPos, connectedState, pos).orElse(0)-1);
                }
                else {
                    //we don't know the direction, and non-wire only connects normally anyway
                    for(Direction toConnectedDirection: Direction.Type.HORIZONTAL){
                        if(pos.offset(toConnectedDirection).equals(connectedPos)){
                            int connectedStrength = world.getEmittedRedstonePower(connectedPos, toConnectedDirection);
                            if(connectedStrength > newStrength){
                                newStrength = connectedStrength;
                            }
                            break;
                        }
                    }
                }
            }
            if(oldStrength != newStrength) {
                LogUtils.getLogger().warn("signal "+signalIndex+" changed from "+oldStrength+" to "+newStrength);
                world.setBlockState(pos, state.with(adapter.getSignalProperty(world,state,signalIndex),newStrength),Block.NOTIFY_LISTENERS);
                for(BlockPos connectedPos: adapter.getConnectedBlocks(world, pos, state, signalIndex)){
                    world.updateNeighbor(connectedPos, state.getBlock(), pos);
                }
            }
        }
    }
}
