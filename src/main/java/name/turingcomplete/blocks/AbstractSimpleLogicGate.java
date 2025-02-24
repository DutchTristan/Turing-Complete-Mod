package name.turingcomplete.blocks;

import name.turingcomplete.init.propertyInit;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.tick.TickPriority;

public abstract class AbstractSimpleLogicGate extends AbstractGate{
    private static final EnumProperty<DyeColor> COLOR = EnumProperty.of("color", DyeColor.class);
    protected static final BooleanProperty POWERED = Properties.POWERED;

    protected AbstractSimpleLogicGate(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(COLOR, DyeColor.RED));
        setDefaultState(getDefaultState().with(POWERED,false));
    }

    @Override
    protected void properties(StateManager.Builder<Block, BlockState> builder) {builder.add(COLOR, POWERED);}

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!state.contains(propertyInit.SWAPPED_DIR)) return ActionResult.PASS;

        BlockState new_state = state.with(propertyInit.SWAPPED_DIR,!state.get(propertyInit.SWAPPED_DIR));

        if (state.get(propertyInit.SWAPPED_DIR))
            world.playSound(player, pos, SoundEvents.BLOCK_COMPARATOR_CLICK, SoundCategory.BLOCKS, 0.3F, 0.5F);
        else world.playSound(player,pos,SoundEvents.BLOCK_COMPARATOR_CLICK, SoundCategory.BLOCKS, 0.3F, 0.55F);

        updateImmediate(world,pos,new_state.with(POWERED, gateConditionMet(world, pos, new_state)));
        updateTarget(world,pos,state);
        return ActionResult.SUCCESS_NO_ITEM_USED;
    }

    //=============================================
    //=============================================

    protected int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction)
    {return direction == state.get(FACING) && state.get(POWERED) ? 15 : 0;}

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return super.getPlacementState(ctx)
                .with(POWERED, gateConditionMet(ctx.getWorld(),ctx.getBlockPos(),super.getPlacementState(ctx))
        );
    }


    @Override
    protected boolean shouldUpdate(World world, BlockState state, BlockPos pos)
    {return gateConditionMet(world, pos, state) != state.get(POWERED) && !world.getBlockTickScheduler().isTicking(pos, this);}

    //=============================================
    //=============================================

    @Override
    protected void update(World world, BlockState state, BlockPos pos) {
        boolean powered = state.get(POWERED);
        boolean has_power = this.gateConditionMet(world, pos, state);

        if (powered && !has_power) {
            world.setBlockState(pos, state.with(POWERED, false), 2);
        } else if (!powered) {
            world.setBlockState(pos, state.with(POWERED, true), 2);
            if (!has_power) {
                world.scheduleBlockTick(pos, this, this.getUpdateDelayInternal(state), TickPriority.VERY_HIGH);
            }
        }

        updateTarget(world,pos,state);
    }
}
