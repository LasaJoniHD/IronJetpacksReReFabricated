package com.blakebr0.ironjetpacks.crafting.ingredient;

import com.blakebr0.ironjetpacks.IronJetpacks;
import com.blakebr0.ironjetpacks.registry.JetpackRegistry;
import com.blakebr0.ironjetpacks.util.JetpackUtils;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import java.util.stream.Stream;

public record JetpackTierIngredient(int tier) implements CustomIngredient {
    public static final MapCodec<JetpackTierIngredient> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            com.mojang.serialization.Codec.INT.fieldOf("tier").forGetter(JetpackTierIngredient::tier)
    ).apply(builder, JetpackTierIngredient::new));
    public static final CustomIngredientSerializer<JetpackTierIngredient> SERIALIZER = new CustomIngredientSerializer<>() {
        @Override public net.minecraft.resources.Identifier getIdentifier() { return IronJetpacks.id("jetpack_tier"); }
        @Override public MapCodec<JetpackTierIngredient> getCodec() { return CODEC; }
        @Override public StreamCodec<RegistryFriendlyByteBuf, JetpackTierIngredient> getStreamCodec() { return StreamCodec.of((buf, value) -> buf.writeVarInt(value.tier), buf -> new JetpackTierIngredient(buf.readVarInt())); }
    };

    public static Ingredient of(int tier) {
        return new JetpackTierIngredient(tier).toVanilla();
    }

    @Override public boolean test(ItemStack stack) { return stack.getItem() == com.blakebr0.ironjetpacks.init.ModItems.JETPACK && JetpackUtils.getJetpack(stack).tier == tier; }
    @Override public Stream<Holder<Item>> items() { return Stream.of(com.blakebr0.ironjetpacks.init.ModItems.JETPACK.builtInRegistryHolder()); }
    @Override public boolean requiresTesting() { return true; }
    @Override public CustomIngredientSerializer<?> getSerializer() { return SERIALIZER; }
    @Override public Ingredient toVanilla() { return CustomIngredient.super.toVanilla(); }
}
