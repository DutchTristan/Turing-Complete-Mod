package name.turingcomplete.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.Direction;

public interface ConnectsToRedstone {
    // Used By Redstone Wire Mixin
    // Any Block Implementing To This Interface Will Connect To Redstone
    Boolean dustConnectsToThis(BlockState state, Direction fromDustDirection);
}
