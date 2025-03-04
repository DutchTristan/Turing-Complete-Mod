package name.turingcomplete.blocks.block;

import org.jetbrains.annotations.Nullable;

import name.turingcomplete.blocks.AbstractSimpleGate;
import name.turingcomplete.blocks.RelativeSide;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;


public class NOT_Gate_Block extends AbstractSimpleGate {

    public NOT_Gate_Block(Settings settings) {
        super(settings);
    }

    @Override
    protected boolean evaluateGate(World world, BlockPos gatePos, BlockState gateState) {
        boolean back = getInputActive(world, gatePos, gateState,RelativeSide.BACK);
        return !back;
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState state = super.getPlacementState(ctx);
        state = state.with(POWERED, true);
        return state;
    }
    
    @Override
    public Boolean dustConnectsToThis(BlockState gateState, Direction direction){
        return direction.getAxis() == gateState.get(FACING).getAxis();
    }
}