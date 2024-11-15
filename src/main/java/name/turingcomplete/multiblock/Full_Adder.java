package name.turingcomplete.multiblock;

import com.mojang.serialization.MapCodec;
import name.turingcomplete.BLOCK_PART;
import name.turingcomplete.MultiBlockGate;
import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;


public class Full_Adder extends MultiBlockGate {

    public static final MapCodec<ComparatorBlock> CODEC = createCodec(ComparatorBlock::new);
    public Full_Adder(Settings settings) {
        super(settings);
    }

    @Override
    public MapCodec<ComparatorBlock> getCodec() {
        return CODEC;
    }

    @Override
    protected int getUpdateDelayInternal(BlockState state) {
        return 0;
    }

    @Override
    public boolean gateConditionsMet(BlockState state, World world, BlockPos pos) {
        if (state.get(PART) == BLOCK_PART.TOP){
            this.setHalfSum(world,pos,state,getSideInput(world,state,pos) > 0);
            this.setCarryIn(world,pos,state,getcarryInput(world,state,pos) > 0);
        }
        else if (state.get(PART) == BLOCK_PART.BOTTOM){
            this.setHalfSum(world,pos,state, getSideInput(world, state, pos) > 0);
        }
        if (state.get(PART) == BLOCK_PART.MIDDLE) {
            world.setBlockState(pos, state.with(HALFSUM,false));
            Direction dir = state.get(FACING).rotateYCounterclockwise();
            BlockPos bottom = pos.offset(dir);
            BlockPos top = pos.offset(dir.getOpposite());
            BlockState bottomState = world.getBlockState(bottom);
            BlockState topState = world.getBlockState(top);
            if (bottomState.isOf(this) && topState.isOf(this)) {
                boolean a = getSideInput(world, bottomState, bottom) > 0;
                boolean b = getSideInput(world, topState, top) > 0;
                boolean c = getcarryInput(world, topState, top) > 0;
                if ((a && b) || (b && c) || (a && c)){
                    world.setBlockState(bottom,bottomState.with(CARRY, true));
                }
                else{
                    world.setBlockState(bottom, bottomState.with(CARRY, false));
                }
                return (a ^ b ^ c);
            }
            return false;
        }
        return false;
    }

    //=============================================

    @Override
    protected int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        if (state.get(PART) == BLOCK_PART.MIDDLE) {
            if (!(Boolean) state.get(POWERED)) {
                return 0;
            } else {
                return state.get(FACING) == direction ? this.getOutputLevel(world, pos, state) : 0;
            }
        }
        else if (state.get(PART) == BLOCK_PART.BOTTOM){
            if (!(Boolean) state.get(CARRY)) {
                return 0;
            } else {
                return state.get(FACING).rotateYClockwise() == direction ? this.getOutputLevel(world, pos, state) : 0;
            }
        }
        return 0;
    }

    //=============================================
    
    @Override
    public Boolean dustConnectsToThis(BlockState state, Direction dir) {
        //get gate state dir
        Direction output_face = state.get(FACING);
        if (state.get(PART) == BLOCK_PART.MIDDLE && dir == output_face){
            return true;
        }
        if (state.get(PART) == BLOCK_PART.TOP && (dir == output_face.getOpposite() || dir == output_face.rotateYCounterclockwise())){
            return true;
        }
        if (state.get(PART) == BLOCK_PART.BOTTOM && (dir == output_face.getOpposite() || dir == output_face.rotateYClockwise())){
            return true;
        }
        return false;
    }

    public void setHalfSum(World world, BlockPos pos, BlockState state, boolean value){
        world.setBlockState(pos, state.with(HALFSUM,value));
    }
    public void setCarryIn(World world, BlockPos pos, BlockState state, boolean value){
        if (state.get(PART) == BLOCK_PART.TOP) {
            world.setBlockState(pos, state.with(CARRY,value));
        }
    }
}
