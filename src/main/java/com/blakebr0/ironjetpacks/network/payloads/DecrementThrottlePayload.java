package com.blakebr0.ironjetpacks.network.payloads;

import com.blakebr0.ironjetpacks.IronJetpacks;
import com.blakebr0.ironjetpacks.item.JetpackItem;
import com.blakebr0.ironjetpacks.util.JetpackUtils;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record DecrementThrottlePayload() implements CustomPacketPayload {
    public static final Type<DecrementThrottlePayload> TYPE = new Type<>(IronJetpacks.id("decrement_throttle"));
    public static final StreamCodec<ByteBuf, DecrementThrottlePayload> STREAM_CODEC = StreamCodec.unit(new DecrementThrottlePayload());
    @Override public Type<DecrementThrottlePayload> type() { return TYPE; }
    public static void handleServer(DecrementThrottlePayload payload, ServerPlayNetworking.Context context) { var stack = JetpackUtils.getEquippedJetpack(context.player()); if (stack.getItem() instanceof JetpackItem) JetpackUtils.decrementThrottle(stack); }
}
