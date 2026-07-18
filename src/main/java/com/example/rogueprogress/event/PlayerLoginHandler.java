package com.example.rogueprogress.event;

import com.example.rogueprogress.RogueProgress;
import com.example.rogueprogress.data.ProgressData;
import com.example.rogueprogress.data.ProgressManager;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.UUID;

public final class PlayerLoginHandler {
    private PlayerLoginHandler() {
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        UUID playerId = player.getUUID();
        ProgressData data = ProgressManager.reload(playerId);

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
}
