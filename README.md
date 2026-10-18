# 📚 Class Schedule

**Class Schedule** is a feature-rich Android application built to manage daily class sessions, schedules, and academic routines efficiently[cite: 2].

## 🛠️ Project Structure & Architecture

The application is structured into modular packages within the `app` module[cite: 2]:

* **Data Management**: 
  * Managed by `RoutineManager.kt` to handle routine logic and persistence[cite: 2].
  * Utilizes local assets including `routine.json` for schedule data[cite: 2].
* **Models**: 
  * Contains core data structures such as `ClassSession.kt`[cite: 2].
* **UI Screens & Components**: 
  * Features interactive screens like `MainScreen.kt`, `TableViewScreen.kt`, `EditRoutineScreen.kt`, and `JsonEditorScreen.kt`[cite: 2].
  * Includes reusable components such as `ClassCard.kt` and `TableComponents.kt`[cite: 2].
* **Theme & Styling**: 
  * Implements custom Material design themes, colors, typography, and widget colors via `Color.kt`, `Theme.kt`, `Type.kt`, and `WidgetColors.kt`[cite: 2].
* **Widgets & Workers**: 
  * Provides home screen widgets and updates through `ClassWidgets.kt`, `WidgetComponents.kt`, `WidgetReceivers.kt`, and `WidgetUpdateWorker.kt`[cite: 2].

## 🚀 Tech Stack & Build System

* **Language**: Kotlin[cite: 2]
* **Platform**: Android[cite: 2]
* **Build Configuration**: Gradle build system utilizing `build.gradle.kts`, `libs.versions.toml`, and `settings.gradle.kts`[cite: 2]
