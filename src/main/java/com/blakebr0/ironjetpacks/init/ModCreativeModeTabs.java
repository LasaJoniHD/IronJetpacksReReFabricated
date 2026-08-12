package com.blakebr0.ironjetpacks.init;

import com.blakebr0.ironjetpacks.IronJetpacks;
import com.blakebr0.ironjetpacks.registry.JetpackRegistry;
import com.blakebr0.ironjetpacks.util.JetpackUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import team.reborn.energy.api.EnergyStorage;

public final class ModCreativeModeTabs {
    public static CreativeModeTab CREATIVE_TAB;

    public static void initialize() {
        CREATIVE_TAB = net.minecraft.core.Registry.register(
                BuiltInRegistries.CREATIVE_MODE_TAB,
                IronJetpacks.id("creative_tab"),
                CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
                        .title(Component.translatable("itemGroup.ironjetpacks"))
                        .icon(() -> {
                            var jetpack = JetpackRegistry.getInstance().getJetpacks().stream().findFirst().orElse(null);
                            return jetpack != null ? JetpackUtils.getItemForJetpack(jetpack).create() : new ItemStack(ModItems.STRAP);
                        })
                        .displayItems((parameters, output) -> {
                            output.accept(ModItems.STRAP);
                            output.accept(ModItems.BASIC_COIL);
                            output.accept(ModItems.ADVANCED_COIL);
                            output.accept(ModItems.ELITE_COIL);
                            output.accept(ModItems.ULTIMATE_COIL);
                            // Every configured variant is a distinct, usable stack because its
                            // jetpack_id component selects the tier. Static recipes expose these
                            // same variants to recipe viewers; keep them in the creative tab too.
                            for (var jetpack : JetpackRegistry.getInstance().getJetpacks()) {
                                output.accept(JetpackUtils.getItemForComponent(ModItems.CELL, jetpack).create());
                                output.accept(JetpackUtils.getItemForComponent(ModItems.THRUSTER, jetpack).create());
                                output.accept(JetpackUtils.getItemForComponent(ModItems.CAPACITOR, jetpack).create());
                                var emptyJetpack = JetpackUtils.getItemForJetpack(jetpack).create();
                                output.accept(emptyJetpack);

                                // Infinite jetpacks have zero capacity, so their empty and full
                                // stacks are identical. Adding both would crash the tab builder.
                                if (jetpack.capacity > 0) {
                                    var chargedJetpack = emptyJetpack.copy();
                                    chargedJetpack.set(EnergyStorage.ENERGY_COMPONENT, (long) jetpack.capacity);
                                    output.accept(chargedJetpack);
                                }
                            }
                        })
                        .build()
        );
    }
}
