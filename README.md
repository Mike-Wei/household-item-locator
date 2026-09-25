# Household Item Locator

A small Android app for tracking household items and their locations.

Features:
- Search for an item by name
- Check whether the item already exists in the local record
- Create a new household item
- Update the location of an existing item
- Search items by location
- Store the information locally on device with Room

## Tech stack
- Kotlin
- Jetpack Compose
- Material 3
- Room database

## Setup
1. Open this project in Android Studio.
2. Let Android Studio sync Gradle.
3. Choose an emulator or device and run the app.

## Core flow
- Enter item name and location.
- Tap "Check Item" to see whether it exists.
- Tap "Save / Update Item" to create or update the record.
- Use the location search box to view all matching household items in a room, area, or zone.

## Project structure
- `app/src/main/java/.../data` - Room entities, DAO, database
- `app/src/main/java/.../ui` - Compose UI and state
- `app/src/main/res` - Android resources
