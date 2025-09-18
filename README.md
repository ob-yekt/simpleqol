# 🌟 simpleqol Mod Changelog and Feature List

🛠️ A lightweight, server-side Minecraft mod focused on quality-of-life improvements.

🌐 **Fully server-side**: No client-side installation required.

## Features

### 🪓 Stonecutter Enhancements
- Stonecutter can now cut **normal deepslate** ⛏️.
- Stonecutter supports cutting **all  (and wood/stripped) types** 🌳.
- CRAFTING TABLE stair recipe now produces 6 stairs on the crafting table
  - Stairs can be created on the stonecutter for 1 of the base material e.g. 1 stone or 1 oak plank.

### 🌞🌙 Custom Day and Night Lengths
- Configure **day** and **night lengths** in real-life minutes ⏰.
  - Default: **30 minutes** (day), **10 minutes** (night).
  - Commands:
    - `/simpleqol daylength get/set <minutes>`: View or set day length ☀️.
    - `/simpleqol nightlength get/set <minutes>`: View or set night length 🌑.
  - **Note**: Disables `daynightcycle` gamerule, which may affect mods relying on it ⚠️.
    - Sleeping to skip nighttime and the `playersleepingpercentage` gamerule remain functional 😴.

### 🍂 Custom Leaf Decay Multiplier
- Leaves decay faster. This multiplier is adjustable in the config (1=vanilla).
- Jungle Saplings now drop at the same rate as all other saplings (vanilla behavior was 2.5% for jungle, 5% for all other saplings).

### ⚓ Respawn Anchor Overhaul
- **Respawn anchors** are usable in **all dimensions** and do not explode 🚫💥.
- Players respawn at their anchor regardless of the dimension they died in (e.g., die in The End, respawn at their anchor in The Nether) 🔄.
- Players with an active respawn anchor cannot set their spawn with a **bed**, but can still sleep to skip the night 🛏️.
- Multiple players can link to the same respawn anchor 👥.
- If a respawn anchor is destroyed, connected players’ spawns revert to the **world spawn** 🌍.
- Respawn anchors remain **lit (charge level 4)** as long as at least one player is linked 💡.
- No longer require **glowstone** to function 🚫✨.

### 🌍 Simple Biome Replacement
- Hate a certain biome? Want more of a specific biome? Simple!
- Adjust the simpleqol_config.json to replace biomes **(BEFORE GENERATION)** as you see fit, adding adjusting, or removing them from the config json.
- Default replacement examples include:
    - "minecraft:stony_shore", "minecraft:beach" (**replaces stony_shore with beach**)
    - "minecraft:windswept_gravelly_hills", "minecraft:windswept_hills" (**replaces windswept_gravelly_hills with windswept_hills**)

Set
```
    "biomeReplacements": {
    "minecraft:stony_shore": "minecraft:beach",
    "minecraft:windswept_gravelly_hills": "minecraft:windswept_hills"
  },
```
to
```
"biomeReplacements": {},
```
if you do not wish to replace any biomes.

### 🌱 Composting
- **Poisonous potatoes** are now compostable 🥔.

### 👁️‍🗨️ Enderman Behavior
- **Enderman griefing** is disabled by default 🚫.
    - Adjustable via config ⚙️.

### 🧳 Ender Chest
- **Ender chests** drop as themselves regardless of the tool used, no silk touch required 🔨.

### 🧶 Wool Crafting
- **Wool** can be crafted into **4 strings** 🧵.

### 💡 Plant Lighting
- **Torchflower**, **Pitcher Plant**, and **Eyeblossom** emit light ✨.
    - Light levels adjustable via config ⚙️.
    - **Note**: Clients only see lighting changes after rejoining the server 🔄.

### 🍓 Sweet Berry Bushes
- **Berry bushes** no longer deal damage to players 🚫💥 (configurable).
- Allows for greater decorative possibilities.

### 👻 Phantom Spawning
- **Phantoms** spawn very rarely in the Overworld and specific End biomes (End Highlands, End Midlands) 🏞️.
    - Customizable spawn weight and pack sizes via config (set to 0 to disable and set doInsomnia to true for vanilla behavior) ⚙️.
    - Gamerule `doInsomnia` (spawns Phantoms when player has not slept) is set to **False** by default, adjustable in config 😴.

### 🖥️ Mod Characteristics
- **Fully server-side**: No client-side installation required 🌐.
- Configurable settings for:
    - Enderman griefing 👁️‍🗨️.
    - Leaf decay multiplier 🍂.
    - Berry bushes 🍓.
    - Night length 🌙.
    - Fully customizable biome replacements 🌍.
    - Plant lighting levels 💡.
    - Phantom spawn rates 👻.

## Make sure to also check out my other mod 'simpleskills' on [Modrinth](https://modrinth.com/mod/simpleskills) or [GitHub](https://github.com/ob-yekt/simpleskills)!