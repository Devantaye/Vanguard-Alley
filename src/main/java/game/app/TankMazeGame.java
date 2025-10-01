package game.app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.opencv.opencv_java;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_N;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetKey;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;

import org.lwjgl.system.MemoryUtil;

import game.audio.AudioPlayer;
import game.gameplay.Bullet;
import game.gameplay.MazeGenerator;
import game.gameplay.Player;
import game.gameplay.Player.Direction;
import game.gameplay.enemies.Enemy;
import game.input.GestureManager;
import game.render.FontRenderer;
import game.render.GameRenderer;
import game.render.LevelRenderer;
import game.render.LoseRenderer;
import game.render.WinRenderer;
import game.ui.menu.PrelaunchMenu;
import game.gameplay.enemies.EnemyType;
import game.gameplay.enemies.EnemySpawning;

// NEW: tutorial
import game.tutorial.TutorialDemo;

public class TankMazeGame {

    static { Loader.load(opencv_java.class); }

    // Window properties
    private long window;

    // Game components
    private MazeGenerator generator;
    private int[][] maze;
    private Player player;
    private GameRenderer renderer;
    private FontRenderer fontRenderer;
    private WinRenderer winRenderer;
    private LoseRenderer loseRenderer;
    private LevelRenderer levelRenderer;
    private GameState state;

    // Gameplay elements
    private final List<Bullet> bullets = new ArrayList<>();
    private List<Enemy> enemies;
    private List<Bullet> enemyBullets;
    private final Random rand = new Random();

    // Shooting
    private double lastShootTime = 0.0;

    // Level progression
    private int currentLevel = 1;

    // Gestures
    private GestureManager gestures;

    // Movement cadence
    private double lastMoveTime = 0.0;

    // ── Tutorial glue ─────────────────────────────────────────────
    private TutorialDemo tutorial;
    private boolean tutorialAllowShooting = false;
    
    // Stationary + slow-fire behavior in tutorial
    private double tutEnemyLastShotTime = 0.0;
    private double tutEnemyShotInterval = 4; // seconds between shots (tweak to taste)
    private Float tutEnemySpawnX = null, tutEnemySpawnY = null; // remember spawn point to pin enemy


