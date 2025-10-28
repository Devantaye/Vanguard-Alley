# Tank Maze Game – Vanguard Alley
**R&D Project 2025 | COMP702 / COMP703**

Vanguard Alley is a fast-paced, grid-based tank maze game powered by gesture controls (Webcam + OpenCV).
Navigate mazes, dodge enemy fire, and clear each level through precision and quick thinking.

## Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Screens / Flow](#screens--flow)
- [Controls](#controls)
- [Requirements](#requirements)
- [Build](#build)
- [Run](#run)
- [Configuration](#configuration)
- [Project Structure](#project-structure)
- [Contributing](#contributing)
- [Credits](#credits)

---

## Overview
- **Language:** Java
- **Libraries:** LWJGL (OpenGL/GLFW), JavaCV/OpenCV
- **Build:** Maven
- **Platforms:** Desktop (Windows/macOS/Linux)
- **Inputs:** Webcam + Optional Keyboard and mouse (For testing/Debugging purposes)

> Developed as part of the Research & Development paper – AUT University, 2025.

---

## Features
- **Gesture Controls** — Perform in-game actions via real-time hand tracking.  
- **Dynamic Maze Generation** — Each run offers a new maze layout.  
- **Enemy AI** — Multiple enemy types with unique behaviors and attack patterns.  
- **Audio System** — Background music and sound effects via `AudioPlayer` and `MusicPlayer`.  
- **HUD & UI** — Interactive menus, volume settings, and brightness adjustments.  
- **Configurable Settings** — Adjustable difficulty, volume, and display options.  

---

## Screens / Flow
1. **Pre-Launch Menu** → Start, Settings, Credits  
2. **Demo / Tutorial Mode** → (Gesture introduction and testing area)  
3. **Gameplay** → Navigate maze, clear enemies, reach exit  
4. **Win / Lose / Level Complete Screens** → Display performance and next-level options  

---

## Controls
Move Forward: Hand Tilt Up / W
Move Backward: Hand Tilt Down / S
Turn Left: Hand Left / A
Turn Right: Hand Right / D
Shoot: Fist Close / Space
Pause: Open Palm / Esc

Gestures are processed via OpenCV using the GestureManager class.

Requirements
• Java 11 (JDK)
• Maven 3.9+
• Webcam (required for gesture control)
• GPU drivers that support OpenGL (LWJGL)

Build

Clone the repository

Navigate to the project directory

Build using Maven

Command example:
git clone https://github.com/
<your-username>/Vanguard-Alley.git
cd Vanguard-Alley
mvn clean install

Run
To run the game:
mvn exec:java -Dexec.mainClass="game.app.TankMazeGame"

Alternatively, run directly from your IDE (IntelliJ / VS Code) by executing TankMazeGame.java in game.app.

Configuration
GameConfig.java stores adjustable settings, including:
• Window resolution and fullscreen mode
• Audio volume and toggles
• Level difficulty modifiers
• Debug mode (gesture overlays, frame info)

Project Structure
src/
main/
java/
game/
app/
GameConfig.java
GameState.java
TankMazeGame.java
audio/
AudioPlayer.java
ClickSound.java
MusicPlayer.java
gameplay/
Bullet.java
MazeGenerator.java
PathFinder.java
Player.java
enemies/
Enemy.java
EnemySpawning.java
EnemyType.java
MiniEnemy.java
SniperEnemy.java
TankEnemy.java
input/
GestureManager.java
render/
AWTTextureUtil.java
FontRenderer.java
GameRenderer.java
LevelRenderer.java
LoseRenderer.java
WinRenderer.java
tutorial/
PracticeRange.java
ui/menu/
BrightnessPanel.java
Credits.java
GameMenu.java
PreLaunchMenu.java
Rules.java
SettingsChangeListener.java
SettingsDialog.java
VolumePanel.java
util/
ResourceLoader.java
resources/
audio/
click.wav
game_music.wav
menu_music.wav
music.wav
cascade/ (TBD)
fonts/
ARCADECLASSIC.ttf
Arial.ttf
Roboto.ttf
pom.xml
README.md

Contributing

Fork the repository

Create a new branch (feature/new-feature-name)

Commit your changes (git commit -m "Add new feature")

Push to your branch and open a Pull Request

Credits
Team Vanguard
Devante — Gameplay Logic, Rendering & AI
Leo — Gameplay Loop & Core Mechanics
Shayne — Product Owner / Team Lead
Yunsu — Gesture Recognition (JavaCV/OpenCV)
Kanak & Frank — Menu Systems, XML Integration

Developed with passion for the AUT Research & Development module (COMP702 / COMP703).

# Build using Maven
mvn clean install
