package com.example.rogueprogress.data;

import java.util.ArrayList;
import java.util.List;

public final class ProgressData {
    private int soul;
    private int level;
    private int strength;
    private int health;
    private int speed;
    private int armor;
    private int luck;
    private List<String> unlocks;

    public ProgressData() {
        this.soul = 0;
        this.level = 1;
        this.strength = 0;
        this.health = 0;
        this.speed = 0;
        this.armor = 0;
        this.luck = 0;
        this.unlocks = new ArrayList<>();
    }

    public static ProgressData createDefault() {
        return new ProgressData();
    }

    public void sanitize() {
        soul = Math.max(0, soul);
        level = Math.max(1, level);
        strength = Math.max(0, strength);
        health = Math.max(0, health);
        speed = Math.max(0, speed);
        armor = Math.max(0, armor);
        luck = Math.max(0, luck);

        if (unlocks == null) {
            unlocks = new ArrayList<>();
        }
    }

    public int getSoul() {
        return soul;
    }

    public void setSoul(int soul) {
        this.soul = Math.max(0, soul);
    }

    public void addSoul(int amount) {
        if (amount > 0) {
            this.soul += amount;
        }
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = Math.max(1, level);
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = Math.max(0, strength);
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = Math.max(0, health);
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = Math.max(0, speed);
    }

    public int getArmor() {
        return armor;
    }

    public void setArmor(int armor) {
        this.armor = Math.max(0, armor);
    }

    public int getLuck() {
        return luck;
    }

    public void setLuck(int luck) {
        this.luck = Math.max(0, luck);
    }

    public List<String> getUnlocks() {
        return unlocks;
    }

    public void setUnlocks(List<String> unlocks) {
        this.unlocks = unlocks == null ? new ArrayList<>() : new ArrayList<>(unlocks);
    }
}
