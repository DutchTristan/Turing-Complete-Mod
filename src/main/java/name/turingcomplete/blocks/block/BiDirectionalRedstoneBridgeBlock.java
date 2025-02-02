package name.turingcomplete.blocks.block;

import name.turingcomplete.blocks.AbstractGate;
import name.turingcomplete.init.propertyInit;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class BiDirectionalRedstoneBridgeBlock extends AbstractGate {
    private static final BooleanProperty POWERED_X = propertyInit.POWERED_X;
    private static final BooleanProperty POWERED_Z = propertyInit.POWERED_Z;
    private static final BooleanProperty SWAP = propertyInit.SWAPPED_DIR;


    public BiDirectionalRedstoneBridgeBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState()
                .with(POWERED_X,false)
                .with(POWERED_Z, false)
                .with(SWAP,false)
        );
    }

    @Override
    public boolean supportsSideDirection(BlockState state, Direction direction)
    {return true;}
    public boolean supportsBackDirection()
    {return true;}

    @Override
    protected void properties(StateManager.Builder<Block, BlockState> builder)
    {builder.add(POWERED_X,POWERED_Z,SWAP);}


    protected BlockState getBlockPlacementState(ItemPlacementContext ctx) {return getDefaultState();}
    protected boolean gateConditionMet(World world, BlockPos pos, BlockState state) {return true;}

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!state.contains(propertyInit.SWAPPED_DIR)) return ActionResult.PASS;

        BlockState new_state = state.with(propertyInit.SWAPPED_DIR,!state.get(propertyInit.SWAPPED_DIR));

        if (state.get(propertyInit.SWAPPED_DIR))
            world.playSound(player, pos, SoundEvents.BLOCK_COMPARATOR_CLICK, SoundCategory.BLOCKS, 0.3F, 0.5F);
        else world.playSound(player,pos,SoundEvents.BLOCK_COMPARATOR_CLICK, SoundCategory.BLOCKS, 0.3F, 0.55F);

        this.update(world,new_state,pos);
        return ActionResult.SUCCESS_NO_ITEM_USED;
    }

    @Override
    protected int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        Direction SideInputDirection = getSideInputDirection(state).getRelativeDirection(state.get(FACING));
        Direction ForwardInputDirection = state.get(FACING);

        if (direction == ForwardInputDirection)
            return state.get(POWERED_Z) ? 15 : 0;
        else if (direction == SideInputDirection)
            return state.get(POWERED_X) ? 15 : 0;
        else
            return 0;
    }

    //=============================================
    //=============================================

    @Override
    public void update(World world, BlockState state, BlockPos pos) {
        Direction side_direction = getSideInputDirection(state).getRelativeDirection(state.get(FACING));

        boolean should_power_z = isInputPowered(world, state, pos, InputDirection.BACK);
        boolean should_power_x = isInputPowered(world, state, pos, getSideInputDirection(state));

        BlockState newState = state;

        if(state.get(POWERED_X) != should_power_x)
            newState = newState.with(POWERED_X, should_power_x);
        if(state.get(POWERED_Z) != should_power_z)
            newState = newState.with(POWERED_Z, should_power_z);

        world.setBlockState(pos, newState);

        updateTarget(world,pos,state);
        updateSideTarget(world,pos,state);
    }

    protected void updateSideTarget(World world, BlockPos pos, BlockState state) {
        Direction direction = getSideInputDirection(state).getRelativeDirection(state.get(FACING));
        BlockPos blockPos = pos.offset(direction.getOpposite());

        world.updateNeighbor(blockPos, this, pos);
        world.updateNeighborsExcept(blockPos, this, direction);
    }

    @Override
    protected boolean shouldUpdate(World world, BlockState state, BlockPos pos) {
        boolean should_power_z = isInputPowered(world, state, pos, InputDirection.BACK);
        boolean should_power_x = isInputPowered(world,state, pos, getSideInputDirection(state));

        BlockState newState = state;

        if(state.get(POWERED_X) != should_power_x)
            newState = newState.with(POWERED_X, should_power_x);
        if(state.get(POWERED_Z) != should_power_z)
            newState = newState.with(POWERED_Z, should_power_z);

        if(newState != state)
            return true;

        return false;
    }
    private InputDirection getSideInputDirection(BlockState state){
        return state.get(SWAP) ? InputDirection.LEFT : InputDirection.RIGHT;
    }
}
