package com.example.rogueprogress.config;

import com.example.rogueprogress.RogueProgress;
import com.example.rogueprogress.data.ProgressManager;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;

/**
 * 监听配置重载事件，清理运行时上限覆盖并重新钳制所有已加载玩家的属性。
 */
@EventBusSubscriber(modid = RogueProgress.MOD_ID)
public final class ConfigEvents {
    private ConfigEvents() {}

    @SubscribeEvent
    public static void onModConfigReload(ModConfigEvent.Reloading event) {
        if (event.getConfig().getSpec() == Config.SPEC) {
            Config.clearRuntimeCaps();
            ProgressManager.enforceCapsAcrossCache();
            RogueProgress.LOGGER.info("Applied config changes to runtime caps");
        }
    }
}
