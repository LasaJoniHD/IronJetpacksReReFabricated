package com.blakebr0.ironjetpacks.handler;

import com.blakebr0.ironjetpacks.init.ModItems;
import com.blakebr0.ironjetpacks.util.JetpackEnergyStorage;
import com.blakebr0.ironjetpacks.util.JetpackUtils;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import team.reborn.energy.api.EnergyStorage;

public final class RegisterCapabilityHandler {
    public static void initialize() {
        EnergyStorage.ITEM.registerForItems(
                (stack, context) -> {
                    if (stack.getItem() != ModItems.JETPACK) return null;
                    var jetpack = JetpackUtils.getJetpack(stack);
                    return JetpackEnergyStorage.forContext(context, jetpack.capacity);
                },
                ModItems.JETPACK
        );
    }
}
