package com.blakebr0.cucumber.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class Tooltip {
    private final String key;
    private final ChatFormatting defaultColor;
    private Object[] args = new Object[0];
    private ChatFormatting color;

    public Tooltip(String key) {
        this(key, null);
    }

    public Tooltip(String key, ChatFormatting defaultColor) {
        this.key = key;
        this.defaultColor = defaultColor;
        this.color = defaultColor;
    }

    public Tooltip args(Object... args) {
        var tooltip = new Tooltip(this.key, this.defaultColor);
        tooltip.args = args;
        tooltip.color = this.color;
        return tooltip;
    }

    public Tooltip color(ChatFormatting color) {
        var tooltip = new Tooltip(this.key, this.defaultColor);
        tooltip.args = this.args;
        tooltip.color = color;
        return tooltip;
    }

    public MutableComponent toComponent() {
        var component = Component.translatable(this.key, this.args);
        return this.color == null ? component : component.withStyle(this.color);
    }

    @Override
    public String toString() {
        return this.toComponent().getString();
    }
}
