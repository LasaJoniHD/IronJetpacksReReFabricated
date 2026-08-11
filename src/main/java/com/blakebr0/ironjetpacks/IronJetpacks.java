package com.blakebr0.ironjetpacks;

import com.blakebr0.ironjetpacks.config.ModConfigs;
import com.blakebr0.ironjetpacks.init.ModCreativeModeTabs;
import com.blakebr0.ironjetpacks.init.ModDataComponentTypes;
import com.blakebr0.ironjetpacks.init.ModItems;
import com.blakebr0.ironjetpacks.init.ModSounds;
import com.blakebr0.ironjetpacks.network.NetworkHandler;
import com.blakebr0.ironjetpacks.registry.JetpackRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class IronJetpacks implements ModInitializer {
    public static final String MOD_ID = "ironjetpacks";
    public static final String NAME = "Iron Jetpacks";
    public static final Logger LOGGER = LoggerFactory.getLogger(NAME);

    @Override
    public void onInitialize() {
        ModConfigs.load();
        JetpackRegistry.getInstance().loadJetpacks();
        ModItems.initialize();
        com.blakebr0.ironjetpacks.handler.RegisterCapabilityHandler.initialize();
        ModCreativeModeTabs.initialize();
        ModSounds.initialize();
        net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer.register(com.blakebr0.ironjetpacks.crafting.ingredient.JetpackComponentIngredient.SERIALIZER);
        net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer.register(com.blakebr0.ironjetpacks.crafting.ingredient.JetpackTierIngredient.SERIALIZER);
        NetworkHandler.initialize();

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) ->
                com.blakebr0.ironjetpacks.handler.InputHandler.remove(handler.getPlayer()));
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            JetpackRegistry.getInstance().loadJetpacks();
            JetpackRegistry.getInstance().syncToAll(server);
        });
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
                JetpackRegistry.getInstance().syncToPlayer(handler.getPlayer()));

        LOGGER.info("Initialized {}", NAME);
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    public static Identifier resource(String path) {
        return id(path);
    }
}
