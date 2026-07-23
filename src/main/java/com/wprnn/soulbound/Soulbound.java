package com.wprnn.soulbound;

import com.wprnn.soulbound.command.ProgressCommand;
import com.wprnn.soulbound.config.Config;
import com.wprnn.soulbound.event.EntityKillHandler;
import com.wprnn.soulbound.event.PlayerDeathHandler;
import com.wprnn.soulbound.event.PlayerLoginHandler;
import com.mojang.logging.LogUtils;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

/**
 * Soulbound 主入口。
 * <p>
 * 注册 COMMON 配置，并在 NeoForge 事件总线上注册所有处理器。
 * </p>
 */
@Mod(Soulbound.MOD_ID)
public final class Soulbound {
    public static final String MOD_ID = "soulbound";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Soulbound(ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        NeoForge.EVENT_BUS.register(EntityKillHandler.class);
        NeoForge.EVENT_BUS.register(PlayerDeathHandler.class);
        NeoForge.EVENT_BUS.register(PlayerLoginHandler.class);
        NeoForge.EVENT_BUS.register(ProgressCommand.class);

        LOGGER.info("Soulbound loaded.");
    }
}
