package name.turingcomplete.blocks.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import name.turingcomplete.TuringComplete;
import name.turingcomplete.blocks.logicwire.AbstractLogicWire;
import net.minecraft.block.*;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class OmniDirectionalRedstoneBridgeBlock extends AbstractLogicWire {
    private static final int POWER_Z_INDEX = 1;
    private static final int POWER_X_INDEX = 0;
    private final IntProperty POWER_Z;
    private final IntProperty POWER_X;

    private static final Vec3d[] COLORS;

    public OmniDirectionalRedstoneBridgeBlock(Settings settings) {
        super(settings);
        POWER_Z = signalStrengthProperties.get(POWER_Z_INDEX);
        POWER_X = signalStrengthProperties.get(POWER_X_INDEX);
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        return Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
    }

    public static int getWireColor(BlockState state, int tintIndex) {
        OmniDirectionalRedstoneBridgeBlock block = (OmniDirectionalRedstoneBridgeBlock)state.getBlock();
        if(tintIndex<0 || tintIndex >= block.maxSignalCount()){
            TuringComplete.LOGGER.error("attempt to get tint index "+tintIndex+" for "+block.getClass().getName()+", for which valid indices range from 0 to "+(block.maxSignalCount()-1));
        }
        Vec3d vec3d = COLORS[state.get(block.signalStrengthProperties.get(tintIndex))];
        return MathHelper.packRgb((float)vec3d.getX(), (float)vec3d.getY(), (float)vec3d.getZ());
    }

    public static int getWireColor(BlockState state, IntProperty property) {
        Vec3d vec3d = COLORS[state.get(property)];
        return MathHelper.packRgb((float)vec3d.getX(), (float)vec3d.getY(), (float)vec3d.getZ());
    }

    @Override
    protected int maxSignalCount(){
        return 2;
    }

    @Override
    protected Optional<Integer> getConnectedSignalStrength(BlockPos pos, BlockState state, BlockPos connectedPos){
        if(
            connectedPos.equals(pos.offset(Direction.EAST)) ||
            connectedPos.equals(pos.offset(Direction.WEST))
        )
        {
            return Optional.of(state.get(POWER_X));
        }
        else if(
            connectedPos.equals(pos.offset(Direction.NORTH)) ||
            connectedPos.equals(pos.offset(Direction.SOUTH))
        ) {
            return Optional.of(state.get(POWER_Z));
        }
        else {
            return Optional.empty();
        }
    }

    @Override
    protected List<BlockPos> getConnectedBlocks(BlockPos pos, BlockState state, int signalIndex){
        List<BlockPos> positions = new ArrayList<>(2);
        switch(signalIndex){
            case POWER_X_INDEX:
                positions.add(pos.offset(Direction.EAST));
                positions.add(pos.offset(Direction.WEST));
                break;
            case POWER_Z_INDEX:
                positions.add(pos.offset(Direction.NORTH));
                positions.add(pos.offset(Direction.SOUTH));
                break;
            default:
        }
        return positions;
    }

    @Override
    public Boolean dustConnectsToThis(BlockState state, Direction di) {
        return true;
    }

    @Override
    public boolean isDirectional(){
        return false;
    }

    static {
        COLORS = Util.make(new Vec3d[16], (colors) -> {
            for(int i = 0; i <= 15; ++i) {
                float f = (float)i / 15.0F;
                float g = f * 0.6F + (f > 0.0F ? 0.4F : 0.3F);
                float h = MathHelper.clamp(f * f * 0.7F - 0.5F, 0.0F, 1.0F);
                float j = MathHelper.clamp(f * f * 0.6F - 0.7F, 0.0F, 1.0F);
                colors[i] = new Vec3d(g, h, j);
            }

        });
    }

}
