package name.turingcomplete.blocks.multiblock;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.mojang.logging.LogUtils;

import name.turingcomplete.blocks.AbstractLogicMultiblock;
import name.turingcomplete.blocks.BLOCK_PART;
import name.turingcomplete.blocks.RelativeSide;
import name.turingcomplete.init.propertyInit;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.tick.TickPriority;

public final class Adder extends AbstractLogicMultiblock{
    public static final EnumProperty<BLOCK_PART> PART = propertyInit.BLOCK_PART;

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
    public Boolean dustConnectsToThis(BlockState state, Direction direction) {
        Direction facing = state.get(FACING);
        boolean mirrored = state.get(MIRRORED);
        switch(state.get(PART)) {
            case BOTTOM:
                if (mirrored && direction == RelativeSide.LEFT.onDirection(facing)) {
                    return true;
                }
                if (!mirrored && direction == RelativeSide.RIGHT.onDirection(facing)) {
                    return true;
                }
                return direction == facing.getOpposite();
            case MIDDLE:
                return direction == facing;
            case TOP:
                if (hasCarryIn) {
                    if (mirrored && direction == RelativeSide.RIGHT.onDirection(facing)) {
                        return true;
                    }
                    if (!mirrored && direction == RelativeSide.LEFT.onDirection(facing)) {
                        return true;
                    }
                }
                return direction == facing.getOpposite();
            default:
                return false;
        }
    }

    @Override
    public @Nullable BlockPos getMainPos(World world, BlockState partState, BlockPos partPos) {
        boolean mirrored = partState.get(MIRRORED);
        switch(partState.get(PART)) {
            //B side
            case BOTTOM:
                if (mirrored) {
                    return partPos.offset(RelativeSide.LEFT.onDirection(partState.get(FACING)));
                }
                else {
                    return partPos.offset(RelativeSide.RIGHT.onDirection(partState.get(FACING)));
                }
            case MIDDLE:
                return partPos;
            //A side
            case TOP:
                if (mirrored) {
                    return partPos.offset(RelativeSide.RIGHT.onDirection(partState.get(FACING)));
                }
                else {
                    return partPos.offset(RelativeSide.LEFT.onDirection(partState.get(FACING)));
                }
            default:
                throw new IllegalStateException("illegal part property on adder");

        }
    }

    @Override
    public List<BlockPos> getPartPosses(World world, BlockPos mainPos, BlockState mainState) {
        Direction facing = mainState.get(FACING);
        return List.of(
            mainPos.offset(RelativeSide.LEFT.onDirection(facing)),
            mainPos.offset(RelativeSide.RIGHT.onDirection(facing))
        );
    }

    @Override
    public boolean isMultiblockValid(World world, BlockPos mainPos, BlockState mainState) {
        BlockPos aPos = getAPos(world,mainPos,mainState);
        BlockPos bPos = getBPos(world,mainPos,mainState);

        if (!(world.getBlockState(aPos).getBlock() instanceof Adder)) {
            return false;
        }

        if (!(world.getBlockState(bPos).getBlock() instanceof Adder)) {
            return false;
        }

        //do we need to check for matching mirror and facing?
        return true;
    }

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.scheduledTick(state, world, pos, random);
        if (state.get(PART)!=BLOCK_PART.MIDDLE){
            return;
        }
        if (!isMultiblockValid(world, pos, state)) {
            return;
        }
        boolean newSum = evaluateSum(world, pos, state);
        boolean newCarryOut = evaluateCarryOut(world, pos, state);

        if (newSum != state.get(POWERED)) {
            state = state.with(POWERED,newSum);
            updateSumBlock(world,pos,state);
            world.setBlockState(pos, state);
        }

