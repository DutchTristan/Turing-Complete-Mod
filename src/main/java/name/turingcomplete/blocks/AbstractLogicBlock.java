package name.turingcomplete.blocks;

import org.jetbrains.annotations.MustBeInvokedByOverriders;

import com.mojang.serialization.MapCodec;

import name.turingcomplete.init.propertyInit;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ComparatorBlock;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.SideShapeType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

//anything on a logic plate
public abstract class AbstractLogicBlock extends HorizontalFacingBlock implements ConnectsToRedstone{
    public static final BooleanProperty MIRRORED = propertyInit.SWAPPED_DIR;

    protected AbstractLogicBlock(Settings settings) {
        super(settings);
        if(canMirror()) {
            setDefaultState(getDefaultState()
            .with(MIRRORED,false));
        }
    }

    //allow immediate block state changes in response to input, such as highlighting active inputs
    protected void onInputChange(World world, BlockPos gatePos, BlockState gateState){}

    protected int getInputStrength(World world, BlockPos pos, BlockState state, RelativeSide inputSide) {
        Direction checkDirection = inputSide.withBackDirection(state.get(FACING));
        BlockPos checkPos = pos.offset(checkDirection);
        return world.getEmittedRedstonePower(checkPos,checkDirection);
    }

    protected boolean getInputActive(World world, BlockPos pos, BlockState state, RelativeSide inputSide) {
        return getInputStrength(world,pos,state,inputSide) > 0;
    }
    
    //warning: called in constructor, and value must not change after AbstractLogicBlock constructor called
    protected boolean canMirror(){
        return false;
    }

    //warning: called in constructor, and value must not change after AbstractLogicBlock constructor called
    protected boolean isDirectional(){
        return true;
    }

    //main* naming because on a multiblock, this should only be called on the main block
    protected boolean canMirrorHere(BlockPos mainPos, BlockState mainState){
        return false;
    }

    //default implementation assumes outputs are always potentially strong-powered by the logic block
    protected void updateOutputBlock(World world, BlockPos sourcePos, Direction direction) {
        BlockPos targetPos = sourcePos.offset(direction);
        //update the block being powered
        world.updateNeighbor(targetPos, this, sourcePos);
        //don't update ourself
        world.updateNeighborsExcept(targetPos, this, direction.getOpposite());
    }

    //default implementation assumes outputs are always potentially strong-powered by the logic block
    //void updateOutputBlock(World world, BlockPos sourcePos, Direction direction) is prefered when easy to use
    protected void updateOutputBlock(World world, BlockPos sourcePos, BlockPos targetPos) {
        Direction toSourceDirection = null;

        int diffX = targetPos.getX()-sourcePos.getX();
        int diffY = targetPos.getY()-sourcePos.getY();
        int diffZ = targetPos.getZ()-sourcePos.getZ();

        if (diffX == 1 && diffY == 0 && diffZ == 0) {
            toSourceDirection = Direction.WEST;
        }
        else if (diffX == -1 && diffY == 0 && diffZ == 0) {
            toSourceDirection = Direction.EAST;
        }
        else if (diffX == 0 && diffY == 1 && diffZ == 0) {
            toSourceDirection = Direction.DOWN;
        }
        else if (diffX == 0 && diffY == -1 && diffZ == 0) {
            toSourceDirection = Direction.UP;
        }
        else if (diffX == 0 && diffY == 0 && diffZ == 1) {
            toSourceDirection = Direction.NORTH;
        }
        else if (diffX == 0 && diffY == 0 && diffZ == -1) {
            toSourceDirection = Direction.SOUTH;
        }

        //update the block being powered
        world.updateNeighbor(targetPos, this, sourcePos);
        //update neighbors of the block being powered.
        //we need this for strong powering to work
        if (toSourceDirection == null) {
            //not our neighbor, so won't update ourself
            world.updateNeighborsAlways(targetPos, this);
        }
        else {
            //don't update ourself
            world.updateNeighborsExcept(targetPos, this, toSourceDirection);
        }
    }

    protected boolean canPlaceAbove(WorldView world, BlockPos pos, BlockState state)
    {
        return state.isSideSolid(world, pos, Direction.UP, SideShapeType.RIGID);
    }

    @Override
    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos)
    {
        return this.canPlaceAbove(world, pos.down(), world.getBlockState(pos.down()));
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        return Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
    }

    @Override
    protected MapCodec<? extends HorizontalFacingBlock> getCodec() {return AbstractBlock.createCodec(ComparatorBlock::new);}

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx)
    {
        BlockState state = getDefaultState();
        if(isDirectional()) {
            state = state.with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
        }
        return state;
    }

    @Override
    protected int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction)
    {
        return getWeakRedstonePower(state, world, pos, direction);
    }

    @Override
    @MustBeInvokedByOverriders
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (!canPlaceAt(state,world, pos)) {
            world.breakBlock(pos, true);

            for (Direction direction : DIRECTIONS)
                world.updateNeighborsAlways(pos.offset(direction), this);
        }
    }

    @Override
    @MustBeInvokedByOverriders
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        if(isDirectional()) {
            builder.add(FACING);
        }
        if(canMirror()) {
            builder.add(MIRRORED);
        }
    }

    public enum RelativeSide {
        FRONT,
        BACK,
        LEFT,
        RIGHT;
        
        public Direction withFrontDirection(Direction direction){
            if (direction.getAxis() == Axis.Y) {
                throw new IllegalArgumentException("direction must not be vertical");
            }
            switch(this) {
                case FRONT:
                    return direction;
                case BACK:
                    return direction.getOpposite();
                case LEFT:
                    if (direction.getAxis() == Axis.Z) {
                        return Direction.from(Axis.X, direction.getDirection());
                    }
                    else {
                        return Direction.from(Axis.Z,direction.getDirection().getOpposite());
                    }
                case RIGHT:
                    if (direction.getAxis() == Axis.Z) {
                        return Direction.from(Axis.X, direction.getDirection().getOpposite());
                    }
                    else {
                        return Direction.from(Axis.Z,direction.getDirection());
                    }
                default:
                    //Java should be smart enough to know this is unreachable. Because it's not, we won't get an error if the above code
                    //stops being comprehensive
                    throw new IllegalStateException();
            }
        }

        //the facing property of most blocks actually points to the back of the block, so this is for that situation
        public Direction withBackDirection(Direction direction) {
            //RelativeSide's getOpposite is trivial; Direction's is not
            return this.getOpposite().withFrontDirection(direction);
        }

        public RelativeSide getOpposite(){
            switch(this) {
                case BACK:
                    return FRONT;
                case FRONT:
                    return BACK;
                case LEFT:
                    return RIGHT;
                case RIGHT:
                    return LEFT;
                default:
                    throw new IllegalStateException("Relative side not Front, Back, Left, or Right");
            }
        }
    }
}
