package com.blakebr0.ironjetpacks.init;

import com.blakebr0.ironjetpacks.IronJetpacks;
import com.blakebr0.ironjetpacks.crafting.recipe.JetpackUpgradeRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;

public final class ModRecipeSerializers {
    public static RecipeSerializer<JetpackUpgradeRecipe> CRAFTING_JETPACK_UPGRADE;

    public static void initialize() {
        CRAFTING_JETPACK_UPGRADE = net.minecraft.core.Registry.register(
                BuiltInRegistries.RECIPE_SERIALIZER,
                IronJetpacks.id("crafting_jetpack_upgrade"),
                JetpackUpgradeRecipe.SERIALIZER
        );
    }
}
