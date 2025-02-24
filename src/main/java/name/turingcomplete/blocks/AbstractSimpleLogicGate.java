package name.turingcomplete.blocks;

import name.turingcomplete.init.propertyInit;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
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
        if (updateBlockColor(state, world, pos, player.getMainHandStack())) {
            return ActionResult.SUCCESS_NO_ITEM_USED;
        }

        if (!state.contains(propertyInit.SWAPPED_DIR)) return ActionResult.PASS;

        BlockState new_state = state.with(propertyInit.SWAPPED_DIR,!state.get(propertyInit.SWAPPED_DIR));

        if (state.get(propertyInit.SWAPPED_DIR))
            world.playSound(player, pos, SoundEvents.BLOCK_COMPARATOR_CLICK, SoundCategory.BLOCKS, 0.3F, 0.5F);
        else world.playSound(player,pos,SoundEvents.BLOCK_COMPARATOR_CLICK, SoundCategory.BLOCKS, 0.3F, 0.55F);

        updateImmediate(world,pos,new_state.with(POWERED, gateConditionMet(world, pos, new_state)));
        updateTarget(world,pos,state);
        return ActionResult.SUCCESS_NO_ITEM_USED;
    }

    protected boolean updateBlockColor(BlockState state, World world, BlockPos pos, ItemStack item) {
        DyeColor newColor = null;
        if (item.isIn(TagKey.of(RegistryKeys.ITEM, Identifier.of("c", "dyes/white")))) {
            newColor = DyeColor.WHITE;
        } else if (item.isIn(TagKey.of(RegistryKeys.ITEM, Identifier.of("c", "dyes/orange")))) {
            newColor = DyeColor.ORANGE;
        } else if (item.isIn(TagKey.of(RegistryKeys.ITEM, Identifier.of("c", "dyes/magenta")))) {
            newColor = DyeColor.MAGENTA;
        } else if (item.isIn(TagKey.of(RegistryKeys.ITEM, Identifier.of("c", "dyes/light_blue")))) {
            newColor = DyeColor.LIGHT_BLUE;
        } else if (item.isIn(TagKey.of(RegistryKeys.ITEM, Identifier.of("c", "dyes/yellow")))) {
            newColor = DyeColor.YELLOW;
        } else if (item.isIn(TagKey.of(RegistryKeys.ITEM, Identifier.of("c", "dyes/lime")))) {
            newColor = DyeColor.LIME;
        } else if (item.isIn(TagKey.of(RegistryKeys.ITEM, Identifier.of("c", "dyes/pink")))) {
            newColor = DyeColor.PINK;
        } else if (item.isIn(TagKey.of(RegistryKeys.ITEM, Identifier.of("c", "dyes/gray")))) {
            newColor = DyeColor.GRAY;
        } else if (item.isIn(TagKey.of(RegistryKeys.ITEM, Identifier.of("c", "dyes/light_gray")))) {
            newColor = DyeColor.LIGHT_GRAY;
        } else if (item.isIn(TagKey.of(RegistryKeys.ITEM, Identifier.of("c", "dyes/cyan")))) {
            newColor = DyeColor.CYAN;
        } else if (item.isIn(TagKey.of(RegistryKeys.ITEM, Identifier.of("c", "dyes/purple")))) {
            newColor = DyeColor.PURPLE;
        } else if (item.isIn(TagKey.of(RegistryKeys.ITEM, Identifier.of("c", "dyes/blue")))) {
            newColor = DyeColor.BLUE;
        } else if (item.isIn(TagKey.of(RegistryKeys.ITEM, Identifier.of("c", "dyes/brown")))) {
            newColor = DyeColor.BROWN;
        } else if (item.isIn(TagKey.of(RegistryKeys.ITEM, Identifier.of("c", "dyes/green")))) {
            newColor = DyeColor.GREEN;
        } else if (item.isIn(TagKey.of(RegistryKeys.ITEM, Identifier.of("c", "dyes/red")))) {
            newColor = DyeColor.RED;
        } else if (item.isIn(TagKey.of(RegistryKeys.ITEM, Identifier.of("c", "dyes/black")))) {
            newColor = DyeColor.BLACK;
        }

        if (newColor == null || state.get(COLOR) == newColor) {return false;}

        updateImmediate(world, pos, state.with(COLOR, newColor));
        return true;
    }

    public static int getBlockColor(BlockState state, int tintIndex) {
        DyeColor colorState = state.get(COLOR);
        return state.get(POWERED)? colorState.getSignColor() : colorState.getFireworkColor();
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
