package com.wprnn.soulbound.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

/**
 * 属性重置确认弹窗，防止误操作。
 */
public class ConfirmResetScreen extends Screen {
    private final Screen parent;
    private final Runnable onConfirm;

    public ConfirmResetScreen(@Nullable Screen parent, Runnable onConfirm) {
        super(Component.translatable("soulbound.gui.reset_title"));
        this.parent = parent;
        this.onConfirm = onConfirm;
    }

    @Override
    protected void init() {
        int w = this.width;
        int h = this.height;
        addRenderableWidget(Button.builder(Component.translatable("soulbound.gui.confirm"), b -> {
            if (onConfirm != null) onConfirm.run();
            Minecraft.getInstance().setScreen(parent);
        }).bounds((w - 200) / 2, h / 2 + 10, 90, 20).build());

        addRenderableWidget(Button.builder(Component.translatable("soulbound.gui.cancel"), b -> Minecraft.getInstance().setScreen(parent))
                .bounds((w - 200) / 2 + 110, h / 2 + 10, 90, 20).build());
    }
}
