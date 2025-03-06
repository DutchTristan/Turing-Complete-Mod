package name.turingcomplete.blocks.block;

import name.turingcomplete.blocks.AbstractSimpleLogicGate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import static net.minecraft.state.property.Properties.POWERED;

public class T_LATCH_Block extends AbstractSimpleLogicGate {
    private static final BooleanProperty ENABLED = BooleanProperty.of("enabled");

    public T_LATCH_Block(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState()
                .with(ENABLED,false)
        );
    }

    @Override
    protected void properties(StateManager.Builder<Block, BlockState> builder)
    {super.properties(builder);     builder.add(ENABLED);}

    @Override
    public boolean gateConditionMet( World world, BlockPos pos, BlockState state) {
        boolean toggle = isInputPowered(world,state,pos,InputDirection.BACK);
        boolean powered = state.get(POWERED);

        if (toggle) return !powered;

        return powered;
    }

    @Override
    protected boolean shouldUpdate(World world, BlockState state, BlockPos pos) {
        return isInputPowered(world,state,pos,InputDirection.BACK) && !state.get(ENABLED)
                && !world.getBlockTickScheduler().isTicking(pos, this);
    }

    @Override
    protected void updateImmediate(World world, BlockPos pos, BlockState state) {
        world.setBlockState(pos,state.with(ENABLED, isInputPowered(world,state,pos,InputDirection.BACK)));
    }

    @Override
    protected boolean shouldUpdateImmediate(World world, BlockState state, BlockPos pos) {
        return state.get(ENABLED) != isInputPowered(world,state,pos,InputDirection.BACK);
    }

    @Override
    public boolean supportsSideDirection(BlockState state, Direction direction) {return false;}
    @Override
    public boolean supportsBackDirection() {return true;}

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        return ActionResult.PASS;
    }
}
