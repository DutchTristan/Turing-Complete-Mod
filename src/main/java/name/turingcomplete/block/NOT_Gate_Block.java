package name.turingcomplete.block;

import com.mojang.serialization.MapCodec;
import name.turingcomplete.init.blockEntityTypeInit;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.RedstoneView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import net.minecraft.state.property.BooleanProperty;

public class NOT_Gate_Block extends AbstractRedstoneGateBlock{


    public static final MapCodec<ComparatorBlock> CODEC = createCodec(ComparatorBlock::new);
    public static final BooleanProperty POWERED = Properties.POWERED;
    public static final BooleanProperty SWAPPED_DIR = BooleanProperty.of("swapped_direction");

    public NOT_Gate_Block(Settings settings) {
        super(settings);

        setDefaultState(getDefaultState().with(POWERED, true));
        if(supportsSideDirection())
            setDefaultState(getDefaultState().with(SWAPPED_DIR, false));
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        this.updateTarget(world, pos, state);
    }

    public MapCodec<ComparatorBlock> getCodec() {
        return CODEC;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, net.minecraft.block.BlockState> builder) {
        builder.add(Properties.HORIZONTAL_FACING, Properties.POWERED);
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        return VoxelShapes.cuboid(0, 0, 0, 1, 0.125, 1);
    }

    public BlockState getPlacementState(ItemPlacementContext context) {
        return super.getPlacementState(context).with(Properties.HORIZONTAL_FACING, context.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    protected int getUpdateDelayInternal(BlockState state) {
        return 0;
    }


    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return blockEntityTypeInit.NOT_GATE.instantiate(pos, state);
    }

    @Override
    protected boolean hasPower(World world, BlockPos pos, BlockState state) {
        Direction frontDir = state.get(FACING);
        BlockPos frontPos = pos.offset(frontDir);
        int i = this.getPower(world, pos, state);
        return i == 0;
    }

    @Override
    protected int getMaxInputLevelSides(RedstoneView world, BlockPos pos, BlockState state) {
        return 0;
    }

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (hasPower(world, pos, state)) {
            world.setBlockState(pos, (BlockState)state.with(POWERED, true));
        } else {
            world.setBlockState(pos, (BlockState)state.with(POWERED, false));
        }

        super.scheduledTick(state, world, pos, random);
    }

    @Override
    protected boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    protected int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return super.getWeakRedstonePower(state, world, pos, direction);
    }

    @Override
    protected int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return super.getStrongRedstonePower(state, world, pos, direction);
    }

    public Boolean dustConnectsToThis(BlockState state, Direction dir) {
        //get gate state dir
        Direction face_front = state.get(FACING);
        Direction face_side = getGateSideDir(state);

        //check front and back, and check side direction
        boolean a = (dir == face_front.getOpposite());
        boolean b = (dir == face_front || a);
        boolean c = (supportsSideDirection() && dir == face_side.getOpposite());

        //return
        if(b || c) return true;
        else return false;
    }

    private boolean supportsSideDirection() {
        return false;
    }

    @Nullable
    public Direction getGateSideDir(BlockState state)
    {
        //get direction
        if(!supportsSideDirection()) return null;
        Direction sideDir = state.get(FACING);

        //rotate front direction
        if(state.get(SWAPPED_DIR)) sideDir = sideDir.rotateYClockwise();
        else sideDir = sideDir.rotateYCounterclockwise();

        //return
        return sideDir;
    }
}
