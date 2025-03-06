package name.turingcomplete.blocks.multiblock;

import com.mojang.serialization.MapCodec;
import name.turingcomplete.blocks.BLOCK_PART;
import name.turingcomplete.blocks.MultiBlockGate;
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
    protected BlockState getAdjacentState(World world, BlockPos pos, BlockState initial, BLOCK_PART part) {
        return super.getAdjacentState(world, pos, part == BLOCK_PART.LEFT ? initial.with(CARRY, getCarryInput(world, initial, pos) > 0) : initial, part);
    }

    @Override
    public boolean gateConditionMet(World world, BlockPos pos, BlockState state) {
        switch (state.get(PART)) {
            case LEFT: {
                world.setBlockState(pos,state.with(CARRY, getCarryInput(world,state,pos)>0)
                        .with(HALFSUM,getSideInput(world,state,pos)>0));
                return false;
            }
            case RIGHT: {
                world.setBlockState(pos, state.with(HALFSUM, getSideInput(world, state, pos) > 0));
                return false;
            }
            case MIDDLE: {
                world.setBlockState(pos, state.with(HALFSUM,false));
                Direction right = state.get(FACING).rotateYCounterclockwise();
                BlockPos rightPos = pos.offset(right);
                BlockPos leftPos = pos.offset(right.getOpposite());
                BlockState rightState = world.getBlockState(rightPos);
                BlockState leftState = world.getBlockState(leftPos);
                if (rightState.isOf(this) && leftState.isOf(this)) {
                    boolean a = getSideInput(world, rightState, rightPos) > 0;
                    boolean b = getSideInput(world, leftState, leftPos) > 0;
                    boolean c = getCarryInput(world, leftState, leftPos) > 0;
                    boolean carry = (a && b) || (b && c) || (a && c);
                    world.setBlockState(rightPos, rightState.with(CARRY, carry));
                    //updateTarget(world, rightPos,state.get(FACING).rotateYCounterclockwise());
                    return (a ^ b ^ c);
                }
            }
            default: return false;
        }
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
        //get gate state dir
        Direction output_face = state.get(FACING);
        return switch (state.get(PART)) {
            case LEFT -> (dir == output_face.getOpposite() || dir == output_face.rotateYCounterclockwise());
            case MIDDLE -> dir == output_face;
            case RIGHT -> (dir == output_face.getOpposite() || dir == output_face.rotateYClockwise());
        };
    }

}
