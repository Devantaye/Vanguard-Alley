package game.tutorial;

import game.app.GameState;
import game.gameplay.Player;
import game.gameplay.enemies.Enemy;
import game.input.GestureManager;
import game.render.GameRenderer;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

/**
 * TutorialDemo
 * Flow:
 * 1) UP    – short vertical corridor (center column), start at bottom -> goal band at top
 * 2) DOWN  – same vertical, goal band at bottom
 * 3) LEFT  – teleport to grid center; short horizontal corridor; goal band at LEFT end
 * 4) RIGHT – same horizontal; goal band at RIGHT end
 * 5) SHOOT – spawn enemy at LEFT end; (no goal yet)
 * 6) EXIT  – exit band spawns exactly where the enemy was; reach it to finish
 *
 * Supports gestures AND WASD for testing. SPACE = skip.
 * Draws the goal/exit band BEFORE the scene so it renders under the player.
 *
 * Exposes a dynamic respawn checkpoint (grid coords) via getRespawnGrid()
 * so the host game can teleport the player to the correct, current location.
 */
public class TutorialDemo {

    // ───────────────────────────── Callbacks ─────────────────────────────
    public interface Callbacks {
        Enemy spawnEnemyAt(float worldX, float worldY);
        void enableShooting(boolean enabled);
        void onTutorialComplete();

        /** Optional: configure the just-spawned enemy (freeze, face RIGHT, slow fire, etc.). */
        default void configureSpawnedEnemy(Enemy e) { /* no-op by default */ }
    }

    // ───────────────────────────── Fields ─────────────────────────────
    private final int N;
    private final int[][] maze;
    private final float cellSize;
    private final float halfCell;

    private final Player player;
    private final GestureManager gm;
    private final Callbacks cb;
    private final GameRenderer renderer;

    // corridor thickness stays the same; we shorten corridor length with these:
    private final int corridorHalf = 1;          // visual thickness = 2*half + 1 cells
    private final int vHalfLen;                  // vertical half-length (rows from center)
    private final int hHalfLen;                  // horizontal half-length (cols from center)

    private enum Step { UP, DOWN, LEFT, RIGHT, SHOOT, EXIT, DONE }
    private Step step = Step.UP;

    private final long window;                   // for WASD/SPACE checks

    // Remember center row/col and horizontal ends
    private int midC, midR;
    private int hLeftC, hRightC;

    // Enemy state (to place EXIT exactly there)
    private boolean enemyAlive = false;
    private Enemy spawnedEnemy = null;
    private int enemyGridC = -1, enemyGridR = -1;

    // Goal/Exit as a BAND (rectangle), not a single cell
    private int goalC0, goalC1, goalR0, goalR1;

    // Dynamic respawn checkpoint (grid coords) that follows current tutorial layout
    private int respawnC, respawnR;

    private final List<Enemy> tempEnemies = new ArrayList<>(1);

    // ───────────────────────────── Ctor ─────────────────────────────
    public TutorialDemo(long window,
                        int gridSizeOdd,
                        int[][] mazeRef,
                        Player player,
                        GestureManager gestureManager,
                        Callbacks callbacks,
                        GameRenderer rendererUsingSameMaze) {
        this.window = window;
        this.N = gridSizeOdd;
        this.maze = mazeRef;
        this.player = player;
        this.gm = gestureManager;
        this.cb = callbacks;
        this.renderer = rendererUsingSameMaze;

        this.cellSize = 2.0f / N;
        this.halfCell = cellSize / 2f;

        // Shorter corridors: default ≈ 1/4 of grid each side (min 3)
        this.vHalfLen = Math.max(3, N / 4);
        this.hHalfLen = Math.max(3, N / 4);

        this.midC = N / 2;
        this.midR = N / 2;

        rebuildForCurrentStep(false);
    }

    // ───────────────────────────── Update ─────────────────────────────
    public void update(float dt) {
        // Skip tutorial entirely
        if (glfwGetKey(window, GLFW_KEY_SPACE) == GLFW_PRESS) {
            step = Step.DONE;
            cb.onTutorialComplete();
            return;
        }

        boolean keyLeft  = glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS;
        boolean keyRight = glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS;
        boolean keyUp    = glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS;
        boolean keyDown  = glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS;

        boolean gesLeft  = gm != null && gm.isLeft();
        boolean gesRight = gm != null && gm.isRight();
        boolean gesUp    = gm != null && gm.isUp();
        boolean gesDown  = gm != null && gm.isDown();

        switch (step) {
            case UP:
                if ((keyUp || gesUp) && playerInGoalBand()) advance();
                break;
            case DOWN:
                if ((keyDown || gesDown) && playerInGoalBand()) advance();
                break;
            case LEFT:
                if ((keyLeft || gesLeft) && playerInGoalBand()) advance();
                break;
            case RIGHT:
                if ((keyRight || gesRight) && playerInGoalBand()) advance();
                break;
            case SHOOT:
                // wait for notifyEnemyKilled() from game loop
                break;
            case EXIT:
                if (playerInGoalBand()) {
                    step = Step.DONE;
                    cb.onTutorialComplete();
                }
                break;
            case DONE:
                break;
        }
    }

