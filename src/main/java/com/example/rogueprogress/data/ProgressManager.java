package com.example.rogueprogress.data;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ProgressManager {
    private static final Map<UUID, ProgressData> CACHE = new ConcurrentHashMap<>();
    private static final Map<UUID, Integer> RUN_KILLS = new ConcurrentHashMap<>();
    private static final Map<UUID, Integer> RUN_SOULS = new ConcurrentHashMap<>();

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

    public static void addSoul(UUID playerId, int amount) {
        if (amount <= 0) {
            return;
        }

        ProgressData data = getOrCreate(playerId);
        data.addSoul(amount);
        RUN_SOULS.merge(playerId, amount, Integer::sum);
        save(playerId, data);
    }

    public static void recordKill(UUID playerId) {
        RUN_KILLS.merge(playerId, 1, Integer::sum);
    }

    public static int getRunKills(UUID playerId) {
        return RUN_KILLS.getOrDefault(playerId, 0);
    }

    public static int getRunSouls(UUID playerId) {
        return RUN_SOULS.getOrDefault(playerId, 0);
    }

    public static int calculateDeathSettlementSoul(UUID playerId) {
        return getRunKills(playerId) / 2;
    }

    public static void resetRun(UUID playerId) {
        RUN_KILLS.remove(playerId);
        RUN_SOULS.remove(playerId);
    }

    public static void unload(UUID playerId) {
        save(playerId);
        resetRun(playerId);
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
