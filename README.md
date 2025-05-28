# 🌟 simpleqol Mod Changelog and Feature List

🛠️ A lightweight, server-side Minecraft mod focused on quality-of-life improvements.

## Features

### 🪓 Stonecutter Enhancements
- Stonecutter can now cut **normal deepslate** ⛏️.
- Stonecutter supports cutting **all wood types** 🌳.

### 🌞🌙 Custom Day and Night Lengths
- Configure **day** and **night lengths** in real-life minutes ⏰.
    - Default: **20 minutes** (day), **10 minutes** (night).
    - Commands:
        - `/simpleqol daylength get/set <minutes>`: View or set day length ☀️.
        - `/simpleqol nightlength get/set <minutes>`: View or set night length 🌑.
    - **Note**: Disables `daynightcycle` gamerule, which may affect mods relying on it ⚠️.
      - Sleeping to skip nighttime and the `playersleepingpercentage` gamerule remain functional 😴.

### ⚓ Respawn Anchor Overhaul
- **Respawn anchors** are usable in **all dimensions** and do not explode 🚫💥.
- Players respawn at their anchor regardless of the dimension they died in (e.g., die in The End, respawn at their anchor in The Nether) 🔄.
- Players with an active respawn anchor cannot set their spawn with a **bed**, but can still sleep to skip the night 🛏️.
- Multiple players can link to the same respawn anchor 👥.
- If a respawn anchor is destroyed, connected players’ spawns revert to the **world spawn** 🌍.
- Respawn anchors remain **lit (charge level 4)** as long as at least one player is linked 💡.
- No longer require **glowstone** to function 🚫✨.

### 🌱 Composting
- **Poisonous potatoes** are now compostable 🥔.

### 👁️‍🗨️ Enderman Behavior
- **Enderman griefing** is disabled by default 🚫.
    - Adjustable via config ⚙️.

### 🧳 Ender Chest
- **Ender chests** drop as themselves regardless of the tool used 🔨.

### 🧶 Wool Crafting
- **Wool** can be crafted into **4 strings** 🧵.

### 💡 Plant Lighting
- **Torchflower**, **Pitcher Plant**, and **Eyeblossom** emit light ✨.
    - Light levels adjustable via config ⚙️.
    - **Note**: Clients only see lighting changes after rejoining the server 🔄.

### 🍓 Berry Bushes
- **Berry bushes** no longer deal damage to players 🚫💥.

### 👻 Phantom Spawning
- **Phantoms** spawn very rarely in the Overworld and specific End biomes (End Highlands, End Midlands) 🏞️.
    - Customizable spawn weight and pack sizes via config (set to 0 to disable) ⚙️.
    - Gamerule `doInsomnia` is set to **False** by default, adjustable in config 😴.



### 🖥️ Mod Characteristics
- **Fully server-side**: No client-side installation required 🌐.
- Configurable settings for:
    - Enderman griefing 👁️‍🗨️.
    - Night length 🌙.
    - Plant lighting levels 💡.
    - Phantom spawn rates 👻.