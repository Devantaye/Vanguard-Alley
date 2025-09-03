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

    // NEW: Multithreaded gesture system
    private GestureManager gestures;

    // NEW: Movement cadence (grid step every moveInterval seconds while held)
    private double lastMoveTime = 0.0;

    // ──────────────────────────────────────────────
    // Main game loop
    // ──────────────────────────────────────────────
    public void run() throws IOException {
        initWindow();

        fontRenderer = new FontRenderer();
        winRenderer = new WinRenderer(fontRenderer);
        loseRenderer = new LoseRenderer(fontRenderer);
        levelRenderer = new LevelRenderer(fontRenderer);

        state = GameState.PLAYING;
        currentLevel = 1;
        startNewGame(currentLevel);

        double lastTime = glfwGetTime();

        while (!glfwWindowShouldClose(window)) {
            double now = glfwGetTime();
            float dt = (float) (now - lastTime);
            lastTime = now;

            float[] bg = GameConfig.BG_COLORS[Math.min(currentLevel - 1, GameConfig.BG_COLORS.length - 1)];

            glClearColor(bg[0], bg[1], bg[2], 1.0f);
            glClear(GL_COLOR_BUFFER_BIT);

            if (state == GameState.PLAYING) {
                glMatrixMode(GL_PROJECTION);
                glLoadIdentity();
                glOrtho(-1, 1, -1, 1, -1, 1);
                glMatrixMode(GL_MODELVIEW);

                handleGame(); // ← includes rate-limited movement

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
    final float margin = GameConfig.cellsToWorld(6f);

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
    // Input Handlers
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

        if (glfwGetKey(window, GLFW_KEY_F) == GLFW_PRESS && now - lastShootTime >= GameConfig.SHOOT_COOLDOWN) {
            bullets.add(player.shoot());
            lastShootTime = now;
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
