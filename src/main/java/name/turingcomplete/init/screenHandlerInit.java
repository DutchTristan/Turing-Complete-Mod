package name.turingcomplete.init;

import name.turingcomplete.TuringComplete;
import name.turingcomplete.blocks.truthtable.TruthTableScreenHandler;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;


public class screenHandlerInit {
    public static ScreenHandlerType<TruthTableScreenHandler> TRUTH_TABLE = register("truth_table", TruthTableScreenHandler::new);

    public static <T extends ScreenHandler> ScreenHandlerType<T> register(String id, ScreenHandlerType.Factory<T> factory) {
        return Registry.register(Registries.SCREEN_HANDLER, TuringComplete.id(id), new ScreenHandlerType<>(factory, FeatureFlags.VANILLA_FEATURES));
    }

    public static void load(){}
}
