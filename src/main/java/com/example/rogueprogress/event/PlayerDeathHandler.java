package com.example.rogueprogress.event;

import com.example.rogueprogress.data.ProgressManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

import java.util.UUID;

/**
 * 玩家死亡时结算本次运行奖励并发送统计摘要。
 */
public final class PlayerDeathHandler {
    private PlayerDeathHandler() {}

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        UUID playerId = player.getUUID();
        int runKills = ProgressManager.getRunKills(playerId);
        int settlementSoul = ProgressManager.calculateDeathSettlementSoul(playerId);

        if (settlementSoul > 0) {
            ProgressManager.addSoul(playerId, settlementSoul);
        } else {
            ProgressManager.save(playerId);
        }

        int runSouls = ProgressManager.getRunSouls(playerId);

        player.sendSystemMessage(Component.translatable("rogueprogress.message.header").withStyle(ChatFormatting.GOLD));
        player.sendSystemMessage(Component.translatable("rogueprogress.message.run").withStyle(ChatFormatting.YELLOW));
        player.sendSystemMessage(Component.translatable("rogueprogress.message.kills", runKills).withStyle(ChatFormatting.GRAY));
        player.sendSystemMessage(Component.translatable("rogueprogress.message.souls_earned", runSouls).withStyle(ChatFormatting.AQUA));

        ProgressManager.resetRun(playerId);
    }
}
