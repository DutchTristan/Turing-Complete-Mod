package name.turingcomplete;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
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
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.tick.TickPriority;

public abstract class MultiBlockGate extends AbstractLogicGate{
    public static final EnumProperty<BLOCK_PART> PART = EnumProperty.of("part", BLOCK_PART.class);


    public static final BooleanProperty SUM = Properties.POWERED;
    public static final BooleanProperty CARRY = Properties.ENABLED;
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
        BlockState state = getDefaultState();
        state = state.with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
        return state;
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player){
        Direction direction = state.get(FACING).rotateYCounterclockwise();
        if (state.get(PART) != BLOCK_PART.TOP && state.get(PART) != BLOCK_PART.BOTTOM){
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
        }
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

    @Override
    protected boolean hasPower(World world, BlockPos pos, BlockState state) {
        //boolean b = hasEnable(world,pos,state);
        return gateConditionsMet(state, world, pos) ;//&& !b;
    }


}
