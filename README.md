# Flipnic save editor

This tool allows you to edit Flipnic save files as well as partially fix corrupted data by regenerating the checksum.

***NOTE***: This tool is in beta, so bugs are to be expected.

***TODO***: Improve README.md

***TODO***: "Fix save file structure" button functionality

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

Allows you to toggle on/off unlocked stages in both original game and free play modes. The game normally duplicates the state of original game, but when you use the "retry" option in-game, it only resets the original game unlocks.

### "Missions" tab

Allows you to change states of various missions (e.g. COMPLETED) and their in-game labels (e.g. FREEZE OVER).