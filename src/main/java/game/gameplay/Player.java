package game.gameplay;

import org.joml.Vector2f;

import game.app.GameConfig;

public class Player {
    private final int[][] maze;
    private final int rows;
    private final float cellSize;
    private final float size;
    private final float speed;

    private float x, y;
    private Direction dir = Direction.RIGHT;

    public enum Direction { UP, DOWN, LEFT, RIGHT }

    public Player(int[][] maze, int rows) {
        this.maze     = maze;
        this.rows     = rows;
        this.cellSize = 2.0f / rows;

        // pick these to taste — here tank is 0.3× cell, speed same
        this.size  = cellSize * 0.2f;
        this.speed = cellSize * 0.08f;

        // spawn in top-left corner of logical cell (1,1)
        this.x = -1 + 1 * cellSize + cellSize/2f;
        this.y =  1 - 1 * cellSize - cellSize/2f;
    }

    public void move(int dx, int dy) {
        // update facing
        if (dx < 0) dir = Direction.LEFT;
        if (dx > 0) dir = Direction.RIGHT;
        if (dy < 0) dir = Direction.DOWN;
        if (dy > 0) dir = Direction.UP;

        float nx = x + dx * speed;
        float ny = y + dy * speed;
        if (!isColliding(nx, ny)) {
            x = nx; y = ny;
        }
    }

    private boolean isColliding(float px, float py) {
        float half = cellSize / 2f;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < rows; c++) {
                if (maze[r][c] == 1) {
                    float cx = -1 + c * cellSize + half;
                    float cy =  1 - r * cellSize - half;
                    if (Math.abs(px - cx) < half + size &&
                        Math.abs(py - cy) < half + size) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean reachedGoal() {
    // Compute the center of the goal cell at (rows-2, rows-2)
    int goalCell = rows - 2;
    float cx = -1 + goalCell * cellSize + cellSize/2f;
    float cy =  1 - goalCell * cellSize - cellSize/2f;
    float half = cellSize / 2f;  // half-width of the goal cell

    // Player bounding box
    float pMinX = x - size, pMaxX = x + size;
    float pMinY = y - size, pMaxY = y + size;

    // Goal bounding box
    float gMinX = cx - half,  gMaxX = cx + half;
    float gMinY = cy - half,  gMaxY = cy + half;

    // Return true if the two boxes overlap at all
    return pMaxX >= gMinX && pMinX <= gMaxX
        && pMaxY >= gMinY && pMinY <= gMaxY;
}
    public boolean isOverCell(int cellR, int cellC) {
    // get logical center of that cell
    float cx = -1 + cellC * cellSize + cellSize/2f;
    float cy =  1 - cellR * cellSize - cellSize/2f;
    // bounding‐box overlap test
    return Math.abs(x - cx) < size + cellSize/2f
        && Math.abs(y - cy) < size + cellSize/2f;
}

public Bullet shoot() {
        return new Bullet(x, y, dir, maze, rows, GameConfig.PLAYER_BULLET_SPEED);
    }

/** Returns the player’s current world‐space position. */
    public Vector2f getPosition() {
        return new Vector2f(x, y);
    }

    public float getX()               { return x;      }
    public float getY()               { return y;      }
    public float getSize()            { return size;   }
    public Direction getDirection()   { return dir;    }
}



