[![Discord](https://img.shields.io/discord/869218732650688543?color=008080&label=DISCORD&style=for-the-badge)](https://discord.com/invite/S5nffJbuvA)

[![Ko-fi](https://ko-fi.com/img/githubbutton_sm.svg)](https://ko-fi.com/D1D8LKA5N)

[![My projects](https://img.shields.io/badge/CurseForge-projects-008080?style=for-the-badge&logo=curseforge)](https://www.curseforge.com/members/finndog_123/projects)

[![My projects](https://img.shields.io/badge/Modrinth-projects-008080?style=for-the-badge&logo=modrinth)](https://modrinth.com/user/FinnSetchell)

### ABOUT

Vinery Wine Toggle is a small companion mod for [Vinery](https://www.curseforge.com/minecraft/mc-mods/lets-do-wine) that lets you completely disable any wine through a config.

A disabled wine can't be crafted (fermentation barrel, apple press, or Create), doesn't show up in the creative menu, JEI/REI, or the recipe book, isn't sold by the winemaker villager or wandering trader, and any bottles you already have can't be drunk. The `wine_collector` advancement stays completable, and the item stays registered so existing worlds aren't affected.

It runs on Fabric and Forge for Minecraft 1.20.1 and never modifies Vinery itself — it just filters things by item ID.

### REQUIREMENTS

- [Vinery](https://www.curseforge.com/minecraft/mc-mods/lets-do-wine)
- [Architectury API](https://www.curseforge.com/minecraft/mc-mods/architectury-api)
- [Cloth Config](https://www.curseforge.com/minecraft/mc-mods/cloth-config)
- On Fabric: [Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api), plus [Mod Menu](https://www.curseforge.com/minecraft/mc-mods/modmenu) for the config button

### USAGE

Edit the disabled wines in-game (Mod Menu on Fabric, or the mod list **Config** button on Forge), or edit `config/winetoggle.json` directly:

```json
{
  "disabled_wines": ["vinery:eiswein"]
}
```

Recipe and advancement changes apply on world reload (`/reload`); creative-menu and drinking changes apply immediately.

### SUPPORT

The best and fastest way to get replies is to join the [Discord server](https://discord.gg/S5nffJbuvA).
