package com.example.rogueprogress.data;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ProgressManager {
    private static final Map<UUID, ProgressData> CACHE = new ConcurrentHashMap<>();

    private ProgressManager() {
    }

    public static ProgressData getOrCreate(UUID playerId) {
        return CACHE.computeIfAbsent(playerId, ProgressManager::loadOrCreate);
    }

    public static ProgressData reload(UUID playerId) {
        ProgressData data = loadOrCreate(playerId);
        CACHE.put(playerId, data);
        return data;
    }

    public static void save(UUID playerId) {
        ProgressData data = CACHE.get(playerId);
        if (data != null) {
            JsonStorage.save(playerId, data);
        }
    }

    public static void save(UUID playerId, ProgressData data) {
        CACHE.put(playerId, data);
        JsonStorage.save(playerId, data);
    }

    public static void unload(UUID playerId) {
        save(playerId);
        CACHE.remove(playerId);
    }

    private static ProgressData loadOrCreate(UUID playerId) {
        return JsonStorage.load(playerId).orElseGet(() -> {
            ProgressData data = ProgressData.createDefault();
            JsonStorage.save(playerId, data);
            return data;
        });
    }
}
