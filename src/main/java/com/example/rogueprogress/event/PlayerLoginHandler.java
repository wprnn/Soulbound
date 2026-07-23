package com.example.rogueprogress.event;

import com.example.rogueprogress.RogueProgress;
import com.example.rogueprogress.data.ProgressData;
import com.example.rogueprogress.data.ProgressManager;
import com.example.rogueprogress.util.AttributeUtil;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.UUID;

/**
 * 管理玩家生命周期中的进度加载、属性应用及登出清理。
 */
public final class PlayerLoginHandler {
    private PlayerLoginHandler() {
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        UUID playerId = player.getUUID();
        // 从磁盘重新加载，确保跨世界或外部 JSON 编辑的变更被识别。
        ProgressData data = ProgressManager.reload(playerId);
        AttributeUtil.applyProgressAttributes(player, data);

        RogueProgress.LOGGER.info(
                "Loaded Rogue Progress data for {} ({}): soul={}, level={}, strength={}, health={}, speed={}, armor={}, luck={}",
                player.getGameProfile().getName(),
                playerId,
                data.getSoul(),
                data.getLevel(),
                data.getStrength(),
                data.getHealth(),
                data.getSpeed(),
                data.getArmor(),
                data.getLuck()
        );
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        // 复活会重新创建玩家实体，需要重新应用属性修饰符。
        ProgressData data = ProgressManager.getOrCreate(player.getUUID());
        AttributeUtil.applyProgressAttributes(player, data);
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            // 保存进度并清理内存，避免长期运行的服务端出现脏数据。
            ProgressManager.unload(player.getUUID());
        }
    }
}
