package name.turingcomplete.init;

import name.turingcomplete.TuringComplete;
import name.turingcomplete.blocks.truthtable.TruthTableScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;


public class screenHandlerInit {
    public static ScreenHandlerType<TruthTableScreenHandler> TRUTH_TABLE;

    public static void register() {
        ExtendedScreenHandlerType<TruthTableScreenHandler, Object> type =
                new ExtendedScreenHandlerType<>(
                        (syncId, inventory, buf) -> new TruthTableScreenHandler(syncId, inventory)
                );
        TRUTH_TABLE = Registry.register(
                Registries.SCREEN_HANDLER,
                Identifier.of(TuringComplete.MOD_ID, "truth_table"),
                type
        );
    }

    public static void load(){}
}
