package name.turingcomplete.blocks;

import com.mojang.serialization.MapCodec;
import name.turingcomplete.init.propertyInit;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;


public abstract class Abstract2WayGate extends HorizontalFacingBlock implements ConnectsToRedstone {
    private static final MapCodec<ComparatorBlock> CODEC = createCodec(ComparatorBlock::new);

    public static final BooleanProperty POWERED_Z = propertyInit.POWERED_Z;
    public static final BooleanProperty POWERED_X = propertyInit.POWERED_X;

    public static final BooleanProperty SWAPPED_DIR = propertyInit.SWAPPED_DIR;

    protected Abstract2WayGate(Settings settings) {
        super(settings);

        setDefaultState(getDefaultState()
                .with(POWERED_X,false)
                .with(POWERED_Z,false)
                .with(SWAPPED_DIR,false)
                .with(FACING,Direction.NORTH)
        );
    }

    @Override
    protected MapCodec<? extends HorizontalFacingBlock> getCodec() {return CODEC;}

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(
                SWAPPED_DIR,FACING,POWERED_X,POWERED_Z
        );
    }

    @Override
    protected boolean emitsRedstonePower(BlockState state) {return true;}

    //=============================================


    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        this.checkForUpdate(world,pos,state);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState state = getDefaultState();
        state = state.with(FACING, ctx.getHorizontalPlayerFacing());
        return state;
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        boolean swapped = state.get(SWAPPED_DIR);
        state = state.with(SWAPPED_DIR, !swapped);

        if (swapped)
            world.playSound(player, pos, SoundEvents.BLOCK_COMPARATOR_CLICK, SoundCategory.BLOCKS, 0.3F, 0.5F);
        else
            world.playSound(player,pos,SoundEvents.BLOCK_COMPARATOR_CLICK, SoundCategory.BLOCKS, 0.3F, 0.55F);

        this.update(world, pos, state);
        this.updateShape(world,pos,state);
        return ActionResult.SUCCESS;
    }

    //=============================================

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (state.canPlaceAt(world, pos))
            this.checkForUpdate(world,pos,state);
        else {
            BlockEntity blockEntity = state.hasBlockEntity() ? world.getBlockEntity(pos) : null;

            dropStacks(state, world, pos, blockEntity);
            world.removeBlock(pos, false);

            Direction[] var8 = Direction.values();

            for (Direction direction : var8) {
                world.updateNeighborsAlways(pos.offset(direction), this);
            }
        }
    }

    public abstract void update(World world, BlockPos pos, BlockState state);
    protected void updateShape(World world,BlockPos pos, BlockState state){}
    protected abstract void checkForUpdate(World world, BlockPos pos, BlockState state);

    protected void updateTarget(World world, BlockPos pos, Direction direction) {
        BlockPos blockPos = pos.offset(direction.getOpposite());
        world.updateNeighbor(blockPos, this, pos);
        world.updateNeighborsExcept(blockPos, this, direction);
    }

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {update(world,pos,state);}

    //=============================================

    protected boolean isInputPowered(World world, BlockPos pos, Direction direction) {
        int power = world.getEmittedRedstonePower(pos.offset(direction),direction);
        return power > 0;
    }

    @Override
    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockPos blockPos = pos.down();
        return this.canPlaceAbove(world, blockPos, world.getBlockState(blockPos));
    }

    protected boolean canPlaceAbove(WorldView world, BlockPos pos, BlockState state) {
        return state.isSideSolid(world, pos, Direction.UP, SideShapeType.RIGID);
    }

    protected int getUpdateDelayInternal(BlockState state) {return 2;}

    @Override
    protected int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return this.getWeakRedstonePower(state, world, pos, direction);
    }

    @Override
    protected int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        Direction ForwardInputDirection = state.get(FACING).getOpposite();
        Direction SideInputDirection = state.get(FACING).rotateYCounterclockwise();

        if(state.get(SWAPPED_DIR)) SideInputDirection = state.get(FACING).rotateYClockwise();

        if (direction == ForwardInputDirection)
            return state.get(POWERED_Z) ? 15 : 0;
        else if (direction == SideInputDirection)
            return state.get(POWERED_X) ? 15 : 0;
        else
            return 0;
    }
}