    // ──────────────────────────────────────────────
    // Main game loop
    // ──────────────────────────────────────────────
    public void run() throws IOException {
        initWindow();

        fontRenderer = new FontRenderer();
        winRenderer = new WinRenderer(fontRenderer);
        loseRenderer = new LoseRenderer(fontRenderer);
        levelRenderer = new LevelRenderer(fontRenderer);

        // Start in tutorial
        state = GameState.TUTORIAL;
        currentLevel = 1;
        initTutorial();

        double lastTime = glfwGetTime();

        while (!glfwWindowShouldClose(window)) {
            double now = glfwGetTime();
            float dt = (float) (now - lastTime);
            lastTime = now;

            float[] bg = GameConfig.BG_COLORS[Math.min(currentLevel - 1, GameConfig.BG_COLORS.length - 1)];
            glClearColor(bg[0], bg[1], bg[2], 1.0f);
            glClear(GL_COLOR_BUFFER_BIT);

            if (state == GameState.TUTORIAL) {
                // world projection
                glMatrixMode(GL_PROJECTION);
                glLoadIdentity();
                glOrtho(-1, 1, -1, 1, -1, 1);
                glMatrixMode(GL_MODELVIEW);

                // handle movement + bullets + collisions during tutorial
                handleTutorial();        // moves player, handles bullets
                tutorial.update(dt);     // <-- advance steps / exit logic happens here 

                // let the tutorial render walls/player/its enemy + exit marker
                tutorial.render(state, 1);

                // player bullets rendering (white)
                for (int i = 0; i < bullets.size(); i++) {
                    Bullet b = bullets.get(i);
                    b.update();
                    if (!b.isAlive()) {
                        bullets.remove(i--);
                    } else {
                        glColor3f(1f, 1f, 1f);
                        b.render();
                    }
                }

            } else if (state == GameState.PLAYING) {
                // world projection
                glMatrixMode(GL_PROJECTION);
                glLoadIdentity();
                glOrtho(-1, 1, -1, 1, -1, 1);
                glMatrixMode(GL_MODELVIEW);

                handleGame(); // includes movement, shooting, some state transitions

                // Update enemies
                List<Bullet> newEnemyShots = new ArrayList<>();
                for (Enemy e : enemies) e.update(dt, player, newEnemyShots);
                enemyBullets.addAll(newEnemyShots);

                // Enemy bullets → hit player
                for (int i = 0; i < enemyBullets.size(); i++) {
                    Bullet b = enemyBullets.get(i);
                    b.update();

                    if (Math.abs(b.getX() - player.getX()) < player.getSize() &&
                        Math.abs(b.getY() - player.getY()) < player.getSize()) {
                        state = GameState.LOSE;
                        break;
                    }

                    if (!b.isAlive()) {
                        enemyBullets.remove(i--);
                    } else {
                        glColor3f(1f, 0f, 0f);
                        b.render();
                    }
                }

                // Player bullets → hit enemies
                for (int i = 0; i < bullets.size(); i++) {
                    Bullet pb = bullets.get(i);
                    for (int j = 0; j < enemies.size(); j++) {
                        Enemy en = enemies.get(j);
                        if (Math.abs(pb.getX() - en.getX()) < en.getSize() &&
                            Math.abs(pb.getY() - en.getY()) < en.getSize()) {
                            bullets.remove(i--);
                            if (en.hit()) enemies.remove(j--);
                            break;
                        }
                    }
                }

                // Render everything
                renderer.render(state, enemies.isEmpty(), enemies, currentLevel);

                // Player bullets rendering
                for (int i = 0; i < bullets.size(); i++) {
                    Bullet b = bullets.get(i);
                    b.update();
                    if (!b.isAlive()) {
                        bullets.remove(i--);
                    } else {
                        glColor3f(1f, 1f, 1f);
                        b.render();
                    }
                }

            } else {
                // UI states
                glMatrixMode(GL_PROJECTION);
                glLoadIdentity();
                glOrtho(0, GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT, 0, -1, 1);
                glMatrixMode(GL_MODELVIEW);

                switch (state) {
                    case WIN:
                        winRenderer.render();
                        handleWin();
                        break;
                    case LOSE:
                        loseRenderer.render();
                        handleLose();
                        break;
                    case LEVEL_COMPLETE:
                        glMatrixMode(GL_PROJECTION);
                        glLoadIdentity();
                        glOrtho(0, GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT, 0, -1, 1);
                        glMatrixMode(GL_MODELVIEW);
                        levelRenderer.render(currentLevel);
                        handleLevelComplete();
                        break;
                    default:
                        break;
                }
            }
            glfwSwapBuffers(window);
            glfwPollEvents();
        }

        // Cleanup
        glfwDestroyWindow(window);
        glfwTerminate();
        if (gestures != null) gestures.stop();
    }

    // ──────────────────────────────────────────────
    // Initialization
    // ──────────────────────────────────────────────
    private void initWindow() {
        initGestures(); // start gesture threads first

        if (!glfwInit()) throw new IllegalStateException("Failed to init GLFW");

        window = glfwCreateWindow(GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT, "Tank Maze Game", MemoryUtil.NULL, MemoryUtil.NULL);
        if (window == MemoryUtil.NULL) throw new RuntimeException("Window creation failed");

        GLFWVidMode vid = glfwGetVideoMode(glfwGetPrimaryMonitor());
        if (vid != null) glfwSetWindowPos(window, (vid.width() - GameConfig.WINDOW_WIDTH) / 2, (vid.height() - GameConfig.WINDOW_HEIGHT) / 2);

        glfwMakeContextCurrent(window);
        GL.createCapabilities();

        if (GameConfig.SOUND_ON) new AudioPlayer(GameConfig.MUSIC_PATH).play();

        glfwSwapInterval(1);
        glfwShowWindow(window);
        glClearColor(0.12f, 0.12f, 0.15f, 1.0f);
    }

