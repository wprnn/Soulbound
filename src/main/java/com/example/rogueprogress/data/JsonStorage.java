package com.example.rogueprogress.data;

import com.example.rogueprogress.RogueProgress;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import net.neoforged.fml.loading.FMLPaths;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

public final class JsonStorage {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path DATA_DIRECTORY = FMLPaths.CONFIGDIR.get().resolve(RogueProgress.MOD_ID);

    private JsonStorage() {
    }

    public static Path getDataDirectory() {
        return DATA_DIRECTORY;
    }

    public static Path getPlayerDataPath(UUID playerId) {
        return DATA_DIRECTORY.resolve(playerId + ".json");
    }

    public static Optional<ProgressData> load(UUID playerId) {
        Path path = getPlayerDataPath(playerId);

        if (!Files.exists(path)) {
            return Optional.empty();
        }

        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            ProgressData data = GSON.fromJson(reader, ProgressData.class);
            if (data == null) {
                return Optional.empty();
            }

            data.sanitize();
            return Optional.of(data);
        } catch (IOException | JsonSyntaxException exception) {
            RogueProgress.LOGGER.error("Failed to load Rogue Progress data for player {} from {}", playerId, path, exception);
            return Optional.empty();
        }
    }

    public static void save(UUID playerId, ProgressData data) {
        Path path = getPlayerDataPath(playerId);

        try {
            Files.createDirectories(DATA_DIRECTORY);
            data.sanitize();

            try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                GSON.toJson(data, writer);
            }
        } catch (IOException exception) {
            RogueProgress.LOGGER.error("Failed to save Rogue Progress data for player {} to {}", playerId, path, exception);
        }
    }
}
