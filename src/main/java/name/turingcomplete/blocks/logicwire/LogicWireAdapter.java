package name.turingcomplete.blocks.logicwire;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.block.enums.WireConnection;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

/**
 * Allows blocks matching the type parameter Block to be treated as though they are an AbstractLogicWire.
 * 
 * LogicWireAdapter.LOGIC_WIRE is a trivial adapter for an actual AbstractLogicWire
 * LogicWireAdapter.REDSTONE is an adapter for vanilla redstone dust
 */
abstract class LogicWireAdapter<Block extends net.minecraft.block.Block> {
    public static final LogicWireAdapter<AbstractLogicWire> LOGIC_WIRE = new TrivialAdapter();
    public static final LogicWireAdapter<RedstoneWireBlock> REDSTONE = new RedstoneAdapter();

    public abstract int getSignalCount(BlockState state);
    public abstract IntProperty getSignalProperty(World world, BlockState state, int signalIndex);
    public abstract Optional<Integer> getConnectedSignalStrength(World world, BlockPos pos, BlockState state, BlockPos connectedPos);
    public abstract List<BlockPos> getConnectedBlocks(World world, BlockPos pos, BlockState state, int signalIndex);

    private static class TrivialAdapter extends LogicWireAdapter<AbstractLogicWire>{

        @Override
        public int getSignalCount(BlockState state) {
            net.minecraft.block.Block block = state.getBlock();
            if(!(block instanceof AbstractLogicWire)) {
                throw new IllegalArgumentException("Attempt to use Trivial LogicWireAdapter not on AbstractLogicWire");
            }
            return ((AbstractLogicWire)block).signalCount(state);
        }

        @Override
        public IntProperty getSignalProperty(World world, BlockState state, int signalIndex) {
            net.minecraft.block.Block block = state.getBlock();
            if(!(block instanceof AbstractLogicWire)) {
                throw new IllegalArgumentException("Attempt to use Trivial LogicWireAdapter not on AbstractLogicWire");
            }
            return ((AbstractLogicWire)block).signalStrengthProperties.get(signalIndex);
        }

        @Override
        public Optional<Integer> getConnectedSignalStrength(World world, BlockPos pos, BlockState state, BlockPos connectedPos) {
            net.minecraft.block.Block block = state.getBlock();
            if(!(block instanceof AbstractLogicWire)) {
                throw new IllegalArgumentException("Attempt to use Trivial LogicWireAdapter not on AbstractLogicWire");
            }
            return ((AbstractLogicWire)block).getConnectedSignalStrength(pos, state, connectedPos);
        }

        @Override
        public List<BlockPos> getConnectedBlocks(World world, BlockPos pos, BlockState state, int signalIndex) {
            net.minecraft.block.Block block = state.getBlock();
            if(!(block instanceof AbstractLogicWire)) {
                throw new IllegalArgumentException("Attempt to use Trivial LogicWireAdapter not on AbstractLogicWire");
            }
            return ((AbstractLogicWire)block).getConnectedBlocks(pos,state,signalIndex);
        }

    }

    private static class RedstoneAdapter extends LogicWireAdapter<RedstoneWireBlock>{

        @Override
        public int getSignalCount(BlockState state) {
            net.minecraft.block.Block block = state.getBlock();
            if(!(block == Blocks.REDSTONE_WIRE)) {
                throw new IllegalArgumentException("Attempt to use RedstoneAdapter not on redstone dust");
            }
            return 1;
        }

        @Override
        public IntProperty getSignalProperty(World world, BlockState state, int signalIndex) {
            net.minecraft.block.Block block = state.getBlock();
            if(!(block == Blocks.REDSTONE_WIRE)) {
                throw new IllegalArgumentException("Attempt to use RedstoneAdapter not on redstone dust");
            }
            else if(signalIndex == 0){
                return RedstoneWireBlock.POWER;
            }
            else {
                throw new IndexOutOfBoundsException("attempt to get singal index "+signalIndex+" for redstone wire, which only has a signal 0");
            }
        }

        @Override
        public Optional<Integer> getConnectedSignalStrength(World world, BlockPos pos, BlockState state, BlockPos connectedPos) {
            net.minecraft.block.Block block = state.getBlock();
            if(!(block == Blocks.REDSTONE_WIRE)) {
                throw new IllegalArgumentException("Attempt to use RedstoneAdapter not on redstone dust");
            }
            if(getConnectedBlocks(world, pos, state, 0).contains(connectedPos)){
                return Optional.of(state.get(RedstoneWireBlock.POWER));
            }
            else{
                return Optional.empty();
            }
        }

        @Override
        public List<BlockPos> getConnectedBlocks(World world, BlockPos pos, BlockState state, int signalIndex) {
            net.minecraft.block.Block block = state.getBlock();
            if(!(block == Blocks.REDSTONE_WIRE)) {
                throw new IllegalArgumentException("Attempt to use RedstoneAdapter not on redstone dust");
            }

             List<BlockPos> positions = new ArrayList<>(4);
            if(signalIndex != 0){
                return positions;
            }
            for(Direction direction: Direction.Type.HORIZONTAL){
                switch(state.get(RedstoneWireBlock.DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction))){
                case WireConnection.UP:
                    positions.add(pos.offset(direction).offset(Direction.UP));
                    break;
                case WireConnection.SIDE:
                    BlockPos connectedPos = pos.offset(direction).offset(Direction.DOWN);
                    BlockState connectedState = world.getBlockState(connectedPos);
                    if(
                        connectedState.isOf(Blocks.REDSTONE_WIRE) && 
                        connectedState.get(RedstoneWireBlock.DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction.getOpposite())).equals(WireConnection.UP)){

                        positions.add(connectedPos);
                    }
                    else{
                        positions.add(connectedPos.offset(Direction.UP));
                    }
                    break;
                    default:
                }
            }
            
            return Collections.unmodifiableList(positions);
        }
    }
}