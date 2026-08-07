package com.blakebr0.ironjetpacks.network;

import com.blakebr0.ironjetpacks.network.payloads.*;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public final class NetworkHandler {
    public static void initialize() {
        PayloadTypeRegistry.serverboundPlay().register(DecrementThrottlePayload.TYPE, DecrementThrottlePayload.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(IncrementThrottlePayload.TYPE, IncrementThrottlePayload.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(ToggleEnginePayload.TYPE, ToggleEnginePayload.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(ToggleHoverPayload.TYPE, ToggleHoverPayload.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(ToggleHUDPayload.TYPE, ToggleHUDPayload.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(UpdateInputPayload.TYPE, UpdateInputPayload.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(SyncJetpacksPayload.TYPE, SyncJetpacksPayload.STREAM_CODEC);

        ServerPlayNetworking.registerGlobalReceiver(DecrementThrottlePayload.TYPE, DecrementThrottlePayload::handleServer);
        ServerPlayNetworking.registerGlobalReceiver(IncrementThrottlePayload.TYPE, IncrementThrottlePayload::handleServer);
        ServerPlayNetworking.registerGlobalReceiver(ToggleEnginePayload.TYPE, ToggleEnginePayload::handleServer);
        ServerPlayNetworking.registerGlobalReceiver(ToggleHoverPayload.TYPE, ToggleHoverPayload::handleServer);
        ServerPlayNetworking.registerGlobalReceiver(ToggleHUDPayload.TYPE, ToggleHUDPayload::handleServer);
        ServerPlayNetworking.registerGlobalReceiver(UpdateInputPayload.TYPE, UpdateInputPayload::handleServer);
    }
}
