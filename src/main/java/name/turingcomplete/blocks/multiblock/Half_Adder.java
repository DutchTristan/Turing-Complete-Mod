package name.turingcomplete.blocks.multiblock;

import com.mojang.serialization.MapCodec;
import name.turingcomplete.blocks.BLOCK_PART;
import name.turingcomplete.blocks.MultiBlockGate;
import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;


public class Half_Adder extends MultiBlockGate {

    public static final MapCodec<ComparatorBlock> CODEC = createCodec(ComparatorBlock::new);
    public Half_Adder(Settings settings) {
        super(settings);
    }

    @Override
    public MapCodec<ComparatorBlock> getCodec() {
        return CODEC;
    }

    @Override
    public boolean gateConditionsMet(BlockState state, World world, BlockPos pos) {
        if (state.get(PART) == BLOCK_PART.TOP){
            if (getSideInput(world,state,pos) > 0) {
                this.setHalfSum(world,pos,state,true);
            } else {
                this.setHalfSum(world,pos,state,false);
            }
        }
        else if (state.get(PART) == BLOCK_PART.BOTTOM){
            if (getSideInput(world,state,pos) > 0) {
                this.setHalfSum(world,pos,state,true);
            } else {
                this.setHalfSum(world,pos,state,false);
            }
        }
        if (state.get(PART) == BLOCK_PART.MIDDLE) {
            world.setBlockState(pos, state.with(HALFSUM,false));
            Direction dir = state.get(FACING).rotateYCounterclockwise();
            BlockPos pos1 = pos.offset(dir);
            BlockPos pos2 = pos.offset(dir.getOpposite());
            BlockState state1 = world.getBlockState(pos1);
            BlockState state2 = world.getBlockState(pos2);
            if (state1.isOf(this) && state2.isOf(this)) {
                if (getSideInput(world, state1, pos1) > 0 && getSideInput(world, state2, pos2) > 0){
                    world.setBlockState(pos1,state1.with(CARRY, true));
                }
                else{
                    world.setBlockState(pos1, state1.with(CARRY, false));
                }
                return ((getSideInput(world, state1, pos1) > 0) ^ (getSideInput(world, state2, pos2) > 0));
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
        if (state.get(PART) == BLOCK_PART.TOP && dir == output_face.getOpposite()){
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
}
