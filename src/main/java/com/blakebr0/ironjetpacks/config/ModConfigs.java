package com.blakebr0.ironjetpacks.config;

import com.blakebr0.ironjetpacks.IronJetpacks;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;

import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ModConfigs {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve("ironjetpacks.json");

    public static final BoolValue ENABLE_JETPACK_SOUNDS = new BoolValue("jetpackSounds", true);
    public static final BoolValue ENABLE_JETPACK_PARTICLES = new BoolValue("jetpackParticles", true);
    public static final BoolValue ENABLE_ADVANCED_INFO_TOOLTIPS = new BoolValue("advancedTooltips", true);
    public static final BoolValue ENABLE_HUD = new BoolValue("enableHud", true);
    public static final IntValue HUD_POSITION = new IntValue("hudPosition", 1, 0, 5);
    public static final IntValue HUD_OFFSET_X = new IntValue("hudOffsetX", 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
    public static final IntValue HUD_OFFSET_Y = new IntValue("hudOffsetY", 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
    public static final DoubleValue HUD_ANIMATION_SPEED = new DoubleValue("hudAnimationSpeed", 0.1D, 0.0D, 1.0D);
    public static final BoolValue SHOW_HUD_OVER_CHAT = new BoolValue("showHudOverChat", false);

    public static final BoolValue ENCHANTABLE_JETPACKS = new BoolValue("enchantableJetpacks", false);

    private ModConfigs() {
    }

    /** Loads the config and writes missing/default values back to disk. */
    public static void load() {
        var values = new JsonObject();
        if (Files.isRegularFile(CONFIG_FILE)) {
            try (Reader reader = Files.newBufferedReader(CONFIG_FILE, StandardCharsets.UTF_8)) {
                var parsed = JsonParser.parseReader(reader);
                if (parsed.isJsonObject()) values = parsed.getAsJsonObject();
            } catch (Exception e) {
                IronJetpacks.LOGGER.error("Could not read config {}; using defaults", CONFIG_FILE, e);
            }
        }

        ENABLE_JETPACK_SOUNDS.load(values);
        ENABLE_JETPACK_PARTICLES.load(values);
        ENABLE_ADVANCED_INFO_TOOLTIPS.load(values);
        ENABLE_HUD.load(values);
        HUD_POSITION.load(values);
        HUD_OFFSET_X.load(values);
        HUD_OFFSET_Y.load(values);
        HUD_ANIMATION_SPEED.load(values);
        SHOW_HUD_OVER_CHAT.load(values);
        ENCHANTABLE_JETPACKS.load(values);

        try {
            Files.createDirectories(CONFIG_FILE.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG_FILE, StandardCharsets.UTF_8)) {
                GSON.toJson(values, writer);
            }
        } catch (Exception e) {
            IronJetpacks.LOGGER.error("Could not write config {}", CONFIG_FILE, e);
        }
    }

    public static final class BoolValue {
        private final String key;
        private final boolean defaultValue;
        private boolean value;

        public BoolValue(String key, boolean defaultValue) {
            this.key = key;
            this.defaultValue = defaultValue;
            this.value = defaultValue;
        }

        private void load(JsonObject values) {
            var json = values.get(key);
            value = json != null && json.isJsonPrimitive() && json.getAsJsonPrimitive().isBoolean()
                    ? json.getAsBoolean() : defaultValue;
            values.addProperty(key, value);
        }

        public boolean get() {
            return value;
        }
    }

    public static final class IntValue {
        private final String key;
        private final int defaultValue;
        private final int min;
        private final int max;
        private int value;

        public IntValue(String key, int defaultValue, int min, int max) {
            this.key = key;
            this.defaultValue = defaultValue;
            this.min = min;
            this.max = max;
            this.value = defaultValue;
        }

        private void load(JsonObject values) {
            var json = values.get(key);
            value = defaultValue;
            if (json != null && json.isJsonPrimitive() && json.getAsJsonPrimitive().isNumber()) {
                try {
                    var number = json.getAsJsonPrimitive().getAsBigDecimal();
                    if (number.stripTrailingZeros().scale() <= 0) {
                        value = Math.clamp(number.intValueExact(), min, max);
                    }
                } catch (ArithmeticException ignored) {
                    // Keep the default for non-integral or out-of-range values.
                }
            }
            values.addProperty(key, value);
        }

        public int get() {
            return value;
        }
    }

    public static final class DoubleValue {
        private final String key;
        private final double defaultValue;
        private final double min;
        private final double max;
        private double value;

        public DoubleValue(String key, double defaultValue, double min, double max) {
            this.key = key;
            this.defaultValue = defaultValue;
            this.min = min;
            this.max = max;
            this.value = defaultValue;
        }

        private void load(JsonObject values) {
            var json = values.get(key);
            value = defaultValue;
            if (json != null && json.isJsonPrimitive() && json.getAsJsonPrimitive().isNumber()) {
                double candidate = json.getAsDouble();
                if (Double.isFinite(candidate)) value = Math.clamp(candidate, min, max);
            }
            values.addProperty(key, value);
        }

        public double get() {
            return value;
        }
    }
}
