package com.blakebr0.ironjetpacks.network.payloads;

import com.blakebr0.ironjetpacks.IronJetpacks;
import com.blakebr0.ironjetpacks.item.JetpackItem;
import com.blakebr0.ironjetpacks.util.JetpackUtils;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record IncrementThrottlePayload() implements CustomPacketPayload {
    public static final Type<IncrementThrottlePayload> TYPE = new Type<>(IronJetpacks.id("increment_throttle"));
    public static final StreamCodec<ByteBuf, IncrementThrottlePayload> STREAM_CODEC = StreamCodec.unit(new IncrementThrottlePayload());
    @Override public Type<IncrementThrottlePayload> type() { return TYPE; }
    public static void handleServer(IncrementThrottlePayload payload, ServerPlayNetworking.Context context) { var stack = JetpackUtils.getEquippedJetpack(context.player()); if (stack.getItem() instanceof JetpackItem) JetpackUtils.incrementThrottle(stack); }
}
