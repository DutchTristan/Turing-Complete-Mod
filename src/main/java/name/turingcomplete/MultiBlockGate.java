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
        Direction dir = state.get(FACING);
        if (direction == dir.rotateYCounterclockwise() && state.get(PART) == BLOCK_PART.MIDDLE){
            return neighborState.isOf(this) ? state : Blocks.AIR.getDefaultState();
        }
        else if (direction == dir.rotateYClockwise() && state.get(PART) == BLOCK_PART.MIDDLE){
            return neighborState.isOf(this) ? state : Blocks.AIR.getDefaultState();
        }
        else if (state.get(PART) == BLOCK_PART.TOP && direction == dir.rotateYCounterclockwise()){
            return neighborState.isOf(this) ? state : Blocks.AIR.getDefaultState();
        }
        else if (state.get(PART) == BLOCK_PART.BOTTOM && direction == dir.rotateYClockwise()){
            return neighborState.isOf(this) ? state : Blocks.AIR.getDefaultState();
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
        return super.onBreak(world,pos,state,player);
    }

    public int getSideInput(World world, BlockState state, BlockPos pos){
        Direction redstoneDir = state.get(FACING);
        BlockPos redstonePos = pos.offset(redstoneDir);
        int output = getInput(world,redstonePos,redstoneDir);
        return output;
    }
}
