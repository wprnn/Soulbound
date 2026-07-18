package com.example.rogueprogress.command;

import com.example.rogueprogress.data.ProgressData;
import com.example.rogueprogress.data.ProgressManager;
import com.example.rogueprogress.util.AttributeUtil;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public final class ProgressCommand {
    private ProgressCommand() {
    }

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
                                                        IntegerArgumentType.getInteger(context, "amount")
                                                )))))
        );
    }

    private static int showProgress(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        ProgressData data = ProgressManager.getOrCreate(player.getUUID());

        player.sendSystemMessage(Component.literal("===== Rogue Progress =====").withStyle(ChatFormatting.GOLD));
        player.sendSystemMessage(Component.literal("灵魂:").withStyle(ChatFormatting.YELLOW));
        player.sendSystemMessage(Component.literal(String.valueOf(data.getSoul())).withStyle(ChatFormatting.AQUA));
        player.sendSystemMessage(Component.literal(""));
        player.sendSystemMessage(Component.literal("等级:").withStyle(ChatFormatting.YELLOW));
        player.sendSystemMessage(Component.literal(String.valueOf(data.getLevel())).withStyle(ChatFormatting.WHITE));
        player.sendSystemMessage(Component.literal(""));
        player.sendSystemMessage(Component.literal("力量:").withStyle(ChatFormatting.YELLOW));
        player.sendSystemMessage(Component.literal(String.valueOf(data.getStrength())).withStyle(ChatFormatting.WHITE));
        player.sendSystemMessage(Component.literal(""));
        player.sendSystemMessage(Component.literal("生命:").withStyle(ChatFormatting.YELLOW));
        player.sendSystemMessage(Component.literal(String.valueOf(data.getHealth())).withStyle(ChatFormatting.WHITE));
        player.sendSystemMessage(Component.literal(""));
        player.sendSystemMessage(Component.literal("速度:").withStyle(ChatFormatting.YELLOW));
        player.sendSystemMessage(Component.literal(String.valueOf(data.getSpeed())).withStyle(ChatFormatting.WHITE));
        player.sendSystemMessage(Component.literal(""));
        player.sendSystemMessage(Component.literal("护甲:").withStyle(ChatFormatting.YELLOW));
        player.sendSystemMessage(Component.literal(String.valueOf(data.getArmor())).withStyle(ChatFormatting.WHITE));
        player.sendSystemMessage(Component.literal(""));
        player.sendSystemMessage(Component.literal("幸运:").withStyle(ChatFormatting.YELLOW));
        player.sendSystemMessage(Component.literal(String.valueOf(data.getLuck())).withStyle(ChatFormatting.WHITE));

        return 1;
    }

    private static int addSoul(CommandSourceStack source, int amount) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        ProgressManager.addSoul(player.getUUID(), amount);

        ProgressData data = ProgressManager.getOrCreate(player.getUUID());
        AttributeUtil.applyProgressAttributes(player, data);

        source.sendSuccess(
                () -> Component.literal("Added " + amount + " Soul. Current Soul: " + data.getSoul())
                        .withStyle(ChatFormatting.GREEN),
                false
        );

        return amount;
    }
}
