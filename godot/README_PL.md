# Football Empire — Godot 4

## Projekt
- Silnik: Godot 4.x
- Platforma docelowa: Android APK
- Renderer: Compatibility / OpenGL ES 3
- Główna scena: `Main.tscn`
- Kod gry: `scripts/main.gd`
- Dane kariery: zapis JSON w `user://football_empire_save.json`
- Schemat bazy SQLite: `scripts/career_database.gd` (wymaga dodatku Godot SQLite, nie jest jeszcze podłączony do sceny)

## Uruchomienie
1. Pobierz Godot 4.x Standard z oficjalnej strony.
2. W Godot wybierz Import i wskaż folder `godot`.
3. Otwórz `project.godot`, uruchom F6/F5.
4. Rozpocznij karierę i wybierz klub.

## Stan funkcji
W tym fundamencie działają lokalna kariera, skład generowany dla wybranego klubu, taktyka, rynek transferowy demonstracyjny, trening, skauting symulowany, morale, terminarz, tabela, zapis/odczyt JSON oraz mecz z animowanym widokiem 2D. Baza światowych klubów i prawdziwych składów nie jest jeszcze kompletna; nie należy traktować nazw rywali generowanych w terminarzu jako oficjalnych klubów.

## Android
Eksport APK wymaga Godot 4.x, szablonów eksportu Android, JDK oraz Android SDK skonfigurowanych w Editor Settings > Export > Android. Sam projekt źródłowy nie jest jeszcze plikiem APK.
