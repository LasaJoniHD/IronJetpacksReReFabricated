package com.blakebr0.cucumber.iface;

import net.minecraft.world.item.ItemStack;

public interface IColored {
    default int getColor(int index) {
        return -1;
    }

    default int getColor(int index, ItemStack stack) {
        return this.getColor(index);
    }
}
