Tank Maze Game - Vanguard Alley - RND Project 2025

## Short Descirption
Vanguard Alley is a fast, grid-based tank maze game with *Gesture Controls* (Webcam + OpenCV). Navigate through mazes, dodge enemy fire
and clear levels. 
note: This project was developed for our Uni Paper: [Research and Development - Comp702/703]

## Table of Contents NOTE: Add (#bracket) when section is complete
[Overview](#Overview)
[Features]
[Screens/Flow]
[Controls]
[Requirements](#Requirements)
[Build]
[Run]
[Configuration]
[Project Structure]
[Contributing]
[Credits]

## Overview
- **Language:** Java
- **Libraries:** LWJGL (OpenGL/GLFW), JavaCV/OpenCV
- **Build:** Maven
- **Platforms:** Desktop (Windows/macOS/Linux)
- **Inputs:** Webcam + Optional Keyboard and mouse (For testing/Debugging purposes)

## Features
*Add features here*
- gesture control
- maze components etc

## Screens/Flow
1. **Prelaunch Menu** → Start/Settings
2. **Demo** → TBD
3. **Game** → Clear enemies, reach the exit, advance levels.
4. **Win/Lose/Level Complete** → Overlays

## Controls
*Controls here*

## Requirements
 - **Java 11** (JDK)
 - **Maven 3.9+**
 - **Webcam** (Required for gestures)
 - GPU Drivers that support OpenGL(LWJGL)

 ## Build
 *Add build here*

## Run
* Add run here*

## Configuration
*Config here*

## Project Structure
src/
   main/
      java/
         game/
            app/          # GameConfig.Java, GameState.java, TankmazeGame.java
            audio/        # AudioPlayer.java, ClickSound.java, MusicPlayer.java
            gameplay/     # Bullet,java, MazeGenerator,java, PathFinder.java, Player.java
               enemies/   # Enemy.java, EnemySpawning.java, EnemyType.java, MiniEnemy.java, SniperEenemy.java, TankEnemy.java
            input/        # GestureManager.java
            render/       # AWTTextureUtil.java, FontRenderer.java, GameRenderer.java, LevelRenderer.java, LoseRenderer.java, WinRenderer.java
            tutorial/     # TBD
            ui/
               menu/      # BrightnessPanel.java, Credits.java, GameMenu.java, PreLaunchMenu.java, Rules.java, SettingsChangeListener.java, SettingsDialog.java, VolumePanel.java
            util/         # ResourceLoader.java
         resources/
            audio/        # click.wav, game_music.wav, menu_music.wav, music.wav
            cascade/      # TBD (change with final ones)
            fonts/        # ARCADECLASSIC.ttf, Arial.ttf, Roboto.ttf
Additional files:         # pom.xml,  README.txt    

## Contributing
*Add contributing here*

## Credits
*Add credits here*


  


