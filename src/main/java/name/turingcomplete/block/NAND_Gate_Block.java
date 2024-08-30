package name.turingcomplete.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class NAND_Gate_Block extends Block {

    public NAND_Gate_Block(Settings settings) {
        super(settings);
    }

    @Override
    protected int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return super.getStrongRedstonePower(state, world, pos, direction);
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context){

        return VoxelShapes.cuboid(0, 0, 0, 1, 0.125, 1);

    }


}