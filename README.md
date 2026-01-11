# StudySprint

StudySprint is a student-focused Pomodoro app that does more than timing: it tracks study sessions linked to **Courses** and **Exams**, and stores a **readiness status** after each session so you can quickly see what you’ve finished and what still needs work.

## USP (Unique Selling Proposition)
Unlike a basic Pomodoro timer, StudySprint links every study session to a **Course** (required) and optionally to an **Exam**, then asks for **readiness feedback** at the end. This creates a simple “progress + readiness” overview per course/exam.

---

## Features

### Courses
- Create courses (required before studying)
- Delete courses
- Mark courses as **completed** (meaning you’ve studied enough)

### Exams
- Create upcoming exams with:
    - Title
    - Date
    - Linked course
- Delete exams

### Study Timer (Pomodoro)
- Presets:
    - Standard (25/5)
    - Long (50/10)
    - Demo (short preset for presentation)
- Select:
    - Course (**required**)
    - Exam (**optional**)
- Choose how many **rounds** to run (Focus + Break repeated N times)
- When the timer finishes:
    - Add an optional note
    - If an exam was selected: set **Ready / Not ready**
- Sessions are saved locally (offline)

### Stats
- Total focus time per **course**
- Total focus time per **exam**
- Exam readiness **status** (latest feedback)

---

## Tech Stack
- Kotlin + Android Jetpack Compose (Material 3)
- Room (local persistence, offline)
- Simple MVVM + Repository architecture
- Navigation Compose

---

## Architecture (high level)
- **data/local**: Room entities, DAO, database
- **repository**: single source of truth for the app data
- **ui/screens**: Compose screens
- **ui/navigation**: navigation + scaffold
- **util**: small helpers (time formatting, date utils, etc.)

---

## How to Run
1. Open the project in Android Studio
2. Sync Gradle
3. Run on an emulator or physical device

---

## APK
A built APK is included in the repository:

- `apk/app-debug.apk`

(Your file name may differ slightly, but it will be inside the `apk/` folder.)

---

## Tools & Credits
- Developed with Android Studio.
- AI assistance: GitHub Copilot was used during development for code suggestions and productivity.
