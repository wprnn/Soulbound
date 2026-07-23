package com.example.rogueprogress;

import com.example.rogueprogress.command.ProgressCommand;
import com.example.rogueprogress.config.Config;
import com.example.rogueprogress.event.EntityKillHandler;
import com.example.rogueprogress.event.PlayerDeathHandler;
import com.example.rogueprogress.event.PlayerLoginHandler;
import com.mojang.logging.LogUtils;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

/**
 * Rogue Progress 主入口。
 * <p>
 * 注册 COMMON 配置，并在 NeoForge 事件总线上注册所有处理器。
 * </p>
 */
@Mod(RogueProgress.MOD_ID)
public final class RogueProgress {
    public static final String MOD_ID = "rogueprogress";
    public static final Logger LOGGER = LogUtils.getLogger();

    public RogueProgress(ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        NeoForge.EVENT_BUS.register(EntityKillHandler.class);
        NeoForge.EVENT_BUS.register(PlayerDeathHandler.class);
        NeoForge.EVENT_BUS.register(PlayerLoginHandler.class);
        NeoForge.EVENT_BUS.register(ProgressCommand.class);

        LOGGER.info("Rogue Progress loaded.");
    }
}
