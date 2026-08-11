package com.blakebr0.ironjetpacks.crafting.ingredient;

import com.blakebr0.ironjetpacks.IronJetpacks;
import com.blakebr0.ironjetpacks.init.ModDataComponentTypes;
import com.blakebr0.ironjetpacks.init.ModItems;
import com.blakebr0.ironjetpacks.registry.Jetpack;
import com.blakebr0.ironjetpacks.registry.JetpackRegistry;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import java.util.stream.Stream;

public record JetpackComponentIngredient(Identifier jetpack, ComponentType type) implements CustomIngredient {
    public static final MapCodec<JetpackComponentIngredient> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            Identifier.CODEC.fieldOf("jetpack").forGetter(JetpackComponentIngredient::jetpack),
            ComponentType.CODEC.fieldOf("component").forGetter(JetpackComponentIngredient::type)
    ).apply(builder, JetpackComponentIngredient::new));
    public static final CustomIngredientSerializer<JetpackComponentIngredient> SERIALIZER = new CustomIngredientSerializer<>() {
        @Override public Identifier getIdentifier() { return IronJetpacks.id("jetpack_component"); }
        @Override public MapCodec<JetpackComponentIngredient> getCodec() { return CODEC; }
        @Override public StreamCodec<RegistryFriendlyByteBuf, JetpackComponentIngredient> getStreamCodec() { return StreamCodec.of((buf, value) -> { buf.writeIdentifier(value.jetpack); buf.writeEnum(value.type); }, buf -> new JetpackComponentIngredient(buf.readIdentifier(), buf.readEnum(ComponentType.class))); }
    };

    @Override public boolean test(ItemStack input) { return input.is(type.item) && jetpack.equals(input.get(ModDataComponentTypes.JETPACK_ID)); }
    @Override public Stream<Holder<Item>> items() { return Stream.of(type.item.builtInRegistryHolder()); }
    @Override public boolean requiresTesting() { return true; }
    @Override public CustomIngredientSerializer<?> getSerializer() { return SERIALIZER; }
    @Override public SlotDisplay display() {
        var jetpack = JetpackRegistry.getInstance().getJetpackById(this.jetpack);
        return jetpack == Jetpack.UNDEFINED
                ? SlotDisplay.Empty.INSTANCE
                : new SlotDisplay.ItemStackSlotDisplay(com.blakebr0.ironjetpacks.util.JetpackUtils.getItemForComponent(this.type.item, jetpack));
    }
    @Override public Ingredient toVanilla() { return CustomIngredient.super.toVanilla(); }

    public enum ComponentType {
        CELL(ModItems.CELL), THRUSTER(ModItems.THRUSTER), CAPACITOR(ModItems.CAPACITOR);
        public static final com.mojang.serialization.Codec<ComponentType> CODEC = com.mojang.serialization.Codec.STRING.xmap(ComponentType::valueOf, Enum::name);
        private final Item item;
        ComponentType(Item item) { this.item = item; }
    }
}
