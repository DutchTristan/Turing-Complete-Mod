package name.turingcomplete.blocks.block;

import name.turingcomplete.blocks.AbstractSimpleLogicGate;
import name.turingcomplete.init.propertyInit;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

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
        boolean enabled = state.get(ENABLED);
        boolean powered = state.get(POWERED);

        if (toggle && !enabled){
            world.setBlockState(pos,state.with(ENABLED,true));
            world.scheduleBlockTick(pos, this, 1);
            return !powered;
        }
        else if (!toggle && enabled){
            world.setBlockState(pos,state.with(ENABLED,false));
            world.scheduleBlockTick(pos, this, 1);
            return powered;
        }
        return powered;
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
