package name.turingcomplete.init;

import name.turingcomplete.TuringComplete;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;

public class PropertyInit {
    public static final BooleanProperty SWAPPED_DIR = BooleanProperty.of("swapped_direction");
    public static final BooleanProperty POWERED_X = BooleanProperty.of("powered_x");
    public static final BooleanProperty POWERED_Z = BooleanProperty.of("powered_z");

    public static final IntProperty POWER = IntProperty.of("power",0,15);
    public static final IntProperty POWER_X = IntProperty.of("power_x",0,15);
    public static final IntProperty POWER_Z = IntProperty.of("power_z",0,15);

    public static final IntProperty DELAY_8 = IntProperty.of("delay", 1,8);
    public static final IntProperty TIMER = IntProperty.of("timer",0,8);
    public static final IntProperty TICK = IntProperty.of("tick",0,5);

    public static void load(){
        TuringComplete.LOGGER.info("Properties initialised...");
    }
}
