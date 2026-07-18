package com.example.rogueprogress.event;

import com.example.rogueprogress.data.ProgressManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

public final class EntityKillHandler {
    private static final int NORMAL_SOUL_REWARD = 1;
    private static final int ELITE_SOUL_REWARD = 5;
    private static final int BOSS_SOUL_REWARD = 25;
    private static final double ELITE_MAX_HEALTH_THRESHOLD = 40.0D;

    private EntityKillHandler() {
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity killedEntity = event.getEntity();

        if (killedEntity.level().isClientSide() || killedEntity instanceof ServerPlayer) {
            return;
        }

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
        return entity.getMaxHealth() >= ELITE_MAX_HEALTH_THRESHOLD || entity.hasCustomName();
    }
}
