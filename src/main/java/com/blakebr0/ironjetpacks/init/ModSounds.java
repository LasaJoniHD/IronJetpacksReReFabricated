package com.blakebr0.ironjetpacks.init;

import com.blakebr0.ironjetpacks.IronJetpacks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;

public final class ModSounds {
    public static SoundEvent JETPACK;

    public static void initialize() {
        JETPACK = net.minecraft.core.Registry.register(
                BuiltInRegistries.SOUND_EVENT,
                IronJetpacks.id("jetpack"),
                SoundEvent.createVariableRangeEvent(IronJetpacks.id("jetpack"))
        );
    }
}
