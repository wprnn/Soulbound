package com.wprnn.soulbound.client;

import com.wprnn.soulbound.Soulbound;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;

/**
 * 在主菜单添加 Soulbound 按钮，点击打开升级界面。
 */
@EventBusSubscriber(modid = Soulbound.MOD_ID, value = Dist.CLIENT)
public final class ClientEvents {
    private ClientEvents() {}

    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.Init.Post event) {
        if (!(event.getScreen() instanceof TitleScreen screen)) return;

        int x = screen.width / 2 + 104;
        int y = screen.height / 4 + 48;
        event.addListener(Button.builder(
                        Component.translatable("soulbound.gui.button"),
                        button -> Minecraft.getInstance().setScreen(new RogueProgressScreen(screen))
                )
                .bounds(x, y, 98, 20)
                .build());
    }
}
