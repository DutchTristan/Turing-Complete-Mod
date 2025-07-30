package name.turingcomplete.blocks.logicwire;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.MustBeInvokedByOverriders;

import name.turingcomplete.blocks.AbstractSimpleLogicBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

/**
 * Algorithm overview:
 * When a wire is updated, it loops through connected blocks, and sets its strength to the maximum of the following:
 * - the greatest weak strength of connected non-wire blocks
 * - one less than the greatest signal strength of connected wire blocks
 * If this block's signal strength is updated, neighboring connected blocks are updated, with the possible exception'
 * of the update source block.
 * If the update source block is a wire block, and the signal strength was set to one less than the source block's strength, then the
 * update source block will be skipped when updating neighbors. Otherwise, if it is a wire block, it will be updated.
 * A non-wire update source block will not be updated
 * 
 * Vanilla redstone dust is considered a wire block
 * 
 * TODO: add a registry for third-party mods to inform TC of their wire-like blocks.
 * Without this registry, transitioning between TC and third-party wires will cause redstone signals to continue 1 block extra
 */
public abstract class AbstractLogicWire extends AbstractSimpleLogicBlock {
    //immutable
    //set in appendProperties because it is needed in super constructor
    //cannot use explicit =null because that will run after the super constructor, and thus erase
    //the initialization from appendProperties
    protected List<IntProperty> signalStrengthProperties;

    protected AbstractLogicWire(Settings settings) {
        super(settings);

        //set signal strength defaults to 0
        BlockState defaultState = getDefaultState();
        for(IntProperty signalStrengthProperty: signalStrengthProperties){
            defaultState = defaultState.with(signalStrengthProperty, 0);
        }
        setDefaultState(defaultState);
    }

    protected int maxSignalCount(){
        return 1;
    }

    protected int signalCount(BlockState state){
        return maxSignalCount();
    }

    //FIXME: not actually checked
    protected int maxSignalStrength(){
        return 15;
    }

    protected List<BlockPos> getConnectedBlocks(BlockPos pos, BlockState state, int signalIndex){
        List<BlockPos> positions = new ArrayList<>(4);
        if (signalIndex != 0) return positions;
        for(Direction fromDustDirection: Direction.Type.HORIZONTAL){
            if (dustConnectsToThis(state,fromDustDirection)){
                positions.add(pos.offset(fromDustDirection.getOpposite()));
            }
        }
        return positions;
    }

    protected Optional<Integer> getConnectedSignalStrength(BlockPos pos, BlockState state, BlockPos connectedPos){
        if(signalCount(state)!=1){
            throw new IllegalStateException("incorrect implementation of AbstractLogicWire: signalCount or maxSignalCount was overriden, but getConnectedSignalStrength was not");
        }
        for(Direction fromConnectedDirection: Direction.Type.HORIZONTAL){
            if((connectedPos.offset(fromConnectedDirection)).equals(pos)){
                if(dustConnectsToThis(state, fromConnectedDirection)){
                    return Optional.of(state.get(signalStrengthProperties.get(0)));
                }
                break;
            }
        }
        return Optional.empty();
    }

    @Override
    @MustBeInvokedByOverriders
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        if(signalStrengthProperties == null){
            //create signal strength properties
            int maxStrength = maxSignalStrength();
            List<IntProperty> signalStrengthProperties = new ArrayList<>(maxSignalCount());
            if(maxSignalCount()==1){
                //simplified name
                signalStrengthProperties.add(IntProperty.of("power", 0, maxStrength));
            }
            else {
                for(int signalIndex = 0; signalIndex < maxSignalCount(); signalIndex++){
                    signalStrengthProperties.add(IntProperty.of("power_"+(signalIndex+1), 0, maxStrength));
                }
            }
            //make immutable
            this.signalStrengthProperties = Collections.unmodifiableList(signalStrengthProperties);
        }

        //actually append the properties
        for(IntProperty signalStrengthProperty: signalStrengthProperties) {
            builder.add(signalStrengthProperty);
        }
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {

        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);

        new OnePassWireUpdateStrategy().onNeighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
    }    
    
    @Override
    protected int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction toThisDirection)
    {
        return getWeakRedstonePower(state, world, pos, toThisDirection);
    }

    @Override
    protected int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction toThisDirection)
    {
        BlockPos targetPos = pos.offset(toThisDirection.getOpposite());
        Block targetBlock = world.getBlockState(targetPos).getBlock();
        for(int signalIndex = 0; signalIndex < signalCount(state); signalIndex++){
            for(BlockPos connectedPos: getConnectedBlocks(pos, state, signalIndex)){
                if(targetPos.equals(connectedPos)){
                    //this is the correct signal
                    if(targetBlock == Blocks.REDSTONE_WIRE) {
                        //fudge factor to decrease strength into foreign wire
                        return state.get(signalStrengthProperties.get(signalIndex))-1;
                    }
                    else {
                        return state.get(signalStrengthProperties.get(signalIndex));
                    }
                }
            }
        }
        //not connected
        return 0;
    }
}
