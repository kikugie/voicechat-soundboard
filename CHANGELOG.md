## Added
- Chinese translation (@suoyukii)

## Fixes
- Some audio files are not playing.
- Crash with Visual Overhaul when opening the audio cutter.
- Occasional crash when playing the sound, I have no idea what causes it, but it's there.

## Internal changes
This functionality is not accessible directly in the mod yet, but can be changed in `.minecraft/config/soundboard.json`.

### Cobalt access
[Cobalt](https://cobalt.tools/) is the service used by Soundboard to download audio files from YouTube and other platforms.
Unfortunately, due to abuse the API is not going to be public for long. This update brings initial configuration for accessing
custom cobalt instances.
- `cobalt_api_endpoint` - Base URL or IP for the cobalt instance.
- `cobalt_api_version` - Must be `V7` or `V10`. Describes the method to parse cobalt responses. The main instance only supports V7 for public requests. If you're running a custom instance, change it to V10.
- `cobalt_api_token` - Token for accessing the specified cobalt instance.

### Audio providers
If you're having audio playback issues, try changing `audio_provider` property to `ARRAY`.

### The rest
If you opened this file, you may notice some other fields. These are not fully implemented and may have bugs, but who am I to stop you.