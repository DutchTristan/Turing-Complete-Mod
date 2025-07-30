package name.turingcomplete.blocks.logicwire;

import java.util.Optional;

import name.turingcomplete.TuringComplete;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class OnePassWireUpdateStrategy extends WireUpdateStrategy {

    @Override
    public void onNeighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        TuringComplete.LOGGER.trace("wire updated at "+pos+" from "+sourcePos);
        Block block = state.getBlock();
        Optional<LogicWireAdapter<? extends Block>> maybeAdapter = getAdapter(block);
        if(maybeAdapter.isEmpty()){
            TuringComplete.LOGGER.error("attempt to use VanillaWireUpdateStrategy on non-wire block "+block+" (at "+pos+")");
            return;
        }
        LogicWireAdapter<? extends Block> adapter = maybeAdapter.get();

        for(int signalIndex = 0; signalIndex < adapter.getSignalCount(state); signalIndex++){
            int oldStrength = state.get(adapter.getSignalProperty(world,state,signalIndex));
            int newStrength = 0;
            for(BlockPos connectedPos: adapter.getConnectedBlocks(world, pos, state, signalIndex)){
                BlockState connectedState = world.getBlockState(connectedPos);
                Block connectedBlock = connectedState.getBlock();

                if(isWireBlock(connectedBlock)){
                    LogicWireAdapter<? extends Block> connectedAdapter = getAdapter(connectedBlock).orElseThrow();
                    Optional<Integer> connectedStrength = connectedAdapter.getConnectedSignalStrength(world, connectedPos, connectedState, pos);
                    if(
                        connectedStrength.isPresent() && 
                        (
                            // if the connected wire's strength is greater than or equal to the old strength, then the connected wire was not powered by us
                            // and thus has power from elsewhere we should honor
                            connectedStrength.get() >= oldStrength || 
                            // algorithm invariant: if the update source is a logic wire, then that logic wire is powered not by us
                            connectedPos.equals(sourcePos)
                        ) &&
                        connectedStrength.get() -1 > newStrength
                    ){
                        newStrength = connectedStrength.get() -1;
                    }
                }
                //non-wire
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

            if(newStrength != oldStrength){

                TuringComplete.LOGGER.trace("signal "+signalIndex+" changed from "+oldStrength+" to "+newStrength);
                //explicit flags to prevent spurious updates
                world.setBlockState(pos, state.with(adapter.getSignalProperty(world,state,signalIndex),newStrength),Block.NOTIFY_LISTENERS);

                for(BlockPos connectedPos: adapter.getConnectedBlocks(world,pos, state, signalIndex)){
                    BlockState connectedState = world.getBlockState(connectedPos);
                    Block connectedBlock = connectedState.getBlock();

                    if(isWireBlock(connectedBlock)){
                        LogicWireAdapter<? extends Block> connectedAdapter = getAdapter(connectedBlock).orElseThrow();
                        Optional<Integer> neighborStrength = connectedAdapter.getConnectedSignalStrength(world,connectedPos, connectedState, pos);
                        if(
                            neighborStrength.isPresent() && //skip update if neighbor does not connect to us
                            !(
                                //skip update if neighbor is already consistent with being powered by us (power form elsewhere coincidentally matches)
                                (neighborStrength.get()==newStrength-1) ||
                                //skip update if neighbor is already consistent with it and us being unpowered
                                (neighborStrength.get()==0 && newStrength == 0) ||
                                //skip update for source if it is still consistent with us being powered by it
                                (connectedPos.equals(sourcePos) && newStrength == neighborStrength.get()-1)
                            )
                        ){
                            world.updateNeighbor(connectedPos, block, pos);
                        }
                    }
                    else if(!connectedPos.equals(sourcePos)){
                        world.updateNeighbor(connectedPos, block, pos);
                    }
                }
            }
        }
    }
}
