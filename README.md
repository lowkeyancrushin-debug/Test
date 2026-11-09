# Pool Aim Overlay (starter project)

An original, no-key Android floating-overlay starter project.

## What works in this starter
- Requests Android's display-over-other-apps permission.
- Starts a foreground service with a draggable floating bubble.
- Opens a floating menu with switches for Ghost Ball, Extended Lines, Bank Shot,
  Pocket Prediction, Auto Aim, and Hard Hit.
- No activation key, account, or root access is used.

## Important limitations
This is a UI/overlay starter, not a complete pool-game analyzer. The switches are
currently controls/placeholders: this build does not capture the game screen, detect
balls, calculate a real shot from live game state, move the in-game cue, or fire shots.
Actual ball detection and guideline rendering require a separate, user-approved
Android MediaProjection screen-capture implementation and calibration/testing.
It intentionally does not automate gameplay input.

## Build
Open this folder in Android Studio, or push it to a GitHub repository. The included
GitHub Actions workflow builds a debug APK and uploads it as an Actions artifact.

The debug APK is unsigned for Play Store release but can be installed after enabling
"Install unknown apps" for the app used to open it. Android may show an overlay
permission prompt the first time you start the floating menu.

Package: `com.poolassistant.overlay`
Minimum Android: 8.0 (API 26)
