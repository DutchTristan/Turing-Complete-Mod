package name.turingcomplete;

import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
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
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.*;
import net.minecraft.world.tick.TickPriority;

public abstract class MultiBlockGate extends AbstractLogicGate{
    public static final EnumProperty<BLOCK_PART> PART = EnumProperty.of("part", BLOCK_PART.class);


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
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockPos bottomPos = pos.down();
        return this.canPlaceAbove(world,bottomPos,world.getBlockState(bottomPos));
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        Direction direction = state.get(FACING);
        if (state.get(PART) == BLOCK_PART.MIDDLE){
            switch (direction) {
                case NORTH: {
                    world.setBlockState(pos.west(), state.with(PART, BLOCK_PART.BOTTOM).with(FACING, Direction.NORTH), Block.NOTIFY_ALL);
                    world.setBlockState(pos.east(), state.with(PART, BLOCK_PART.TOP).with(FACING, Direction.NORTH), Block.NOTIFY_ALL);
                    return;
                }
                case SOUTH: {
                    world.setBlockState(pos.west(), state.with(PART, BLOCK_PART.TOP).with(FACING, Direction.SOUTH), Block.NOTIFY_ALL);
                    world.setBlockState(pos.east(), state.with(PART, BLOCK_PART.BOTTOM).with(FACING, Direction.SOUTH), Block.NOTIFY_ALL);
                    return;
                }
                case EAST: {
                    world.setBlockState(pos.south(), state.with(PART, BLOCK_PART.TOP).with(FACING, Direction.EAST), Block.NOTIFY_ALL);
                    world.setBlockState(pos.north(), state.with(PART, BLOCK_PART.BOTTOM).with(FACING, Direction.EAST), Block.NOTIFY_ALL);
                    return;
                }
                case WEST: {
                    world.setBlockState(pos.south(), state.with(PART, BLOCK_PART.BOTTOM).with(FACING, Direction.WEST), Block.NOTIFY_ALL);
                    world.setBlockState(pos.north(), state.with(PART, BLOCK_PART.TOP).with(FACING, Direction.WEST), Block.NOTIFY_ALL);
                }
            }
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, net.minecraft.block.BlockState> builder) {
        builder.add(Properties.HORIZONTAL_FACING, SUM, CARRY, HALFSUM, PART);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx)
    {
        World world = ctx.getWorld();
        BlockPos midPos = ctx.getBlockPos();
        BlockState midState = this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());

        Direction topDir = midState.get(FACING).rotateYClockwise();
        BlockPos topPos = midPos.offset(topDir);
        BlockState topState = this.getDefaultState().with(FACING, midState.get(FACING));
        boolean top = topState.canPlaceAt(world,topPos) && world.getBlockState(topPos).canReplace(ctx);

        BlockPos bottomPos = midPos.offset(topDir.getOpposite());
        BlockState bottomState = this.getDefaultState().with(FACING, midState.get(FACING));
        boolean bottom = bottomState.canPlaceAt(world,bottomPos) && world.getBlockState(bottomPos).canReplace(ctx);
        if (!top || !bottom){
            return null;
        }
        else {
            BlockState state = getDefaultState();
            state = state.with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
            return state;
        }
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {

        boolean validPlace = !canPlaceAt(state,world, pos);
        boolean sideExists;
        Direction dir = state.get(FACING);
        if (state.get(PART) == BLOCK_PART.MIDDLE){
            BlockState top = world.getBlockState(pos.offset(dir.rotateYClockwise()));
            BlockState bottom = world.getBlockState(pos.offset(dir.rotateYCounterclockwise()));
            sideExists = top.isOf(this) && bottom.isOf(this);
        }
        else if (state.get(PART) == BLOCK_PART.TOP){
            BlockState mid = world.getBlockState(pos.offset(dir.rotateYCounterclockwise()));
            sideExists = mid.isOf(this);
        }
        else {
            BlockState mid = world.getBlockState(pos.offset(dir.rotateYClockwise()));
            sideExists = mid.isOf(this);
        }
        if (sideExists && validPlace){
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(state,direction,neighborState,world,pos,neighborPos);
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player){
        Direction direction = state.get(FACING);
        BlockPos topPos = null;
        BlockPos bottomPos = null;
        BlockPos midPos = null;
        if (state.get(PART) == BLOCK_PART.MIDDLE){
            topPos = pos.offset(state.get(FACING).rotateYClockwise());
            bottomPos = pos.offset(state.get(FACING).rotateYCounterclockwise());
            midPos = pos;
        }
        else if (state.get(PART) == BLOCK_PART.TOP){
            topPos = pos;
            bottomPos = pos.offset(direction.rotateYCounterclockwise(), 2);
            midPos = pos.offset(direction.rotateYCounterclockwise());
        }
        else{
            topPos = pos.offset(direction.rotateYClockwise(), 2);
            bottomPos = pos;
            midPos = pos.offset(direction.rotateYClockwise());
        }
        world.breakBlock(midPos,!player.isCreative(),player,1);
        world.breakBlock(topPos,false,player,1);
        world.breakBlock(bottomPos,false,player,1);
            /*
            BlockPos top = findEnd(world,pos,state);
            BlockState Top = world.getBlockState(top);
            BlockPos next;
            while (Top.get(PART) != BLOCK_PART.BOTTOM && Top.isOf(this)){
                next = top.offset(direction);
                world.setBlockState(top, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);//| Block.SKIP_DROPS);
                world.syncWorldEvent(player, WorldEvents.BLOCK_BROKEN, top, Block.getRawIdFromState(Top));
                top = next;
                Top = world.getBlockState(top);
            }
            if (Top.isOf(this)) {
                world.setBlockState(top, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
                world.syncWorldEvent(player, WorldEvents.BLOCK_BROKEN, top, Block.getRawIdFromState(Top));
            }
        } else {
            BLOCK_PART Start = state.get(PART);
            BLOCK_PART End = BLOCK_PART.BOTTOM;
            if (Start == BLOCK_PART.BOTTOM){
                direction = direction.getOpposite();
                End = BLOCK_PART.TOP;
            }
            BlockPos current = pos;
            BlockState Current = world.getBlockState(pos);
            BlockPos next;
            while (Current.isOf(this) && Current.get(PART) != End){
                next = current.offset(direction);
                world.setBlockState(current, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);// | Block.SKIP_DROPS);
                world.syncWorldEvent(player, WorldEvents.BLOCK_BROKEN, current, Block.getRawIdFromState(Current));
                current = next;
                Current = world.getBlockState(current);
            }
            if (Current.isOf(this)) {
                world.setBlockState(current, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
                world.syncWorldEvent(player, WorldEvents.BLOCK_BROKEN, current, Block.getRawIdFromState(Current));
            }
        }*/
        return super.onBreak(world,pos,state,player);
    }

    public BlockPos findEnd(World world, BlockPos pos, BlockState state){
        Direction direction = state.get(FACING).rotateYClockwise();
        BlockPos top = pos.offset(direction);
        BlockState Top = world.getBlockState(top);
        while (Top.get(PART) != BLOCK_PART.TOP && Top.isOf(this)){
            top = pos.offset(direction);
            Top = world.getBlockState(top);
        }
        return top;
    }

    public int getSideInput(World world, BlockState state, BlockPos pos){
        Direction redstoneDir = state.get(FACING);
        BlockPos redstonePos = pos.offset(redstoneDir);
        int output = getInput(world,redstonePos,redstoneDir);
        return output;
    }
}
