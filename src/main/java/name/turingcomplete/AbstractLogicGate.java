package name.turingcomplete;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;


public abstract class AbstractLogicGate extends AbstractRedstoneGateBlock{
    public static final MapCodec<ComparatorBlock> CODEC = createCodec(ComparatorBlock::new);
    public static final BooleanProperty POWERED = Properties.POWERED;

    public AbstractLogicGate(AbstractBlock.Settings settings) {
        super(settings);

        setDefaultState(getDefaultState());
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

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx)
    {
        BlockState state = getDefaultState();
        state = state.with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
        state = state.with(POWERED, hasPower(ctx.getWorld(), ctx.getBlockPos(), state));
        return state;
    }

    @Override
    protected int getUpdateDelayInternal(BlockState state) {
        return 0;
    }

    //=============================================

    @Override
    protected boolean hasPower(World world, BlockPos pos, BlockState state) {
        return gateConditionsMet(state, world, pos);
    }

    public Boolean dustConnectsToThis(BlockState state, Direction dir) {
        //get gate state dir
        Direction face_front = state.get(FACING);
        Direction left_side = getGateSideDir(state, 0);
        Direction right_side = getGateSideDir(state, 1);

        //return
        if (dir == left_side || dir == right_side){
            return supportsSideDirection();
        }
        if(dir == face_front) {
            return true;//supportsBackDirection();
        }
        else return supportsBackDirection();
    }

    public boolean supportsSideDirection() {
        return false;
    }

    public boolean supportsBackDirection(){
        return true;
    }

    @Nullable
    public Direction getGateSideDir(BlockState state, int right)
    {
        //get direction
        if(!supportsSideDirection()) return null;
        Direction sideDir = state.get(FACING);

        //rotate front direction
        if(right == 1) sideDir = sideDir.rotateYClockwise();
        else sideDir = sideDir.rotateYCounterclockwise();

        //return
        return sideDir;
    }


    protected int getInput(WorldView world, BlockPos pos, Direction dir)
    {
        BlockState blockState = world.getBlockState(pos);
        boolean a =
                blockState.getWeakRedstonePower(world, pos, dir) +
                        blockState.getStrongRedstonePower(world, pos, dir) > 0;
        if (!a) {
            return 0;
        }
        else{
            return 15;
        }
    }

    protected int getSideInputLevel(BlockState state, WorldView world, BlockPos pos, int right)
    {
        //get side dir
        Direction sideDir = getGateSideDir(state, right);
        if(sideDir == null) return 0;

        //get input level
        BlockPos sidePos = pos.offset(sideDir);
        return getInput(world, sidePos, sideDir);
    }

    //===============================================================================

    public String getBlockIdPath() {
        return null;
    }

    public abstract boolean gateConditionsMet(BlockState state, World world, BlockPos pos);
}
