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

public class SWITCH_Gate_Block extends AbstractSimpleLogicGate {
    private static final BooleanProperty SWAP = propertyInit.SWAPPED_DIR;
    private static final BooleanProperty ENABLED = Properties.ENABLED;

    public SWITCH_Gate_Block(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState()
                .with(SWAP,false)
                .with(ENABLED,false)
        );
    }

    @Override
    protected void properties(StateManager.Builder<Block, BlockState> builder)
    {super.properties(builder);     builder.add(SWAP,ENABLED);}

    @Override
    public boolean gateConditionMet(World world, BlockPos pos,BlockState state) {
        boolean enable = isInputPowered(world,state,pos,getEnabledSide(state));
        boolean back = isInputPowered(world,state,pos,InputDirection.BACK);

        return enable && back;
    }

    @Override
    protected BlockState updateExtras(World world, BlockPos pos, BlockState state) {
        boolean has_enabled = isInputPowered(world,state,pos,getEnabledSide(state));
        boolean enabled = state.get(ENABLED);

        if (enabled != has_enabled) return state.with(ENABLED,has_enabled);

        return state;
    }

    private InputDirection getEnabledSide(BlockState state){
        return state.get(SWAP) ? InputDirection.LEFT : InputDirection.RIGHT;
    }


    @Override
    public boolean supportsSideDirection(BlockState state, Direction direction)
    {return direction == getEnabledSide(state).getRelativeDirection(state.get(FACING)).getOpposite();}

    @Override
    public boolean supportsBackDirection()
    {return true;}

    @Override
    protected boolean shouldUpdateExtras(World world, BlockState state, BlockPos pos)
    {return state.get(ENABLED) != isInputPowered(world,state,pos,getEnabledSide(state));}



    @Override
    public void appendTooltip(ItemStack itemStack, Item.TooltipContext context, List<Text> tooltip, TooltipType options) {
        tooltip.add(Text.translatable("block.turingcomplete.switch_gate_block.tooltip").formatted(Formatting.RED).formatted(Formatting.ITALIC));
    }

}