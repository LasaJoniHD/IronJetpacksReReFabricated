package com.blakebr0.ironjetpacks.registry;

import com.blakebr0.ironjetpacks.IronJetpacks;
import com.blakebr0.ironjetpacks.init.ModItems;
import com.blakebr0.ironjetpacks.lib.ModJetpacks;
import com.blakebr0.ironjetpacks.network.payloads.SyncJetpacksPayload;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class JetpackRegistry {
    private static final JetpackRegistry INSTANCE = new JetpackRegistry();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private final Map<Identifier, Jetpack> jetpacks = new LinkedHashMap<>();
    private final ArrayList<Integer> tiers = new ArrayList<>();
    private int lowestTier = Integer.MAX_VALUE;

    public void register(Jetpack jetpack) {
        if (jetpacks.containsKey(jetpack.getId())) throw new IllegalArgumentException("Duplicate jetpack: " + jetpack.name);
        jetpacks.put(jetpack.getId(), jetpack);
        if (jetpack.tier > -1 && !tiers.contains(jetpack.tier)) { tiers.add(jetpack.tier); tiers.sort(Integer::compareTo); }
        if (jetpack.tier > -1 && jetpack.tier < lowestTier) lowestTier = jetpack.tier;
    }
    public List<Jetpack> getJetpacks() { return new ArrayList<>(jetpacks.values()); }
    public List<Integer> getAllTiers() { return tiers; }
    public Integer getLowestTier() { return lowestTier; }
    public Jetpack getDefaultJetpack() {
        return jetpacks.values().stream()
                .filter(jetpack -> !jetpack.creative && !jetpack.disabled)
                .min(Comparator.comparingInt(Jetpack::getTier))
                .orElse(Jetpack.UNDEFINED);
    }
    public Jetpack getJetpackById(Identifier id) { return jetpacks.getOrDefault(id, Jetpack.UNDEFINED); }

    public Item getCoilForTier(int tier) {
        float size = tiers.size(); float index = tiers.indexOf(tier);
        if (index / size > 0.75F) return ModItems.ULTIMATE_COIL;
        if (index / size > 0.5F) return ModItems.ELITE_COIL;
        if (index / size > 0.25F) return ModItems.ADVANCED_COIL;
        return ModItems.BASIC_COIL;
    }

    public void loadJetpacks(SyncJetpacksPayload payload) {
        jetpacks.clear(); tiers.clear(); lowestTier = Integer.MAX_VALUE;
        for (var jetpack : payload.jetpacks()) register(jetpack);
        IronJetpacks.LOGGER.info("Loaded {} jetpacks from the server", jetpacks.size());
    }

    public void syncToPlayer(ServerPlayer player) { ServerPlayNetworking.send(player, new SyncJetpacksPayload(getJetpacks())); }
    public void syncToAll(MinecraftServer server) { for (var player : PlayerLookup.all(server)) syncToPlayer(player); }

    public void writeDefaultJetpackFiles() {
        var dir = configDir().resolve("ironjetpacks/jetpacks").toFile();
        if (!dir.exists() && !dir.mkdirs()) {
            IronJetpacks.LOGGER.warn("Could not create jetpack configuration directory {}", dir);
            return;
        }
        for (var jetpack : ModJetpacks.getDefaults()) {
            var file = new File(dir, jetpack.name + ".json");
            if (!file.isFile()) writeJetpack(file, jetpack.toJson());
        }
    }

    public void loadJetpacks() {
        writeDefaultJetpackFiles();
        var dir = configDir().resolve("ironjetpacks/jetpacks").toFile();
        jetpacks.clear(); tiers.clear(); lowestTier = Integer.MAX_VALUE;
        var files = dir.listFiles((file, name) -> name.endsWith(".json"));
        if (files == null) return;
        var loaded = new ArrayList<Jetpack>();
        for (var file : files) {
            try (var reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
                var json = JsonParser.parseReader(reader).getAsJsonObject();
                writeJson(file, json);
                var jetpack = Jetpack.fromJson(json);
                if (!jetpack.disabled) loaded.add(jetpack);
            } catch (Exception e) { IronJetpacks.LOGGER.error("Could not load jetpack {}", file, e); }
        }
        loaded.sort(Comparator.comparingInt(Jetpack::getTier));
        for (var jetpack : loaded) register(jetpack);
        IronJetpacks.LOGGER.info("Loaded {} jetpack type(s)", jetpacks.size());
    }

    private static Path configDir() { return net.fabricmc.loader.api.FabricLoader.getInstance().getConfigDir(); }
    private static void writeJetpack(File file, com.google.gson.JsonObject json) { try (var writer = new FileWriter(file)) { GSON.toJson(json, writer); } catch (Exception e) { IronJetpacks.LOGGER.error("Could not write jetpack {}", file, e); } }
    private static void writeJson(File file, com.google.gson.JsonObject json) { writeJetpack(file, json); }
    public static JetpackRegistry getInstance() { return INSTANCE; }
}
