## Resource pack sounds (AKA sound packs)
This update allows packaging sounds as resource packs to share them with other players.  

In your resource pack add `.wav` files in `assets/<namespace>/soundboard`.  
You can create any subdirectories inside to organize files or 
create other namespaces to override other sounds. 
Created subdirectories will form collapsible groups in the soundboard menu.

By default, provided sounds won't look pretty. 
You need to provide a translation key for them.  
Translations are stored in `assets/<namespace>/lang`. 
The default language is `en_us.json`.  

Category names are prefixed with `soundboard.dir.<namespace>`.  
For example, if you have a file in `assets/mysoundpack/soundboard/scary/ghost_boo.wav`,
you'll have the following translation file:
```json
{
  "soundboard.dir.mysoundpack": "My first sound pack!",
  "soundboard.dir.mysoundpack.scary": "Spooky sounds",
  "soundboard.file.mysoundpack.scary.ghost_boo": "Very scary ghost sound"
}
```

You can see the example sound pack in the [Soundboard repository](https://github.com/kikugie/voicechat-soundboard/tree/multiaddon/src/main/resources/resourcepacks/default).  
Translation files also support [OwO-lib rich translations](https://docs.wispforest.io/owo/rich-translations/)

## Translatable local sounds
You still can put `.wav` files in `.minecraft/config/soundboard`, 
but this update allows you to customize their appearance.

To provide a sound name, put a `<filename>.properties` in its directory. 
For the category name put a `.properties` file inside it.

Property files follow the [Java properties format](https://docs.oracle.com/cd/E23095_01/Platform.93/ATGProgGuide/html/s0204propertiesfileformat01.html), but only the `title` field matters.

For example, if you have the sound `.minecraft/config/soundboard/scary/ghost_boo.wav`, 
you use these files:

```properties
# .minecraft/config/soundboard/scary/.properties
title=Spooky sounds
```

```properties
# .minecraft/config/soundboard/scary/ghost_boo.properties
title=Very scary ghost sound
```

## Audio editor
Ctrl-click on any sound to bring up the editor:
![Editor GUI](https://i.imgur.com/lpIdP65.png)

The audio editor allows you to cut the duration of the sound and modify the volume.
You can also use play button in the corner to play it locally.
(Others won't hear it, unless you have bad noise suppression)

The audio editor doesn't modify the original file and saves changes when closed,
so you can bring it up again to adjust the values.