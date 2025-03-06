package name.turingcomplete.blocks;

import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.MapCodec;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ComparatorBlock;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.SideShapeType;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

//anything on a logic plate
public abstract class AbstractLogicBlock extends HorizontalFacingBlock implements ConnectsToRedstone{
    public static final BooleanProperty MIRRORED = BooleanProperty.of("mirrored");

    private static final MapCodec<ComparatorBlock> CODEC = createCodec(ComparatorBlock::new);

    protected AbstractLogicBlock(Settings settings) {
        super(settings);
        if(canMirror()) {
            setDefaultState(getDefaultState()
            .with(MIRRORED,false));
        }
        
    }
    
    //warning: value must not change
    public boolean canMirror(){
        return false;
    }

    //main* naming because on a multiblock, this should only be called on the main block
    public boolean canMirrorHere(BlockPos mainPos, BlockState mainState){
        return false;
    }

    @Override
    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos)
    {
        return this.canPlaceAbove(world, pos.down(), world.getBlockState(pos.down()));
    }

    public static final int getInputStrength(World world, BlockPos pos, BlockState state, RelativeSide inputSide) {
        Direction checkDirection = inputSide.onDirection(state.get(FACING).getOpposite());
        BlockPos checkPos = pos.offset(checkDirection);
        return world.getEmittedRedstonePower(checkPos,checkDirection);
    }

    public static final boolean getInputActive(World world, BlockPos pos, BlockState state, RelativeSide inputSide) {
        return getInputStrength(world,pos,state,inputSide) > 0;
    }

    @Override
    protected MapCodec<? extends HorizontalFacingBlock> getCodec() {return CODEC;}

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx)
    {
        BlockState state = getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
        if (canMirror()) {
            state = state.with(MIRRORED,false);
        }
        return state;
    }

    @Override
    @MustBeInvokedByOverriders
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        builder.add(FACING);
        if(canMirror()) {
            builder.add(MIRRORED);
        }
    }

    @Override
    protected int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction)
    {
        return getWeakRedstonePower(state, world, pos, direction);
    }

    @Override
    @MustBeInvokedByOverriders
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (!canPlaceAt(state,world, pos)) {
            BlockEntity blockEntity = state.hasBlockEntity() ? world.getBlockEntity(pos) : null;
            dropStacks(state, world, pos, blockEntity);

            world.removeBlock(pos, false);

            for (Direction direction : DIRECTIONS)
                world.updateNeighborsAlways(pos.offset(direction), this);
        }
    }

    private boolean canPlaceAbove(WorldView world, BlockPos pos, BlockState state)
    {
        return state.isSideSolid(world, pos, Direction.UP, SideShapeType.RIGID);
    }
}
