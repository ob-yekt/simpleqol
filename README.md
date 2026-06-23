# 🌟 simpleqol Mod Changelog and Feature List

🛠️ A lightweight, server-side Minecraft mod focused on quality-of-life improvements.

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

### 🍃 Custom Leaf Decay Multiplier
- Leaves decay faster. This multiplier is adjustable in  config.json (1=vanilla).
- Jungle Saplings now drop at the same rate as all other saplings (vanilla behavior was 2.5% for jungle, 5% for all other saplings).

### 🍂 Less Leaf Litter
- Amount of **Leaf Litter** has been reduced by 75% (Configurable in config.json, 1 = vanilla)
 
### 🌍 Simple Biome Replacement
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


### 🌱 Composting (Add your own items + compost chance in the config.json)
- **Poisonous potatoes** are now compostable 🥔.
- **Rotten Flesh** is now compostable.
- **Fish** are now compostable.
- **Meats** are now compostable.
- **Eggs** are now compostable.

### Anvil
- Repairing items does not add prior work penalty
- Repairing items always costs 1 level
- Logic for operations that combine enchantments remain unchanged

### 👁️‍🗨️ Enderman Behavior
- **Enderman griefing** is disabled by default 🚫. (Configurable in config.json)

### 🧳 Ender Chest
- **Ender chests** drop as themselves regardless of the tool used, no silk touch required 🔨.

### 🕊️ Elytra (Enable/disable in config.json)
- **Elytra** function has been overhauled: it now allows players to hover as in Creative mode (without sprinting).
- **Elytra** no longer allows users to glide.
- **Fireworks** no longer launch players that are using an **Elytra**.



### 🍓 Sweet Berry Bushes
- **Berry bushes** no longer deal damage to **players**.

### 👻 Phantom Spawning
- **Phantoms** spawn naturally in the Overworld and specific End biomes (End Highlands, End Midlands) 🏞️.
    - Customizable spawn weight and pack sizes via config ⚙️.
    - Gamerule `doInsomnia` (spawns Phantoms when player has not slept) is set to **False** by default 😴.

### 🖥️ Mod Characteristics
- **Fully server-side**: No client-side installation required 🌐.

## Make sure to also check out my other mod 'simpleskills' on [Modrinth](https://modrinth.com/mod/simpleskills) or [GitHub](https://github.com/ob-yekt/simpleskills)!