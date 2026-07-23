package com.wprnn.soulbound.data;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 持久化数据与本次运行统计的中央访问点。
 *
 * <p>
 * 玩家进度通过 JSON 持久化到磁盘。仅本次运行内的数据（击杀数、获得灵魂数）保存在内存中，
 * 玩家死亡或登出时会自动重置。
 * </p>
 */
public final class ProgressManager {
    // 缓存避免在击杀、命令和 UI 交互时重复读取 JSON。
    private static final Map<UUID, ProgressData> CACHE = new ConcurrentHashMap<>();
    // 以下两个 Map 不入盘，仅表示本次 Roguelike 运行内的临时数据。
    private static final Map<UUID, Integer> RUN_KILLS = new ConcurrentHashMap<>();
    private static final Map<UUID, Integer> RUN_SOULS = new ConcurrentHashMap<>();

    private ProgressManager() {
    }

    public static ProgressData getOrCreate(UUID playerId) {
        return CACHE.computeIfAbsent(playerId, ProgressManager::loadOrCreate);
    }

    public static ProgressData reload(UUID playerId) {
        // 登录和打开主菜单时调用，以便外部编辑的 JSON 立即生效。
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
        // 累计本次运行总灵魂，含死亡结算奖励。
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
        // 平衡钩子：每 2 次击杀结算为 1 个灵魂。
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

    /**
     * 遍历内存缓存，强制所有玩家的属性值不超过当前上限（配置重载时调用）。
     * 每个属性通过 setter 重新钳制，再重新计算等级并写盘。
     */
    public static void enforceCapsAcrossCache() {
        for (Map.Entry<UUID, ProgressData> e : CACHE.entrySet()) {
            UUID playerId = e.getKey();
            ProgressData data = e.getValue();
            if (data == null)
                continue;
            // 重新钳制每项属性至当前上限
            data.setStrength(data.getStrength());
            data.setHealth(data.getHealth());
            data.setSpeed(data.getSpeed());
            data.setArmor(data.getArmor());
            data.setLuck(data.getLuck());
            // 重新计算展示等级
            data.recalculateLevelFromAttributes();
            // 写盘保存变更
            save(playerId, data);
        }
    }

    private static ProgressData loadOrCreate(UUID playerId) {
        return JsonStorage.load(playerId).orElseGet(() -> {
            // 首次进入：立即创建 JSON 文件，方便用户直接编辑查看。
            ProgressData data = new ProgressData();
            JsonStorage.save(playerId, data);
            return data;
        });
    }
}
