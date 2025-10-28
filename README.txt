# Tank Maze Game - Vanguard Alley
### R&D Project 2025 | COMP702 / COMP703

*Vanguard Alley* is a fast-paced, grid-based tank maze game powered by gesture controls (Webcam + OpenCV).  
Navigate mazes, dodge enemy fire, and clear each level through precision and quick thinking.

---

## Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Screens / Flow](#screens-flow)
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

**Language:** Java  
**Libraries / Frameworks:** LWJGL (OpenGL / GLFW), JavaCV / OpenCV  
**Build Tool:** Maven  
**Supported Platforms:** Windows, macOS, Linux  
**Input Methods:** Webcam (Gesture Control) + Optional Keyboard/Mouse (for debugging)

Developed as part of the **Research & Development** paper — AUT University, 2025.

---

## Features
- Gesture controls for real-time hand tracking  
- Dynamic maze generation (new layout each run)  
- Enemy AI with unique behaviors and attack patterns  
- Audio system (music and sound effects)  
- HUD & UI with menus, volume, and brightness control  
- Configurable difficulty and display settings  

---

## Screens / Flow
1. Pre-Launch Menu → Start, Settings, Credits  
2. Demo / Tutorial Mode → Gesture introduction and testing area  
3. Gameplay → Navigate maze, clear enemies, reach exit  
4. Win / Lose / Level Complete Screens → Show results and next level options  

---

## Controls

**Move Forward:** Hand Tilt Up / W  
**Move Backward:** Hand Tilt Down / S  
**Turn Left:** Hand Left / A  
**Turn Right:** Hand Right / D  
**Shoot:** Fist Close / Space  
**Pause:** Open Palm / Esc  

Gestures are processed via **OpenCV** using the `GestureManager` class.

---

## Requirements
- Java 11 (JDK)  
- Maven 3.9+  
- Webcam (for gesture input)  
- GPU with OpenGL support  

---

## Build
```bash
# Clone the repository
git clone https://github.com/<your-username>/Vanguard-Alley.git
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

Or run it directly from your IDE (IntelliJ / VS Code) using `TankMazeGame.java` in `game.app`.

---

## Configuration
Configurations are defined in `GameConfig.java` and include:
- Window resolution and fullscreen mode  
- Audio volume and toggles  
- Level difficulty and layout options  
- Debug mode (gesture overlay, FPS info)  

---

## Project Structure
```
src/
 └── main/
     ├── java/game/
     │   ├── app/
     │   ├── audio/
     │   ├── gameplay/
     │   ├── input/
     │   ├── render/
     │   ├── tutorial/
     │   ├── ui/menu/
     │   └── util/
     └── resources/
         ├── audio/
         ├── cascade/
         └── fonts/
pom.xml
README.md
```

---

## Contributing
1. Fork the repository  
2. Create a new branch (`feature/new-feature`)  
3. Commit your changes  
4. Push and open a Pull Request  

---

## Credits
**Team Vanguard**  
- Devante — Gameplay Logic, Rendering, AI  
- Leo — Gameplay Loop, Core Mechanics  
- Shayne — Product Owner, Team Lead  
- Yunsu — Gesture Recognition (JavaCV/OpenCV)  
- Kanak & Frank — Menu Systems, XML Integration  

Developed with passion for the AUT Research & Development module (COMP702 / COMP703).
