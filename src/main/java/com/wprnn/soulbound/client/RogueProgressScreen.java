package com.wprnn.soulbound.client;

import com.wprnn.soulbound.config.Config;
import com.wprnn.soulbound.data.ProgressData;
import com.wprnn.soulbound.data.ProgressManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

/**
 * 属性升级主界面。
 * <p>
 * 显示玩家灵魂数、等级与五项属性，每条属性对应一个升级按钮。
 * 按住 Shift 时批量升级 10 级，否则单次升级 1 级。
 * 右侧预览区域根据当前 {@link Config} 中的加成配置实时计算展示。
 * </p>
 */
public final class RogueProgressScreen extends Screen {

    private static final int BUTTON_WIDTH = 260;
    private static final int BUTTON_HEIGHT = 20;

    private final Screen previousScreen;
    private final List<UpgradeButton> upgradeButtons = new ArrayList<>();

    private ProgressData data;
    private UUID playerId;

    public RogueProgressScreen(@Nullable Screen previousScreen) {
        super(Component.translatable("soulbound.gui.title"));
        this.previousScreen = previousScreen;
    }

    /** 单次升级所需的灵魂数：10 + 当前等级 × 5。 */
    private static int getUpgradeCost(int level) {
        return 10 + level * 5;
    }

    /** 从当前等级连续升级 amount 级所需的总灵魂数。 */
    private static int getTotalCost(int current, int amount) {
        int cost = 0;
        for (int i = 0; i < amount; i++) {
            cost += getUpgradeCost(current + i);
        }
        return cost;
    }

    /**
     * 分段加成计算，与 {@code AttributeUtil.computeTiered} 逻辑一致。
     *
     * @param level         当前属性等级
     * @param perLevel      分界前每级加成
     * @param tierBreak     分界等级（0 表示不分段）
     * @param laterPerLevel 分界后每级加成
     */
    private static double computeTiered(int level, double perLevel, int tierBreak, double laterPerLevel) {
        if (level <= 0) return 0.0D;
        if (tierBreak > 0 && level > tierBreak) {
            return tierBreak * perLevel + (level - tierBreak) * laterPerLevel;
        }
        return level * perLevel;
    }

    @Override
    protected void init() {
        Minecraft minecraft = Minecraft.getInstance();
        playerId = minecraft.getUser().getProfileId();
        data = ProgressManager.reload(playerId);
        upgradeButtons.clear();

        int x = (width - BUTTON_WIDTH) / 2;
        int startY = 84;
        int spacing = 24;
        List<AttributeSpec> specs = List.of(
                new AttributeSpec("strength", data::getStrength, data::setStrength, AttributeType.ATTACK),
                new AttributeSpec("health", data::getHealth, data::setHealth, AttributeType.HEALTH),
                new AttributeSpec("speed", data::getSpeed, data::setSpeed, AttributeType.SPEED),
                new AttributeSpec("armor", data::getArmor, data::setArmor, AttributeType.ARMOR),
                new AttributeSpec("luck", data::getLuck, data::setLuck, AttributeType.LUCK));

        for (int i = 0; i < specs.size(); i++) {
            AttributeSpec s = specs.get(i);
            addUpgradeButton(x, startY + i * spacing, s.key, s.getter, s.setter, s.type);
        }

        int completeX = (width - 100) / 2;
        int resetX = completeX - 110;
        int percent = com.wprnn.soulbound.config.Config.REFUND_PERCENT.get();

        addRenderableWidget(
                Button.builder(Component.translatable("soulbound.gui.reset", percent), b -> onReset())
                        .bounds(resetX, 220, 100, BUTTON_HEIGHT).build());

        addRenderableWidget(
                Button.builder(Component.translatable("soulbound.gui.done"), b -> onClose())
                        .bounds(completeX, 220, 100, BUTTON_HEIGHT).build());

        updateUpgradeButtons();
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.fill(0, 0, width, height, 0xCC080808);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);

        guiGraphics.drawCenteredString(font, title, width / 2, 24, 0xFFDFA64A);
        guiGraphics.drawCenteredString(font, Component.translatable("soulbound.gui.soul", data.getSoul()), width / 2, 44, 0x55FFFF);
        guiGraphics.drawCenteredString(font, Component.translatable("soulbound.gui.level", data.getLevel()), width / 2, 58, 0xFFFF55);

        super.render(guiGraphics, mouseX, mouseY, partialTick);

