package com.blakebr0.ironjetpacks.util;

import com.blakebr0.ironjetpacks.config.ModConfigs;
import com.blakebr0.ironjetpacks.handler.InputHandler;
import com.blakebr0.ironjetpacks.init.ModDataComponentTypes;
import com.blakebr0.ironjetpacks.init.ModItems;
import com.blakebr0.ironjetpacks.item.JetpackItem;
import com.blakebr0.ironjetpacks.registry.Jetpack;
import com.blakebr0.ironjetpacks.registry.JetpackRegistry;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import team.reborn.energy.api.EnergyStorage;

public final class JetpackUtils {
    public static boolean isFlying(Player player) {
        if (player.isSpectator()) return false;
        var stack = getEquippedJetpack(player);
        if (!stack.isEmpty() && isEngineOn(stack)) {
            var jetpack = getJetpack(stack);
            var energy = getEnergyStorage(stack);
            if (energy.getAmount() > 0 || player.isCreative() || jetpack.creative) {
                return isHovering(stack)
                        ? !player.onGround()
                        : InputHandler.isHoldingUp(player)
                                || (InputHandler.isHoldingDown(player) && !player.onGround());
            }
        }
        return false;
    }

    public static ItemStack getEquippedJetpack(Player player) {
        var stack = player.getItemBySlot(EquipmentSlot.CHEST);
        return !stack.isEmpty() && stack.getItem() instanceof JetpackItem ? stack : ItemStack.EMPTY;
    }

    public static EnergyStorage getEnergyStorage(ItemStack stack) {
        if (!(stack.getItem() instanceof JetpackItem)) return EnergyStorage.EMPTY;
        return new JetpackEnergyStorage(stack, getJetpack(stack).capacity);
    }

    /**
     * Jetpacks start with their engine disabled unless the stack explicitly
     * stores an enabled state.
     */
    public static boolean isEngineOn(ItemStack stack) { return stack.getOrDefault(ModDataComponentTypes.JETPACK_ENGINE, false); }
    public static boolean toggleEngine(ItemStack stack) { boolean next = !isEngineOn(stack); setEngine(stack, next); return next; }
    public static void setEngine(ItemStack stack, boolean enabled) { stack.set(ModDataComponentTypes.JETPACK_ENGINE, enabled); }
    public static boolean isHovering(ItemStack stack) { return stack.getOrDefault(ModDataComponentTypes.JETPACK_HOVER, false); }
    public static boolean toggleHover(ItemStack stack) { boolean next = !isHovering(stack); setHover(stack, next); return next; }
    public static void setHover(ItemStack stack, boolean enabled) { stack.set(ModDataComponentTypes.JETPACK_HOVER, enabled); }
    public static double getThrottle(ItemStack stack) { return stack.getOrDefault(ModDataComponentTypes.JETPACK_THROTTLE, 1.0D); }

    public static double incrementThrottle(ItemStack stack) {
        double throttle = getThrottle(stack);
        if (throttle < 1.0D) { throttle = Math.min(throttle + 0.1D, 1.0D); stack.set(ModDataComponentTypes.JETPACK_THROTTLE, throttle); }
        return throttle;
    }

    public static double decrementThrottle(ItemStack stack) {
        double throttle = getThrottle(stack);
        if (throttle > 0.1D) { throttle = Math.max(throttle - 0.1D, 0.1D); stack.set(ModDataComponentTypes.JETPACK_THROTTLE, throttle); }
        return throttle;
    }

    public static boolean isHUDEnabled(ItemStack stack) { return stack.getOrDefault(ModDataComponentTypes.JETPACK_HUD, true); }
    public static boolean toggleHUD(ItemStack stack) { boolean current = isHUDEnabled(stack); stack.set(ModDataComponentTypes.JETPACK_HUD, !current); return !current; }

    public static ItemStackTemplate getItemForJetpack(Jetpack jetpack) {
        var components = DataComponentPatch.builder().set(ModDataComponentTypes.JETPACK_ID, jetpack.getId()).build();
        return new ItemStackTemplate(ModItems.JETPACK, components);
    }

    public static ItemStackTemplate getItemForComponent(Item item, Jetpack jetpack) {
        var components = DataComponentPatch.builder().set(ModDataComponentTypes.JETPACK_ID, jetpack.getId()).build();
        return new ItemStackTemplate(item, components);
    }

    public static Jetpack getJetpack(ItemStack stack) {
        var id = stack.get(ModDataComponentTypes.JETPACK_ID);
        return id == null ? Jetpack.UNDEFINED : JetpackRegistry.getInstance().getJetpackById(id);
    }
}
