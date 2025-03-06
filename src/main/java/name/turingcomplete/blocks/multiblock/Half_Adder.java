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
    public boolean gateConditionMet(World world, BlockPos pos, BlockState state) {
        switch (state.get(PART)) {
            case LEFT:
            case RIGHT:
                world.setBlockState(pos, state.with(HALFSUM, getSideInput(world,state,pos) > 0));
                return false;
            case MIDDLE:
                world.setBlockState(pos, state.with(HALFSUM,false));
                Direction right = state.get(FACING).rotateYCounterclockwise();
                BlockPos rightPos = pos.offset(right);
                BlockPos leftPos = pos.offset(right.getOpposite());
                BlockState rightState = world.getBlockState(rightPos);
                BlockState leftState = world.getBlockState(leftPos);
                if (rightState.isOf(this) && leftState.isOf(this)) {
                    boolean a = getSideInput(world, rightState, rightPos) > 0;
                    boolean b = getSideInput(world, leftState, leftPos) > 0;
                    boolean carry = a & b;
                    world.setBlockState(rightPos, rightState.with(CARRY, carry));
                    updateTarget(world, rightPos,state.get(FACING).rotateYClockwise());
                    return a ^ b;
                }
        }
        return false;
    }

    //=============================================

    @Override
    protected int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        if (state.get(PART) == BLOCK_PART.MIDDLE) {
            if (!(Boolean) state.get(SUM)) {
                return 0;
            } else {
                return state.get(FACING) == direction ? 15 : 0;
            }
        }
        else if (state.get(PART) == BLOCK_PART.RIGHT){
            if (!(Boolean) state.get(CARRY)) {
                return 0;
            } else {
                return state.get(FACING).rotateYClockwise() == direction ? 15 : 0;
            }
        }
        return 0;
    }

    //=============================================
    
    @Override
    public Boolean dustConnectsToThis(BlockState state, Direction dir) {
        //get gate state right
        Direction output_face = state.get(FACING);
        if (state.get(PART) == BLOCK_PART.MIDDLE && dir == output_face){
            return true;
        }
        if (state.get(PART) == BLOCK_PART.LEFT && dir == output_face.getOpposite()){
            return true;
        }
        if (state.get(PART) == BLOCK_PART.RIGHT && (dir == output_face.getOpposite() || dir == output_face.rotateYClockwise())){
            return true;
        }
        return false;
    }

}
