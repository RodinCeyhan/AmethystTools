![AmethystTools](https://cdn.modrinth.com/data/cached_images/170092caa460eae2df6758bbff844b08c41978ef.png)

<hr>

<div align="center">

<a href="https://github.com/RodinCeyhan/AmethystTools/" target="_blank" rel="noopener noreferrer">
    <img alt="Github" src="https://img.shields.io/static/v1?label=&message=Github&messageColor=black&color=e911f7&labelColor=e911f7&style=for-the-badge&logo=github&logoColor=black">
</a>

<a href="https://modrinth.com/plugin/amethystools" target="_blank" rel="noopener noreferrer">
    <img alt="Modrinth" src="https://img.shields.io/static/v1?label=&message=Modrinth&messageColor=black&color=e911f7&labelColor=e911f7&style=for-the-badge&logo=modrinth&logoColor=black">
</a>

<a href="https://www.spigotmc.org/resources/133794/" target="_blank" rel="noopener noreferrer">
    <img alt="SpigotMC" src="https://img.shields.io/static/v1?label=&message=SpigotMC&messageColor=black&color=e911f7&labelColor=e911f7&style=for-the-badge&logo=spigotmc&logoColor=black">
</a>

<a href="https://github.com/RodinCeyhan/AmethystTools/wiki" target="_blank" rel="noopener noreferrer">
    <img alt="Wiki" src="https://img.shields.io/static/v1?label=&message=Wiki&messageColor=black&color=e911f7&labelColor=e911f7&style=for-the-badge&logo=gitbook&logoColor=black">
</a>

</div>

<hr>

# ✨ AmethystTools

AmethystTools is a customizable **Paper / Folia** plugin that adds amethyst-themed **3x3 mining tools** — pickaxes, shovels and axes in seven tiers — together with a clean GUI, a tree-felling axe and a fully configurable look.

Inspired by the Amethyst Tools found on DonutSMP. AmethystTools is an independent project and is not affiliated with DonutSMP.

---

## ✨ Features

- ⛏️ **3x3 area mining** — pickaxes and shovels break a 3x3 area that follows where you look: a wall in front of you, or a floor/ceiling layer when you look steeply up or down
- 🪓 **Tree-felling axe** — an axe fells a whole connected tree (up to 64 logs) in one swing
- 🧱 **Seven tiers** — wooden, stone, iron, copper, golden, diamond and netherite
- 🖼️ **Interactive GUI** — browse every tier and take any tool from a menu
- 🛡️ **Protected blocks** — a configurable blacklist keeps ores, containers, bedrock and similar blocks safe from area breaking
- 🏷️ **Reliable item detection** — tools are tagged with plugin-specific persistent data, so a look-alike item with the same model ID does not get the ability
- 🎨 **Fully configurable** — messages, item names and menu titles support MiniMessage, `&` color codes and `&#RRGGBB` hex colors
- 🔄 **Two mining modes** — `NEW` (smart tools) and `OLD` (classic 3x3 for every tool)
- 🔔 **Update notifications** — operators are told when a new version is available on Modrinth
- 🧩 **Paper and Folia ready**

---

## ⛏️ Commands

| Command | Description | Permission |
|---|---|---|
| `/amethysttools` | Opens the tool menu (players only) | `amethysttools.give` |
| `/amethysttools give <tier> <tool> [players]` | Gives a tool to yourself, or to the selected players | `amethysttools.give` |
| `/amethysttools reload` | Reloads `config.yml` and refreshes the names of tools in online players' inventories | `amethysttools.admin` |

**Tiers:** `wooden`, `stone`, `iron`, `copper` *(1.21.9+ only)*, `golden`, `diamond`, `netherite`
**Tools:** `pickaxe`, `shovel`, `axe`

```txt
/amethysttools give netherite axe
/amethysttools give golden shovel Steve
/amethysttools give diamond pickaxe @a
```

All arguments are tab-completed. A wrong or incomplete command shows Minecraft's own error message.

---

## 🔑 Permissions

| Permission | Default | Allows |
|---|---|---|
| `amethysttools.give` | op | Opening the menu and giving tools |
| `amethysttools.admin` | op | Reloading the plugin |

---

## 🖼️ Menu

<img width="683" height="357" alt="Main menu" src="https://github.com/user-attachments/assets/37782bac-066f-45ce-8f95-4b8d997e738a" />

<img width="683" height="357" alt="Tier menu" src="https://github.com/user-attachments/assets/e9c9c65f-7045-4d74-81c8-5a7bcc478732" />

<img width="683" height="357" alt="Tools" src="https://github.com/user-attachments/assets/f678874e-f834-4bfe-bc9f-9fab72010075" />

---


## 📦 Compatibility

- **Minecraft:** 1.21 and newer (including the 26.x versions)
- **Java:** 21
- **Platforms:** Paper, Purpur, Folia and other forks of them
- Copper tools are **not available** in Minecraft `1.21 – 1.21.8`; they exist from `1.21.9` onward
- Starting with version `1.4`, the plugin is distributed as a single JAR file

Tools created by older versions of AmethystTools keep working after an update, and the config is updated automatically without touching your values.

---

## ❤️ Support

Found a bug or have a suggestion? Open an [issue on GitHub](https://github.com/RodinCeyhan/AmethystTools/issues).

## 📄 License

AmethystTools is licensed under the [GNU General Public License v3.0](LICENSE).