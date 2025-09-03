package game.gameplay;

import java.util.Collections;
import java.util.List;

import org.joml.Vector2f;

import game.gameplay.Player.Direction;

public class Enemy {
    public enum State { ROAMING, CHASING, SHOOTING }

    // ── AI state & timing ─────────────────────────────────────────────────
    protected float shootCooldown    = 0f;
    private   float shootInterval    = 1f;

    // ── Stats & movement ──────────────────────────────────────────────────
    private int   health     = 1;     //only adjusts for default enemies (Level 1)
    private float speed      = 0.22f;  //only adjusts for default enemies (Level 1)
    protected int maxHealth;     
    private float chaseRange;   
    private float shootRange;   

    // ── Facing direction ───────────────────────────────────────────────────
    private Direction direction = Direction.DOWN;
    public Direction getDirection() { return direction; }

    // ── Position & collision ──────────────────────────────────────────────
    private final Vector2f position;
    private final int[][]  maze;
    private final int      rows;
    private final float    cellSize;
    private float          size;

    // ── Path-following fields ──────────────────────────────────────────────
    private List<int[]> path        = Collections.emptyList();
    private int         pathIndex   = 0;
    private float       recalcTimer = 0f;
    private static final float RECALC_INTERVAL = 0.5f;

    public Enemy(float startX, float startY, int[][] maze, int health, float speed, float chaseRange, float shootRange) {
        this.position = new Vector2f(startX, startY);
        this.maze     = maze;
        this.rows     = maze.length;
        this.cellSize = 2f / rows;
        this.size     = cellSize * 0.2f;

        this.health     = health;
        this.maxHealth  = health;
        this.speed      = speed;
        this.chaseRange = chaseRange;
        this.shootRange = shootRange;
    }

    public Enemy(float startX, float startY, int[][] maze) {
    this.position = new Vector2f(startX, startY);
    this.maze     = maze;
    this.rows     = maze.length;
    this.cellSize = 2f / rows;
    this.size     = cellSize * 0.2f;
    this.maxHealth = this.health;
    this.chaseRange = 4f * cellSize;
    this.shootRange = 3f * cellSize;
}

    /** Actively chase the player at all times; shoot when in range. */
    public void update(float dt, Player player, List<Bullet> outBullets) {
        // 1) Recompute shortest path to the player on a cadence
        recalcTimer -= dt;
        if (recalcTimer <= 0f) {
            recalcTimer = RECALC_INTERVAL;
            int er = (int)((1 - position.y) / cellSize);
            int ec = (int)((position.x + 1) / cellSize);
            Vector2f pp = player.getPosition();
            int pr = (int)((1 - pp.y) / cellSize);
            int pc = (int)((pp.x + 1) / cellSize);

            path = PathFinder.findPath(maze, er, ec, pr, pc);
            pathIndex = 1; // skip current cell
        }

        // 2) Distance to player in cells (via path length)
        int distCells = path.size() > 1 ? (path.size() - 1) : Integer.MAX_VALUE;

        // 3) Always move toward the player
        followPath(dt);

        // 4) Shoot if in range (keep moving while shooting)
        if (distCells * cellSize <= shootRange) {
            shoot(dt, player, outBullets);
        }
    }

    /** Moves along the A* path, one cell at a time, and sets facing. */
    protected void followPath(float dt) {
        if (pathIndex >= path.size()) return;

        int[] cell = path.get(pathIndex);
        float tx = -1 + cell[1] * cellSize + cellSize/2f;
        float ty =  1 - cell[0] * cellSize - cellSize/2f;

        Vector2f toTarget = new Vector2f(tx, ty).sub(position);
        if (toTarget.length() < cellSize * 0.1f) {
            pathIndex++;
        } else {
            toTarget.normalize();
            // set facing based on movement vector
            if (Math.abs(toTarget.x) > Math.abs(toTarget.y)) {
                direction = toTarget.x > 0 ? Direction.RIGHT : Direction.LEFT;
            } else {
                direction = toTarget.y > 0 ? Direction.UP : Direction.DOWN;
            }
            tryMove(toTarget.x * speed * dt, toTarget.y * speed * dt);
        }
    }

