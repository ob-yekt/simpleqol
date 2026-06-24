# 🌟 simpleqol Mod Changelog and Feature List

🛠️ A lightweight, customizable, server-side Minecraft mod focused on quality-of-life improvements and balance tweaks.

🌐 **Fully server-side**: No client-side installation required.

## Features

### 🪓 Stonecutter for Wood Blocks
- Stonecutter supports cutting **all 'Wood' types** 🌳.

### 🧶 Crafting Recipes
- The **CRAFTING TABLE** stair recipe now produces 6 stairs instead of the vanilla 4.
    - Stairs can be created on the stonecutter for 1 of the base material e.g. 1 stone or 1 oak plank.
- **Wool** can be crafted into **4 strings** 🧵.
- **Packed Ice** can be crafted into **9 Ice** 🧊.
- **Blue Ice** can be crafted into **9 Packed Ice** ❄️.
- **Block of Quartz** can be crafted into **9 Nether Quartz**

### 🌞🌙 Custom Day and Night Lengths
- Configure **day** and **night lengths** in real-life minutes ⏰.
  - Default: **30 minutes** (day), **10 minutes** (night).
  - Commands:
    - `/simpleqol daylength get/set <minutes>`: View or set day length ☀️.
    - `/simpleqol nightlength get/set <minutes>`: View or set night length 🌑.
  - **Note**: Disables `daynightcycle` gamerule, which may affect mods relying on it ⚠️.
    - Sleeping to skip nighttime and the `playersleepingpercentage` gamerule remain functional 😴.

### 🔨 Anvil
- Repairing items does not add prior work penalty
- Repairing items always costs 1 level
- Logic for operations that combine enchantments remain unchanged

### 🧳 Ender Chest
- **Ender chests** drop as themselves regardless of the tool used, no silk touch required 🔨.

### 🕊️ Custom Elytra (Enable/disable in config.json, flight speed adjustable in config.json)
- **Elytra** has been overhauled: it now allows players to hover as in Creative mode.
- **Elytra** no longer allows users to glide.
- **Fireworks** no longer launch players that are using an **Elytra**.

### 📚 Librarian Rebalance (Configurable in config.json)
- Librarians **can no longer** offer Enchanted Books at levels 1, 2, 3, and 4.
- Librarians **ALWAYS** offer an Enchanted Book at level 5.
- The Level 5 Enchanted Book trade has a stock level of 1.
- After restocking, the Enchanted Book is randomized.

### ⚡ Natural Charged Creepers (Configurable in config.json)
- Creepers have a 2% chance to spawn as charged.
- Adjust float in config.json for more or less charged Creepers! (0 = no charged Creepers)

### 🍃 Trees & Leaves (Configurable in config.json)
- Leaves decay instantly. Set to true/false in config.
- Jungle Saplings now drop at the same rate as all other saplings (vanilla behavior was 2.5% for jungle, 5% for all other saplings).

### 🍂 Less Leaf Litter (Configurable in config.json)
- Amount of **Leaf Litter** has been reduced by 75% (Configurable in config.json, 1 = vanilla)

### 🍓 Sweet Berry Bushes (Configurable in config.json)
- **Berry bushes** no longer deal damage to **players**.

### 🌱 Composting (Add your own items + compost chance in the config.json)
- **Poisonous potatoes** are now compostable 🥔.
- **Rotten Flesh** is now compostable.
- **Fish** are now compostable.
- **Meats** are now compostable.
- **Eggs** are now compostable.

### 👁️‍🗨️ Enderman Behavior (Configurable in config.json)
- **Enderman griefing** is disabled by default 🚫. (Configurable in config.json)


### 👻 Phantom Spawning (Configurable in config.json)
- **Phantoms** spawn naturally in the Overworld and the End 🏞️.
    - Customizable spawn weight and pack sizes via config ⚙️.
    - Gamerule `doInsomnia` (spawns Phantoms when player has not slept) is set to **False** by default 😴.

### 🌍 Simple Biome Replacement (Configurable in config.json)
- Hate a certain biome? Want more of a specific biome? Simple!
- Adjust the simpleqol_config.json to replace biomes **(BEFORE GENERATION)** as you see fit, adding adjusting, or removing them from the config json.
- Default replacement examples include:
    - "minecraft:stony_shore", "minecraft:beach" (**replaces stony_shore with beach**)
    - "minecraft:windswept_gravelly_hills", "minecraft:windswept_hills" (**replaces windswept_gravelly_hills with windswept_hills**)
- Only affects BIOME GENERATION. If a replaced biome has ALREADY BEEN GENERATED it will remain as it is in the game.

Examples:
Setting
```
"biomeReplacements": {},
```
to
```
    "biomeReplacements": {
    "minecraft:stony_shore": "minecraft:beach",
    "minecraft:windswept_gravelly_hills": "minecraft:windswept_hills"
  },
```
will change all **Stony Shore** biomes to **Beach** biomes, as well as **Windswept Gravelly Hills** to **Windswept Hills**.

or
```
    "biomeReplacements": {
    "minecraft:desert": "minecraft:tundra"
  },
```
if you want to change all **Desert** biomes to **Tundra** biomes.

### 🖥️ Mod Characteristics
- **Fully server-side**: No client-side installation required 🌐.
- Reload config.json changes with `/simpleqol reload`, no need to restart the server.
## Make sure to also check out my other mod 'simpleskills' on [Modrinth](https://modrinth.com/mod/simpleskills) or [GitHub](https://github.com/ob-yekt/simpleskills)!