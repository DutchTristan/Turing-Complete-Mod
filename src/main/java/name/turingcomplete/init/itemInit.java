package name.turingcomplete.init;

import name.turingcomplete.TuringComplete;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class itemInit {

    public static <T extends Item> T register(String name, T item){
        return Registry.register(Registries.ITEM, TuringComplete.id(name), item);
    }

    public static void load(){}
}
