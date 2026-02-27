package name.turingcomplete.blocks.logicwire;
import java.util.Optional;

import name.turingcomplete.TuringComplete;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class VanillaWireUpdateStrategy extends WireUpdateStrategy{
    public void onNeighborUpdate(
        BlockState currentWireState,
        World world,
        BlockPos currentWirePos,
        Optional<Block> sourceBlock,
        Optional<BlockPos> sourcePos,
        boolean notify
    ) {
        TuringComplete.LOGGER.trace("wire updated at "+currentWirePos+" from "+sourcePos);
        Block block = currentWireState.getBlock();
        Optional<LogicWireAdapter<? extends Block>> maybeAdapter = getAdapter(block);
        if(maybeAdapter.isEmpty()){
            TuringComplete.LOGGER.error(
                "attempt to use VanillaWireUpdateStrategy on non-wire block "+block+" (at "+currentWirePos+")");
            return;
        }
        LogicWireAdapter<? extends Block> adapter = maybeAdapter.get();

        for(int signalIndex = 0; signalIndex < adapter.getSignalCount(currentWireState); signalIndex++){
            int oldStrength = currentWireState.get(adapter.getSignalProperty(world,currentWireState,signalIndex));
            int newStrength = 0;
            for(BlockPos connectedPos: adapter.getConnectedBlocks(world, currentWirePos, currentWireState, signalIndex)){
                BlockState connectedState = world.getBlockState(connectedPos);
                Block connectedBlock = connectedState.getBlock();
                if(isWireBlock(connectedBlock)){
                    LogicWireAdapter<? extends Block> connectedAdapter = getAdapter(connectedBlock).orElseThrow();
                    newStrength = Math.max(
                        newStrength,
                        connectedAdapter
                            .getConnectedSignalStrength(
                                world,
                                connectedPos,
                                connectedState,
                                currentWirePos)
                            .orElse(0)-1);
                }
                else {
                    //we don't know the direction, and non-wire only connects normally anyway
                    for(Direction toConnectedDirection: Direction.Type.HORIZONTAL){
                        if(currentWirePos.offset(toConnectedDirection).equals(connectedPos)){
                            int connectedStrength = getEmittedStrongPower(world, connectedPos, toConnectedDirection);
                            if(connectedStrength > newStrength){
                                newStrength = connectedStrength;
                            }
                            break;
                        }
                    }
                }
            }
            if(oldStrength != newStrength) {
                TuringComplete.LOGGER.trace("signal "+signalIndex+" changed from "+oldStrength+" to "+newStrength);
                world.setBlockState(
                    currentWirePos,
                    currentWireState.with(
                        adapter.getSignalProperty(world,currentWireState,signalIndex),
                        newStrength),
                    Block.NOTIFY_LISTENERS);
                for(BlockPos connectedPos: adapter.getConnectedBlocks(world, currentWirePos, currentWireState, signalIndex)){
                    world.updateNeighbor(connectedPos, currentWireState.getBlock(), currentWirePos);
                    //update repeaters and such pulling out of a solid block
                    if(world.getBlockState(connectedPos).isSolidBlock(world,connectedPos)){
                        for(Direction toSecondaryConnectedDirection: Direction.Type.HORIZONTAL){
                            if(connectedPos.offset(toSecondaryConnectedDirection).equals(currentWirePos)){
                                world.updateNeighborsExcept(
                                    connectedPos,
                                    currentWireState.getBlock(),
                                    toSecondaryConnectedDirection);
                                return;
                            }
                        }
                    }
                    world.updateNeighbors(currentWirePos, block);
                }
            }
        }
    }

    //based on vanilla redstone
    private int getEmittedStrongPower(World world, BlockPos pos, Direction direction){
        // get source block
        BlockState source = world.getBlockState(pos);

        // if the block is solid, check surrounding blocks for strong powering
        if (source.isSolidBlock(world,pos)){
            int power = 0;
            for(Direction sourceNeighbourDirection : Direction.Type.HORIZONTAL){
                BlockPos sourceNeighbourPosition = pos.offset(sourceNeighbourDirection);
                BlockState sourceNeighbour = world.getBlockState(sourceNeighbourPosition);

                // if block is a logic wire, ignore it
                if(isWireBlock(sourceNeighbour.getBlock())) continue;

                // else check if the received power is bigger than the current biggest
                int neighbourPower = sourceNeighbour.getStrongRedstonePower(
                    world,
                    sourceNeighbourPosition,
                    sourceNeighbourDirection);
                if(neighbourPower > power) power = neighbourPower;
            }

            // return biggest power from nearby source
            return power;

        } // else:

        // else get weak power emitted from the block
        return source.getWeakRedstonePower(world,pos,direction);
    }
}
