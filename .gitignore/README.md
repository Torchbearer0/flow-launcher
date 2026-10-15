# Flow Launcher

A minimalist Android launcher with a vertical app list, search, and theming.

## Build

### GitHub Actions (automatic)

Every push to `main` triggers a build. The APK is available as a downloadable artifact.

1. Push this code to a GitHub repository
2. Go to **Actions** tab to see the build progress
3. Download the APK from **Artifacts** or the Releases section

### Local Build

```bash
./gradlew assembleDebug
```

APK output: `app/build/outputs/apk/debug/app-debug.apk`

## Features

- Vertical scrolling app list
- Search with fuzzy matching
- At A Glance clock
- Theme picker (6 themes)
- Adjustable text/icon size, spacing, opacity
- Gesture actions (double-tap, swipe up/down)
- Notification dots
- Widget hosting
- Lock screen accessibility service
