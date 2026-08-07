package com.blakebr0.ironjetpacks.init;

import com.blakebr0.ironjetpacks.IronJetpacks;
import com.blakebr0.ironjetpacks.item.ComponentItem;
import com.blakebr0.ironjetpacks.item.JetpackItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

public final class ModItems {
    public static Item STRAP;
    public static Item BASIC_COIL;
    public static Item ADVANCED_COIL;
    public static Item ELITE_COIL;
    public static Item ULTIMATE_COIL;
    public static Item CELL;
    public static Item THRUSTER;
    public static Item CAPACITOR;
    public static Item JETPACK;

    public static void initialize() {
        STRAP = register("strap", new Item(properties("strap")));
        BASIC_COIL = register("basic_coil", new Item(properties("basic_coil")));
        ADVANCED_COIL = register("advanced_coil", new Item(properties("advanced_coil")));
        ELITE_COIL = register("elite_coil", new Item(properties("elite_coil")));
        ULTIMATE_COIL = register("ultimate_coil", new Item(properties("ultimate_coil")));
        CELL = register("cell", new ComponentItem(IronJetpacks.id("cell"), "cell"));
        THRUSTER = register("thruster", new ComponentItem(IronJetpacks.id("thruster"), "thruster"));
        CAPACITOR = register("capacitor", new ComponentItem(IronJetpacks.id("capacitor"), "capacitor"));
        JETPACK = register("jetpack", new JetpackItem(IronJetpacks.id("jetpack")));
    }

    private static Item.Properties properties(String name) {
        return new Item.Properties().setId(ResourceKey.create(Registries.ITEM, IronJetpacks.id(name)));
    }

    private static Item register(String name, Item item) {
        return net.minecraft.core.Registry.register(BuiltInRegistries.ITEM, IronJetpacks.id(name), item);
    }
}
