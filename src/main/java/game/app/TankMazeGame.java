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
import game.gameplay.Enemy;
import game.gameplay.MazeGenerator;
import game.gameplay.MiniEnemy;
import game.gameplay.Player;
import game.gameplay.SniperEnemy;
import game.gameplay.TankEnemy;
import game.input.GestureManager;
import game.render.FontRenderer;
import game.render.GameRenderer;
import game.render.LevelRenderer;
import game.render.LoseRenderer;
import game.render.WinRenderer;
import game.ui.menu.PrelaunchMenu;

public class TankMazeGame {

    static { Loader.load(opencv_java.class); }

    // Window properties
    private long window;
    private final int width = 800;
    private final int height = 800;
    private final int rows = 21; // must be odd

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
    private final double shootCooldown = 0.5;
    private double lastShootTime = 0.0;

    // Level progression
    private int currentLevel = 1;
    private final int maxLevel = 5;
    private final int enemyCount = 2; // base enemies

    // Background colors by level
    private final float[][] backgroundColors = {
        {0.1f, 0.1f, 0.1f},
        {0.05f, 0.1f, 0.2f},
        {0.1f, 0.05f, 0.15f},
        {0.1f, 0.05f, 0.02f},
        {0.02f, 0.1f, 0.05f}
    };

    // NEW: Multithreaded gesture system
    private GestureManager gestures;

    // NEW: Movement cadence (grid step every moveInterval seconds while held)
    private final double moveInterval = 0.10;  // tweak 0.10–0.18 to taste
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

            float[] bg = backgroundColors[Math.min(currentLevel - 1, backgroundColors.length - 1)];
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
                glOrtho(0, width, height, 0, -1, 1);
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
                        glOrtho(0, width, height, 0, -1, 1);
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

        window = glfwCreateWindow(width, height, "Tank Maze Game", MemoryUtil.NULL, MemoryUtil.NULL);
        if (window == MemoryUtil.NULL) throw new RuntimeException("Window creation failed");

        GLFWVidMode vid = glfwGetVideoMode(glfwGetPrimaryMonitor());
        if (vid != null) glfwSetWindowPos(window, (vid.width() - width) / 2, (vid.height() - height) / 2);

        glfwMakeContextCurrent(window);
        GL.createCapabilities();

        new AudioPlayer("audio/music.wav").play();

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
        generator = new MazeGenerator(rows, rows);
        maze = generator.getMaze();
        player = new Player(maze, rows);
        renderer = new GameRenderer(maze, player, fontRenderer);

        bullets.clear();
        enemyBullets = new ArrayList<>();
        lastShootTime = 0;
        enemies = new ArrayList<>();

        float cellSize = 2f / rows;
        float margin = cellSize * 6;

        int levelEnemyCount = (level == 5) ? 12 : enemyCount + level;

        int normalCount = 0, miniCount = 0, sniperCount = 0, tankCount = 0;

        for (int i = 0; i < levelEnemyCount; i++) {
            int er, ec;
            float ex, ey;
            do {
                er = rand.nextInt(rows - 2) + 1;
                ec = rand.nextInt(rows - 2) + 1;
                ex = -1 + ec * cellSize + cellSize / 2f;
                ey = 1 - er * cellSize - cellSize / 2f;
            } while (Math.hypot(ex - player.getX(), ey - player.getY()) < margin || maze[er][ec] == 1);

            Enemy enemy;
            switch (level) {
                case 1:  enemy = new Enemy(ex, ey, maze); break;
                case 2:  enemy = new TankEnemy(ex, ey, maze); break;
                case 3:  enemy = new MiniEnemy(ex, ey, maze); break;
                case 4:  enemy = new SniperEnemy(ex, ey, maze); break;
                default:
                    while (true) {
                        int type = rand.nextInt(4);
                        if (type == 0 && normalCount < 3) { enemy = new Enemy(ex, ey, maze); normalCount++; break; }
                        if (type == 1 && miniCount   < 3) { enemy = new MiniEnemy(ex, ey, maze);  miniCount++;  break; }
                        if (type == 2 && sniperCount < 3) { enemy = new SniperEnemy(ex, ey, maze); sniperCount++; break; }
                        if (type == 3 && tankCount   < 3) { enemy = new TankEnemy(ex, ey, maze);   tankCount++;  break; }
                    }
                    break;
            }
            enemies.add(enemy);
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
        if ((dx != 0 || dy != 0) && now - lastMoveTime >= moveInterval) {
            player.move(dx, dy);
            lastMoveTime = now;
        }

        if (enemies.isEmpty() && player.reachedGoal()) {
            state = (currentLevel < maxLevel) ? GameState.LEVEL_COMPLETE : GameState.WIN;
        }

        if (glfwGetKey(window, GLFW_KEY_N) == GLFW_PRESS) {
            state = (currentLevel < maxLevel) ? GameState.LEVEL_COMPLETE : GameState.WIN;
        }

        if (glfwGetKey(window, GLFW_KEY_F) == GLFW_PRESS && now - lastShootTime >= shootCooldown) {
            bullets.add(player.shoot());
            lastShootTime = now;
        }
    }


    private void handleWin() {
        if (glfwGetKey(window, GLFW_KEY_SPACE) == GLFW_PRESS) {
            if (currentLevel < maxLevel) {
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
