package game;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertex2f;

public class Bullet {
    float x;                // current position
    float y;
    private final float dx, dy;  // direction vector
    private final float speed;   // bullet speed
    private final float size;    // size of quad
    private boolean alive = true;

    private final int[][] maze;
    private final int rows;
    private final float cellSize;

    // --- Default bullet constructor (normal enemies, player) ---
    public Bullet(float startX, float startY, Player.Direction dir, int[][] maze, int rows) {
        this(startX, startY, dir, maze, rows, 1.0f); // 1.0f = normal speed multiplier
    }

    // --- New constructor for custom bullet speed ---
    public Bullet(float startX, float startY, Player.Direction dir, int[][] maze, int rows, float speedMultiplier) {
        this.x = startX;
        this.y = startY;
        this.maze = maze;
        this.rows = rows;
        this.cellSize = 2.0f / rows;

        // Apply speed multiplier
        this.speed = (cellSize * 0.1f) * speedMultiplier;
        this.size  = cellSize * 0.1f;

        // Set direction vector
        switch (dir) {
            case UP:    dx =  0f; dy =  1f; break;
            case DOWN:  dx =  0f; dy = -1f; break;
            case LEFT:  dx = -1f; dy =  0f; break;
            case RIGHT: dx =  1f; dy =  0f; break;
            default:    dx = 0f;  dy = 0f;  break;
        }
    }

    // --- New constructor for custom speed & custom size ---
    public Bullet(float startX, float startY, Player.Direction dir, int[][] maze, int rows, float speedMultiplier, float sizeMultiplier) {
        this.x = startX;
        this.y = startY;
        this.maze = maze;
        this.rows = rows;
        this.cellSize = 2.0f / rows;

        // Apply multipliers
        this.speed = (cellSize * 0.1f) * speedMultiplier;
        this.size  = (cellSize * 0.1f) * sizeMultiplier;

        // Set direction vector
        switch (dir) {
            case UP:    dx =  0f; dy =  1f; break;
            case DOWN:  dx =  0f; dy = -1f; break;
            case LEFT:  dx = -1f; dy =  0f; break;
            case RIGHT: dx =  1f; dy =  0f; break;
            default:    dx = 0f;  dy = 0f;  break;
        }
    }


    public boolean isAlive() {
        return alive;
    }

    public void update() {
        if (!alive) return;
        x += dx * speed;
        y += dy * speed;

        // Check collision with walls/bounds
        int col = (int) ((x + 1f) / cellSize);
        int row = (int) ((1f - y) / cellSize);
        if (row < 0 || row >= rows || col < 0 || col >= rows || maze[row][col] == 1) {
            alive = false;
        }
    }

    public void render() {
        if (!alive) return;
        glLoadIdentity();
        glTranslatef(x, y, 0f);
        float s = size;
        glBegin(GL_QUADS);
          glVertex2f(-s, -s);
          glVertex2f( s, -s);
          glVertex2f( s,  s);
          glVertex2f(-s,  s);
        glEnd();
    }
}



