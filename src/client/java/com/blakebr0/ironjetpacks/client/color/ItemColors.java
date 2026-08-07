package com.blakebr0.ironjetpacks.client.color;

import com.blakebr0.cucumber.iface.IColored;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public record ItemColors(int index) implements ItemTintSource {
    public static final MapCodec<ItemColors> MAP_CODEC = RecordCodecBuilder.mapCodec(builder ->
            builder.group(Codec.INT.fieldOf("index").forGetter(ItemColors::index))
                    .apply(builder, ItemColors::new));

    @Override
    public int calculate(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity owner) {
        return stack.getItem() instanceof IColored colored ? colored.getColor(index, stack) : -1;
    }

    @Override
    public MapCodec<? extends ItemTintSource> type() {
        return MAP_CODEC;
    }
}
