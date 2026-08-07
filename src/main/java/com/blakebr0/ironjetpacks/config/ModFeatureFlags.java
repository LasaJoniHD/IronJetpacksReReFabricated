package com.blakebr0.ironjetpacks.config;

public final class ModFeatureFlags {
    public static final FeatureFlag CAPACITOR_RECIPES = new FeatureFlag(ModConfigs.ENABLE_CAPACITOR_RECIPES);
    public static final FeatureFlag CELL_RECIPES = new FeatureFlag(ModConfigs.ENABLE_CELL_RECIPES);
    public static final FeatureFlag CURIOS_INTEGRATION = new FeatureFlag(ModConfigs.ENABLE_CURIOS_INTEGRATION);
    public static final FeatureFlag ENCHANTABLE_JETPACKS = new FeatureFlag(ModConfigs.ENCHANTABLE_JETPACKS);
    public static final FeatureFlag HUD = new FeatureFlag(ModConfigs.ENABLE_HUD);
    public static final FeatureFlag JETPACK_PARTICLES = new FeatureFlag(ModConfigs.ENABLE_JETPACK_PARTICLES);
    public static final FeatureFlag JETPACK_RECIPES = new FeatureFlag(ModConfigs.ENABLE_JETPACK_RECIPES);
    public static final FeatureFlag JETPACK_SOUNDS = new FeatureFlag(ModConfigs.ENABLE_JETPACK_SOUNDS);
    public static final FeatureFlag THRUSTER_RECIPES = new FeatureFlag(ModConfigs.ENABLE_THRUSTER_RECIPES);

    public record FeatureFlag(ModConfigs.BoolValue value) {
        public boolean enabled() {
            return value.get();
        }
    }
}
