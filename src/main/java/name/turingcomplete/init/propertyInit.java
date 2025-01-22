package name.turingcomplete.init;

import name.turingcomplete.blocks.BLOCK_PART;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;

public class propertyInit {
    public static final BooleanProperty SET = BooleanProperty.of("set");
    public static final BooleanProperty SWAPPED_DIR = BooleanProperty.of("swapped_direction");
    public static final EnumProperty<BLOCK_PART> BLOCK_PART = EnumProperty.of("part", BLOCK_PART.class);

    public static void load(){}
}
