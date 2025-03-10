package name.turingcomplete.blocks.block;

import org.jetbrains.annotations.Nullable;

import name.turingcomplete.blocks.AbstractSimpleGate;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;


public class NOR_Gate_Block extends AbstractSimpleGate {

    public NOR_Gate_Block(Settings settings) {
        super(settings);
    }

    @Override
    protected boolean evaluateGate(World world, BlockPos gatePos, BlockState gateState) {
        boolean left = getInputActive(world, gatePos, gateState,RelativeSide.LEFT);
        boolean right = getInputActive(world, gatePos, gateState,RelativeSide.RIGHT);
        return !(left || right);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState state = super.getPlacementState(ctx);
        state = state.with(POWERED, true);
        return state;
    }
}