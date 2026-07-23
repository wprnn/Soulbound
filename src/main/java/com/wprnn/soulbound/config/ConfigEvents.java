package com.wprnn.soulbound.config;

import com.wprnn.soulbound.Soulbound;
import com.wprnn.soulbound.data.ProgressManager;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;

/**
 * 监听配置重载事件，清理运行时上限覆盖并重新钳制所有已加载玩家的属性。
 */
@EventBusSubscriber(modid = Soulbound.MOD_ID)
public final class ConfigEvents {
    private ConfigEvents() {}

    @SubscribeEvent
    public static void onModConfigReload(ModConfigEvent.Reloading event) {
        if (event.getConfig().getSpec() == Config.SPEC) {
            Config.clearRuntimeCaps();
            ProgressManager.enforceCapsAcrossCache();
            Soulbound.LOGGER.info("Applied config changes to runtime caps");
        }
    }
}