    private void initGestures() {
        gestures = new GestureManager(
            "/cascade/left.xml",
            "/cascade/right.xml",
            "/cascade/up.xml",
            "/cascade/down.xml",
            true // set false to hide the webcam preview window
        );
        try {
            gestures.start();
        } catch (org.bytedeco.javacv.FrameGrabber.Exception e) {
            throw new RuntimeException("Failed to start gesture system", e);
        }
    }

    // ──────────────────────────────────────────────
    // Tutorial init
    // ──────────────────────────────────────────────
    private void initTutorial() {
        int size = GameConfig.MAZE_ROWS;   // same grid size as your game
        maze = new int[size][size];
        player = new Player(maze, size);
        renderer = new GameRenderer(maze, player, fontRenderer);

        enemies = new ArrayList<>();
        enemyBullets = new ArrayList<>();
        bullets.clear();

        tutorialAllowShooting = false;

        tutorial = new TutorialDemo(
    window,
    size,
    maze,
    player,
    gestures,
    new TutorialDemo.Callbacks() {
        @Override
public Enemy spawnEnemyAt(float worldX, float worldY) {
    Enemy e = EnemySpawning.create(EnemyType.NORMAL, worldX, worldY, maze);

    // Hard-set its world position in case the factory ignores the args
    e.setPosition(worldX, worldY);

    // ❄️ Freeze AI so it doesn't chase/rotate in the tutorial
    e.setTutorialFrozen(true);
    e.setDirection(Direction.RIGHT);

    // Keep the barrel facing the corridor (right) so it doesn't spin toward player
    e.lockDirection(Direction.RIGHT);

    // Let it shoot on its own slowly while frozen (or disable and do it in the loop)
    e.setFrozenShoot(true, 1.2f); // one shot every ~1.2s

    enemies.add(e);

    // remember spawn point for pinning
    tutEnemySpawnX = worldX;
    tutEnemySpawnY = worldY;
    tutEnemyLastShotTime = glfwGetTime(); // start the slow-fire timer

    return e;
}


        @Override
        public void enableShooting(boolean enabled) {
            tutorialAllowShooting = enabled; // only true during SHOOT step
        }

        @Override
        public void onTutorialComplete() {
            tutorial = null;
            state = GameState.PLAYING;
            startNewGame(1);
        }
    },
    renderer
);

    }

    // ──────────────────────────────────────────────
    // Game start logic
    // ──────────────────────────────────────────────
    private void startNewGame(int level) {
        generator = new MazeGenerator(GameConfig.MAZE_ROWS, GameConfig.MAZE_ROWS);
        maze = generator.getMaze();
        player = new Player(maze, GameConfig.MAZE_ROWS);
        renderer = new GameRenderer(maze, player, fontRenderer);

        bullets.clear();
        enemyBullets = new ArrayList<>();
        lastShootTime = 0;
        enemies = new ArrayList<>();

        final int rows = GameConfig.MAZE_ROWS;
        final float cellSize = GameConfig.cellSize();
        final float margin = GameConfig.cellsToWorld(GameConfig.SPAWN_MARGIN_CELLS);

        int levelEnemyCount = (level == GameConfig.MAX_LEVEL)
            ? GameConfig.FINAL_LEVEL_BONUS_COUNT
            : GameConfig.BASE_ENEMY_COUNT + level;

        int normalCount = 0, miniCount = 0, sniperCount = 0, tankCount = 0;

        for (int i = 0; i < levelEnemyCount; i++) {
            int er, ec;
            float ex, ey;
            do {
                er = rand.nextInt(rows - 2) + 1;
                ec = rand.nextInt(rows - 2) + 1;
                ex = -1 + ec * cellSize + cellSize / 2f;
                ey =  1 - er * cellSize - cellSize / 2f;
            } while (Math.hypot(ex - player.getX(), ey - player.getY()) < margin || maze[er][ec] == 1);

            EnemyType type;
            switch (level) {
                case 1:  type = EnemyType.NORMAL; break;
                case 2:  type = EnemyType.TANK;   break;
                case 3:  type = EnemyType.MINI;   break;
                case 4:  type = EnemyType.SNIPER; break;
                default:
                    while (true) {
                        int t = rand.nextInt(4);
                        if (t == 0 && normalCount < 3) { type = EnemyType.NORMAL; normalCount++; break; }
                        if (t == 1 && miniCount   < 3) { type = EnemyType.MINI;   miniCount++;   break; }
                        if (t == 2 && sniperCount < 3) { type = EnemyType.SNIPER; sniperCount++; break; }
                        if (t == 3 && tankCount   < 3) { type = EnemyType.TANK;   tankCount++;   break; }
                    }
            }

            enemies.add(EnemySpawning.create(type, ex, ey, maze));
        }

        state = GameState.PLAYING;
    }

