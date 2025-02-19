package name.turingcomplete.blocks.block;

import name.turingcomplete.blocks.AbstractSimpleLogicGate;
import name.turingcomplete.init.propertyInit;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;

public class MEMORY_Cell_Block extends AbstractSimpleLogicGate {
    public static final BooleanProperty ENABLED = Properties.ENABLED;
    public static final BooleanProperty SWAP = propertyInit.SWAPPED_DIR;

    public MEMORY_Cell_Block(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState()
                .with(ENABLED,false)
                .with(SWAP,false)
        );
    }

    @Override
    protected void update(World world, BlockState state, BlockPos pos) {
        if (state.get(ENABLED) || world.getBlockState(pos).get(SWAP) != state.get(SWAP)) super.update(world,state,pos);
    }

    @Override
    protected void properties(StateManager.Builder<Block, BlockState> builder) {
        super.properties(builder);
        builder.add(ENABLED,SWAP);
    }

    @Override
    protected boolean gateConditionMet(World world, BlockPos pos, BlockState state) {
        boolean store = isInputPowered(world,state,pos,getStoreDirection(state));

        if (store) return isInputPowered(world,state,pos,InputDirection.BACK);

        return state.get(POWERED);
    }

    @Override
    protected void updateImmediate(World world, BlockPos pos, BlockState state) {
        boolean store = isInputPowered(world,state,pos,getStoreDirection(state));
        boolean enabled = state.get(ENABLED);
        BlockState old_state = world.getBlockState(pos);
        BlockState new_state = state;

        if (enabled != store) new_state = new_state.with(ENABLED,store);
        if (state.get(SWAP) != old_state.get(SWAP)) new_state = new_state.with(SWAP, state.get(SWAP));

        world.setBlockState(pos,new_state);
    }

    private InputDirection getStoreDirection(BlockState state){
        return state.get(SWAP) ? InputDirection.LEFT : InputDirection.RIGHT;
    }

    @Override
    public boolean supportsSideDirection(BlockState state, Direction direction)
    {return direction == getStoreDirection(state).getRelativeDirection(state.get(FACING)).getOpposite();}

    @Override
    public boolean supportsBackDirection()
    {return true;}


    @Override
    protected boolean shouldUpdateImmediate(World world, BlockState state, BlockPos pos)
    {return state.get(ENABLED) != isInputPowered(world,state,pos,getStoreDirection(state));}

    @Override
    public void appendTooltip(ItemStack itemStack, Item.TooltipContext context, List<Text> tooltip, TooltipType options) {
        tooltip.add(Text.translatable("block.turingcomplete.memory_cell_gate.tooltip").formatted(Formatting.RED).formatted(Formatting.ITALIC));
    }
}