    // ───────────────────────────── Render ─────────────────────────────
    public void render(GameState state, int level) {
        // 1) Draw goal/exit band first (UNDER player) — but not during SHOOT
        if (step == Step.UP || step == Step.DOWN || step == Step.LEFT || step == Step.RIGHT || step == Step.EXIT) {
            drawGoalBand();
        }

        // 2) Then render the scene (walls, player, tutorial enemy if alive)
        tempEnemies.clear();
        if (enemyAlive && spawnedEnemy != null) tempEnemies.add(spawnedEnemy);
        renderer.render(state, false, tempEnemies, level);
    }

    // ───────────────────────────── Notifications ─────────────────────────────
    /** Call this from the main game when the tutorial enemy is killed. */
    public void notifyEnemyKilled() {
        if (step == Step.SHOOT && enemyAlive) {
            enemyAlive = false;
            spawnedEnemy = null;

            // EXIT band exactly where the enemy stood (span the corridor thickness)
            setGoalBand(enemyGridC, enemyGridC, enemyGridR - corridorHalf, enemyGridR + corridorHalf);
            step = Step.EXIT;
        }
    }

    // ───────────────────────────── Progression ─────────────────────────────
    private void advance() {
        switch (step) {
            case UP:
                step = Step.DOWN;
                rebuildForCurrentStep(true);
                break;
            case DOWN:
                step = Step.LEFT;
                rebuildForCurrentStep(false); // teleport to center for horizontal segment
                break;
            case LEFT:
                step = Step.RIGHT;
                rebuildForCurrentStep(true);
                break;
            case RIGHT:
                step = Step.SHOOT;
                rebuildForCurrentStep(true);
                break;
            default:
                break;
        }
    }

    // ───────────────────────── Maze Building ─────────────────────────
    private void rebuildForCurrentStep(boolean preservePlayerPos) {
        fillWalls();
        midC = N / 2;
        midR = N / 2;

        switch (step) {
            case UP: {
                int topR = clamp(1, midR - vHalfLen, N - 2);
                int botR = clamp(1, midR + vHalfLen, N - 2);
                carveVertical(midC, topR, botR, corridorHalf);
                if (!preservePlayerPos) setPlayerGrid(midC, botR); // bottom of the segment
                // goal band spans the full corridor width at the TOP end
                setGoalBand(midC - corridorHalf, midC + corridorHalf, topR, topR);

                // respawn at the bottom of the short vertical
                setRespawn(midC, botR);

                cb.enableShooting(false);
                clearEnemyState();
            } break;

            case DOWN: {
                int topR = clamp(1, midR - vHalfLen, N - 2);
                int botR = clamp(1, midR + vHalfLen, N - 2);
                carveVertical(midC, topR, botR, corridorHalf);
                // goal band spans the full corridor width at the BOTTOM end
                setGoalBand(midC - corridorHalf, midC + corridorHalf, botR, botR);

                // respawn now at the TOP end
                setRespawn(midC, topR);

                cb.enableShooting(false);
                clearEnemyState();
            } break;

            case LEFT: {
                // center on the grid for the horizontal sequence
                if (!preservePlayerPos) setPlayerGrid(midC, midR);

                hLeftC  = clamp(1, midC - hHalfLen, N - 2);
                hRightC = clamp(1, midC + hHalfLen, N - 2);

                carveBubble(midC, midR, corridorHalf);
                carveHorizontal(hLeftC, hRightC, midR, corridorHalf);

                // goal band spans the full corridor height at the LEFT end
                setGoalBand(hLeftC, hLeftC, midR - corridorHalf, midR + corridorHalf);

                // during horizontal, respawn from RIGHT end (start point)
                setRespawn(hRightC, midR);

                cb.enableShooting(false);
                clearEnemyState();
            } break;

            case RIGHT: {
                // keep/rebuild same centered short horizontal corridor
                if (hLeftC == 0 && hRightC == 0) {
                    hLeftC  = clamp(1, midC - hHalfLen, N - 2);
                    hRightC = clamp(1, midC + hHalfLen, N - 2);
                }
                carveBubble(midC, midR, corridorHalf);
                carveHorizontal(hLeftC, hRightC, midR, corridorHalf);

                // goal band spans the full corridor height at the RIGHT end
                setGoalBand(hRightC, hRightC, midR - corridorHalf, midR + corridorHalf);

                // checkpoint remains RIGHT end
                setRespawn(hRightC, midR);

                cb.enableShooting(false);
                clearEnemyState();
            } break;

            case SHOOT: {
                // Keep the same centered SHORT horizontal corridor
                carveBubble(midC, midR, corridorHalf);
                carveHorizontal(hLeftC, hRightC, midR, corridorHalf);

                // Enemy spawns at LEFT end (player should be near right end)
                float ex = gridCToWorldX(hLeftC);
                float ey = gridRToWorldY(midR);
                spawnedEnemy = cb.spawnEnemyAt(ex, ey);
                enemyAlive = (spawnedEnemy != null);
                enemyGridC = hLeftC; enemyGridR = midR;

                // Let host configure facing/freeze/fire rate
                if (spawnedEnemy != null) cb.configureSpawnedEnemy(spawnedEnemy);

                // No goal marker in SHOOT; respawn stays RIGHT end during the fight
                setRespawn(hRightC, midR);

                cb.enableShooting(true);
            } break;

            case EXIT:
            case DONE:
                // nothing to carve
                break;
        }
    }

