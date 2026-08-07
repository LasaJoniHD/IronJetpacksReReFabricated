package com.blakebr0.ironjetpacks.network.payloads;

import com.blakebr0.ironjetpacks.IronJetpacks;
import com.blakebr0.ironjetpacks.item.JetpackItem;
import com.blakebr0.ironjetpacks.util.JetpackUtils;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ToggleHoverPayload(boolean enabled) implements CustomPacketPayload {
    public static final Type<ToggleHoverPayload> TYPE = new Type<>(IronJetpacks.id("toggle_hover"));
    public static final StreamCodec<ByteBuf, ToggleHoverPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, ToggleHoverPayload::enabled, ToggleHoverPayload::new);

    @Override
    public Type<ToggleHoverPayload> type() {
        return TYPE;
    }

    public static void handleServer(ToggleHoverPayload payload, ServerPlayNetworking.Context context) {
        var stack = JetpackUtils.getEquippedJetpack(context.player());
        if (stack.getItem() instanceof JetpackItem) JetpackUtils.setHover(stack, payload.enabled);
    }
}
