package com.blakebr0.ironjetpacks.crafting;

import com.blakebr0.ironjetpacks.IronJetpacks;
import com.blakebr0.ironjetpacks.config.ModConfigs;
import com.blakebr0.ironjetpacks.crafting.ingredient.JetpackComponentIngredient;
import com.blakebr0.ironjetpacks.crafting.ingredient.JetpackTierIngredient;
import com.blakebr0.ironjetpacks.crafting.recipe.JetpackUpgradeRecipe;
import com.blakebr0.ironjetpacks.init.ModItems;
import com.blakebr0.ironjetpacks.registry.Jetpack;
import com.blakebr0.ironjetpacks.registry.JetpackRegistry;
import com.blakebr0.ironjetpacks.util.JetpackUtils;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class DynamicRecipeManager {
    private DynamicRecipeManager() {
    }

    public static RecipeMap augment(RecipeMap recipes, HolderLookup.Provider registries) {
        JetpackRegistry.getInstance().loadJetpacks();
        var generated = new ArrayList<RecipeHolder<?>>();
        for (var jetpack : JetpackRegistry.getInstance().getJetpacks()) {
            add(generated, makeCellRecipe(jetpack, registries));
            add(generated, makeThrusterRecipe(jetpack, registries));
            add(generated, makeCapacitorRecipe(jetpack, registries));
            add(generated, makeJetpackRecipe(jetpack, registries));
            add(generated, makeJetpackUpgradeRecipe(jetpack, registries));
        }
        if (generated.isEmpty()) return recipes;
        var all = new ArrayList<RecipeHolder<?>>(recipes.values());
        all.addAll(generated);
        return RecipeMap.create(all);
    }

    private static void add(List<RecipeHolder<?>> recipes, RecipeHolder<?> recipe) {
        if (recipe != null) recipes.add(recipe);
    }

    private static RecipeHolder<ShapedRecipe> makeCellRecipe(Jetpack jetpack, HolderLookup.Provider registries) {
        if (!ModConfigs.ENABLE_CELL_RECIPES.get()) return null;
        var material = jetpack.getCraftingMaterial(registries);
        if (material == null) return null;
        var keys = Map.of('M', material, 'C', Ingredient.of(JetpackRegistry.getInstance().getCoilForTier(jetpack.tier)), 'R', tagIngredient(registries, "dusts/redstone"));
        return shaped(jetpack.name + "_cell", List.of(" R ", "MCM", " R "), keys,
                JetpackUtils.getItemForComponent(ModItems.CELL, jetpack), CraftingBookCategory.MISC, "ironjetpacks:cells");
    }

    private static RecipeHolder<ShapedRecipe> makeThrusterRecipe(Jetpack jetpack, HolderLookup.Provider registries) {
        if (!ModConfigs.ENABLE_THRUSTER_RECIPES.get()) return null;
        var material = jetpack.getCraftingMaterial(registries);
        if (material == null) return null;
        var keys = Map.of('M', material, 'C', Ingredient.of(JetpackRegistry.getInstance().getCoilForTier(jetpack.tier)),
                'E', componentIngredient(jetpack, JetpackComponentIngredient.ComponentType.CELL), 'F', Ingredient.of(Blocks.FURNACE));
        return shaped(jetpack.name + "_thruster", List.of("MCM", "CEC", "MFM"), keys,
                JetpackUtils.getItemForComponent(ModItems.THRUSTER, jetpack), CraftingBookCategory.MISC, "ironjetpacks:thrusters");
    }

    private static RecipeHolder<ShapedRecipe> makeCapacitorRecipe(Jetpack jetpack, HolderLookup.Provider registries) {
        if (!ModConfigs.ENABLE_CAPACITOR_RECIPES.get()) return null;
        var material = jetpack.getCraftingMaterial(registries);
        if (material == null) return null;
        var keys = Map.of('M', material, 'E', componentIngredient(jetpack, JetpackComponentIngredient.ComponentType.CELL));
        return shaped(jetpack.name + "_capacitor", List.of("MEM", "MEM", "MEM"), keys,
                JetpackUtils.getItemForComponent(ModItems.CAPACITOR, jetpack), CraftingBookCategory.MISC, "ironjetpacks:capacitors");
    }

    private static RecipeHolder<ShapedRecipe> makeJetpackRecipe(Jetpack jetpack, HolderLookup.Provider registries) {
        if (!ModConfigs.ENABLE_JETPACK_RECIPES.get() || jetpack.tier != JetpackRegistry.getInstance().getLowestTier()) return null;
        var material = jetpack.getCraftingMaterial(registries);
        if (material == null) return null;
        var keys = Map.of('M', material, 'C', componentIngredient(jetpack, JetpackComponentIngredient.ComponentType.CAPACITOR),
                'S', Ingredient.of(ModItems.STRAP), 'T', componentIngredient(jetpack, JetpackComponentIngredient.ComponentType.THRUSTER));
        return shaped(jetpack.name + "_jetpack", List.of("MCM", "MSM", "T T"), keys,
                JetpackUtils.getItemForJetpack(jetpack), CraftingBookCategory.EQUIPMENT, "ironjetpacks:jetpacks");
    }

    private static RecipeHolder<JetpackUpgradeRecipe> makeJetpackUpgradeRecipe(Jetpack jetpack, HolderLookup.Provider registries) {
        if (!ModConfigs.ENABLE_JETPACK_RECIPES.get() || jetpack.tier == JetpackRegistry.getInstance().getLowestTier()) return null;
        var material = jetpack.getCraftingMaterial(registries);
        if (material == null) return null;
        var keys = Map.of('M', material, 'C', componentIngredient(jetpack, JetpackComponentIngredient.ComponentType.CAPACITOR),
                'J', JetpackTierIngredient.of(jetpack.tier - 1), 'T', componentIngredient(jetpack, JetpackComponentIngredient.ComponentType.THRUSTER));
        var pattern = ShapedRecipePattern.of(keys, List.of("MCM", "MJM", "T T"));
        var result = JetpackUtils.getItemForJetpack(jetpack);
        return new RecipeHolder<>(key(jetpack.name + "_jetpack"), new JetpackUpgradeRecipe(
                new Recipe.CommonInfo(false), new CraftingRecipe.CraftingBookInfo(CraftingBookCategory.EQUIPMENT, "ironjetpacks:jetpacks"), pattern, result));
    }

    private static Ingredient componentIngredient(Jetpack jetpack, JetpackComponentIngredient.ComponentType type) {
        return new JetpackComponentIngredient(jetpack.getId(), type).toVanilla();
    }

    private static Ingredient tagIngredient(HolderLookup.Provider registries, String path) {
        var tag = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", path));
        return Ingredient.of(registries.lookupOrThrow(Registries.ITEM).getOrThrow(tag));
    }

    private static RecipeHolder<ShapedRecipe> shaped(String name, List<String> shape, Map<Character, Ingredient> keys,
                                                       net.minecraft.world.item.ItemStackTemplate result,
                                                       CraftingBookCategory category, String group) {
        return new RecipeHolder<>(key(name), new ShapedRecipe(new Recipe.CommonInfo(false),
                new CraftingRecipe.CraftingBookInfo(category, group), ShapedRecipePattern.of(keys, shape), result));
    }

    private static ResourceKey<Recipe<?>> key(String name) {
        return ResourceKey.create(Registries.RECIPE, IronJetpacks.id(name));
    }
}