    private void clearEnemyState() {
        enemyAlive = false;
        spawnedEnemy = null;
        enemyGridC = enemyGridR = -1;
    }

    private void fillWalls() {
        for (int r = 0; r < N; r++)
            for (int c = 0; c < N; c++)
                maze[r][c] = 1;
    }

    private void carveVertical(int c, int r0, int r1, int half) {
        int rr0 = Math.min(r0, r1), rr1 = Math.max(r0, r1);
        int cc0 = Math.max(1, c - half), cc1 = Math.min(N - 2, c + half);
        rr0 = Math.max(1, rr0); rr1 = Math.min(N - 2, rr1);
        for (int r = rr0; r <= rr1; r++)
            for (int cc = cc0; cc <= cc1; cc++)
                maze[r][cc] = 0;
    }

    private void carveHorizontal(int c0, int c1, int r, int half) {
        int cc0 = Math.min(c0, c1), cc1 = Math.max(c0, c1);
        int rr0 = Math.max(1, r - half), rr1 = Math.min(N - 2, r + half);
        cc0 = Math.max(1, cc0); cc1 = Math.min(N - 2, cc1);
        for (int c = cc0; c <= cc1; c++)
            for (int rr = rr0; rr <= rr1; rr++)
                maze[rr][c] = 0;
    }

    private void carveBubble(int c, int r, int half) {
        int cc0 = Math.max(1, c - half), cc1 = Math.min(N - 2, c + half);
        int rr0 = Math.max(1, r - half), rr1 = Math.min(N - 2, r + half);
        for (int cc = cc0; cc <= cc1; cc++)
            for (int rr = rr0; rr <= rr1; rr++)
                maze[rr][cc] = 0;
    }

    // ───────────────────── Goal/Exit Band ─────────────────────
    private void setGoalBand(int c0, int c1, int r0, int r1) {
        goalC0 = clamp(1, Math.min(c0, c1), N - 2);
        goalC1 = clamp(1, Math.max(c0, c1), N - 2);
        goalR0 = clamp(1, Math.min(r0, r1), N - 2);
        goalR1 = clamp(1, Math.max(r0, r1), N - 2);
    }

    private void drawGoalBand() {
        float x0 = gridCToWorldX(goalC0) - halfCell;
        float x1 = gridCToWorldX(goalC1) + halfCell;
        float yTop = gridRToWorldY(goalR0) + halfCell; // smaller row index => higher y
        float yBot = gridRToWorldY(goalR1) - halfCell;

        glColor3f(0f, 1f, 1f);
        glLoadIdentity();
        glBegin(GL_QUADS);
            glVertex2f(x0, yBot);
            glVertex2f(x1, yBot);
            glVertex2f(x1, yTop);
            glVertex2f(x0, yTop);
        glEnd();
    }

    private boolean playerInGoalBand() {
        float px = player.getX(), py = player.getY();
        float x0 = gridCToWorldX(goalC0) - halfCell;
        float x1 = gridCToWorldX(goalC1) + halfCell;
        float yTop = gridRToWorldY(goalR0) + halfCell;
        float yBot = gridRToWorldY(goalR1) - halfCell;
        return (px >= x0 && px <= x1 && py >= yBot && py <= yTop);
    }

    // ───────────────────── Respawn Checkpoint ─────────────────────
    private void setRespawn(int c, int r) {
        respawnC = clamp(1, c, N - 2);
        respawnR = clamp(1, r, N - 2);
    }

    /** Return the current respawn checkpoint in GRID coords {c, r}. */
    public int[] getRespawnGrid() { return new int[]{respawnC, respawnR}; }

    // ───────────────────── Utilities ─────────────────────
    private void setPlayerGrid(int c, int r) { player.setPositionGrid(c, r, cellSize); }

    private float gridCToWorldX(int c) { return -1f + c * cellSize + halfCell; }
    private float gridRToWorldY(int r) { return  1f - r * cellSize - halfCell; }

    private static int clamp(int lo, int v, int hi) { return Math.max(lo, Math.min(hi, v)); }

    /** Safe respawn in WORLD coords (already mapped from grid). */
public float[] getRespawnWorld() {
    return new float[] { gridCToWorldX(respawnC), gridRToWorldY(respawnR) };
}

    

}


