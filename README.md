# Soulbound
1
跨世界 Roguelike 元进度模组。

## 概述

Soulbound 为 Minecraft 添加了类似 Roguelike 的持久化角色成长机制。击杀怪物获取 **灵魂**，消耗灵魂升级五项属性。所有进度全局存储——删除或切换世界不会丢失任何数据。

## 特性

- **灵魂与成长** — 击杀普通/精英/Boss 生物获得 1/5/25 灵魂
- **死亡结算** — 死亡时将击杀数按比例转化为灵魂奖励，轮回统计重置
- **升级属性**
  | 属性 | 效果 | 默认每级加成 |
  |---|---|---|
  | 力量 | 攻击伤害（固定 + 百分比） | 固定 0，+3% |
  | 生命 | 最大生命值（固定 + 百分比） | +2 HP，0% |
  | 速度 | 移动速度（固定 + 百分比） | 固定 0，+1% |
  | 护甲 | 护甲值（固定 + 百分比） | 固定 0，+4% |
  | 幸运 | 幸运值（固定 + 百分比） | +1，0% |
- **分段加成** — 力量和护甲在超过分界等级后使用较弱的后期加成（默认 5 级）
- **全配置化** — 上限、加成数值、分界等级、返还比例均可自由调整
- **全局持久化** — 数据存储在 `config/soulbound/`，独立于存档文件
- **重置返还** — 重置全部属性并按比例返还已消耗灵魂（默认 90%）

## 安装

1. 在 [Releases](https://github.com/anomalyco/Soulbound/releases) 下载最新 jar
2. 放入 `mods` 文件夹

## 使用

### 打开界面
点击主菜单标题画面的 **Soulbound** 按钮。

### 命令
所有命令需要管理员权限。

```
/soulbound                           查看当前进度
/soulbound add soul <数量>           添加灵魂
/soulbound caps show                 显示当前生效上限
/soulbound caps set <属性> <值>      修改属性上限
/soulbound caps reload               重载
```

## 配置

配置文件：`config/soulbound-common.toml`（首次运行自动生成）。

可在游戏内 Mods → Config → Soulbound 界面编辑，或直接修改 TOML 文件。

### 上限

| 配置项           | 默认值 | 说明      |
|---------------|-----|---------|
| `strengthCap` | 10  | 力量属性最大值 |
| `healthCap`   | 20  | 生命属性最大值 |
| `speedCap`    | 10  | 速度属性最大值 |
| `armorCap`    | 15  | 护甲属性最大值 |
| `luckCap`     | 10  | 幸运属性最大值 |

### 加成

每级加成（分界前）：

| 配置项                            | 默认值   | 说明             |
|--------------------------------|-------|----------------|
| `strengthFlatPerLevel`         | 0.0   | 力量每级固定伤害       |
| `strengthPercentPerLevel`      | 0.03  | 力量每级百分比攻击加成    |
| `strengthTierBreak`            | 5     | 力量分界等级（0 禁用分段） |
| `strengthFlatPerLevelLater`    | 0.0   | 分界后每级固定伤害      |
| `strengthPercentPerLevelLater` | 0.015 | 分界后每级百分比攻击加成   |
| `healthFlatPerLevel`           | 2.0   | 生命每级固定生命值      |
| `healthPercentPerLevel`        | 0.0   | 生命每级百分比生命加成    |
| `speedFlatPerLevel`            | 0.0   | 速度每级固定移速       |
| `speedPercentPerLevel`         | 0.01  | 速度每级百分比移速加成    |
| `armorFlatPerLevel`            | 0.0   | 护甲每级固定护甲       |
| `armorPercentPerLevel`         | 0.04  | 护甲每级百分比护甲加成    |
| `armorTierBreak`               | 5     | 护甲分界等级（0 禁用分段） |
| `armorFlatPerLevelLater`       | 0.0   | 分界后每级固定护甲      |
| `armorPercentPerLevelLater`    | 0.02  | 分界后每级百分比护甲加成   |
| `luckFlatPerLevel`             | 1.0   | 幸运每级固定幸运值      |
| `luckPercentPerLevel`          | 0.0   | 幸运每级百分比幸运加成    |

### 其他

| 配置项             | 默认值 | 说明            |
|-----------------|-----|---------------|
| `refundPercent` | 90  | 重置属性时返还灵魂的百分比 |

### 构建
```bash
git clone https://github.com/anomalyco/Soulbound.git
cd Soulbound
./gradlew build
```

## 许可

All Rights Reserved.
