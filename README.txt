# Tank Maze Game - Vanguard Alley
### R&D Project 2025 | COMP702 / COMP703

**Vanguard Alley** is a fast-paced, grid-based tank maze game powered by gesture controls (Webcam + OpenCV).  
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

| Category | Details |
|-----------|----------|
| Language | Java |
| Libraries / Frameworks | LWJGL (OpenGL / GLFW), JavaCV / OpenCV |
| Build Tool | Maven |
| Supported Platforms | Desktop — Windows, macOS, Linux |
| Input Methods | Webcam (Gesture Control) + Optional Keyboard/Mouse (for debugging) |

Developed as part of the **Research & Development** paper — AUT University, 2025.

---

## Features
- Gesture Controls – Perform in-game actions via real-time hand tracking.  
- Dynamic Maze Generation – Each run offers a new maze layout.  
- Enemy AI – Multiple enemy types with unique behaviors and attack patterns.  
- Audio System – Background music and sound effects via `AudioPlayer` and `MusicPlayer`.  
- HUD & UI – Interactive menus, volume settings, and brightness adjustments.  
- Configurable Settings – Adjustable difficulty, volume, and display options.  

---

## Screens / Flow
1. Pre-Launch Menu → Start, Settings, Credits  
2. Demo / Tutorial Mode → Gesture introduction and testing area  
3. Gameplay → Navigate maze, clear enemies, reach exit  
4. Win / Lose / Level Complete Screens → Display performance and next-level options  

---

## Controls

| Action | Gesture / Key |
|--------|----------------|
| Move Forward | Hand Tilt Up / W |
| Move Backward | Hand Tilt Down / S |
| Turn Left | Hand Left / A |
| Turn Right | Hand Right / D |
| Shoot | Fist Close / Space |
| Pause | Open Palm / Esc |

Gestures are processed via **OpenCV** using the `GestureManager` class.

---

## Requirements
- Java 11 (JDK)  
- Maven 3.9+  
- Webcam (required for gesture control)  
- GPU drivers that support OpenGL (LWJGL)  

---

## Build
```bash
# Clone the repository
git clone https://github.com/<your-username>/Vanguard-Alley.git
cd Vanguard-Alley

# Build using Maven
mvn clean install
