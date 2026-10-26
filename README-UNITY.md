# Football Empire — Unity + C#

This is the first coded Unity branch for Football Empire. The previous Android/Java prototype remains available on `main` and in `backup/java-prototype-2026-10-26`.

## Current prototype

- Unity 6 project structure
- C# runtime-created mobile interface
- Career home screen, squad and tactics sections
- A temporary score-only match simulation (not yet a moving 2D match engine)
- Android APK build workflow for GitHub Actions

## Build from GitHub Actions

1. Open **Settings → Secrets and variables → Actions** in this repository.
2. Add a repository secret named `UNITY_LICENSE` containing the Unity license data supported by GameCI.
3. Open **Actions → Build Football Empire Unity APK → Run workflow** on branch `unity-rebuild`.
4. When the run finishes, download the `Football-Empire-Unity-Android-APK` artifact.

The workflow cannot build until a valid Unity license secret is configured. Android signing and release-store setup can be added later; this workflow is intended for an initial test APK.

## Next development steps

1. Verify the first Unity CI build and fix any import/build issues.
2. Replace the temporary score simulation with a 2D pitch, players, ball movement, match clock and events.
3. Add real career data, squads, league tables, transfers, save/load and progression.
4. Improve art, sound, crests and match presentation using assets that we have permission to use.

Do not delete the old Java prototype until the Unity build is confirmed on the phone.
