# Football Empire — Unity rebuild

This branch is the clean foundation for the Unity + C# version. The previous Java prototype remains on its own backup branch.

## Target
- Android portrait game
- Football manager career
- 2D match presentation as the first match-view milestone
- Club/squad/tactics/transfers and persistent career saves
- Later expansion only after the core career loop works

## Current foundation
- `PlayerData`: player attributes and development potential
- `ClubData`: club finances and squad
- `MatchEngine`: initial strength-based match result and statistics
- `CareerSaveData`: career-season progress
- `CareerSaveSystem`: local JSON save via Unity PlayerPrefs

## Milestones
1. Get a clean Android build through GitHub Actions.
2. Build a polished career home screen and working navigation.
3. Add a real squad with player details, formations and tactics.
4. Add a minute-by-minute 2D match simulation with moving players and ball.
5. Add league fixtures, table, transfer negotiations and player growth.
6. Add more leagues and data after the core loop is stable.

The match engine above is only an initial result simulator. It does not yet animate players on a pitch. A Unity editor build and Android APK still need to be validated.
