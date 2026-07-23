package com.example.rogueprogress.data;

import com.example.rogueprogress.config.Config;

/**
 * 单个玩家的进度数据模型。
 * <p>
 * 包含灵魂数、展示等级和五项属性值，所有 setter 会自动钳制到当前配置的上限。
 * </p>
 */
public final class ProgressData {
    private int soul;
    private int level;
    private int strength;
    private int health;
    private int speed;
    private int armor;
    private int luck;

    public ProgressData() {
        this.soul = 0;
        this.level = 1;
        this.strength = 0;
        this.health = 0;
        this.speed = 0;
        this.armor = 0;
        this.luck = 0;
    }

    /** 将所有字段修正为非负值，用于从 JSON 反序列化后的清理。 */
    public void sanitize() {
        soul = Math.max(0, soul);
        level = Math.max(1, level);
        strength = Math.max(0, strength);
        health = Math.max(0, health);
        speed = Math.max(0, speed);
        armor = Math.max(0, armor);
        luck = Math.max(0, luck);
    }

    public int getSoul() { return soul; }

    public void addSoul(int amount) {
        if (amount > 0) this.soul += amount;
    }

    public boolean spendSoul(int amount) {
        if (amount <= 0 || soul < amount) return false;
        soul -= amount;
        return true;
    }

    public int getLevel() { return level; }

    public void setLevel(int level) { this.level = Math.max(1, level); }

    public int getStrength() { return strength; }

    public void setStrength(int strength) {
        this.strength = Math.clamp(strength, 0, Config.getEffectiveCap("strength"));
    }

    public int getHealth() { return health; }

    public void setHealth(int health) {
        this.health = Math.clamp(health, 0, Config.getEffectiveCap("health"));
    }

    public int getSpeed() { return speed; }

    public void setSpeed(int speed) {
        this.speed = Math.clamp(speed, 0, Config.getEffectiveCap("speed"));
    }

    public int getArmor() { return armor; }

    public void setArmor(int armor) {
        this.armor = Math.clamp(armor, 0, Config.getEffectiveCap("armor"));
    }

    public int getLuck() { return luck; }

    public void setLuck(int luck) {
        this.luck = Math.clamp(luck, 0, Config.getEffectiveCap("luck"));
    }

    /** 展示等级 = 1 + 五项属性之和。 */
    public void recalculateLevelFromAttributes() {
        setLevel(1 + strength + health + speed + armor + luck);
    }
}
