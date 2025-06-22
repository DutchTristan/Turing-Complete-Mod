package name.turingcomplete.blocks.block;

import name.turingcomplete.blocks.AbstractSimpleLogicBlock;
import name.turingcomplete.init.blockInit;
import name.turingcomplete.init.propertyInit;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class LogicBasePlateBlock extends AbstractSimpleLogicBlock {
    private static final IntProperty POWER = propertyInit.POWER;
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

    //from OmniDirectionalRedstoneBridgeBlock
    private boolean wiresGivePower = true;

    //from OmniDirectionalRedstoneBridgeBlock
    @Override
    protected boolean emitsRedstonePower(BlockState state) {
        return this.wiresGivePower;
    }

    //from OmniDirectionalRedstoneBridgeBlock
    // returns the power received
    private int getReceivedPowerFromDirection(World world, BlockPos pos, Direction direction){
        // Get Received Power By This Block Position
        this.wiresGivePower = false;
        int received = this.getReceivedRedstonePowerFromDirection(world,pos,direction);
        this.wiresGivePower = true;

        int nearby_power = 0;

        // If the power is 15 don't check for higher power levels nearby
        if (received < 15) {
            if(isConnectedOnSide(world.getBlockState(pos),direction)){
                // Variable Setup for block in current direction
                BlockPos blockPos = pos.offset(direction);
                BlockState blockState = world.getBlockState(blockPos);

                if (blockState.isOf(this)) {
                    if(isConnectedOnSide(blockState, direction.getOpposite())){
                        nearby_power = Math.max(nearby_power, this.increasePower(blockState,direction.getAxis()));
                    }
                }
                else {
                    nearby_power = Math.max(nearby_power, this.increasePower(blockState,direction.getAxis()));
                }
            }
        }

        // Returns The Highest Signal Between Received Power And Nearby Power Sources
        return Math.max(received, nearby_power - 1);
    }

    //from OmniDirectionalRedstoneBridgeBlock
    private int getReceivedRedstonePowerFromDirection(World world, BlockPos pos,Direction direction) {
        int i = 0;

        if(!isConnectedOnSide(world.getBlockState(pos), direction)){
            return 0;
        }

        
        int j = world.getEmittedRedstonePower(pos.offset(direction), direction);
        if (world.getBlockState(pos.offset(direction)).isOf(Blocks.REDSTONE_WIRE) || world.getBlockState(pos.offset(direction)).isOf(blockInit.OMNI_DIRECTIONAL_REDSTONE_BRIDGE_BLOCK))
            j = j-1;

        if (j >= 15) return 15;
        if (j > i) i = j;
        

        return i;
    }

    //from OmniDirectionalRedstoneBridgeBlock
    private int increasePower(BlockState state,Axis axis) {
        if (state.isOf(Blocks.REDSTONE_WIRE))
            return state.get(Properties.POWER);

        if (state.isOf(this))
            return state.get(POWER);

        if(state.isOf(blockInit.OMNI_DIRECTIONAL_REDSTONE_BRIDGE_BLOCK)){
            if (axis == Direction.Axis.X) return state.get(OmniDirectionalRedstoneBridgeBlock.POWER_X);
            else return state.get(OmniDirectionalRedstoneBridgeBlock.POWER_Z);
        }

        return 0;
    }

    //from OmniDirectionalRedstoneBridgeBlock
    // On Receive Block Update
    private void update(World world, BlockPos pos, BlockState state) {
        // Variable Setup
        int received_power = 0;
        
        for (Direction direction: Direction.Type.HORIZONTAL){
            int dir_received_power = this.getReceivedPowerFromDirection(world, pos,direction);
            if (dir_received_power > received_power) received_power=dir_received_power;
        }

        // If Received Power Different Then Current Power
        if (state.get(POWER) != received_power){
            // Update Block State (With State Check To Make Sure It Doesn't Update Other Blocks)
            if (world.getBlockState(pos) == state){
                world.setBlockState(pos,state.with(POWER,received_power),Block.NOTIFY_LISTENERS);
            }

            // Update neighbors
            
            this.updateNeighborsHorizontally(world,pos);
        }
    }

    //from OmniDirectionalRedstoneBridgeBlock
    private void updateNeighborsHorizontally(World world, BlockPos pos){
        for (Direction direction : Direction.Type.HORIZONTAL) {
            this.updateNeighbors(world, pos.offset(direction));
        }
    }

    //from OmniDirectionalRedstoneBridgeBlock
    // update Nearby Neighbours
    private void updateNeighbors(World world, BlockPos pos) {
        if (!(world.getBlockState(pos).isOf(this) || world.getBlockState(pos).isOf(Blocks.REDSTONE_WIRE)))
            return;

        world.updateNeighborsAlways(pos, this);

        for(Direction direction : DIRECTIONS)
            world.updateNeighborsAlways(pos.offset(direction), this);
    }

    //from OmniDirectionalRedstoneBridgeBlock
    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        super.onStateReplaced(state, world, pos, newState, false);
        if(!newState.isOf(this)) return;
        // If World Is Client World, Ignore Rest Of Code
        if(world.isClient) return;

        // Update Nearby Blocks
        for (Direction direction : DIRECTIONS)
            world.updateNeighborsAlways(pos.offset(direction), this);

        // Update This AnD Other Wires
        this.update(world, pos, state);
        this.updateNeighborsHorizontally(world, pos);
    }

    //from OmniDirectionalRedstoneBridgeBlock
    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!oldState.isOf(state.getBlock()) && !world.isClient) {
            // Updates This Block
            this.update(world, pos, state);

            // Updates Above And Bellow Blocks
            for (Direction direction : Direction.Type.VERTICAL) {
                world.updateNeighborsAlways(pos.offset(direction), this);
            }

            // Updates Blocks Horizontally
            this.updateNeighborsHorizontally(world,pos);
        }
    }

    //from OmniDirectionalRedstoneBridgeBlock
    @Override
    protected int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return this.wiresGivePower ? state.getWeakRedstonePower(world, pos, direction) : 0;
    }

    //from OmniDirectionalRedstoneBridgeBlock
    @Override
    protected int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        // If The Block Doesn't Give Power, Return 0
        if (!this.wiresGivePower) return 0;

        if(!isConnectedOnSide(state, direction.getOpposite())) return 0;

        BlockState block_to_power = world.getBlockState(pos.offset(direction.getOpposite()));

        if (block_to_power.isOf(Blocks.REDSTONE_WIRE)){
            return state.get(POWER) -1;
        }
        return state.get(POWER);
    }

    public static int getWireColor(BlockState state) {
        Vec3d vec3d = COLORS[state.get(POWER)];
        return MathHelper.packRgb((float)vec3d.getX(), (float)vec3d.getY(), (float)vec3d.getZ());
    }

    public boolean isConnectedOnSide(BlockState state, Direction direction){
        return state.get(getPropertyFromDirection(direction));
    }

    public LogicBasePlateBlock(Settings settings) {
        super(settings);

        setDefaultState(getDefaultState()
        .with(POWER, 0)
        .with(NORTH_DUST,false)
        .with(SOUTH_DUST,false)
        .with(EAST_DUST,false)
        .with(WEST_DUST,false));
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if(!state.canPlaceAt(world,pos)){
            BlockEntity blockEntity = state.hasBlockEntity() ? world.getBlockEntity(pos) : null;
            dropStacks(state, world, pos, blockEntity);

            world.removeBlock(pos, false);

            for (Direction direction : DIRECTIONS)
                world.updateNeighborsAlways(pos.offset(direction), this);

        }
        else {
            //From OmniDirectionalRedstoneBridgeBlock
            this.update(world, pos, state);
        }
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
        builder.add(POWER);
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