    // ────────────────────────────────────────────────
    // Input/Update for normal gameplay
    // ────────────────────────────────────────────────
    private void handleGame() {
        boolean left  = (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS)
                        || (gestures != null && gestures.isLeft());
        boolean right = (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS)
                        || (gestures != null && gestures.isRight());
        boolean up    = (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS)
                        || (gestures != null && gestures.isUp());
        boolean down  = (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS)
                        || (gestures != null && gestures.isDown());

        // allow exit in-game
        if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS) {
            glfwSetWindowShouldClose(window, true);
            return;
        }

        // Choose one direction (prevents double-stepping)
        int dx = 0, dy = 0;
        if (left)       { dx = -1; dy =  0; }
        else if (right) { dx =  1; dy =  0; }
        else if (up)    { dx =  0; dy =  1; }
        else if (down)  { dx =  0; dy = -1; }

        double now = glfwGetTime();
        if ((dx != 0 || dy != 0) && now - lastMoveTime >= GameConfig.MOVE_INTERVAL) {
            player.move(dx, dy);
            lastMoveTime = now;
        }

        if (enemies.isEmpty() && player.reachedGoal()) {
            state = (currentLevel < GameConfig.MAX_LEVEL) ? GameState.LEVEL_COMPLETE : GameState.WIN;
        }

        if (glfwGetKey(window, GLFW_KEY_N) == GLFW_PRESS) {
            state = (currentLevel < GameConfig.MAX_LEVEL) ? GameState.LEVEL_COMPLETE : GameState.WIN;
        }

        // SHOOT (only in PLAYING)
        if (glfwGetKey(window, GLFW_KEY_F) == GLFW_PRESS && now - lastShootTime >= GameConfig.SHOOT_COOLDOWN) {
            bullets.add(player.shoot());
            lastShootTime = now;
        }
    }

    // ────────────────────────────────────────────────
    // Tutorial input/update (movement + bullets + collisions)
    // ────────────────────────────────────────────────
    private void handleTutorial() {
    // Movement (WASD or gestures)
    boolean left  = (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) || (gestures != null && gestures.isLeft());
    boolean right = (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) || (gestures != null && gestures.isRight());
    boolean up    = (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) || (gestures != null && gestures.isUp());
    boolean down  = (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) || (gestures != null && gestures.isDown());

    if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS) {
        glfwSetWindowShouldClose(window, true);
        return;
    }

    int dx = 0, dy = 0;
    if (left)       { dx = -1; dy =  0; }
    else if (right) { dx =  1; dy =  0; }
    else if (up)    { dx =  0; dy =  1; }
    else if (down)  { dx =  0; dy = -1; }

    double now = glfwGetTime();
    if ((dx != 0 || dy != 0) && now - lastMoveTime >= GameConfig.MOVE_INTERVAL) {
        player.move(dx, dy);
        lastMoveTime = now;
    }

    // SHOOT (only if tutorial enabled it during SHOOT step)
    if (tutorialAllowShooting && glfwGetKey(window, GLFW_KEY_F) == GLFW_PRESS
            && now - lastShootTime >= GameConfig.SHOOT_COOLDOWN) {
        bullets.add(player.shoot());
        lastShootTime = now;
    }

    // ── Stationary + slow fire enemy logic ─────────────────────────
    // We expect ONE tutorial enemy. Pin it to spawn and only allow a shot every tutEnemyShotInterval seconds.
    if (!enemies.isEmpty() && tutEnemySpawnX != null && tutEnemySpawnY != null) {
        Enemy e = enemies.get(0);

        // 1) pin to spawn
        e.setPosition(tutEnemySpawnX, tutEnemySpawnY);

        // 2) slow fire: only call e.update when it's time to shoot
        //    (many enemy AIs shoot inside update; this throttles ROF without letting it roam)
        if (now - tutEnemyLastShotTime >= tutEnemyShotInterval) {
            List<Bullet> newEnemyShots = new ArrayList<>();
            e.update((float)(now - tutEnemyLastShotTime), player, newEnemyShots);
            enemyBullets.addAll(newEnemyShots);
            tutEnemyLastShotTime = now;

            // re-pin in case update nudged it
            e.setPosition(tutEnemySpawnX, tutEnemySpawnY);
        }
    }

    // Enemy bullets → if hit player, teleport back to RIGHT end of middle row and keep trying
    // Enemy bullets → hit player = teleport to tutorial's live checkpoint (WORLD coords)
