package com.wprnn.soulbound.util;

import com.wprnn.soulbound.Soulbound;
import com.wprnn.soulbound.config.Config;
import com.wprnn.soulbound.data.ProgressData;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

/**
 * 将玩家进度数据以永久属性修饰符的形式应用到玩家实体上。
 * <p>
 * 每个修饰符使用稳定且唯一的 {@link ResourceLocation} 标识。
 * 应用前会先移除同名旧修饰符，防止重新登录、复活或配置变更后数值重复叠加。
 * 每项属性支持固定数值（ADD_VALUE）和百分比（ADD_MULTIPLIED_BASE）两种修饰符，
 * 具体值由 {@link Config} 中的加成配置决定。
 * </p>
 */
public final class AttributeUtil {
    private static final ResourceLocation MAX_HEALTH_FLAT_ID = modifierId("max_health_flat");
    private static final ResourceLocation MAX_HEALTH_PCT_ID = modifierId("max_health_pct");
    private static final ResourceLocation ATTACK_DAMAGE_FLAT_ID = modifierId("attack_damage_flat");
    private static final ResourceLocation ATTACK_DAMAGE_PCT_ID = modifierId("attack_damage_pct");
    private static final ResourceLocation MOVEMENT_SPEED_FLAT_ID = modifierId("movement_speed_flat");
    private static final ResourceLocation MOVEMENT_SPEED_PCT_ID = modifierId("movement_speed_pct");
    private static final ResourceLocation ARMOR_FLAT_ID = modifierId("armor_flat");
    private static final ResourceLocation ARMOR_PCT_ID = modifierId("armor_pct");
    private static final ResourceLocation LUCK_FLAT_ID = modifierId("luck_flat");
    private static final ResourceLocation LUCK_PCT_ID = modifierId("luck_pct");

    private AttributeUtil() {}

    /** 根据玩家当前进度数据，重新计算并应用所有属性修饰符。 */
    public static void applyProgressAttributes(Player player, ProgressData data) {
        int s = data.getStrength();
        int h = data.getHealth();
        int sp = data.getSpeed();
        int a = data.getArmor();
        int l = data.getLuck();

        double healthFlat = computeTiered(h, Config.HEALTH_FLAT_PER_LEVEL.get(), 0, 0);
        double healthPct = computeTiered(h, Config.HEALTH_PERCENT_PER_LEVEL.get(), 0, 0);
        applyAddValueModifier(player, Attributes.MAX_HEALTH, MAX_HEALTH_FLAT_ID, healthFlat);
        applyAddMultipliedBaseModifier(player, Attributes.MAX_HEALTH, MAX_HEALTH_PCT_ID, healthPct);

        double strFlat = computeTiered(s, Config.STRENGTH_FLAT_PER_LEVEL.get(), Config.STRENGTH_TIER_BREAK.get(), Config.STRENGTH_FLAT_PER_LEVEL_LATER.get());
        double strPct = computeTiered(s, Config.STRENGTH_PERCENT_PER_LEVEL.get(), Config.STRENGTH_TIER_BREAK.get(), Config.STRENGTH_PERCENT_PER_LEVEL_LATER.get());
        applyAddValueModifier(player, Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE_FLAT_ID, strFlat);
        applyAddMultipliedBaseModifier(player, Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE_PCT_ID, strPct);

        double speedFlat = computeTiered(sp, Config.SPEED_FLAT_PER_LEVEL.get(), 0, 0);
        double speedPct = computeTiered(sp, Config.SPEED_PERCENT_PER_LEVEL.get(), 0, 0);
        applyAddValueModifier(player, Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED_FLAT_ID, speedFlat);
        applyAddMultipliedBaseModifier(player, Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED_PCT_ID, speedPct);

        double armorFlat = computeTiered(a, Config.ARMOR_FLAT_PER_LEVEL.get(), Config.ARMOR_TIER_BREAK.get(), Config.ARMOR_FLAT_PER_LEVEL_LATER.get());
        double armorPct = computeTiered(a, Config.ARMOR_PERCENT_PER_LEVEL.get(), Config.ARMOR_TIER_BREAK.get(), Config.ARMOR_PERCENT_PER_LEVEL_LATER.get());
        applyAddValueModifier(player, Attributes.ARMOR, ARMOR_FLAT_ID, armorFlat);
        applyAddMultipliedBaseModifier(player, Attributes.ARMOR, ARMOR_PCT_ID, armorPct);

        double luckFlat = computeTiered(l, Config.LUCK_FLAT_PER_LEVEL.get(), 0, 0);
        double luckPct = computeTiered(l, Config.LUCK_PERCENT_PER_LEVEL.get(), 0, 0);
        applyAddValueModifier(player, Attributes.LUCK, LUCK_FLAT_ID, luckFlat);
        applyAddMultipliedBaseModifier(player, Attributes.LUCK, LUCK_PCT_ID, luckPct);

        if (player.getHealth() > player.getMaxHealth()) {
            player.setHealth(player.getMaxHealth());
        }
    }

    /**
     * 分段加成计算。
     *
     * @param level         当前属性等级
     * @param perLevel      分界前每级加成
     * @param tierBreak     分界等级（0 表示不分段）
     * @param laterPerLevel 分界后每级加成
     */
    private static double computeTiered(int level, double perLevel, int tierBreak, double laterPerLevel) {
        if (level <= 0) return 0.0D;
        if (tierBreak > 0 && level > tierBreak) {
            return tierBreak * perLevel + (level - tierBreak) * laterPerLevel;
        }
        return level * perLevel;
    }

    private static void applyAddValueModifier(Player player, Holder<Attribute> attribute, ResourceLocation modifierId,
            double amount) {
        applyModifier(player, attribute, modifierId, amount, AttributeModifier.Operation.ADD_VALUE);
    }

    private static void applyAddMultipliedBaseModifier(Player player, Holder<Attribute> attribute,
            ResourceLocation modifierId, double amount) {
        applyModifier(player, attribute, modifierId, amount, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    }

    /**
     * 应用（或更新）单条属性修饰符。
     * 先移除同名旧修饰符，若数值为 0 则跳过添加，保证不会重复叠加。
     */
    private static void applyModifier(
            Player player,
            Holder<Attribute> attribute,
            ResourceLocation modifierId,
            double amount,
            AttributeModifier.Operation operation) {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance == null) return;
        instance.removeModifier(modifierId);
        if (amount == 0.0D) return;
        instance.addPermanentModifier(new AttributeModifier(modifierId, amount, operation));
    }

    private static ResourceLocation modifierId(String path) {
        return ResourceLocation.fromNamespaceAndPath(Soulbound.MOD_ID, path);
    }
}
