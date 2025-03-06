package name.turingcomplete.blocks;

import name.turingcomplete.init.propertyInit;
import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.*;
import net.minecraft.world.tick.TickPriority;

public abstract class MultiBlockGate extends AbstractGate{
    public static final EnumProperty<BLOCK_PART> PART = propertyInit.BLOCK_PART;


    public static final BooleanProperty SUM = Properties.POWERED;
    public static final BooleanProperty CARRY = BooleanProperty.of("carry");
    public static final BooleanProperty HALFSUM = BooleanProperty.of("halfsum");

    protected static final VoxelShape SHAPE = VoxelShapes.cuboid(0, 0, 0, 1, 0.125, 1);


    public MultiBlockGate(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState()
                .with(CARRY, false)
                .with(SUM, false)
                .with(PART,BLOCK_PART.MIDDLE)
                .with(FACING, Direction.NORTH)
                .with(HALFSUM, false));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {return SHAPE;}

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        Direction direction = state.get(FACING);
        BlockPos right = pos.offset(direction.rotateYCounterclockwise());
        BlockPos left = pos.offset(direction.rotateYClockwise());
        world.setBlockState(right, getAdjacentState(world, right, state, BLOCK_PART.RIGHT), Block.NOTIFY_ALL);
        world.setBlockState(left, getAdjacentState(world, left, state, BLOCK_PART.LEFT), Block.NOTIFY_ALL);
    }

    protected BlockState getAdjacentState(World world, BlockPos pos, BlockState initial, BLOCK_PART part) {
        return switch (part) {
            case RIGHT, LEFT -> initial.with(PART, part).with(HALFSUM, getSideInput(world, initial, pos) > 0);
            case MIDDLE -> initial.with(PART, part);
        };
    }

    protected void properties(StateManager.Builder<Block, BlockState> builder)
    {builder.add(SUM, CARRY, HALFSUM, PART);}


    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        World world = ctx.getWorld();
        BlockPos midPos = ctx.getBlockPos();
        BlockState midState = this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());

        Direction left = midState.get(FACING).rotateYClockwise();
        BlockPos leftPos = midPos.offset(left);
        BlockState leftState = this.getDefaultState().with(FACING, midState.get(FACING));
        boolean canPlaceLeft = leftState.canPlaceAt(world, leftPos) && world.getBlockState(leftPos).canReplace(ctx);

        BlockPos rightPos = midPos.offset(left.getOpposite());
        BlockState rightState = this.getDefaultState().with(FACING, midState.get(FACING));
        boolean canPlaceRight = rightState.canPlaceAt(world, rightPos) && world.getBlockState(rightPos).canReplace(ctx);
        if (!canPlaceLeft || !canPlaceRight){
            return null;
        }
        else {
            BlockState state = getDefaultState();
            state = state.with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
            return state;
        }
    }

    // ===================================================
    // Not Used As Intended To Be Updated
    // ===================================================

    public boolean supportsSideDirection(BlockState state, Direction direction) {return false;}
    public boolean supportsBackDirection() {return false;}

    @Override
    protected void update(World world, BlockState state, BlockPos pos) {
        if (state.get(PART) == BLOCK_PART.MIDDLE) {
            boolean bl = state.get(SUM);
            boolean bl2 = this.gateConditionMet(world, pos, state);
            if (bl && !bl2) {
                world.setBlockState(pos, state.with(SUM, false), 2);
            } else if (!bl) {
                world.setBlockState(pos, state.with(SUM, true), 2);
                if (!bl2) {
                    world.scheduleBlockTick(pos, this, this.getUpdateDelayInternal(state), TickPriority.VERY_HIGH);
                }
            }

            updateTarget(world,pos,state.get(FACING));
        }
    }

    @Override
    protected boolean shouldUpdate(World world, BlockState state, BlockPos pos) {
        boolean bl = state.get(SUM);
        boolean bl2 = this.gateConditionMet(world, pos, state);
        return bl != bl2 && !world.getBlockTickScheduler().isTicking(pos, this);
    }


    // ===================================================

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        Direction dir = state.get(FACING);
        if (direction == dir.rotateYCounterclockwise() && state.get(PART) == BLOCK_PART.MIDDLE){
            return neighborState.isOf(this) ? state : Blocks.AIR.getDefaultState();
        }
        else if (direction == dir.rotateYClockwise() && state.get(PART) == BLOCK_PART.MIDDLE){
            return neighborState.isOf(this) ? state : Blocks.AIR.getDefaultState();
        }
        else if (state.get(PART) == BLOCK_PART.LEFT && direction == dir.rotateYCounterclockwise()){
            return neighborState.isOf(this) ? state : Blocks.AIR.getDefaultState();
        }
        else if (state.get(PART) == BLOCK_PART.RIGHT && direction == dir.rotateYClockwise()){
            return neighborState.isOf(this) ? state : Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(state,direction,neighborState,world,pos,neighborPos);
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player){
        Direction direction = state.get(FACING);
        BlockPos leftPos, rightPos, midPos;

        if (state.get(PART) == BLOCK_PART.MIDDLE){
            leftPos = pos.offset(state.get(FACING).rotateYClockwise());
            rightPos = pos.offset(state.get(FACING).rotateYCounterclockwise());
            midPos = pos;
        }
        else if (state.get(PART) == BLOCK_PART.LEFT){
            leftPos = pos;
            rightPos = pos.offset(direction.rotateYCounterclockwise(), 2);
            midPos = pos.offset(direction.rotateYCounterclockwise());
        }
        else{
            leftPos = pos.offset(direction.rotateYClockwise(), 2);
            rightPos = pos;
            midPos = pos.offset(direction.rotateYClockwise());
        }
        world.breakBlock(midPos,!player.isCreative(),player,1);
        world.breakBlock(leftPos,false,player,1);
        world.breakBlock(rightPos,false,player,1);
        return super.onBreak(world,pos,state,player);
    }

    public static int getSideInput(World world, BlockState state, BlockPos pos){
        Direction redstoneDir = state.get(FACING);
        BlockPos redstonePos = pos.offset(redstoneDir);
        return world.getEmittedRedstonePower(redstonePos,redstoneDir);
    }

    public static int getCarryInput(World world, BlockState state, BlockPos pos){
        Direction redstoneDir = state.get(FACING).rotateYClockwise();
        BlockPos redstonePos = pos.offset(redstoneDir);
        return world.getEmittedRedstonePower(redstonePos,redstoneDir);
    }
}
