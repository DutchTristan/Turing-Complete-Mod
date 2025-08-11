package name.turingcomplete.blocks.multiblock;

import java.util.List;

import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.Nullable;

import name.turingcomplete.TuringComplete;
import name.turingcomplete.blocks.AbstractLogicMultiblock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.TickPriority;

public final class Adder extends AbstractLogicMultiblock{
    public static final EnumProperty<AdderPart> PART = EnumProperty.of("part", AdderPart.class);

    //to reduce block states, "powered" is used for both the A and B inputs on the end parts,
    //and the Sum output on the middle part, and "carry" is used for both the carry in and the carry out
    public static final BooleanProperty POWERED = Properties.POWERED;
    public static final BooleanProperty CARRY = BooleanProperty.of("carry");
    private static final int gate_delay = 2;

    private final boolean hasCarryIn;

    public Adder(Settings settings, boolean hasCarryIn) {
        super(settings);
        this.hasCarryIn = hasCarryIn;
        setDefaultState(getDefaultState()
                .with(POWERED, false)
                .with(CARRY, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(PART);
        builder.add(POWERED);
        builder.add(CARRY);
    }

    @Override
    public boolean canMirror(){
        return true;
    }

    @Override
    public boolean canMirrorHere(BlockPos mainPos, BlockState mainState){
        return true;
    }

    @Override
    protected BlockPos mirrorBlockParts(World world, BlockPos mainPos, BlockState mainState){
        if(!isMultiblockValid(world, mainPos, mainState)) {
            TuringComplete.LOGGER.warn("invalid multiblock at "+mainPos+"in Adder mirrorBlockParts");
            return mainPos;
        }
        Direction facing = mainState.get(FACING);
        BlockPos oldAPos = getAPos(world, mainPos, mainState);
        BlockPos oldBPos = getBPos(world, mainPos, mainState);

        //TODO: can we avoid some of these neighbor updates when updating state?
        world.setBlockState(oldAPos, getBPlacementState(facing), Block.NOTIFY_ALL);
        world.setBlockState(oldBPos, getAPlacementState(facing), Block.NOTIFY_ALL);

        //notify neighbors of old carry out, since it moved and the neighbors aren't hit by implicit neighbor updates form setBlockState


        return mainPos;
    }

    @Override
    protected void afterMirror(World world, BlockPos mainPos, BlockState mainState) {
        updateOldCarryOutBlockNeighbors(world,mainPos,mainState);
    }

    @Override
    public @Nullable BlockPos getMainPos(World world, BlockState partState, BlockPos partPos) {
        boolean mirrored = partState.get(MIRRORED);
        switch(partState.get(PART)) {
            //B side
            case BOTTOM:
                if (mirrored) {
                    return partPos.offset(RelativeSide.RIGHT.withBackDirection(partState.get(FACING)));
                }
                else {
                    return partPos.offset(RelativeSide.LEFT.withBackDirection(partState.get(FACING)));
                }
            case MIDDLE:
                return partPos;
            //A side
            case TOP:
                if (mirrored) {
                    return partPos.offset(RelativeSide.LEFT.withBackDirection(partState.get(FACING)));
                }
                else {
                    return partPos.offset(RelativeSide.RIGHT.withBackDirection(partState.get(FACING)));
                }
            default:
                throw new IllegalStateException("illegal part property on adder");

        }
    }

    @Override
    public List<BlockPos> getPartPosses(World world, BlockPos mainPos, BlockState mainState) {
        Direction facing = mainState.get(FACING);
        return List.of(
            mainPos.offset(RelativeSide.LEFT.withBackDirection(facing)),
            mainPos.offset(RelativeSide.RIGHT.withBackDirection(facing))
        );
    }

    @Override
    public boolean isMultiblockValid(World world, BlockPos mainPos, BlockState mainState) {
        if (!mainState.isOf(this) || mainState.get(PART)!=AdderPart.MIDDLE){
            return false;
        }
        BlockPos aPos = getAPos(world,mainPos,mainState);
        BlockState aState = world.getBlockState(aPos);
        BlockPos bPos = getBPos(world,mainPos,mainState);
        BlockState bState = world.getBlockState(bPos);

        if (
            !aState.isOf(this) ||
            !bState.isOf(this) ||
            aState.get(PART) != AdderPart.TOP ||
            bState.get(PART) != AdderPart.BOTTOM ||
            aState.get(FACING) != mainState.get(FACING) ||
            bState.get(FACING) != mainState.get(FACING) ||
            aState.get(MIRRORED) != mainState.get(MIRRORED) ||
            bState.get(MIRRORED) != mainState.get(MIRRORED)
            ) {
            return false;
        }

        return true;
    }

    @Override
    public Boolean dustConnectsToThis(BlockState state, Direction direction) {
        Direction facing = state.get(FACING);
        boolean mirrored = state.get(MIRRORED);
        switch(state.get(PART)) {
            case BOTTOM:
                if (mirrored && direction == RelativeSide.RIGHT.withBackDirection(facing)) {
                    return true;
                }
                if (!mirrored && direction == RelativeSide.LEFT.withBackDirection(facing)) {
                    return true;
                }
                return direction == facing.getOpposite();
            case MIDDLE:
                return direction == facing;
            case TOP:
                if (hasCarryIn) {
                    if (mirrored && direction == RelativeSide.LEFT.withBackDirection(facing)) {
                        return true;
                    }
                    if (!mirrored && direction == RelativeSide.RIGHT.withBackDirection(facing)) {
                        return true;
                    }
                }
                return direction == facing.getOpposite();
            default:
                return false;
        }
    }

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.scheduledTick(state, world, pos, random);
        if (state.get(PART)!=AdderPart.MIDDLE){
            TuringComplete.LOGGER.warn("Adder tick scheduled not on middle block)");
            return;
        }
        if (!isMultiblockValid(world, pos, state)) {
            TuringComplete.LOGGER.warn("invalid multiblock at "+pos+" in Adder scheduledTick)");
            return;
        }
        boolean newSum = evaluateSum(world, pos, state);
        boolean newCarryOut = evaluateCarryOut(world, pos, state);

        if (newSum != state.get(POWERED)) {
            state = state.with(POWERED,newSum);
            //don't notify neighbors, because only the output-block neighbor needs to be updated
            //"listeners" includes server -> client communication, so is probably still needed
            world.setBlockState(pos, state,Block.NOTIFY_LISTENERS);
            updateSumBlock(world,pos,state);
        }

        BlockPos bPos = getBPos(world, pos, state);
        BlockState bState = world.getBlockState(bPos);
        if (newCarryOut != bState.get(CARRY)) {
            bState = bState.with(CARRY,newCarryOut);
            //don't notify neighbors, because only the output-block neighbor needs to be updated
            //"listeners" includes server -> client communication, so is probably still needed
            world.setBlockState(bPos, bState,Block.NOTIFY_LISTENERS);
            updateCarryOutBlock(world,pos,state);
        }
    }

    @Override
    protected void onInputChange(World world, BlockPos pos, BlockState state){
        BlockPos mainPos = getMainPos(world, state, pos);
        if (mainPos == null) {
            TuringComplete.LOGGER.debug("invalid multiblock at "+mainPos+"in Adder onInputChange (cannot find main block). May be caused by spurious neighbor updates when mirroring");
            return;
        }

        BlockState mainState = world.getBlockState(mainPos);

        if (!(mainState.getBlock() instanceof Adder) || !isMultiblockValid(world, mainPos, mainState)) {
            TuringComplete.LOGGER.debug("invalid multiblock at "+mainPos+"in Adder onInputChange. May be caused by spurious neighbor updates when mirroring");
            return;
        }

        switch(state.get(PART)) {
            case BOTTOM:
                state = state.with(POWERED,getBActive(world, mainPos, mainState));
                break;
            case TOP:
                state = state
                    .with(POWERED,getAActive(world, mainPos, mainState))
                    .with(CARRY,getCarryInActive(world, mainPos, mainState));
                break;
            default:
                break;
        }

        BlockState bState;
        switch(state.get(PART)) {
            case BOTTOM:
                bState = state;
                break;
            default:
            BlockPos bPos = getBPos(world, mainPos, mainState);
                bState = world.getBlockState(bPos);
        }

        //don't notify neighbors, because they are not affected
        //"listeners" includes server -> client communication, so is probably still needed
        world.setBlockState(pos, state,Block.NOTIFY_LISTENERS);

        if(!isMultiblockValid(world,mainPos,mainState)) {
            TuringComplete.LOGGER.error("Adder multiblock invalid at "+mainPos+"after changing input state");
            return;
        }

        if (
            evaluateSum(world, mainPos, mainState) != mainState.get(POWERED) ||
            evaluateCarryOut(world, mainPos, mainState) != bState.get(CARRY)
            ) {
            world.scheduleBlockTick(mainPos, this, gate_delay, TickPriority.VERY_HIGH);
        }
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);

        Direction facing = state.get(FACING);

        BlockPos aPos = getAPos(world,pos,state);
        BlockState aState = getAPlacementState(facing);
        BlockPos bPos = getBPos(world,pos,state);
        BlockState bState = getBPlacementState(facing);

        world.setBlockState(aPos,aState,Block.NOTIFY_ALL);
        world.setBlockState(bPos,bState,Block.NOTIFY_ALL);

        //redstone dust hasn't been redirected yet, so must schedule unconditionally
        world.scheduleBlockTick(pos,this, gate_delay, TickPriority.VERY_HIGH);
        //update parts because otherwise their input states won't update
        world.updateNeighbor(aPos, this, pos);
        world.updateNeighbor(bPos, this, pos);
    }