        int baseX = (width - BUTTON_WIDTH) / 2;
        for (int idx = 0; idx < upgradeButtons.size(); idx++) {
            UpgradeButton b = upgradeButtons.get(idx);
            int level = b.getter.getAsInt();
            Component attrName = Component.translatable("soulbound.attr." + b.key);

            int cap = getCapFor(b.key);
            if (level >= cap) {
                b.button.setMessage(Component.translatable("soulbound.gui.maxed", attrName, level));
                b.button.active = false;
            } else {
                if (hasShiftDown()) {
                    int amount = Math.clamp(cap - level, 0, 10);
                    int cost = getTotalCost(level, amount);
                    int target = Math.min(cap, level + amount);
                    b.button.setMessage(Component.translatable("soulbound.gui.upgrade", attrName, level, target, cost));
                    b.button.active = data.getSoul() >= cost;
                } else {
                    int cost = getUpgradeCost(level);
                    int target = Math.min(cap, level + 1);
                    b.button.setMessage(Component.translatable("soulbound.gui.upgrade", attrName, level, target, cost));
                    b.button.active = data.getSoul() >= cost;
                }
            }

            int previewX = baseX + BUTTON_WIDTH + 8;
            int previewY = 84 + idx * 24 + 4;
            if (previewX + 160 > width - 10) {
                previewX = width - 160 - 10;
            }

            int amountToShow = hasShiftDown() ? 10 : 1;
            int target = Math.min(getCapFor(b.key), level + amountToShow);
            Component previewComp = Component.empty();
            switch (b.type) {
                case ATTACK: {
                    int tb = Config.STRENGTH_TIER_BREAK.get();
                    double pCur = computeTiered(level, Config.STRENGTH_PERCENT_PER_LEVEL.get(), tb, Config.STRENGTH_PERCENT_PER_LEVEL_LATER.get());
                    double pTarget = computeTiered(target, Config.STRENGTH_PERCENT_PER_LEVEL.get(), tb, Config.STRENGTH_PERCENT_PER_LEVEL_LATER.get());
                    double fCur = computeTiered(level, Config.STRENGTH_FLAT_PER_LEVEL.get(), tb, Config.STRENGTH_FLAT_PER_LEVEL_LATER.get());
                    double fTarget = computeTiered(target, Config.STRENGTH_FLAT_PER_LEVEL.get(), tb, Config.STRENGTH_FLAT_PER_LEVEL_LATER.get());
                    previewComp = Component.translatable("soulbound.gui.preview_attack",
                            formatPreview(pCur, fCur), formatPreview(pTarget, fTarget));
                    break;
                }
                case ARMOR: {
                    int tb = Config.ARMOR_TIER_BREAK.get();
                    double pCur = computeTiered(level, Config.ARMOR_PERCENT_PER_LEVEL.get(), tb, Config.ARMOR_PERCENT_PER_LEVEL_LATER.get());
                    double pTarget = computeTiered(target, Config.ARMOR_PERCENT_PER_LEVEL.get(), tb, Config.ARMOR_PERCENT_PER_LEVEL_LATER.get());
                    double fCur = computeTiered(level, Config.ARMOR_FLAT_PER_LEVEL.get(), tb, Config.ARMOR_FLAT_PER_LEVEL_LATER.get());
                    double fTarget = computeTiered(target, Config.ARMOR_FLAT_PER_LEVEL.get(), tb, Config.ARMOR_FLAT_PER_LEVEL_LATER.get());
                    previewComp = Component.translatable("soulbound.gui.preview_armor",
                            formatPreview(pCur, fCur), formatPreview(pTarget, fTarget));
                    break;
                }
                case HEALTH: {
                    double fCur = computeTiered(level, Config.HEALTH_FLAT_PER_LEVEL.get(), 0, 0);
                    double fTarget = computeTiered(target, Config.HEALTH_FLAT_PER_LEVEL.get(), 0, 0);
                    double pCur = computeTiered(level, Config.HEALTH_PERCENT_PER_LEVEL.get(), 0, 0);
                    double pTarget = computeTiered(target, Config.HEALTH_PERCENT_PER_LEVEL.get(), 0, 0);
                    previewComp = Component.translatable("soulbound.gui.preview_health",
                            formatPreview(pCur, fCur), formatPreview(pTarget, fTarget));
                    break;
                }
                case SPEED: {
                    double pCur = computeTiered(level, Config.SPEED_PERCENT_PER_LEVEL.get(), 0, 0);
                    double pTarget = computeTiered(target, Config.SPEED_PERCENT_PER_LEVEL.get(), 0, 0);
                    double fCur = computeTiered(level, Config.SPEED_FLAT_PER_LEVEL.get(), 0, 0);
                    double fTarget = computeTiered(target, Config.SPEED_FLAT_PER_LEVEL.get(), 0, 0);
                    previewComp = Component.translatable("soulbound.gui.preview_speed",
                            formatPreview(pCur, fCur), formatPreview(pTarget, fTarget));
                    break;
                }
                case LUCK: {
                    double fCur = computeTiered(level, Config.LUCK_FLAT_PER_LEVEL.get(), 0, 0);
                    double fTarget = computeTiered(target, Config.LUCK_FLAT_PER_LEVEL.get(), 0, 0);
                    double pCur = computeTiered(level, Config.LUCK_PERCENT_PER_LEVEL.get(), 0, 0);
                    double pTarget = computeTiered(target, Config.LUCK_PERCENT_PER_LEVEL.get(), 0, 0);
                    previewComp = Component.translatable("soulbound.gui.preview_luck",
                            formatPreview(pCur, fCur), formatPreview(pTarget, fTarget));
                    break;
                }
            }

            guiGraphics.drawString(font, previewComp, previewX, previewY, 0xFFFFFF);
        }
    }

    private int getCapFor(String key) {
        return com.wprnn.soulbound.config.Config.getEffectiveCap(key);
    }

    private void addUpgradeButton(int x, int y, String key, IntSupplier getter, IntConsumer setter,
            AttributeType type) {
        UpgradeButton upgradeButton = new UpgradeButton(key, getter, setter, type);

        Button button = Button.builder(Component.empty(), b -> {
                    int amount = hasShiftDown() ? 10 : 1;
                    upgrade(upgradeButton, amount);
                })
                .bounds(x, y, BUTTON_WIDTH, BUTTON_HEIGHT).build();

        upgradeButton.button = button;
        addRenderableWidget(button);
        upgradeButtons.add(upgradeButton);
    }

    /** 执行升级：扣灵魂 → 提升属性 → 重算等级 → 写盘 → 刷新按钮状态。 */
    private void upgrade(UpgradeButton button, int amount) {
        int current = button.getter.getAsInt();
        int cap = getCapFor(button.key);
        int allowed = Math.clamp(cap - current, 0, amount);
        if (allowed <= 0) return;

        int cost = 0;
        for (int i = 0; i < allowed; i++) {
            cost += getUpgradeCost(current + i);
        }

        if (!data.spendSoul(cost)) return;

        button.setter.accept(current + allowed);
        data.recalculateLevelFromAttributes();
        ProgressManager.save(playerId, data);
        updateUpgradeButtons();
    }

    private void updateUpgradeButtons() {
        for (UpgradeButton button : upgradeButtons) {
            int level = button.getter.getAsInt();
            int cap = getCapFor(button.key);
            Component attrName = Component.translatable("soulbound.attr." + button.key);

            if (level >= cap) {
                button.button.setMessage(Component.translatable("soulbound.gui.maxed", attrName, level));
                button.button.active = false;
                continue;
            }

            int cost = getUpgradeCost(level);
            button.button.setMessage(Component.translatable("soulbound.gui.upgrade", attrName, level, level + 1, cost));
            button.button.active = data.getSoul() >= cost;
        }
    }

    @Override
    public void onClose() {
        if (playerId != null && data != null) {
            ProgressManager.save(playerId, data);
        }
        Minecraft.getInstance().setScreen(previousScreen);
    }

    private void onReset() {
        Minecraft.getInstance().setScreen(new ConfirmResetScreen(this, this::performReset));
    }

    /** 重置所有属性至 0，按配置比例退还灵魂。 */
    private void performReset() {
        int totalSpent = 0;
        for (UpgradeButton b : upgradeButtons) {
            int cur = b.getter.getAsInt();
            if (cur > 0) {
                totalSpent += getTotalCost(0, cur);
            }
        }

        if (totalSpent <= 0) return;

        int percent = com.wprnn.soulbound.config.Config.REFUND_PERCENT.get();
        int refund = (int) Math.floor(totalSpent * (percent / 100.0));

        for (UpgradeButton b : upgradeButtons) {
            b.setter.accept(0);
        }

        data.addSoul(refund);
        data.recalculateLevelFromAttributes();
        ProgressManager.save(playerId, data);
        updateUpgradeButtons();
    }

    /**
     * 格式化预览文本。
     * 纯百分比："+12.00%"，纯固定值："+2.0"，两者皆有："+1.0 (+12.00%)"。
     */
    private static String formatPreview(double pct, double flat) {
        if (pct != 0 && flat != 0) {
            return String.format("+%.1f (+%.2f%%)", flat, pct * 100);
        } else if (pct != 0) {
            return String.format("+%.2f%%", pct * 100);
        } else {
            return String.format("+%.1f", flat);
        }
    }

    private static class UpgradeButton {
        private final String key;
        private final IntSupplier getter;
        private final IntConsumer setter;
        private final AttributeType type;
        private Button button;

        private UpgradeButton(String key, IntSupplier getter, IntConsumer setter, AttributeType type) {
            this.key = key;
            this.getter = getter;
            this.setter = setter;
            this.type = type;
        }
    }

    private enum AttributeType {
        ATTACK, ARMOR, HEALTH, SPEED, LUCK
    }

    private record AttributeSpec(String key, IntSupplier getter, IntConsumer setter, AttributeType type) {
    }
}
