package com.blakebr0.cucumber.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;

import java.text.NumberFormat;
import java.util.Locale;

public final class Formatting {
    private Formatting() {
    }

    public static MutableComponent number(Object value) {
        if (value instanceof Number number) {
            return Component.literal(NumberFormat.getIntegerInstance(Locale.US).format(number.longValue()));
        }
        return Component.literal(String.valueOf(value));
    }

    public static MutableComponent percent(Object value) {
        return Component.literal(String.valueOf(value) + "%");
    }

    public static MutableComponent energy(Object value) {
        return Component.literal(formatEnergy(((Number) value).longValue()));
    }

    public static MutableComponent perTick(Object value) {
        return Component.literal(String.valueOf(value) + "/t");
    }

    public static MutableComponent energyPerTick(Object value) {
        return Component.literal(formatEnergy(((Number) value).longValue()) + "/t");
    }

    /**
     * Formats Tech Reborn energy using the same compact units as the original
     * Iron Jetpacks HUD: FE, k FE, M FE, and G FE.
     */
    public static String formatEnergy(long energy) {
        if (energy >= 1_000_000_000L) {
            return compact(energy, 1_000_000_000L) + "G FE";
        }
        if (energy >= 1_000_000L) {
            return compact(energy, 1_000_000L) + "M FE";
        }
        if (energy >= 1_000L) {
            return compact(energy, 1_000L) + "k FE";
        }
        return energy + " FE";
    }

    private static String compact(long energy, long unit) {
        long whole = energy / unit;
        long fractional = (energy % unit) / (unit / 10L);
        return fractional == 0 ? String.valueOf(whole) : whole + "." + fractional;
    }

    public static MutableComponent itemWithCount(ItemStack stack) {
        return itemWithCount(stack, stack.getCount());
    }

    public static MutableComponent itemWithCount(ItemStack stack, int count) {
        return Component.literal(count + "x ").append(stack.getHoverName());
    }
}
