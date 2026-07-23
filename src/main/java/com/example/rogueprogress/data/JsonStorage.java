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

/**
 * 跨世界进度的磁盘读写层。
 *
 * <p>数据存放在全局 Minecraft 配置目录而非存档文件夹中，
 * 因此删除或更换世界不会丢失元进度数据。</p>
 */
public final class JsonStorage {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    // 发行版路径：.minecraft/config/rogueprogress
    // 开发环境路径：run/config/rogueprogress
    private static final Path DATA_DIRECTORY = FMLPaths.CONFIGDIR.get().resolve(RogueProgress.MOD_ID);

    private JsonStorage() {
    }

    public static Path getPlayerDataPath(UUID playerId) {
        return DATA_DIRECTORY.resolve(playerId + ".json");
    }

    public static Optional<ProgressData> load(UUID playerId) {
        Path path = getPlayerDataPath(playerId);

        if (!Files.exists(path)) {
            // 新玩家首次无文件是正常情况，由 ProgressManager 创建默认数据。
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
            // JSON 解析失败时回退默认值，不阻止玩家加入。
            RogueProgress.LOGGER.error("Failed to load Rogue Progress data for player {} from {}", playerId, path, exception);
            return Optional.empty();
        }
    }

    public static void save(UUID playerId, ProgressData data) {
        Path path = getPlayerDataPath(playerId);

        try {
            Files.createDirectories(DATA_DIRECTORY);
            data.sanitize();

            // UTF-8 确保 UUID 及未来非 ASCII 文本稳定可读。
            try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                GSON.toJson(data, writer);
            }
        } catch (IOException exception) {
            RogueProgress.LOGGER.error("Failed to save Rogue Progress data for player {} to {}", playerId, path, exception);
        }
    }
}
