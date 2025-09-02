Tank Maze Game Prototype v2 - Leo & Devante

## Description  
This is version 2 of our collaborative prototype, combining both our upskilling work into a playable 
tank-based maze shooter game. This version focused on refining AI, adding new enemy behaviours, 
improving visual feedback and increasing game complexity.

## Core Features (Similar to v1)
1. Random Maze Generation – A fresh 21×21 maze is generated for every level.
2. Player Movement – Control a white tank with W / A / S / D.
3. Shooting – Fire bullets using F (with a cooldown).
4. Level Progression –
     > Defeat all enemies to unlock the exit.
     > Advance through 5 levels, each increasing difficulty.
5. Dynamic Visuals –
     > Walls and enemies use different color palettes per level.
     > Background subtly shifts to fit the theme.

## New Features
1. Enemy Types - [Levels are formatted to highlight each enemy type]
     > Default tank (Level 1) - Standard AI that roams and shoots.
     > Tanky Enemy (Level 2) - High HP, takes 3 hits to kill, Bigger bullets = harder to dodge.
     > Mini Tanks (Level 3) - Smaller tanks but move alot faster
     > Sniper Tank (Level 4) - Stationary tanks that rotate vision, shoot very fast if player eneters corridor.
     > All tanks (Level 5) - Roughly demonstrates what a level may look like in later stages of development.
2. Visual Updates - All tank nozzles now scale with body size and stay visually connected
3. Level 5 Rules - Max of 3 units per enemy type, all randomly gennerated.
4. Added a "skip level" button to make testing alot easier [ N Key ] 

## How to Run  
1. Navigate to (./target) location and open CMD in the terminal 
2. Run the command -> java -jar TankMazeGame-1.0-SNAPSHOT.jar  
3. **Controls**:  
   - **W / A / S / D** – Move the tank  
   - **F** – Shoot in facing direction (subject to cooldown)  
   - **SPACE** – Start or restart game  
   - **N** - Skip a level (For testing purposes)
   - **ESC** – Quit  

  


