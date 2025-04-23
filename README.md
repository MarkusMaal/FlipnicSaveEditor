# Flipnic save editor

This tool allows you to edit Flipnic save files as well as partially fix corrupted data by regenerating the checksum.

***NOTE***: This tool is in beta, so bugs are to be expected.

***TODO***: Improve README.md

## Locating the save file

Here is a list of known game IDs for Flipnic:

* NTSC-J (Japan): SCPS-15050
* NTSC-U (US): SLUS-21157
* NTSC-U (US Demo): SLUS-29149
* PAL (EU, AU, etc.): SLES-52065

The Flipnic save file is located on the memory card at <GAME_ID>\<GAME_ID>. Use homebrew such as wLaunchElf to copy this file to a flash drive and then connect it to your computer. At this point, you should be able to open the save file in this save editor.

## User interface

This section lists some of the things that you can do with this save editor.

### "File" menu

Quick actions related to save file:

* Open...    - Allows you to open your save file
* Save as... - Allows you to save any modifications you've made to a separate file you must specify
* Save       - Tries to overwrite the loaded file with the new changes, otherwise functions the same as "Save as..."
* Exit       - Closes the program

### "Help" menu

Currently only has an "About" option to view information about the program.

### "Information" tab

Contains information about the save file. This includes both of the checksums, current original game score and current stage (NOTE: will not reset once you complete original game for the first time). Here you can also update checksums, which is needed any time you make a modification to the save file anywhere else in the program.

### "Ranking" tab

Allows you to view ranking data (scores) from various stages. You can also edit any field by double-clicking on it.

### "Options" tab

Allows you to configure settings that you'd normally have to change by going around various menus in-game.

### "Unlocks" tab

Allows you to toggle on/off unlocked stages in both original game and free play modes. The game normally duplicates the state of original game, but when you use the "retry" option in-game, it only resets the original game unlocks. You can also force score mode on a specific stage, which allows you to play with score enabled on a previously cleared stage.

### "Missions" tab

Allows you to change states of various missions (e.g. COMPLETED) and their in-game labels (e.g. FREEZE OVER).

### "Stages" tab

Allows you to change the order of stages in original game or even assign 2P stages to original game stages. The directory names are internal names for the stages. Here's what stage they correspond to:

* JUNGLE1 = Biology A
* JUNGLE2 = Biology B
* ISEKI1 = Metallurgy A
* ISEKI2 = Metallurgy B
* HIKARI1 = Optics A
* HIKARI2 = Optics B
* RETRO1 = Geometry A
* BOSS1 = Evolution A
* BOSS2 = Evolution B
* BOSS3 = Evolution C
* BOSS4 = Evolution D
* VS1 = Biology 2P
* VS2 = Metallurgy 2P
* VS3 = Optics 2P
* VS4 = Geometry 2P
* DEMO1 = Unused

# Hex pattern file

If you want to manually explore Flipnic save files with a hex editor that supports hexpat files, such as ImHex, you can import the [flipnicsave.hexpat](flipnicsave.hexpat) to get highlighting for what various bytes within the save file do.

This pattern file is used in the building of this save file editor. There are some bytes we are unsure about, but at least 78% of the save file is documented within this file.