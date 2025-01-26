package kosmek.turing_complete.block;

import kosmek.turing_complete.AbstractLogicGate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

import static kosmek.turing_complete.AbstractEnableGate.ENABLED;
import static kosmek.turing_complete.AbstractEnableGate.SWAPPED_DIR;

public class MEMORY_Cell_Block extends AbstractLogicGate {

    public MEMORY_Cell_Block(Settings settings) {
        super(settings);
    }
    public static final BooleanProperty ENABLED = Properties.ENABLED;
    @Override
    public boolean gateConditionsMet(BlockState thisBlockState, World world, BlockPos pos) {
        boolean back = getFrontInputLevel(thisBlockState, world, pos) > 0;
        if (hasEnable(world, pos, thisBlockState)){
            return back;
        }
        return thisBlockState.get(POWERED);
    }
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx)
    {
        BlockState state = getDefaultState().with(ENABLED,false).with(POWERED,false);
        state = state.with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
        state = state.with(POWERED, hasPower(ctx.getWorld(), ctx.getBlockPos(), state));
        state = state.with(ENABLED, hasEnable(ctx.getWorld(),ctx.getBlockPos(), state));
        return state;
    }

    public boolean hasEnable(World world, BlockPos pos, BlockState state){
        boolean left = !state.get(SWAPPED_DIR);
        int leftParameter = 0;
        if (left){
            leftParameter = 1;
        }
        if (getSideInputLevel(state, world, pos, leftParameter) > 0){
            world.setBlockState(pos, state.with(ENABLED, true));
            return true;
        }
        world.setBlockState(pos, state.with(ENABLED, false));
        return false;
    }
}