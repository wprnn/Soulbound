package com.example.rogueprogress.command;

import com.example.rogueprogress.config.Config;
import com.example.rogueprogress.data.ProgressData;
import com.example.rogueprogress.data.ProgressManager;
import com.example.rogueprogress.util.AttributeUtil;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

/**
 * 注册 /rogueprogress 命令，提供进度查看、添加灵魂及上限管理功能。
 */
public final class ProgressCommand {
    private ProgressCommand() {}

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        register(event.getDispatcher());
    }

    private static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("rogueprogress")
                        .executes(context -> showProgress(context.getSource()))
                        .then(Commands.literal("add")
                                .requires(source -> source.hasPermission(2))
                                .then(Commands.literal("soul")
                                        .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                                .executes(context -> addSoul(
                                                        context.getSource(),
                                                        IntegerArgumentType.getInteger(context, "amount"))))))
                        .then(Commands.literal("caps")
                                .requires(source -> source.hasPermission(2))
                                .then(Commands.literal("show")
                                        .executes(context -> showCaps(context.getSource())))
                                .then(Commands.literal("reload")
                                        .executes(context -> reloadCaps(context.getSource())))
                                .then(Commands.literal("set")
                                        .then(Commands.argument("attr", StringArgumentType.word())
                                                .then(Commands.argument("value", IntegerArgumentType.integer(0))
                                                        .executes(context -> setCap(
                                                                context.getSource(),
                                                                StringArgumentType.getString(context, "attr"),
                                                                IntegerArgumentType.getInteger(context, "value"))))))));
    }

    /** 查看当前玩家的灵魂、等级及属性值。 */
    private static int showProgress(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        ProgressData data = ProgressManager.getOrCreate(player.getUUID());

        player.sendSystemMessage(Component.translatable("rogueprogress.command.header").withStyle(ChatFormatting.GOLD));
        sendAttr(player, "rogueprogress.command.soul", String.valueOf(data.getSoul()), ChatFormatting.AQUA);
        sendAttr(player, "rogueprogress.command.level", String.valueOf(data.getLevel()), ChatFormatting.WHITE);
        sendAttr(player, "rogueprogress.attr.strength", String.valueOf(data.getStrength()), ChatFormatting.WHITE);
        sendAttr(player, "rogueprogress.attr.health", String.valueOf(data.getHealth()), ChatFormatting.WHITE);
        sendAttr(player, "rogueprogress.attr.speed", String.valueOf(data.getSpeed()), ChatFormatting.WHITE);
        sendAttr(player, "rogueprogress.attr.armor", String.valueOf(data.getArmor()), ChatFormatting.WHITE);
        sendAttr(player, "rogueprogress.attr.luck", String.valueOf(data.getLuck()), ChatFormatting.WHITE);
        return 1;
    }

    private static void sendAttr(ServerPlayer player, String labelKey, String value, ChatFormatting valueColor) {
        player.sendSystemMessage(Component.translatable(labelKey).withStyle(ChatFormatting.YELLOW));
        player.sendSystemMessage(Component.literal(value).withStyle(valueColor));
    }

    /** 向当前玩家添加指定数量的灵魂并刷新属性。 */
    private static int addSoul(CommandSourceStack source, int amount) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        ProgressManager.addSoul(player.getUUID(), amount);

        ProgressData data = ProgressManager.getOrCreate(player.getUUID());
        AttributeUtil.applyProgressAttributes(player, data);

        source.sendSuccess(
                () -> Component.translatable("rogueprogress.command.add_soul", amount, data.getSoul())
                        .withStyle(ChatFormatting.GREEN), false);
        return amount;
    }

    /** 显示当前所有属性的生效上限。 */
    private static int showCaps(CommandSourceStack source) {
        source.sendSuccess(() -> Component.translatable("rogueprogress.command.caps_header").withStyle(ChatFormatting.GOLD), false);
        sendCap(source, "strength");
        sendCap(source, "health");
        sendCap(source, "speed");
        sendCap(source, "armor");
        sendCap(source, "luck");
        return 1;
    }

    private static void sendCap(CommandSourceStack source, String attrKey) {
        Component name = Component.translatable("rogueprogress.attr." + attrKey);
        source.sendSuccess(() -> Component.translatable("rogueprogress.command.cap_fmt", name, Config.getEffectiveCap(attrKey)).withStyle(ChatFormatting.YELLOW), false);
    }

    /** 运行时覆盖指定属性的上限（不写盘，重启后失效）。 */
    private static int setCap(CommandSourceStack source, String attr, int value) {
        String a = attr.toLowerCase();
        String key;
        switch (a) {
            case "strength": case "力量": key = "strength"; break;
            case "health": case "生命": key = "health"; break;
            case "speed": case "速度": key = "speed"; break;
            case "armor": case "护甲": key = "armor"; break;
            case "luck": case "幸运": key = "luck"; break;
            default:
                source.sendFailure(Component.translatable("rogueprogress.command.unknown_attr", attr));
                return 0;
        }
        Config.setRuntimeCap(key, value);
        source.sendSuccess(() -> Component.translatable("rogueprogress.command.set_cap",
                Component.translatable("rogueprogress.attr." + key), value).withStyle(ChatFormatting.GREEN), false);
        return value;
    }

    /** 清除所有运行时覆盖，恢复到持久化配置值。 */
    private static int reloadCaps(CommandSourceStack source) {
        Config.clearRuntimeCaps();
        source.sendSuccess(() -> Component.translatable("rogueprogress.command.reload").withStyle(ChatFormatting.GREEN), false);
        return 1;
    }
}
