package com.example.rogueprogress.client;

import com.example.rogueprogress.RogueProgress;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

/**
 * 客户端入口。注册 NeoForge 默认配置界面。
 */
@Mod(value = RogueProgress.MOD_ID, dist = Dist.CLIENT)
public class RogueProgressClient {
    public RogueProgressClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }
}
