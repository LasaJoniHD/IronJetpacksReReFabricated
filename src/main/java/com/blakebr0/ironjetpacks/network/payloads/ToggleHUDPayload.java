package com.blakebr0.ironjetpacks.network.payloads;

import com.blakebr0.ironjetpacks.IronJetpacks;
import com.blakebr0.ironjetpacks.item.JetpackItem;
import com.blakebr0.ironjetpacks.util.JetpackUtils;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ToggleHUDPayload() implements CustomPacketPayload {
    public static final Type<ToggleHUDPayload> TYPE = new Type<>(IronJetpacks.id("toggle_hud"));
    public static final StreamCodec<ByteBuf, ToggleHUDPayload> STREAM_CODEC = StreamCodec.unit(new ToggleHUDPayload());
    @Override public Type<ToggleHUDPayload> type() { return TYPE; }
    public static void handleServer(ToggleHUDPayload payload, ServerPlayNetworking.Context context) { var stack = JetpackUtils.getEquippedJetpack(context.player()); if (stack.getItem() instanceof JetpackItem) JetpackUtils.toggleHUD(stack); }
}
