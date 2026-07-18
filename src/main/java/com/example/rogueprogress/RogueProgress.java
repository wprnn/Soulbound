package com.example.rogueprogress;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(RogueProgress.MOD_ID)
public final class RogueProgress {
    public static final String MOD_ID = "rogueprogress";
    public static final Logger LOGGER = LogUtils.getLogger();

    public RogueProgress(IEventBus modEventBus) {
        LOGGER.info("Rogue Progress loaded.");
    }
}