for (int i = 0; i < enemyBullets.size(); i++) {
    Bullet b = enemyBullets.get(i);
    b.update();

    if (Math.abs(b.getX() - player.getX()) < player.getSize() &&
        Math.abs(b.getY() - player.getY()) < player.getSize()) {

        // ⬇️ this is the only change: use tutorial-provided WORLD coords
        float[] wp = tutorial.getRespawnWorld();
        player.setPosition(wp[0], wp[1]);    // <- world-space teleport (no grid math)

        enemyBullets.clear();
        break;
    }

    if (!b.isAlive()) {
        enemyBullets.remove(i--);
    } else {
        glColor3f(1f, 0f, 0f);
        b.render();
    }
}


    // Player bullets → if enemy dies, notify tutorial so EXIT spawns at enemy location
    for (int i = 0; i < bullets.size(); i++) {
        Bullet pb = bullets.get(i);
        for (int j = 0; j < enemies.size(); j++) {
            Enemy en = enemies.get(j);
            if (Math.abs(pb.getX() - en.getX()) < en.getSize() &&
                Math.abs(pb.getY() - en.getY()) < en.getSize()) {
                bullets.remove(i--);
                if (en.hit()) {
                    enemies.remove(j--);

                    // reset pinned info (no more enemy)
                    tutEnemySpawnX = tutEnemySpawnY = null;

                    if (tutorial != null) tutorial.notifyEnemyKilled(); // EXIT appears at enemy location
                }
                break;
            }
        }
    }
}


    private void handleWin() {
        if (glfwGetKey(window, GLFW_KEY_SPACE) == GLFW_PRESS) {
            if (currentLevel < GameConfig.MAX_LEVEL) {
                currentLevel++;
                startNewGame(currentLevel);
            } else {
                state = GameState.MENU;
            }
        }
        if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS)
            glfwSetWindowShouldClose(window, true);
    }

    private void handleLose() {
        if (glfwGetKey(window, GLFW_KEY_SPACE) == GLFW_PRESS) {
            currentLevel = 1;
            startNewGame(currentLevel);
        }
        if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS)
            glfwSetWindowShouldClose(window, true);
    }

    private void handleLevelComplete() {
        if (glfwGetKey(window, GLFW_KEY_SPACE) == GLFW_PRESS) {
            currentLevel++;
            startNewGame(currentLevel);
        }
        if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS)
            glfwSetWindowShouldClose(window, true);
    }

    public static void main(String[] args) throws IOException {
        boolean start = PrelaunchMenu.showAndWait();
        if (!start) return;         // user closed menu
        new TankMazeGame().run();   // now GLFW + webcam start
    }
}

