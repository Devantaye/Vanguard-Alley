package game.render;

import java.util.List;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertex2f;

import game.app.GameState;
import game.gameplay.Player;
import game.gameplay.Player.Direction;
import game.gameplay.enemies.Enemy;
import game.gameplay.enemies.MiniEnemy;
import game.gameplay.enemies.SniperEnemy;
import game.gameplay.enemies.TankEnemy;

public class GameRenderer {

    private final int[][] maze;
    private final Player player;
    public final float cellSize;
    public final float halfCell;

    public GameRenderer(int[][] maze, Player player, FontRenderer fontRenderer) {
        this.maze = maze;
        this.player = player;
        this.cellSize = 2.0f / maze.length;
        this.halfCell = cellSize / 2f;
    }

    // ──────────────────────────────────────────────
    // Level-based color palettes
    // ──────────────────────────────────────────────

    // Wall colors 
    private final float[][] wallColors = {
        {0.4f, 0.4f, 0.4f},   // Level 1: Gray
        {0.25f, 0.45f, 0.7f}, // Level 2: Steel Blue
        {0.45f, 0.3f, 0.15f}, // Level 3: Dark Brown
        {0.35f, 0.25f, 0.5f}, // Level 4: Deep Purple
        {0.15f, 0.5f, 0.2f}   // Level 5: Forest Green
    };

    // Enemy colors
    private final float[] miniColor   = {0.2f, 1.0f, 1.0f}; // cyan
    private final float[] sniperColor = {0.6f, 0.2f, 1.0f}; // purple
    private final float[] heavyColor  = {0.3f, 0.3f, 0.3f}; // dark gray
    private final float[] normalColor = {1.0f, 0.3f, 0.0f}; // orange-red (default)


