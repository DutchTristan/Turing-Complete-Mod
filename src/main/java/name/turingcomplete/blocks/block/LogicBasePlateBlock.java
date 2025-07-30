package name.turingcomplete.blocks.block;

import name.turingcomplete.TuringComplete;
import name.turingcomplete.blocks.logicwire.AbstractLogicWire;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class LogicBasePlateBlock extends AbstractLogicWire {
    //private static final IntProperty POWER = propertyInit.POWER;
    private static final BooleanProperty NORTH_DUST = BooleanProperty.of("north_dust");
    private static final BooleanProperty SOUTH_DUST = BooleanProperty.of("south_dust");
    private static final BooleanProperty EAST_DUST = BooleanProperty.of("east_dust");
    private static final BooleanProperty WEST_DUST = BooleanProperty.of("west_dust");

    //from OmniDirectionalRedstoneBridgeBlock
    private static final Vec3d[] COLORS = Util.make(new Vec3d[16], (colors) -> {
        for(int i = 0; i <= 15; ++i) {
            float f = (float)i / 15.0F;
            float g = f * 0.6F + (f > 0.0F ? 0.4F : 0.3F);
            float h = MathHelper.clamp(f * f * 0.7F - 0.5F, 0.0F, 1.0F);
            float j = MathHelper.clamp(f * f * 0.6F - 0.7F, 0.0F, 1.0F);
            colors[i] = new Vec3d(g, h, j);
        }
    });

    public static int getWireColor(Block block, BlockState state) {
        LogicBasePlateBlock thisBlock = (LogicBasePlateBlock)block;
        //Vec3d vec3d = COLORS[state.get(POWER)];
        Vec3d vec3d = COLORS[state.get(thisBlock.signalStrengthProperties.get(0))];
        return MathHelper.packRgb((float)vec3d.getX(), (float)vec3d.getY(), (float)vec3d.getZ());
    }

    public boolean isConnectedOnSide(BlockState state, Direction direction){
        return state.get(getPropertyFromDirection(direction));
    }

    
    public LogicBasePlateBlock(Settings settings) {
        super(settings);

        setDefaultState(getDefaultState()
        .with(NORTH_DUST,false)
        .with(SOUTH_DUST,false)
        .with(EAST_DUST,false)
        .with(WEST_DUST,false));
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (hit.getSide() != Direction.UP){
            return ActionResult.PASS;
        }

        Vec3d hit_pos = hit.getPos();
        double local_x = hit_pos.x - pos.getX();
        double local_z = hit_pos.z - pos.getZ();

        Direction hit_direction;

        if (local_x>local_z){
            //north or east
            if(local_x+local_z>1){
                hit_direction = Direction.EAST;
            }
            else {
                hit_direction = Direction.NORTH;
            }
        }
        else {
            //south or west
            if(local_x+local_z>1){
                hit_direction = Direction.SOUTH;
            }
            else{
                hit_direction = Direction.WEST;
            }
        }

        BooleanProperty connectionProperty = getPropertyFromDirection(hit_direction);
        boolean already_connected = state.get(connectionProperty);
        if(!already_connected){
            //add connection
            //need redstone to apply
            if(player.getMainHandStack().getItem() != Items.REDSTONE){
                return ActionResult.PASS;
            }
            //consume redstone
            if(!player.isCreative()){
                player.getMainHandStack().decrement(1);
            }
            world.setBlockState(pos,state.with(connectionProperty,true));
        }
        else{
            //remove connection
            if(!player.isCreative()){
                if(player.getMainHandStack().getItem() == Items.REDSTONE){
                    player.getMainHandStack().increment(1);
                }
                else if(player.isSneaking()){
                    player.giveItemStack(new ItemStack(Items.REDSTONE, 1));
                }
                else {
                    //only remove if holding redstone or sneaking
                    return ActionResult.PASS;
                }
            }
            else if(player.getMainHandStack().getItem() != Items.REDSTONE && !player.isSneaking() ){
                //only remove if holding redstone or sneaking
                return ActionResult.PASS;
            }
        }
        world.setBlockState(pos,state.with(connectionProperty,!state.get(connectionProperty)));
        TuringComplete.LOGGER.trace("queueing update for "+pos+" for onUse");
        world.updateNeighbor(pos, this, pos);

        return ActionResult.SUCCESS_NO_ITEM_USED;
    }


    private static BooleanProperty getPropertyFromDirection(Direction direction){
        switch (direction) {
            case EAST:
                return EAST_DUST;
            case NORTH:
                return NORTH_DUST;
            case SOUTH:
                return SOUTH_DUST;
            case WEST:
                return WEST_DUST;
            default:
                throw new IllegalArgumentException("attempt to get logic base plate connection property for invalid direciton "+direction);
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        //builder.add(POWER);
        builder.add(NORTH_DUST);
        builder.add(SOUTH_DUST);
        builder.add(EAST_DUST);
        builder.add(WEST_DUST);
    }

    @Override
    public Boolean dustConnectsToThis(BlockState state, Direction di) {

        switch(di.getOpposite()) {
            case EAST:
                return state.get(EAST_DUST);
            case NORTH:
                return state.get(NORTH_DUST);
            case SOUTH:
                return state.get(SOUTH_DUST);
            case WEST:
                return state.get(WEST_DUST);
            default:
                return false;
        }
    }
}
