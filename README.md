# 🏹 Minecraft Manhunt Mod

A custom-coded Minecraft mod that brings the thrill of Manhunt gameplay into singleplayer or multiplayer worlds — complete with intelligent, respawning AI Hunters that evolve, fight, craft, and chase down their targets anywhere — Overworld, Nether, or End.

---

## 🎮 Features

### 🧠 Smart AI Hunters
- Track players across all dimensions — no matter the distance
- Infinite follow range — they find you even through walls
- React dynamically: fight, retreat, mine, smelt, and upgrade gear
- Critical hits, jumping in combat, hunger-based sprinting
- Respawn at beds or world spawn if killed

### 🪓 Advanced Player-like Behavior
- Mine wood, stone, coal, iron, and diamonds
- Craft tables, furnaces, tools, and armor
- Smelt iron and upgrade gear tiers automatically
- Place blocks and build bridges when stuck
- Prioritize food, gear, survival

### 🧙 Real Combat Intelligence
- Chooses best weapon (axe/sword)
- Strafes, jumps, and switches to bow if available
- Retreats and heals when losing
- Predicts player movement if the target escapes

### 📜 Command System

#### Start a Manhunt:
```bash
/manhunt start <player1> [player2] ... <hunter_count>
```
Spawns `hunter_count` AI Hunters and distributes them across all listed players.

#### Stop the Manhunt:
```bash
/manhunt stop
```
Kills all Hunters and ends the manhunt.

---

## 📦 Installation

This is a standard Minecraft Forge mod for version 1.20.1.

1. Install Forge 1.20.1
2. Place the compiled `manhuntmod-1.0.0.jar` into your `.minecraft/mods` folder
3. Launch the game using the Forge profile

---

## 🔧 Developer Notes

- Fully modular: Each behavior goal is its own AI class
- Uses custom game rule: `manhuntActive` for toggling logic
- Auto-respawn logic inside `HunterEntity` handles persistence
- Designed for extensibility: ranged weapons, team support, and more in future

---

## ✅ Coming Soon
- Boss bar support for target tracking
- Configs for hunter speed, gear scaling, lives
- GitHub Actions for automated mod builds
- CurseForge publishing support

---

## 🡩‍💻 Author

Created by Vernos1337
