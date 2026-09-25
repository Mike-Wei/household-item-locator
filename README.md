# Household Item Locator

A Kotlin/Jetpack Compose Android app for tracking household items and their locations.

## Open and run in Android Studio

1. Clone or download this repository.
2. Open the **repository root** (`household-item-locator`), not the `app` directory.
3. Use Android Studio Ladybug (or newer) with JDK 17 selected under **Settings > Build Tools > Gradle**.
4. Allow Gradle sync to download Gradle 8.7 and the Android/Kotlin dependencies.
5. Select an Android emulator or physical device running API 26 or newer.
6. Click **Run** for the `app` configuration.

The project uses:

- Android Gradle Plugin 8.5.2
- Gradle 8.7
- Kotlin 1.9.24
- Jetpack Compose with the Kotlin Compose compiler extension
- Room 2.6.1 with KSP

No `local.properties` file is committed; Android Studio creates it automatically for each machine.

## Core behavior

- Check whether an item exists by name.
- Create a new item with a location.
- Update the location when an item already exists.
- Search for all items whose location contains a typed search term.
- Persist data locally with Room.
