package com.ff.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigManager {
    private static final Gson GSON = new GsonBuilder()
        .setPrettyPrinting()
        .serializeNulls()
        .create();

    private static final Path CONFIG_PATH = FabricLoader.getInstance()
        .getConfigDir()
        .resolve("fluffyfox.json");

    private static Config config = new Config();

    public static void load() {
        if (Files.exists(CONFIG_PATH)) {
            try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
                Config loaded = GSON.fromJson(reader, Config.class);
                if (loaded != null) {
                    config = loaded;
                }
            } catch (IOException e) {
                System.err.println("[FluffyFox] Failed to read config, using defaults: " + e.getMessage());
            }
        } else {
            save();
        }
    }

    public static void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                GSON.toJson(config, writer);
            }
        } catch (IOException e) {
            System.err.println("[FluffyFox] Failed to save config: " + e.getMessage());
        }
    }

    public static Config get() {
        return config;
    }
}
