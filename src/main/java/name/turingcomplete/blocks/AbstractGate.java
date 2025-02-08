package name.turingcomplete.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractGate extends HorizontalFacingBlock implements ConnectsToRedstone{
    private static final MapCodec<ComparatorBlock> CODEC = createCodec(ComparatorBlock::new);
    private static final VoxelShape SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);

    protected AbstractGate(Settings settings) {super(settings);}

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getBlockPlacementState(ctx)
            .with(FACING,ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    protected MapCodec<? extends HorizontalFacingBlock> getCodec() {return CODEC;}
    @Override
    public Boolean dustConnectsToThis(BlockState state, Direction di) {
        return ( di.getAxis() != state.get(FACING).getAxis() && supportsSideDirection(state,di) ) ||
               ( di == state.get(FACING).getOpposite() && supportsBackDirection() ) ||
               ( di == state.get(FACING));
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {return SHAPE;}

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {builder.add(FACING);   properties(builder);}

    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos)
    {return this.canPlaceAbove(world, pos.down(), world.getBlockState(pos.down()));}

    private boolean canPlaceAbove(WorldView world, BlockPos pos, BlockState state)
    {return state.isSideSolid(world, pos, Direction.UP, SideShapeType.RIGID);}

    //=============================================
    //=============================================

    public abstract boolean supportsSideDirection(BlockState state, Direction direction);
    public abstract boolean supportsBackDirection();
    protected abstract void properties(StateManager.Builder<Block, BlockState> builder);
    protected abstract BlockState getBlockPlacementState(ItemPlacementContext ctx);
    protected void updateImmediate(World world, BlockPos pos, BlockState state){world.setBlockState(pos,state);}

    protected int getUpdateDelayInternal(BlockState state) {return 2;}

    //=============================================
    //=============================================

    protected abstract void update(World world, BlockState state, BlockPos pos);
    protected abstract boolean shouldUpdate(World world, BlockState state, BlockPos pos);
    protected  boolean shouldUpdateImmediate(World world, BlockState state, BlockPos pos) {return false;}


    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {update(world,state,pos);}
    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (!canPlaceAt(state,world, pos)) {
            BlockEntity blockEntity = state.hasBlockEntity() ? world.getBlockEntity(pos) : null;
            dropStacks(state, world, pos, blockEntity);

            world.removeBlock(pos, false);

            for (Direction direction : DIRECTIONS)
                world.updateNeighborsAlways(pos.offset(direction), this);

            return;
        }


        if (shouldUpdateImmediate(world, state, pos))
            updateImmediate(world, pos, state);
        if (shouldUpdate(world,state,pos))
            world.scheduleBlockTick(pos,this, getUpdateDelayInternal(state));
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (shouldUpdateImmediate(world, state, pos))
            updateImmediate(world, pos, state);
        if (shouldUpdate(world,state,pos))
            world.scheduleBlockTick(pos,this, getUpdateDelayInternal(state));
    }

    protected void updateTarget(World world, BlockPos pos, BlockState state) {
        Direction direction = state.get(FACING);
        BlockPos blockPos = pos.offset(direction.getOpposite());

        world.updateNeighbor(blockPos, this, pos);
        world.updateNeighborsExcept(blockPos, this, direction);
    }

    //=============================================
    //=============================================


    protected int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction)
    {return getWeakRedstonePower(state, world, pos, direction);}

    protected abstract boolean gateConditionMet(World world, BlockPos pos, BlockState state);
    protected boolean isInputPowered(World world,BlockState state , BlockPos pos, InputDirection in) {
        Direction direction = in.getRelativeDirection(state.get(FACING));
        int power = world.getEmittedRedstonePower(pos.offset(direction),direction);
        return power > 0;
    }

    //=============================================
    //=============================================

    protected enum InputDirection{
        LEFT(Direction.EAST.asRotation()), RIGHT(Direction.WEST.asRotation()), BACK(0f);

        private final float rotation;
        InputDirection(float rotation) {this.rotation = rotation;}
        public Direction getRelativeDirection(Direction facing){
            return Direction.fromRotation(facing.asRotation() + this.rotation);
        }

        public InputDirection getOpposite(){
            if(this == LEFT) return RIGHT;
            if(this == RIGHT) return LEFT;

            return BACK;
        }
    }
}
