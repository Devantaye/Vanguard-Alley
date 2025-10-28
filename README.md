# Tank Maze Game - Vanguard Alley

### R&D Project 2025 | COMP702 / COMP703

**Vanguard Alley** is a fast-paced, grid-based tank maze game powered by gesture controls (Webcam + OpenCV).
Navigate mazes, dodge enemy fire, and clear each level through precision and quick thinking.

---

## Table of Contents

* [Overview](#overview)
* [Features](#features)
* [Screens / Flow](#screens--flow)
* [Controls](#controls)
* [Requirements](#requirements)
* [Build](#build)
* [Run](#run)
* [Configuration](#configuration)
* [Project Structure](#project-structure)
* [Contributing](#contributing)
* [Credits](#credits)

---

## Overview

| Category               | Details                                                            |
| ---------------------- | ------------------------------------------------------------------ |
| Language               | Java                                                               |
| Libraries / Frameworks | LWJGL (OpenGL / GLFW), JavaCV / OpenCV                             |
| Build Tool             | Maven                                                              |
| Supported Platforms    | Desktop — Windows, macOS, Linux                                    |
| Input Methods          | Webcam (Gesture Control) + Optional Keyboard/Mouse (for debugging) |

Developed as part of the **Research & Development** paper - AUT University, 2025.

---

## Features

* Gesture Controls – Perform in-game actions via real-time hand tracking.
* Dynamic Maze Generation – Each run offers a new maze layout.
* Enemy AI – Multiple enemy types with unique behaviors and attack patterns.
* Audio System – Background music and sound effects via `AudioPlayer` and `MusicPlayer`.
* HUD & UI – Interactive menus, volume settings, and brightness adjustments.
* Configurable Settings – Adjustable difficulty, volume, and display options.

---

## Screens / Flow

1. Pre-Launch Menu → Start, Settings, Credits
2. Gameplay → Navigate maze, clear enemies, reach exit
3. Win / Lose / Level Complete Screens → Display performance and next-level options

---

## Controls

| Action        | Gesture / Key      |
| ------------- | ------------------ |
| Move Up       | Open palm, fingers pointing up / W   |
| Move Down     | Closed palm, fingers pointed down / S |
| Turn Left     | Open palm, fingers pointed left / A      |
| Turn Right    | Open palm, fingers pointed left / D     |
| Shoot         | Fist Close / F |

Gestures are processed via **OpenCV** using the `GestureManager` class.

---

## Requirements

* Java 11 (JDK)
* Maven 3.9+
* Webcam (required for gesture control)
* GPU drivers that support OpenGL (LWJGL)

---

## Build

```bash
# Clone the repository
git clone https://github.com/devantaye/Vanguard-Alley.git
cd Vanguard-Alley

# Build using Maven
mvn clean install
```

---

## Run

```bash
# Run the game
mvn exec:java -Dexec.mainClass="game.app.TankMazeGame"
```

Alternatively, run directly from your IDE (IntelliJ or VS Code) by executing `TankMazeGame.java` in `game.app`.

---

## Configuration

Configurations are stored in `GameConfig.java` and can be adjusted for:

* Window resolution and fullscreen mode
* Audio volume and toggle options
* Level design and difficulty modifiers
* Debug mode (gesture visual overlay, frame info)

---

## Project Structure

```text
src/
 └── main/
     ├── java/
     │   └── game/
     │       ├── app/
     │       │    ├── GameConfig.java
     │       │    ├── GameState.java
     │       │    └── TankMazeGame.java
     │       ├── audio/
     │       │    ├── AudioPlayer.java
     │       │    ├── ClickSound.java
     │       │    └── MusicPlayer.java
     │       ├── gameplay/
     │       │    ├── Bullet.java
     │       │    ├── MazeGenerator.java
     │       │    ├── PathFinder.java
     │       │    ├── Player.java
     │       │    └── enemies/
     │       │         ├── Enemy.java
     │       │         ├── EnemySpawning.java
     │       │         ├── EnemyType.java
     │       │         ├── MiniEnemy.java
     │       │         ├── SniperEnemy.java
     │       │         └── TankEnemy.java
     │       ├── input/
     │       │    └── GestureManager.java
     │       ├── render/
     │       │    ├── AWTTextureUtil.java
     │       │    ├── FontRenderer.java
     │       │    ├── GameRenderer.java
     │       │    ├── LevelRenderer.java
     │       │    ├── LoseRenderer.java
     │       │    └── WinRenderer.java
     │       ├── tutorial/
     │       │    └── PracticeRange.java
     │       ├── ui/menu/
     │       │    ├── BrightnessPanel.java
     │       │    ├── Credits.java
     │       │    ├── GameMenu.java
     │       │    ├── PreLaunchMenu.java
     │       │    ├── Rules.java
     │       │    ├── SettingsChangeListener.java
     │       │    ├── SettingsDialog.java
     │       │    └── VolumePanel.java
     │       └── util/
     │            └── ResourceLoader.java
     └── resources/
         ├── audio/
         │    ├── click.wav
         │    ├── game_music.wav
         │    ├── menu_music.wav
         │    └── music.wav
         ├── cascade/ (TBD)
         ├── fonts/
         │    ├── ARCADECLASSIC.ttf
         │    ├── Arial.ttf
         │    └── Roboto.ttf
pom.xml
README.md
```

---

## Contributing

Contributions are welcome.
To contribute:

1. Fork the repository.
2. Create a new branch (`feature/new-feature-name`).
3. Commit your changes (`git commit -m "Add new feature"`).
4. Push to your branch and open a Pull Request.

---

## Credits

**Team Vanguard**

* Devante 
* Leo 
* Shayne 
* Yunsu
* Kanak
* Frank 


