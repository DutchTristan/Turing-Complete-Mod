package name.turingcomplete.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;

public class Logic_Base_Plate_Block extends Block {
    public Logic_Base_Plate_Block(Settings settings) {
        super(settings);
    }

    @Override
    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return hasTopRim((BlockView)world, pos.down());
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        return VoxelShapes.cuboid(0, 0, 0, 1, 0.125, 1);
    }


}
