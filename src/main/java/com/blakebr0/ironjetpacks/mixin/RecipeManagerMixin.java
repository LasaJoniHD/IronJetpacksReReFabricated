package com.blakebr0.ironjetpacks.mixin;

import com.blakebr0.ironjetpacks.crafting.DynamicRecipeManager;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RecipeManager.class)
public abstract class RecipeManagerMixin {
    @Shadow private RecipeMap recipes;
    @Shadow @Final private HolderLookup.Provider registries;

    @Inject(method = "apply", at = @At("TAIL"))
    private void ironjetpacks$addDynamicRecipes(RecipeMap recipes, ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfo info) {
        this.recipes = DynamicRecipeManager.augment(this.recipes, this.registries);
    }
}
