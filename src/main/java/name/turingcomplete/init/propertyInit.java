package name.turingcomplete.init;

import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;

public class propertyInit {
    public static final BooleanProperty SET = BooleanProperty.of("set");
    public static final BooleanProperty SWAPPED_DIR = BooleanProperty.of("swapped_direction");
    public static final BooleanProperty POWERED_X = BooleanProperty.of("powered_x");
    public static final BooleanProperty POWERED_Z = BooleanProperty.of("powered_z");

    public static final IntProperty POWER_X = IntProperty.of("power_x",0,15);
    public static final IntProperty POWER_Z = IntProperty.of("power_z",0,15);

    public static void load(){}
}
