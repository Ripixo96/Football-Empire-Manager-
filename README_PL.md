# Football Empire: Ultimate Career — fundament 0.1

## W tej wersji
- Natywna aplikacja Android (Java), bez dodatkowych bibliotek.
- Menu kariery i wybór klubu z demonstracyjnej listy.
- Trwały lokalny zapis podstawowych statystyk kariery.
- Wybór przed meczem: szybka symulacja albo ekran boiska 2D.
- Jeden wspólny wynik meczu; statystyki kariery aktualizowane tylko raz.
- Workflow GitHub Actions do zbudowania testowego APK.

## Ograniczenia
To fundament/prototyp, nie kompletna gra. Wyniki są symulowane, ekran 2D nie ma jeszcze animowanego ruchu piłki, tabela nie jest pełną ligą i nie ma jeszcze bazy prawdziwych zawodników. Rzeczywiste dane wymagają legalnego/licencjonowanego źródła.

## Budowanie APK
1. Otwórz repozytorium → Actions → Build Football Empire APK → Run workflow.
2. Po zakończeniu otwórz wykonany workflow i pobierz artefakt `Football-Empire-Ultimate-Career-debug-apk`.
3. Rozpakuj ZIP i zainstaluj `app-debug.apk` na Androidzie. To build debugowy do testów, nie do publikacji w sklepie.

Wymagania CI: Java 17, Gradle 8.9, Android Gradle Plugin 8.7.3, compileSdk 35.