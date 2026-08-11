package com.blakebr0.ironjetpacks.client;

import com.blakebr0.ironjetpacks.IronJetpacks;
import com.blakebr0.ironjetpacks.client.handler.HudHandler;
import com.blakebr0.ironjetpacks.client.handler.JetpackArmorRenderer;
import com.blakebr0.ironjetpacks.init.ModItems;
import com.blakebr0.ironjetpacks.client.handler.JetpackClientHandler;
import com.blakebr0.ironjetpacks.client.handler.JetpackTooltipHandler;
import com.blakebr0.ironjetpacks.client.handler.KeybindHandler;
import com.blakebr0.ironjetpacks.client.handler.ModelHandler;
import com.blakebr0.ironjetpacks.network.payloads.SyncJetpacksPayload;
import com.blakebr0.ironjetpacks.registry.JetpackRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;

public final class IronJetpacksClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(SyncJetpacksPayload.TYPE,
                (payload, context) -> JetpackRegistry.getInstance().loadJetpacks(payload));
        KeybindHandler.initialize();
        JetpackTooltipHandler.initialize();
        ModelLayerRegistry.registerModelLayer(ModelHandler.JETPACK_LAYER, ModelHandler::createLayer);
        ArmorRenderer.register(JetpackArmorRenderer::new, ModItems.JETPACK);
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            KeybindHandler.tick();
            JetpackClientHandler.tick();
        });
        HudHandler.initialize();
    }
}
