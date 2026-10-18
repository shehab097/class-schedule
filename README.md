# 📚 Class Schedule

[![Kotlin](https://img.shields.io/badge/Language-Kotlin-orange.svg)](https://kotlinlang.org)
[![Platform](https://img.shields.io/badge/Platform-Android-green.svg)](https://www.android.com)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

**Class Schedule** is a modern Android application whose **core purpose is displaying interactive home screen widgets** to keep your daily routines, current sessions, and upcoming classes visible without needing to open the app. 

---

## ✨ Key Features

- **🎯 Widget-First Experience**: Designed to power versatile home screen widgets (`ClassWidgets.kt`) including current/upcoming class trackers, daily views, and full routine displays.
- **🔄 Automated Background Updates**: Utilizes a dedicated background worker (`WidgetUpdateWorker.kt`) and receivers (`WidgetReceivers.kt`) to keep your widgets updated seamlessly.
- **📋 Routine Management & Editors**: View and modify your schedule data easily via dedicated screens like `TableViewScreen.kt`, `EditRoutineScreen.kt`, and `JsonEditorScreen.kt`.
- **🎨 Modern Android Architecture**: Built natively with Jetpack Compose, Material Design, and custom styling components (`ClassCard.kt`, `TableComponents.kt`).

---

## 🛠️ Tech Stack & Project Structure

- **Language**: [Kotlin](https://kotlinlang.org/)[cite: 1, 2]
- **Toolkit**: Jetpack Compose (UI)
- **Data & Storage**: Local JSON-based routine management (`routine.json`, `RoutineManager.kt`)
- **Background Processing**: Android WorkManager for widget updates

### Project Layout
```text
class-schedule-main/
├── app/src/main/java/com/shehab/classschedule/
│   ├── data/          # Routine management and data parsing (`RoutineManager.kt`)[cite: 1, 2]
│   ├── model/         # Data classes (`ClassSession.kt`)[cite: 1, 2]
│   ├── ui/            # Compose screens and UI components[cite: 1, 2]
│   ├── theme/         # Material 3 colors, typography, and widget color schemes[cite: 1, 2]
│   └── widget/        # App widget providers, components, receivers, and workers[cite: 1, 2]
└── app/src/main/res/  # Layouts, vector assets, and widget info XML files[cite: 1, 2]
