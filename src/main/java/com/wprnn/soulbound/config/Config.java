package com.wprnn.soulbound.config;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 模组配置定义。所有配置项均通过 {@link ModConfigSpec} 声明，
 * 支持运行时上限覆盖（用于 /Soulbound caps set 命令），
 * 配置重载时自动清理运行时覆盖并重新钳制玩家属性。
 */
public final class Config {
    private Config() {}

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    // ── 属性上限（Caps）─────────────────────────────────────────────────

    public static final ModConfigSpec.IntValue STRENGTH_CAP;
    public static final int STRENGTH_CAP_DEFAULT = 10;
    private static final String STRENGTH_CAP_NAME = "strengthCap";
    private static final String STRENGTH_CAP_COMMENT = "Maximum Strength attribute value players can reach through upgrades";

    public static final ModConfigSpec.IntValue HEALTH_CAP;
    public static final int HEALTH_CAP_DEFAULT = 20;
    private static final String HEALTH_CAP_NAME = "healthCap";
    private static final String HEALTH_CAP_COMMENT = "Maximum Health attribute value players can reach through upgrades";

    public static final ModConfigSpec.IntValue SPEED_CAP;
    public static final int SPEED_CAP_DEFAULT = 10;
    private static final String SPEED_CAP_NAME = "speedCap";
    private static final String SPEED_CAP_COMMENT = "Maximum Speed attribute value players can reach through upgrades";

    public static final ModConfigSpec.IntValue ARMOR_CAP;
    public static final int ARMOR_CAP_DEFAULT = 15;
    private static final String ARMOR_CAP_NAME = "armorCap";
    private static final String ARMOR_CAP_COMMENT = "Maximum Armor attribute value players can reach through upgrades";

    public static final ModConfigSpec.IntValue LUCK_CAP;
    public static final int LUCK_CAP_DEFAULT = 10;
    private static final String LUCK_CAP_NAME = "luckCap";
    private static final String LUCK_CAP_COMMENT = "Maximum Luck attribute value players can reach through upgrades";

    // ── 力量加成 ───────────────────────────────────────────────────────

    public static final ModConfigSpec.DoubleValue STRENGTH_FLAT_PER_LEVEL;
    public static final double STRENGTH_FLAT_PER_LEVEL_DEFAULT = 0.0D;
    private static final String STRENGTH_FLAT_PER_LEVEL_NAME = "strengthFlatPerLevel";
    private static final String STRENGTH_FLAT_PER_LEVEL_COMMENT = "Flat damage added per Strength level (before tier break). e.g. 1.0 = +1 attack damage";

    public static final ModConfigSpec.DoubleValue STRENGTH_PERCENT_PER_LEVEL;
    public static final double STRENGTH_PERCENT_PER_LEVEL_DEFAULT = 0.03D;
    private static final String STRENGTH_PERCENT_PER_LEVEL_NAME = "strengthPercentPerLevel";
    private static final String STRENGTH_PERCENT_PER_LEVEL_COMMENT = "Percent attack damage bonus per Strength level (before tier break), as decimal. e.g. 0.03 = +3%";

    public static final ModConfigSpec.IntValue STRENGTH_TIER_BREAK;
    public static final int STRENGTH_TIER_BREAK_DEFAULT = 5;
    private static final String STRENGTH_TIER_BREAK_NAME = "strengthTierBreak";
    private static final String STRENGTH_TIER_BREAK_COMMENT = "Level at which Strength switches from first-tier to later-tier bonuses. 0 to disable tiering";

    public static final ModConfigSpec.DoubleValue STRENGTH_FLAT_PER_LEVEL_LATER;
    public static final double STRENGTH_FLAT_PER_LEVEL_LATER_DEFAULT = 0.0D;
    private static final String STRENGTH_FLAT_PER_LEVEL_LATER_NAME = "strengthFlatPerLevelLater";
    private static final String STRENGTH_FLAT_PER_LEVEL_LATER_COMMENT = "Flat damage added per Strength level after tier break";

    public static final ModConfigSpec.DoubleValue STRENGTH_PERCENT_PER_LEVEL_LATER;
    public static final double STRENGTH_PERCENT_PER_LEVEL_LATER_DEFAULT = 0.015D;
    private static final String STRENGTH_PERCENT_PER_LEVEL_LATER_NAME = "strengthPercentPerLevelLater";
    private static final String STRENGTH_PERCENT_PER_LEVEL_LATER_COMMENT = "Percent attack damage bonus per Strength level after tier break, as decimal";

    // ── 生命加成 ───────────────────────────────────────────────────────

