package com.example.rogueprogress.util;

import com.example.rogueprogress.RogueProgress;
import com.example.rogueprogress.data.ProgressData;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

public final class AttributeUtil {
    private static final ResourceLocation MAX_HEALTH_ID = modifierId("max_health");
    private static final ResourceLocation ATTACK_DAMAGE_ID = modifierId("attack_damage");
    private static final ResourceLocation MOVEMENT_SPEED_ID = modifierId("movement_speed");
    private static final ResourceLocation ARMOR_ID = modifierId("armor");
    private static final ResourceLocation LUCK_ID = modifierId("luck");

    private AttributeUtil() {
    }

    public static void applyProgressAttributes(Player player, ProgressData data) {
        applyAddValueModifier(player, Attributes.MAX_HEALTH, MAX_HEALTH_ID, data.getHealth() * 2.0D);
        applyAddValueModifier(player, Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE_ID, data.getStrength());
        applyAddMultipliedBaseModifier(player, Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED_ID, data.getSpeed() * 0.01D);
        applyAddValueModifier(player, Attributes.ARMOR, ARMOR_ID, data.getArmor());
        applyAddValueModifier(player, Attributes.LUCK, LUCK_ID, data.getLuck());

        if (player.getHealth() > player.getMaxHealth()) {
            player.setHealth(player.getMaxHealth());
        }
    }

    public static void clearProgressAttributes(Player player) {
        removeModifier(player, Attributes.MAX_HEALTH, MAX_HEALTH_ID);
        removeModifier(player, Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE_ID);
        removeModifier(player, Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED_ID);
        removeModifier(player, Attributes.ARMOR, ARMOR_ID);
        removeModifier(player, Attributes.LUCK, LUCK_ID);
    }

    private static void applyAddValueModifier(Player player, Holder<Attribute> attribute, ResourceLocation modifierId, double amount) {
        applyModifier(player, attribute, modifierId, amount, AttributeModifier.Operation.ADD_VALUE);
    }

    private static void applyAddMultipliedBaseModifier(Player player, Holder<Attribute> attribute, ResourceLocation modifierId, double amount) {
        applyModifier(player, attribute, modifierId, amount, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    }

    private static void applyModifier(
            Player player,
            Holder<Attribute> attribute,
            ResourceLocation modifierId,
            double amount,
            AttributeModifier.Operation operation
    ) {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance == null) {
            return;
        }

        instance.removeModifier(modifierId);

        if (amount == 0.0D) {
            return;
        }

        instance.addPermanentModifier(new AttributeModifier(modifierId, amount, operation));
    }

    private static void removeModifier(Player player, Holder<Attribute> attribute, ResourceLocation modifierId) {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance != null) {
            instance.removeModifier(modifierId);
        }
    }

    private static ResourceLocation modifierId(String path) {
        return ResourceLocation.fromNamespaceAndPath(RogueProgress.MOD_ID, path);
    }
}
