package com.blakebr0.ironjetpacks.network.payloads;

import com.blakebr0.ironjetpacks.IronJetpacks;
import com.blakebr0.ironjetpacks.registry.Jetpack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import java.util.List;

public record SyncJetpacksPayload(List<Jetpack> jetpacks) implements CustomPacketPayload {
    public static final Type<SyncJetpacksPayload> TYPE = new Type<>(IronJetpacks.id("sync_jetpacks"));
    public static final StreamCodec<FriendlyByteBuf, SyncJetpacksPayload> STREAM_CODEC = StreamCodec.composite(Jetpack.STREAM_CODEC.apply(ByteBufCodecs.list()), SyncJetpacksPayload::jetpacks, SyncJetpacksPayload::new);
    @Override public Type<SyncJetpacksPayload> type() { return TYPE; }
}