    public static final ModConfigSpec.DoubleValue HEALTH_FLAT_PER_LEVEL;
    public static final double HEALTH_FLAT_PER_LEVEL_DEFAULT = 2.0D;
    private static final String HEALTH_FLAT_PER_LEVEL_NAME = "healthFlatPerLevel";
    private static final String HEALTH_FLAT_PER_LEVEL_COMMENT = "Flat max health added per Health level. e.g. 2.0 = +1 heart";

    public static final ModConfigSpec.DoubleValue HEALTH_PERCENT_PER_LEVEL;
    public static final double HEALTH_PERCENT_PER_LEVEL_DEFAULT = 0.0D;
    private static final String HEALTH_PERCENT_PER_LEVEL_NAME = "healthPercentPerLevel";
    private static final String HEALTH_PERCENT_PER_LEVEL_COMMENT = "Percent max health bonus per Health level, as decimal";

    // ── 速度加成 ───────────────────────────────────────────────────────

    public static final ModConfigSpec.DoubleValue SPEED_FLAT_PER_LEVEL;
    public static final double SPEED_FLAT_PER_LEVEL_DEFAULT = 0.0D;
    private static final String SPEED_FLAT_PER_LEVEL_NAME = "speedFlatPerLevel";
    private static final String SPEED_FLAT_PER_LEVEL_COMMENT = "Flat movement speed added per Speed level";

    public static final ModConfigSpec.DoubleValue SPEED_PERCENT_PER_LEVEL;
    public static final double SPEED_PERCENT_PER_LEVEL_DEFAULT = 0.01D;
    private static final String SPEED_PERCENT_PER_LEVEL_NAME = "speedPercentPerLevel";
    private static final String SPEED_PERCENT_PER_LEVEL_COMMENT = "Percent movement speed bonus per Speed level, as decimal. e.g. 0.01 = +1%";

    // ── 护甲加成 ───────────────────────────────────────────────────────

    public static final ModConfigSpec.DoubleValue ARMOR_FLAT_PER_LEVEL;
    public static final double ARMOR_FLAT_PER_LEVEL_DEFAULT = 0.0D;
    private static final String ARMOR_FLAT_PER_LEVEL_NAME = "armorFlatPerLevel";
    private static final String ARMOR_FLAT_PER_LEVEL_COMMENT = "Flat armor added per Armor level (before tier break)";

    public static final ModConfigSpec.DoubleValue ARMOR_PERCENT_PER_LEVEL;
    public static final double ARMOR_PERCENT_PER_LEVEL_DEFAULT = 0.04D;
    private static final String ARMOR_PERCENT_PER_LEVEL_NAME = "armorPercentPerLevel";
    private static final String ARMOR_PERCENT_PER_LEVEL_COMMENT = "Percent armor bonus per Armor level (before tier break), as decimal. e.g. 0.04 = +4%";

    public static final ModConfigSpec.IntValue ARMOR_TIER_BREAK;
    public static final int ARMOR_TIER_BREAK_DEFAULT = 5;
    private static final String ARMOR_TIER_BREAK_NAME = "armorTierBreak";
    private static final String ARMOR_TIER_BREAK_COMMENT = "Level at which Armor switches from first-tier to later-tier bonuses. 0 to disable tiering";

    public static final ModConfigSpec.DoubleValue ARMOR_FLAT_PER_LEVEL_LATER;
    public static final double ARMOR_FLAT_PER_LEVEL_LATER_DEFAULT = 0.0D;
    private static final String ARMOR_FLAT_PER_LEVEL_LATER_NAME = "armorFlatPerLevelLater";
    private static final String ARMOR_FLAT_PER_LEVEL_LATER_COMMENT = "Flat armor added per Armor level after tier break";

    public static final ModConfigSpec.DoubleValue ARMOR_PERCENT_PER_LEVEL_LATER;
    public static final double ARMOR_PERCENT_PER_LEVEL_LATER_DEFAULT = 0.02D;
    private static final String ARMOR_PERCENT_PER_LEVEL_LATER_NAME = "armorPercentPerLevelLater";
    private static final String ARMOR_PERCENT_PER_LEVEL_LATER_COMMENT = "Percent armor bonus per Armor level after tier break, as decimal";

    // ── 幸运加成 ───────────────────────────────────────────────────────

    public static final ModConfigSpec.DoubleValue LUCK_FLAT_PER_LEVEL;
    public static final double LUCK_FLAT_PER_LEVEL_DEFAULT = 1.0D;
    private static final String LUCK_FLAT_PER_LEVEL_NAME = "luckFlatPerLevel";
    private static final String LUCK_FLAT_PER_LEVEL_COMMENT = "Flat Luck attribute points added per Luck level";

