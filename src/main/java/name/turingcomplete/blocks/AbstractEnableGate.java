package name.turingcomplete.blocks;

import com.mojang.serialization.MapCodec;
import name.turingcomplete.init.propertyInit;
import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;


public abstract class AbstractEnableGate extends AbstractRedstoneGateBlock{
    public static final MapCodec<ComparatorBlock> CODEC = createCodec(ComparatorBlock::new);
    public static final BooleanProperty POWERED = Properties.POWERED;
    public static final BooleanProperty ENABLED = Properties.ENABLED;
    public static final BooleanProperty SWAPPED_DIR = propertyInit.SWAPPED_DIR;

    // constructor
    public AbstractEnableGate(Settings settings) {
        super(settings);

        setDefaultState(getDefaultState().with(POWERED, false).with(ENABLED, false).with(SWAPPED_DIR, false));
    }

    // When the logic gate is placed, the target and itself is updated, so a block update
    // is not necessary for the logic gate to work
    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        this.updateTarget(world, pos, state);
        this.updatePowered(world,pos,state);
    }

    // don't worry about this, but it is important
    public MapCodec<ComparatorBlock> getCodec() {
        return CODEC;
    }

    // defines the special placement properties that can be set later
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(Properties.HORIZONTAL_FACING, POWERED, ENABLED, SWAPPED_DIR);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        boolean swapped = state.get(SWAPPED_DIR);
        state = state.with(SWAPPED_DIR, !swapped);
        state = state.with(POWERED, hasPower(world,pos, state));
        state = state.with(ENABLED, hasEnable(world,pos,state));
        if (swapped) {
            world.playSound(player, pos, SoundEvents.BLOCK_COMPARATOR_CLICK, SoundCategory.BLOCKS, 0.3F, 0.5F);
        }
        else {
            world.playSound(player,pos,SoundEvents.BLOCK_COMPARATOR_CLICK, SoundCategory.BLOCKS, 0.3F, 0.55F);
        }
        this.updateTarget(world, pos, state);
        return ActionResult.SUCCESS;
    }

    // hitbox for the logic gate
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        return VoxelShapes.cuboid(0, 0, 0, 1, 0.125, 1);
    }

    // Gets information about how the logic gate should be placed (direction and powered state)
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx)
    {
        BlockState state = getDefaultState();
        state = state.with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
        return state;
    }

    // Determines how long the logic gate waits before acting (1 tick repeater = return 2)
    @Override
    protected int getUpdateDelayInternal(BlockState state) {
        return 0;
    }

    // Calls gateConditionsMet() to determine if an output should be on
    @Override
    protected boolean hasPower(World world, BlockPos pos, BlockState state) {
        return gateConditionsMet(state, world, pos);
    }

    public abstract boolean hasEnable(World world, BlockPos pos, BlockState state);


    //=============================================

    // used by RedstoneWireBlockMixin to determine where redstone should connect
    public Boolean dustConnectsToThis(BlockState state, Direction dir) {
        //get gate state dir
        Direction face_front = state.get(FACING);
        Direction enable_side = getGateSideDir(state, 0);
        if (state.get(SWAPPED_DIR)){
            enable_side = getGateSideDir(state, 1);
        }

        //return connect values
        if (dir == enable_side || dir == face_front || dir == face_front.getOpposite()){
            return true;
        }
        else if (dir == enable_side.getOpposite()){
            return false;
        }
        return null;
    }

    //=============================================

    // uses int left more so as a boolean, 0 means turn to the right,
    // 1 means turn to the left.
    @Nullable
    public Direction getGateSideDir(BlockState state, int left)
    {
        //get direction
        Direction sideDir = state.get(FACING);

        //rotate front direction
        if(left == 1) sideDir = sideDir.rotateYClockwise();
        else sideDir = sideDir.rotateYCounterclockwise();

        //return
        return sideDir;
    }

    // gets the input and returns 0 if there is no input, and returns 15 (max redstone level)
    // if there is an input
    protected int getInput(WorldView world, BlockPos pos, Direction dir)
    {
        BlockState blockState = world.getBlockState(pos);
        boolean a =
                blockState.getWeakRedstonePower(world, pos, dir) +
                        blockState.getStrongRedstonePower(world, pos, dir) > 0;
        boolean b = world.getEmittedRedstonePower(pos,dir) > 0;
        if (!a && !b) {
            return 0;
        }
        else{
            return 15;
        }
    }

    // Calls getInput() for the side of the block, again using right as a boolean
    protected int getSideInputLevel(BlockState state, WorldView world, BlockPos pos, int left)
    {
        //get side dir
        Direction sideDir = getGateSideDir(state, left);
        if(sideDir == null) return 0;

        //get input level
        BlockPos sidePos = pos.offset(sideDir);
        return getInput(world, sidePos, sideDir);
    }

    // Does the same as function getSideInputLevel(), but for front direction.
    protected int getFrontInputLevel(BlockState state, WorldView world, BlockPos pos)
    {
        //get side dir
        Direction frontDir = getGateFrontDir(state);
        if(frontDir == null) return 0;

        //get input level
        BlockPos sidePos = pos.offset(frontDir);
        return getInput(world, sidePos, frontDir);
    }

    // gets the direction for the back input (front and back
    // are confusing with redstone gates) if supportsBackDirection() returns true
    @Nullable
    public Direction getGateFrontDir(BlockState state)
    {
        //return
        return state.get(FACING);
    }



    //===============================================================================

    // Abstract function to be overridden by the logic gates with their own logic
    public abstract boolean gateConditionsMet(BlockState state, World world, BlockPos pos);
}
