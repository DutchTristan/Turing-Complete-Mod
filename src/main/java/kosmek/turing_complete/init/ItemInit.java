package kosmek.turing_complete.init;

import kosmek.turing_complete.TuringCompleteUnofficial;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ItemInit {

    public static <T extends Item> T register(String name, T item){
        return Registry.register(Registries.ITEM, new Identifier(TuringCompleteUnofficial.MOD_ID,name), item);
    }

    public static void load(){}
}
