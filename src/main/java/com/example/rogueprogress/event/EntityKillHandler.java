package com.example.rogueprogress.event;

import com.example.rogueprogress.data.ProgressManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

/**
 * 服务端玩家击杀敌对生物时授予灵魂。
 *
 * <p>奖励判定集中在此处。如需添加自定义精英词缀、怪物标签或配置化奖励，
 * 请优先修改 {@link #isElite(LivingEntity)} 和 {@link #isBoss(LivingEntity)} 的实现。</p>
 */
public final class EntityKillHandler {
    // 击杀奖励的平衡参数
    private static final int NORMAL_SOUL_REWARD = 1;
    private static final int ELITE_SOUL_REWARD = 5;
    private static final int BOSS_SOUL_REWARD = 25;
    // 最大生命值不低于此阈值的敌对生物被视为精英。
    private static final double ELITE_MAX_HEALTH_THRESHOLD = 40.0D;

    private EntityKillHandler() {
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity killedEntity = event.getEntity();

        // 仅在逻辑服务端执行，且不处理玩家击杀玩家。
        if (killedEntity.level().isClientSide() || killedEntity instanceof ServerPlayer) {
            return;
        }

        // getEntity() 为直接攻击者。如需投射物归属支持可在此处扩展。
        Entity attacker = event.getSource().getEntity();
        if (!(attacker instanceof ServerPlayer player)) {
            return;
        }

        int reward = getSoulReward(killedEntity);
        if (reward <= 0) {
            return;
        }

        ProgressManager.recordKill(player.getUUID());
        ProgressManager.addSoul(player.getUUID(), reward);
    }

    private static int getSoulReward(LivingEntity killedEntity) {
        // Boss 优先判断，因其未必实现 Enemy 接口。
        if (isBoss(killedEntity)) {
            return BOSS_SOUL_REWARD;
        }

        if (!(killedEntity instanceof Enemy)) {
            return 0;
        }

        if (isElite(killedEntity)) {
            return ELITE_SOUL_REWARD;
        }

        return NORMAL_SOUL_REWARD;
    }

    private static boolean isBoss(LivingEntity entity) {
        EntityType<?> type = entity.getType();
        return type == EntityType.ENDER_DRAGON || type == EntityType.WITHER;
    }

    private static boolean isElite(LivingEntity entity) {
        // 自定义名称是数据包/地图作者标记精英生物的便捷方式。
        return entity.getMaxHealth() >= ELITE_MAX_HEALTH_THRESHOLD || entity.hasCustomName();
    }
}