    // ──────────────────────────────────────────────
    // Render everything
    // ──────────────────────────────────────────────
    public void render(GameState state, boolean enemiesDefeated, List<Enemy> enemies, int level) {

        // Select colors for this level
        float[] wallColor = wallColors[Math.min(level - 1, wallColors.length - 1)];

        // 1) Draw static maze walls
        glColor3f(wallColor[0], wallColor[1], wallColor[2]);
        for (int r = 0; r < maze.length; r++) {
            for (int c = 0; c < maze[r].length; c++) {
                if (maze[r][c] == 1) {
                    float cx = -1 + c * cellSize + halfCell;
                    float cy = 1 - r * cellSize - halfCell;
                    glLoadIdentity();
                    glTranslatef(cx, cy, 0f);
                    glBegin(GL_QUADS);
                        glVertex2f(-halfCell, -halfCell);
                        glVertex2f( halfCell, -halfCell);
                        glVertex2f( halfCell,  halfCell);
                        glVertex2f(-halfCell,  halfCell);
                    glEnd();
                }
            }
        }

        // 2) Draw exit only if all enemies are defeated
        if (enemiesDefeated) {
            int N = maze.length;
            float gx = -1 + (N - 2) * cellSize + halfCell;
            float gy = 1 - (N - 2) * cellSize - halfCell;
            glColor3f(0f, 1f, 1f); // Cyan for unlocked exit
            glLoadIdentity();
            glTranslatef(gx, gy, 0f);
            glBegin(GL_QUADS);
                glVertex2f(-halfCell, -halfCell);
                glVertex2f( halfCell, -halfCell);
                glVertex2f( halfCell,  halfCell);
                glVertex2f(-halfCell,  halfCell);
            glEnd();
        }

        // 3) Draw player (bright blue body + lighter nozzle)
        float s = player.getSize();
        glColor3f(1f, 1f, 1f); // player body
        glLoadIdentity();
        glTranslatef(player.getX(), player.getY(), 0f);
        glBegin(GL_QUADS);
            glVertex2f(-s, -s);
            glVertex2f( s, -s);
            glVertex2f( s,  s);
            glVertex2f(-s,  s);
        glEnd();

        float th = s * 0.6f, nd = s + th, tx = 0f, ty = 0f;
        switch (player.getDirection()) {
            case UP:    ty = nd;  break;
            case DOWN:  ty = -nd; break;
            case LEFT:  tx = -nd; break;
            case RIGHT: tx = nd;  break;
        }
       glColor3f(1f, 1f, 1f); 
        glBegin(GL_QUADS);
            glVertex2f(tx - th, ty - th);
            glVertex2f(tx + th, ty - th);
            glVertex2f(tx + th, ty + th);
            glVertex2f(tx - th, ty + th);
        glEnd();

        // 4) Draw enemies (with distinct colors, scaling, and proper nozzle alignment)
for (Enemy e : enemies) {
    float s1 = e.getSize();

    // ✅ Slight scaling tweak for MiniEnemy
    if (e instanceof MiniEnemy) {
    s1 *= 1.2f;
} else if (e instanceof SniperEnemy) {
    s1 *= 1.1f;  // 🔥 make sniper tanks 15% bigger
}

   // ✅ Color selection by enemy type
float[] color;
if (e instanceof TankEnemy) {
    // Darken the base heavyColor based on remaining health
    float[] base = heavyColor;
    float healthRatio = (float) e.getHealth() / e.getMaxHealth();

    // Darkening factor: 1.0 at full health → 0.3 at low health
    float factor = 0.3f + 0.7f * healthRatio;

    color = new float[]{
        base[0] * factor,
        base[1] * factor,
        base[2] * factor
    };
} else if (e instanceof MiniEnemy) {
    color = miniColor;
} else if (e instanceof SniperEnemy) {
    color = sniperColor;
} else {
    color = normalColor;
}



    glLoadIdentity();
    glTranslatef(e.getX(), e.getY(), 0f);
    glColor3f(color[0], color[1], color[2]);

    // ====== BODY RENDERING ======
    float bodyHalfWidth;
    float bodyHalfHeight;

    if (e instanceof SniperEnemy) {
        // Sniper: slim body, rotated depending on direction
        if (e.getDirection() == Direction.LEFT || e.getDirection() == Direction.RIGHT) {
            bodyHalfWidth = s1;
            bodyHalfHeight = s1 * 0.5f; // thinner height when horizontal
        } else {
            bodyHalfWidth = s1 * 0.5f; // thinner width when vertical
            bodyHalfHeight = s1;
        }
    } else {
        // Default: square body
        bodyHalfWidth = s1;
        bodyHalfHeight = s1;
    }

    glBegin(GL_QUADS);
        glVertex2f(-bodyHalfWidth, -bodyHalfHeight);
        glVertex2f( bodyHalfWidth, -bodyHalfHeight);
        glVertex2f( bodyHalfWidth,  bodyHalfHeight);
        glVertex2f(-bodyHalfWidth,  bodyHalfHeight);
    glEnd();

    // ====== NOZZLE RENDERING (properly aligned & scaled) ======
    float nozzleThickness;
    float nozzleExtra;

    if (e instanceof SniperEnemy) {
        nozzleThickness = s1 * 0.25f; // thin barrel
        nozzleExtra     = s1 * 0.25f;  // slightly shorter
    } else if (e instanceof TankEnemy) {
        nozzleThickness = s1 * 0.5f;  // thick barrel for big tanks
        nozzleExtra     = s1 * 0.5f;
    } else {
        nozzleThickness = s1 * 0.4f;
        nozzleExtra     = s1 * 0.4f;
    }

    float tx1 = 0f, ty1 = 0f;
    switch (e.getDirection()) {
        case UP:    ty1 = bodyHalfHeight + nozzleExtra; break;
        case DOWN:  ty1 = -(bodyHalfHeight + nozzleExtra); break;
        case LEFT:  tx1 = -(bodyHalfWidth + nozzleExtra); break;
        case RIGHT: tx1 = bodyHalfWidth + nozzleExtra; break;
    }

    glColor3f(color[0], color[1], color[2]);
    glBegin(GL_QUADS);
        glVertex2f(tx1 - nozzleThickness, ty1 - nozzleThickness);
        glVertex2f(tx1 + nozzleThickness, ty1 - nozzleThickness);
        glVertex2f(tx1 + nozzleThickness, ty1 + nozzleThickness);
        glVertex2f(tx1 - nozzleThickness, ty1 + nozzleThickness);
    glEnd();
}


}
}

    

