    public static final ModConfigSpec.DoubleValue LUCK_PERCENT_PER_LEVEL;
    public static final double LUCK_PERCENT_PER_LEVEL_DEFAULT = 0.0D;
    private static final String LUCK_PERCENT_PER_LEVEL_NAME = "luckPercentPerLevel";
    private static final String LUCK_PERCENT_PER_LEVEL_COMMENT = "Percent luck bonus per Luck level, as decimal";

    // ── 其他 ───────────────────────────────────────────────────────────

    public static final ModConfigSpec.IntValue REFUND_PERCENT;
    public static final int REFUND_PERCENT_DEFAULT = 90;
    private static final String REFUND_PERCENT_NAME = "refundPercent";
    private static final String REFUND_PERCENT_COMMENT = "Refund percentage when resetting attributes (0-100)";

    static {
        STRENGTH_CAP = BUILDER
                .comment(STRENGTH_CAP_COMMENT)
                .defineInRange(STRENGTH_CAP_NAME, STRENGTH_CAP_DEFAULT, 0, Integer.MAX_VALUE);
        HEALTH_CAP = BUILDER
                .comment(HEALTH_CAP_COMMENT)
                .defineInRange(HEALTH_CAP_NAME, HEALTH_CAP_DEFAULT, 0, Integer.MAX_VALUE);
        SPEED_CAP = BUILDER
                .comment(SPEED_CAP_COMMENT)
                .defineInRange(SPEED_CAP_NAME, SPEED_CAP_DEFAULT, 0, Integer.MAX_VALUE);
        ARMOR_CAP = BUILDER
                .comment(ARMOR_CAP_COMMENT)
                .defineInRange(ARMOR_CAP_NAME, ARMOR_CAP_DEFAULT, 0, Integer.MAX_VALUE);
        LUCK_CAP = BUILDER
                .comment(LUCK_CAP_COMMENT)
                .defineInRange(LUCK_CAP_NAME, LUCK_CAP_DEFAULT, 0, Integer.MAX_VALUE);

        STRENGTH_FLAT_PER_LEVEL = BUILDER
                .comment(STRENGTH_FLAT_PER_LEVEL_COMMENT)
                .defineInRange(STRENGTH_FLAT_PER_LEVEL_NAME, STRENGTH_FLAT_PER_LEVEL_DEFAULT, 0.0D, Double.MAX_VALUE);
        STRENGTH_PERCENT_PER_LEVEL = BUILDER
                .comment(STRENGTH_PERCENT_PER_LEVEL_COMMENT)
                .defineInRange(STRENGTH_PERCENT_PER_LEVEL_NAME, STRENGTH_PERCENT_PER_LEVEL_DEFAULT, 0.0D, Double.MAX_VALUE);
        STRENGTH_TIER_BREAK = BUILDER
                .comment(STRENGTH_TIER_BREAK_COMMENT)
                .defineInRange(STRENGTH_TIER_BREAK_NAME, STRENGTH_TIER_BREAK_DEFAULT, 0, Integer.MAX_VALUE);
        STRENGTH_FLAT_PER_LEVEL_LATER = BUILDER
                .comment(STRENGTH_FLAT_PER_LEVEL_LATER_COMMENT)
                .defineInRange(STRENGTH_FLAT_PER_LEVEL_LATER_NAME, STRENGTH_FLAT_PER_LEVEL_LATER_DEFAULT, 0.0D, Double.MAX_VALUE);
        STRENGTH_PERCENT_PER_LEVEL_LATER = BUILDER
                .comment(STRENGTH_PERCENT_PER_LEVEL_LATER_COMMENT)
                .defineInRange(STRENGTH_PERCENT_PER_LEVEL_LATER_NAME, STRENGTH_PERCENT_PER_LEVEL_LATER_DEFAULT, 0.0D, Double.MAX_VALUE);

        HEALTH_FLAT_PER_LEVEL = BUILDER
                .comment(HEALTH_FLAT_PER_LEVEL_COMMENT)
                .defineInRange(HEALTH_FLAT_PER_LEVEL_NAME, HEALTH_FLAT_PER_LEVEL_DEFAULT, 0.0D, Double.MAX_VALUE);
        HEALTH_PERCENT_PER_LEVEL = BUILDER
                .comment(HEALTH_PERCENT_PER_LEVEL_COMMENT)
                .defineInRange(HEALTH_PERCENT_PER_LEVEL_NAME, HEALTH_PERCENT_PER_LEVEL_DEFAULT, 0.0D, Double.MAX_VALUE);

        SPEED_FLAT_PER_LEVEL = BUILDER
                .comment(SPEED_FLAT_PER_LEVEL_COMMENT)
                .defineInRange(SPEED_FLAT_PER_LEVEL_NAME, SPEED_FLAT_PER_LEVEL_DEFAULT, 0.0D, Double.MAX_VALUE);
        SPEED_PERCENT_PER_LEVEL = BUILDER
                .comment(SPEED_PERCENT_PER_LEVEL_COMMENT)
                .defineInRange(SPEED_PERCENT_PER_LEVEL_NAME, SPEED_PERCENT_PER_LEVEL_DEFAULT, 0.0D, Double.MAX_VALUE);

        ARMOR_FLAT_PER_LEVEL = BUILDER
                .comment(ARMOR_FLAT_PER_LEVEL_COMMENT)
                .defineInRange(ARMOR_FLAT_PER_LEVEL_NAME, ARMOR_FLAT_PER_LEVEL_DEFAULT, 0.0D, Double.MAX_VALUE);
        ARMOR_PERCENT_PER_LEVEL = BUILDER
                .comment(ARMOR_PERCENT_PER_LEVEL_COMMENT)
                .defineInRange(ARMOR_PERCENT_PER_LEVEL_NAME, ARMOR_PERCENT_PER_LEVEL_DEFAULT, 0.0D, Double.MAX_VALUE);
        ARMOR_TIER_BREAK = BUILDER
                .comment(ARMOR_TIER_BREAK_COMMENT)
                .defineInRange(ARMOR_TIER_BREAK_NAME, ARMOR_TIER_BREAK_DEFAULT, 0, Integer.MAX_VALUE);
        ARMOR_FLAT_PER_LEVEL_LATER = BUILDER
                .comment(ARMOR_FLAT_PER_LEVEL_LATER_COMMENT)
                .defineInRange(ARMOR_FLAT_PER_LEVEL_LATER_NAME, ARMOR_FLAT_PER_LEVEL_LATER_DEFAULT, 0.0D, Double.MAX_VALUE);
        ARMOR_PERCENT_PER_LEVEL_LATER = BUILDER
                .comment(ARMOR_PERCENT_PER_LEVEL_LATER_COMMENT)
                .defineInRange(ARMOR_PERCENT_PER_LEVEL_LATER_NAME, ARMOR_PERCENT_PER_LEVEL_LATER_DEFAULT, 0.0D, Double.MAX_VALUE);

        LUCK_FLAT_PER_LEVEL = BUILDER
                .comment(LUCK_FLAT_PER_LEVEL_COMMENT)
                .defineInRange(LUCK_FLAT_PER_LEVEL_NAME, LUCK_FLAT_PER_LEVEL_DEFAULT, 0.0D, Double.MAX_VALUE);
        LUCK_PERCENT_PER_LEVEL = BUILDER
                .comment(LUCK_PERCENT_PER_LEVEL_COMMENT)
                .defineInRange(LUCK_PERCENT_PER_LEVEL_NAME, LUCK_PERCENT_PER_LEVEL_DEFAULT, 0.0D, Double.MAX_VALUE);

        REFUND_PERCENT = BUILDER
                .comment(REFUND_PERCENT_COMMENT)
                .defineInRange(REFUND_PERCENT_NAME, REFUND_PERCENT_DEFAULT, 0, 100);
    }

    public static final ModConfigSpec SPEC = BUILDER.build();

    // 运行时上限覆盖表，由 /Soulbound caps set 写入，配置重载时清空。
    private static final ConcurrentMap<String, Integer> RUNTIME_OVERRIDES = new ConcurrentHashMap<>();

    /**
     * 获取指定属性的生效上限。
     * 优先返回运行时覆盖值，若无覆盖则返回持久化配置值。
     */
    public static int getEffectiveCap(String attrKey) {
        Integer override = RUNTIME_OVERRIDES.get(attrKey);
        if (override != null) return override;
        return switch (attrKey) {
            case "strength" -> STRENGTH_CAP.get();
            case "health" -> HEALTH_CAP.get();
            case "speed" -> SPEED_CAP.get();
            case "armor" -> ARMOR_CAP.get();
            case "luck" -> LUCK_CAP.get();
            default -> Integer.MAX_VALUE;
        };
    }

    public static void setRuntimeCap(String attrKey, int value) {
        RUNTIME_OVERRIDES.put(attrKey, Math.max(0, value));
    }

    public static void clearRuntimeCaps() {
        RUNTIME_OVERRIDES.clear();
    }

}
