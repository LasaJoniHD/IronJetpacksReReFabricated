package com.blakebr0.ironjetpacks.init;

import com.blakebr0.ironjetpacks.IronJetpacks;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;

public final class ModDataComponentTypes {
    // The data components below register themselves through their static field
    // initializers; the class is loaded early by item registration.

    public static final DataComponentType<net.minecraft.resources.Identifier> JETPACK_ID = register(
            "jetpack_id", DataComponentType.<net.minecraft.resources.Identifier>builder()
                    .persistent(net.minecraft.resources.Identifier.CODEC)
                    .networkSynchronized(net.minecraft.resources.Identifier.STREAM_CODEC)
                    .build());
    public static final DataComponentType<Boolean> JETPACK_ENGINE = register(
            "jetpack_engine", DataComponentType.<Boolean>builder()
                    .persistent(Codec.BOOL)
                    .networkSynchronized(ByteBufCodecs.BOOL)
                    .build());
    public static final DataComponentType<Boolean> JETPACK_HOVER = register(
            "jetpack_hover", DataComponentType.<Boolean>builder()
                    .persistent(Codec.BOOL)
                    .networkSynchronized(ByteBufCodecs.BOOL)
                    .build());
    public static final DataComponentType<Double> JETPACK_THROTTLE = register(
            "jetpack_throttle", DataComponentType.<Double>builder()
                    .persistent(Codec.DOUBLE)
                    .networkSynchronized(ByteBufCodecs.DOUBLE)
                    .build());
    public static final DataComponentType<Boolean> JETPACK_HUD = register(
            "jetpack_hud", DataComponentType.<Boolean>builder()
                    .persistent(Codec.BOOL)
                    .networkSynchronized(ByteBufCodecs.BOOL)
                    .build());

    private static <T> DataComponentType<T> register(String name, DataComponentType<T> type) {
        return net.minecraft.core.Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, IronJetpacks.id(name), type);
    }
}