        BlockPos bPos = getBPos(world, pos, state);
        BlockState bState = world.getBlockState(bPos);
        if (newCarryOut != bState.get(CARRY)) {
            bState = bState.with(CARRY,newCarryOut);
            updateCarryOutBlock(world,pos,state);
            world.setBlockState(bPos, bState);
        }
    }

    @SuppressWarnings("incomplete-switch")
    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);

        BlockPos mainPos = getMainPos(world, state, pos);
        if (mainPos == null) return;

        BlockState mainState = world.getBlockState(mainPos);

        switch(state.get(PART)) {
            case BOTTOM:
                state = state.with(POWERED,getBActive(world, mainPos, mainState));
                break;
            case TOP:
                state = state
                    .with(POWERED,getAActive(world, mainPos, mainState))
                    .with(CARRY,getCarryInActive(world, mainPos, mainState));
                break;
            
        }

        BlockPos bPos;
        BlockState bState;
        switch(state.get(PART)) {
            case BOTTOM:
                bPos = pos;
                bState = state;
                break;
            default:
                bPos = getBPos(world, mainPos, mainState);
                bState = world.getBlockState(bPos);
        }

        world.setBlockState(pos, state);

        if(!isMultiblockValid(world,mainPos,mainState)) {
            LogUtils.getLogger().info("multiblock invalid at "+mainPos);
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
                return (direction == RelativeSide.LEFT.onDirection(facing) && state.get(CARRY)) ? 15 : 0;
            }
            else {
                return (direction == RelativeSide.RIGHT.onDirection(facing) && state.get(CARRY)) ? 15 : 0;
            }
            case MIDDLE:
                return (direction == facing && state.get(POWERED)) ? 15 : 0;
            default:
                return 0;

        }
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        return Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        World world = ctx.getWorld();
        BlockPos mainPos = ctx.getBlockPos();
        Direction facing = ctx.getHorizontalPlayerFacing().getOpposite();
        BlockState mainState = getMainPlacementState(facing);
        BlockPos aPos = mainPos.offset(RelativeSide.LEFT.onDirection(facing));
        BlockState aState = getAPlacementState(facing);
        BlockPos bPos = mainPos.offset(RelativeSide.RIGHT.onDirection(facing));
        BlockState bState = getBPlacementState(facing);

        if (
            !canPlaceAt(mainState, world, mainPos) ||
            !canPlaceAt(bState, world, bPos) ||
            !canPlaceAt(aState, world, aPos)
        ) {
            return null;
        }

        return mainState;
    }

    private BlockState getMainPlacementState(Direction facing) {
        return getDefaultState().with(FACING,facing).with(PART,BLOCK_PART.MIDDLE);
    }

    private BlockState getAPlacementState(Direction facing) {
        return getDefaultState().with(FACING,facing).with(PART,BLOCK_PART.TOP);
    }

    private BlockState getBPlacementState(Direction facing) {
        return getDefaultState().with(FACING,facing).with(PART,BLOCK_PART.BOTTOM);
    }

    private boolean evaluateSum(World world, BlockPos gatePos, BlockState gateState){
        boolean isAActive = getAActive(world, gatePos, gateState);
        boolean isBActive = getBActive(world, gatePos, gateState);
        boolean isCarryInActive = getCarryInActive(world, gatePos, gateState);

        LogUtils.getLogger().info("evaluateSum: a "+isAActive+" b "+isBActive + " carry "+isCarryInActive);

        boolean result = isAActive ^ isBActive ^ isCarryInActive;

        LogUtils.getLogger().info("evaluateSum result: "+result);
        return result;
    }
    
    private boolean evaluateCarryOut(World world, BlockPos gatePos, BlockState gateState){
        boolean isAActive = getAActive(world, gatePos, gateState);
        boolean isBActive = getBActive(world, gatePos, gateState);
        boolean isCarryInActive = getCarryInActive(world, gatePos, gateState);

        LogUtils.getLogger().info("evaluateCarryOut: a "+isAActive+" b "+isBActive + " carry "+isCarryInActive);

        boolean result = isAActive && isBActive || (isCarryInActive && (isAActive || isBActive));
        LogUtils.getLogger().info("evaluateCarryOut result: "+result);
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

    private BlockPos getAPos(World world, BlockPos mainPos, BlockState mainState){
        boolean mirrored = mainState.get(MIRRORED);
        if(mirrored) {
            return mainPos.offset(mainState.get(FACING).rotateYCounterclockwise());
        }
        else {
            return mainPos.offset(mainState.get(FACING).rotateYClockwise());
        }
    }

    private BlockPos getBPos(World world, BlockPos mainPos, BlockState mainState){
        boolean mirrored = mainState.get(MIRRORED);
        if(mirrored) {
            return mainPos.offset(mainState.get(FACING).rotateYClockwise());
        }
        else {
            return mainPos.offset(mainState.get(FACING).rotateYCounterclockwise());
        }
    }

    protected final void updateSumBlock(World world, BlockPos mainPos, BlockState mainState){
        Direction facing = mainState.get(FACING);
        BlockPos targetPos = mainPos.offset(facing.getOpposite());

        //I don't understand this part
        world.updateNeighbor(targetPos, this, mainPos);
        world.updateNeighborsExcept(targetPos, this, facing);
    }

    protected final void updateCarryOutBlock(World world, BlockPos mainPos, BlockState mainState){
        Direction facing = mainState.get(FACING);
        boolean mirrored = mainState.get(MIRRORED);
        RelativeSide outputSide = mirrored ? RelativeSide.RIGHT : RelativeSide.LEFT;
        BlockPos targetPos = mainPos.offset(outputSide.onDirection(facing),2);

        //I don't understand this part
        world.updateNeighbor(targetPos, this, mainPos);
        world.updateNeighborsExcept(targetPos, this, outputSide.onDirection(facing));
    }
}
