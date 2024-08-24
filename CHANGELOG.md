## New features
### Audio downloader!
Powered by [cobalt](https://cobalt.tools/), Soundboard just got a new menu for converting video to sound and saving it directly to the soundboard.
![downloader screen](https://i.imgur.com/3Iodyy7.png)

### Favourites indexing and keybinds
The sound settings widget now has a field for the favourite index.  
-1 inserts at the end, 0 at the start and so on for values in between.
This allows you to make your top category more organized, especially for the next feature.

Favourite sounds, or at least the first 10, can now be played with a keybind.  
By default, holding `O` and pressing one of the number keys will play the sound at that position.
This also works with numpad keys and fyi key `0` will play the 10th sound.

## Changes
### Soundpack updates (beta)
Resourcepack provided sounds can now have a `.properties` file, which accepts the following fields:
```properties
# Start offset in seconds
start = 1.22
# End of the audio, **counting from 0, not the 'start' property**
end = 3.45
# Integer 0-100, any other value will be capped in that range
volume = 69
```
This file must be in the same directory as the sound and have the same name up to the `.wav` part.

## Fixes
- Fixed `Missing group grid` error
- Fixed sounds in subdirectories displaying with the directory name