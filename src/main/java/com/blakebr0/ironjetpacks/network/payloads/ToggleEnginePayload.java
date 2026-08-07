package com.blakebr0.ironjetpacks.network.payloads;

import com.blakebr0.ironjetpacks.IronJetpacks;
import com.blakebr0.ironjetpacks.item.JetpackItem;
import com.blakebr0.ironjetpacks.util.JetpackUtils;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ToggleEnginePayload(boolean enabled) implements CustomPacketPayload {
    public static final Type<ToggleEnginePayload> TYPE = new Type<>(IronJetpacks.id("toggle_engine"));
    public static final StreamCodec<ByteBuf, ToggleEnginePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, ToggleEnginePayload::enabled, ToggleEnginePayload::new);

    @Override
    public Type<ToggleEnginePayload> type() {
        return TYPE;
    }

    public static void handleServer(ToggleEnginePayload payload, ServerPlayNetworking.Context context) {
        var stack = JetpackUtils.getEquippedJetpack(context.player());
        if (stack.getItem() instanceof JetpackItem) JetpackUtils.setEngine(stack, payload.enabled);
    }
}