    @Override
    @MustBeInvokedByOverriders
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        BlockPos mainPos = getMainPos(world, state, pos);
        BlockState mainState = world.getBlockState(mainPos);
        if(mainState.get(POWERED)) {
            //need to change blockstate so that getWeakRedstonePower returns 0
            world.setBlockState(mainPos, mainState.with(POWERED,false));
            updateSumBlock(world, mainPos, mainState);
        }
        BlockPos bPos = getBPos(world, mainPos, mainState);
        BlockState bState = world.getBlockState(bPos);
        if(bState.get(CARRY)) {
            //need to change blockstate so that getWeakRedstonePower returns 0
            world.setBlockState(bPos, bState.with(POWERED,false));
            updateCarryOutBlock(world, mainPos, mainState);
        }
        return super.onBreak(world, pos, state, player);
    }

    @Override
    protected final int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction)
    {
        return getWeakRedstonePower(state, world, pos, direction);
    }

    @Override
    protected final int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction)
    {
        Direction facing = state.get(FACING);
        boolean mirrored = state.get(MIRRORED);
        switch(state.get(PART)){
            case BOTTOM:
            if (mirrored) {
                return (direction == RelativeSide.RIGHT.withBackDirection(facing) && state.get(CARRY)) ? 15 : 0;
            }
            else {
                return (direction == RelativeSide.LEFT.withBackDirection(facing) && state.get(CARRY)) ? 15 : 0;
            }
            case MIDDLE:
                return (direction == facing && state.get(POWERED)) ? 15 : 0;
            default:
                return 0;
        }
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        World world = ctx.getWorld();
        BlockPos mainPos = ctx.getBlockPos();
        Direction facing = ctx.getHorizontalPlayerFacing().getOpposite();
        BlockState mainState = getMainPlacementState(facing);
        BlockPos aPos = mainPos.offset(RelativeSide.RIGHT.withBackDirection(facing));
        BlockState aState = getAPlacementState(facing);
        BlockPos bPos = mainPos.offset(RelativeSide.LEFT.withBackDirection(facing));
        BlockState bState = getBPlacementState(facing);

        if (
            !mainState.canPlaceAt(world, mainPos) ||
            !bState.canPlaceAt(world, bPos) ||
            !aState.canPlaceAt(world, aPos) ||
            //canPlaceAt assumes position is not occupied, but that is only known true for mainPos
            world.getBlockState(aPos).getBlock() != Blocks.AIR ||
            world.getBlockState(bPos).getBlock() != Blocks.AIR
        ) {
            //returning null cancels block placement
            return null;
        }

        return mainState;
    }

    private BlockState getMainPlacementState(Direction facing) {
        return getDefaultState().with(FACING,facing).with(PART,AdderPart.MIDDLE);
    }

    private BlockState getAPlacementState(Direction facing) {
        return getDefaultState().with(FACING,facing).with(PART,AdderPart.TOP);
    }

    private BlockState getBPlacementState(Direction facing) {
        return getDefaultState().with(FACING,facing).with(PART,AdderPart.BOTTOM);
    }

    private boolean evaluateSum(World world, BlockPos gatePos, BlockState gateState){
        boolean isAActive = getAActive(world, gatePos, gateState);
        boolean isBActive = getBActive(world, gatePos, gateState);
        boolean isCarryInActive = getCarryInActive(world, gatePos, gateState);

        boolean result = isAActive ^ isBActive ^ isCarryInActive;

        return result;
    }
    
    private boolean evaluateCarryOut(World world, BlockPos gatePos, BlockState gateState){
        boolean isAActive = getAActive(world, gatePos, gateState);
        boolean isBActive = getBActive(world, gatePos, gateState);
        boolean isCarryInActive = getCarryInActive(world, gatePos, gateState);

        boolean result = isAActive && isBActive || (isCarryInActive && (isAActive || isBActive));
        return result;
    }

    private boolean getCarryInActive(World world, BlockPos gatePos, BlockState gateState){
        if (!hasCarryIn) {
            return false;
        }

        boolean mirrored = gateState.get(MIRRORED);

        BlockPos partPos = getAPos(world, gatePos, gateState);
        return getInputActive(world, partPos, world.getBlockState(partPos), mirrored ? RelativeSide.RIGHT : RelativeSide.LEFT);
    }

    private boolean getAActive(World world, BlockPos mainPos, BlockState mainState){
        BlockPos partPos = getAPos(world, mainPos, mainState);
        return getInputActive(world, partPos, world.getBlockState(partPos),RelativeSide.BACK);
    }

    private boolean getBActive(World world, BlockPos mainPos, BlockState mainState){
        BlockPos partPos = getBPos(world, mainPos, mainState);
        return getInputActive(world, partPos, world.getBlockState(partPos),RelativeSide.BACK);
    }

    private BlockPos getAPos(WorldView world, BlockPos mainPos, BlockState mainState){
        boolean mirrored = mainState.get(MIRRORED);
        if(mirrored) {
            return mainPos.offset(mainState.get(FACING).rotateYCounterclockwise());
        }
        else {
            return mainPos.offset(mainState.get(FACING).rotateYClockwise());
        }
    }

    private BlockPos getBPos(WorldView world, BlockPos mainPos, BlockState mainState){
        boolean mirrored = mainState.get(MIRRORED);
        if(mirrored) {
            return mainPos.offset(mainState.get(FACING).rotateYClockwise());
        }
        else {
            return mainPos.offset(mainState.get(FACING).rotateYCounterclockwise());
        }
    }
    
    private final void updateSumBlock(World world, BlockPos mainPos, BlockState mainState){
        Direction facing = mainState.get(FACING);

        updateOutputBlock(world, mainPos, facing.getOpposite());
    }

    private final void updateCarryOutBlock(World world, BlockPos mainPos, BlockState mainState){
        Direction facing = mainState.get(FACING);
        boolean mirrored = mainState.get(MIRRORED);
        RelativeSide outputSide = mirrored ? RelativeSide.LEFT : RelativeSide.RIGHT;
        BlockPos bPos = getBPos(world, mainPos, mainState);

        updateOutputBlock(world, bPos, outputSide.withBackDirection(facing));
    }

    private final void updateOldCarryOutBlockNeighbors(World world, BlockPos mainPos, BlockState mainState){
        Direction facing = mainState.get(FACING);
        boolean mirrored = mainState.get(MIRRORED);
        RelativeSide outputSide = mirrored ? RelativeSide.RIGHT : RelativeSide.LEFT;
        Direction oldOutputDirection = outputSide.withBackDirection(facing);
        BlockPos aPos = getAPos(world, mainPos, mainState);
        BlockPos targetPos = aPos.offset(outputSide.withBackDirection(facing));

         //don't update ourself
         world.updateNeighborsExcept(targetPos, this, oldOutputDirection.getOpposite());
    }

    public enum AdderPart implements StringIdentifiable {
        TOP("top"),
        MIDDLE("middle"),
        BOTTOM("bottom");

        private final String name;

        AdderPart(String name) {
            this.name = name;

        }
        public String toString() {
                return this.name;
        }

        @Override
        public String asString() {
                return this.name;
        }
    }
}
