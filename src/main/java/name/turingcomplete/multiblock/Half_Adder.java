package name.turingcomplete.multiblock;

import com.mojang.serialization.MapCodec;
import name.turingcomplete.BLOCK_PART;
import name.turingcomplete.MultiBlockGate;
import net.minecraft.block.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.tick.TickPriority;

import static net.minecraft.block.RedstoneWireBlock.POWER;


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
    protected int getUpdateDelayInternal(BlockState state) {
        return 2;
    }


    public boolean hasCarry(World world, BlockPos pos, BlockState state) {
        /*if (state.get(PART) == BLOCK_PART.MIDDLE) {
            Direction dir1 = state.get(FACING).rotateYCounterclockwise();
            Direction dir2 = state.get(FACING).rotateYClockwise();
            BlockPos pos1 = pos.offset(dir1);
            BlockPos pos2 = pos.offset(dir2);
            BlockState state1 = world.getBlockState(pos1);
            BlockState state2 = world.getBlockState(pos2);
            BlockPos SumPos = pos1.offset(dir1);
            BlockState SumState = world.getBlockState(SumPos);
            if (SumState.isOf(Blocks.REDSTONE_WIRE)) {
                if (state1.get(HALFSUM) && state2.get(HALFSUM)) {
                    world.setBlockState(SumPos, state.with(POWER, 15), Block.NOTIFY_LISTENERS);
                    return true;
                } else {
                    world.setBlockState(SumPos, state.with(POWER, 0), Block.NOTIFY_LISTENERS);
                    return false;
                }
            }
        }*/
        if (state.get(PART) == BLOCK_PART.BOTTOM){
            BlockState MidBlock = world.getBlockState(pos.offset(state.get(FACING).rotateYClockwise()));
            if (!MidBlock.get(POWERED) && state.get(HALFSUM)){
                return true;
            }
        }
        return false;
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
            Direction dir1 = state.get(FACING).rotateYCounterclockwise();
            Direction dir2 = state.get(FACING).rotateYClockwise();
            BlockPos pos1 = pos.offset(dir1);
            BlockPos pos2 = pos.offset(dir2);
            BlockState state1 = world.getBlockState(pos1);
            BlockState state2 = world.getBlockState(pos2);
            if (state1.isOf(this) && state2.isOf(this)) {
                return (state1.get(HALFSUM) ^ state2.get(HALFSUM));
            }
            return false;
        }
        return false;
    }

    //=============================================

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        boolean bl = (Boolean)state.get(POWERED);
        boolean bl2 = this.hasPower(world, pos, state);
        if (bl && !bl2) {
            world.setBlockState(pos, state.with(POWERED, Boolean.valueOf(false)), Block.NOTIFY_LISTENERS);
        } else if (!bl) {
            world.setBlockState(pos, state.with(POWERED, Boolean.valueOf(true)), Block.NOTIFY_LISTENERS);
            if (!bl2) {
                world.scheduleBlockTick(pos, this, this.getUpdateDelayInternal(state), TickPriority.VERY_HIGH);
            }
        }
        boolean bl3 = this.hasCarry(world,pos,state);
        if (bl3){
            world.setBlockState(pos.offset(state.get(FACING).rotateYCounterclockwise()), state.with(POWER, 15), Block.NOTIFY_LISTENERS);
        }
        else{
            world.setBlockState(pos.offset(state.get(FACING).rotateYCounterclockwise()), state.with(POWER, 15), Block.NOTIFY_LISTENERS);
        }

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
