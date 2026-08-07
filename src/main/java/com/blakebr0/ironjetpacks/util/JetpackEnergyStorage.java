package com.blakebr0.ironjetpacks.util;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;
import team.reborn.energy.api.EnergyStorage;

/**
 * Energy storage that persists its amount in the vanilla item data-component map.
 *
 * <p>The transfer API's constant context is useful for lookups, but it cannot
 * write changes back to the original stack. Jetpack flight therefore uses the
 * stack-backed constructor, while the context-backed constructor keeps the
 * public item Energy API transactional for real inventory slots.</p>
 */
public final class JetpackEnergyStorage implements EnergyStorage {
    private final ItemStack stack;
    private final long capacity;

    public JetpackEnergyStorage(ItemStack stack, long capacity) {
        this.stack = stack;
        this.capacity = Math.max(0L, capacity);
    }

    public static EnergyStorage forContext(ContainerItemContext context, long capacity) {
        return new ContextStorage(context, capacity);
    }

    @Override
    public boolean supportsInsertion() {
        return capacity > 0;
    }

    @Override
    public long insert(long maxAmount, TransactionContext transaction) {
        if (maxAmount <= 0 || capacity <= 0) return 0;
        long before = getAmount();
        long inserted = Math.min(maxAmount, capacity - before);
        if (inserted <= 0) return 0;
        setAmount(before + inserted);
        transaction.addCloseCallback((context, result) -> {
            if (!result.wasCommitted()) setAmount(before);
        });
        return inserted;
    }

    @Override
    public boolean supportsExtraction() {
        return capacity > 0;
    }

    @Override
    public long extract(long maxAmount, TransactionContext transaction) {
        if (maxAmount <= 0) return 0;
        long before = getAmount();
        long extracted = Math.min(maxAmount, before);
        if (extracted <= 0) return 0;
        setAmount(before - extracted);
        transaction.addCloseCallback((context, result) -> {
            if (!result.wasCommitted()) setAmount(before);
        });
        return extracted;
    }

    @Override
    public long getAmount() {
        return Math.clamp(stack.getOrDefault(energyComponent(), capacity), 0L, capacity);
    }

    @Override
    public long getCapacity() {
        return capacity;
    }

    private void setAmount(long amount) {
        stack.set(energyComponent(), Math.clamp(amount, 0L, capacity));
    }

    @SuppressWarnings("unchecked")
    private static DataComponentType<Long> energyComponent() {
        return EnergyStorage.ENERGY_COMPONENT;
    }

    private static final class ContextStorage implements EnergyStorage {
        private final ContainerItemContext context;
        private final long capacity;

        private ContextStorage(ContainerItemContext context, long capacity) {
            this.context = context;
            this.capacity = Math.max(0L, capacity);
        }

        private ItemStack stack() {
            return context.getItemVariant().toStack();
        }

        private long amount() {
            return Math.clamp(stack().getOrDefault(energyComponent(), capacity), 0L, capacity);
        }

        private boolean replace(long amount, TransactionContext transaction) {
            var updated = stack();
            updated.set(energyComponent(), Math.clamp(amount, 0L, capacity));
            return context.exchange(ItemVariant.of(updated), 1L, transaction) == 1L;
        }

        @Override
        public boolean supportsInsertion() {
            return capacity > 0;
        }

        @Override
        public long insert(long maxAmount, TransactionContext transaction) {
            if (maxAmount <= 0 || capacity <= 0) return 0;
            long before = amount();
            long inserted = Math.min(maxAmount, capacity - before);
            return inserted > 0 && replace(before + inserted, transaction) ? inserted : 0;
        }

        @Override
        public boolean supportsExtraction() {
            return capacity > 0;
        }

        @Override
        public long extract(long maxAmount, TransactionContext transaction) {
            if (maxAmount <= 0) return 0;
            long before = amount();
            long extracted = Math.min(maxAmount, before);
            return extracted > 0 && replace(before - extracted, transaction) ? extracted : 0;
        }

        @Override
        public long getAmount() {
            return amount();
        }

        @Override
        public long getCapacity() {
            return capacity;
        }
    }
}