    protected void shoot(float dt, Player player, List<Bullet> outBullets) {
        shootCooldown -= dt;
        if (shootCooldown <= 0f) {
            shootCooldown = shootInterval;
            Vector2f diff = new Vector2f(player.getPosition()).sub(position).normalize();
            Player.Direction shootDir = 
                  Math.abs(diff.x) > Math.abs(diff.y)
                ? (diff.x > 0 ? Direction.RIGHT : Direction.LEFT)
                : (diff.y > 0 ? Direction.UP    : Direction.DOWN);

            outBullets.add(new Bullet(
                position.x, position.y, shootDir, maze, rows
            ));
        }
    }

    /** Stepped move with corner-aware wall collision. */
    private void tryMove(float dx, float dy) {
        float dist    = (float)Math.hypot(dx, dy);
        float maxStep = cellSize * 0.5f;
        int   steps   = (int)Math.ceil(dist / maxStep);
        float stepX   = dx / steps;
        float stepY   = dy / steps;

        for (int i = 0; i < steps; i++) {
            if (!collides(position.x + stepX, position.y)) {
                position.x += stepX;
            }
            if (!collides(position.x, position.y + stepY)) {
                position.y += stepY;
            }
        }
    }

    /** True if any corner of the tank overlaps a wall cell. */
    private boolean collides(float cx, float cy) {
        float r = size;
        return cellIsWall(cx - r, cy - r)
            || cellIsWall(cx - r, cy + r)
            || cellIsWall(cx + r, cy - r)
            || cellIsWall(cx + r, cy + r);
    }

    private boolean cellIsWall(float wx, float wy) {
        int gx = (int)((wx + 1f) / cellSize);
        int gy = (int)((1f - wy) / cellSize);
        if (gx < 0 || gy < 0 || gx >= rows || gy >= rows) return true;
        return maze[gy][gx] == 1;
    }

    /** Called when hit by a bullet; returns true if dead. */
    public boolean hit() {
        health = Math.max(0, health - 1);
        return health == 0;
    }


    // ── Getters for rendering and AI ───────────────────────────────────────
    public float      getX()             { return position.x; }
    public float      getY()             { return position.y; }
    public float      getSize()          { return size; }
    public int        getHealth()        { return health; }
    public Vector2f   getPosition()      { return new Vector2f(position); }
    protected int[][] getMaze()          { return maze;}
    protected int     getMazeRows()      { return rows;}
    public float      getShootInterval() { return shootInterval; }
    protected float   getCellSize()      { return cellSize;}
    protected float   getChaseRange()    { return chaseRange;}
    protected float   getShootRange()    { return shootRange;}
    public int        getMaxHealth()     { return maxHealth;}


    protected int     getDistanceToPlayerInCells(Player player) {
        int er = (int) ((1 - position.y) / cellSize);
        int ec = (int) ((position.x + 1) / cellSize);
        Vector2f pp = player.getPosition();
        int pr = (int) ((1 - pp.y) / cellSize);
        int pc = (int) ((pp.x + 1) / cellSize);

        List<int[]> pathToPlayer = PathFinder.findPath(maze, er, ec, pr, pc);
        return (pathToPlayer.size() > 1) ? pathToPlayer.size() - 1 : Integer.MAX_VALUE;
    }

    // ── Setters for rendering and AI ───────────────────────────────────────
    public float getSpeed()        { return speed; }
    public void  setSpeed(float s) { this.speed = s; }
    public void  setHealth(int h)  { this.health = this.maxHealth = h; }
    public void setShootInterval(float interval) {
        this.shootInterval = interval;
    }

    public void setCustomSize(float scale) {
        this.size = (2f / rows) * 0.2f * scale; // default size scaled
    }

    protected void setDirection(Direction dir) {
        this.direction = dir;
    }

}


