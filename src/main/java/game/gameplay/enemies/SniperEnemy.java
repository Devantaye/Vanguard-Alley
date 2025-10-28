package game.gameplay.enemies;

import java.util.ArrayList;
import java.util.List;

import game.app.GameConfig;
import game.gameplay.Bullet;
import game.gameplay.Player;
import game.gameplay.Player.Direction;

public class SniperEnemy extends Enemy {

    // Behavior (use config for cooldown; keep aim delay here or add a constant)
    private static final float AIM_DELAY = 0.5f;
    private static final float SHOOT_COOLDOWN = (float) GameConfig.ENEMY_SNIPER_FIRE_COOLDOWN;

    private float aimTimer = 0f;
    private boolean isAiming = false;

    private final List<Direction> watchDirections = new ArrayList<>();

    public SniperEnemy(float x, float y, int[][] maze) {
        super(
            x, y, maze,
            GameConfig.ENEMY_SNIPER_HEALTH,
            GameConfig.ENEMY_SNIPER_SPEED, // 0 => stationary, still fine
            GameConfig.cellsToWorld(GameConfig.ENEMY_SNIPER_CHASE_CELLS),
            GameConfig.cellsToWorld(GameConfig.ENEMY_SNIPER_SHOOT_CELLS)
        );
        this.setShootInterval((float) GameConfig.ENEMY_SNIPER_FIRE_COOLDOWN);
        this.setCustomSize(GameConfig.ENEMY_SNIPER_SIZE_SCALE);

        // Detect open corridors around spawn
        int er = (int)((1 - y) / (2f / maze.length));
        int ec = (int)((x + 1) / (2f / maze.length));
        if (isOpen(er - 1, ec, maze)) watchDirections.add(Direction.UP);
        if (isOpen(er + 1, ec, maze)) watchDirections.add(Direction.DOWN);
        if (isOpen(er, ec - 1, maze)) watchDirections.add(Direction.LEFT);
        if (isOpen(er, ec + 1, maze)) watchDirections.add(Direction.RIGHT);
        if (watchDirections.isEmpty()) watchDirections.add(Direction.UP);
        setDirection(watchDirections.get(0));
    }

    private boolean isOpen(int r, int c, int[][] maze) {
        return r >= 0 && c >= 0 && r < maze.length && c < maze[0].length && maze[r][c] == 0;
    }

    @Override
    public void update(float dt, Player player, List<Bullet> enemyBullets) {

        boolean playerInSight = playerInWatchedCorridor(player);

        // ✅ If currently aiming/shooting, LOCK direction & skip global scanning
        if (isAiming || playerInSight) {
            aimTimer -= dt;

            if (!isAiming) {
                isAiming = true;
                aimTimer = AIM_DELAY;
            } else if (aimTimer <= 0f) {
                // 🔹 Fire sniper bullet
                enemyBullets.add(createSniperBullet());
                aimTimer = SHOOT_COOLDOWN;
                isAiming = false;
            }

        } else {
            // ✅ Only rotate if not aiming or shooting
            GameConfig.GLOBAL_SCAN_TIMER += dt;
            if ( GameConfig.GLOBAL_SCAN_TIMER >=  GameConfig.SCAN_INTERVAL) {
                 GameConfig.GLOBAL_SCAN_TIMER = 0f;
                 GameConfig.GLOBAL_DIRECTION_INDEX++;
            }

            if (!watchDirections.isEmpty()) {
                int dirIndex =  GameConfig.GLOBAL_DIRECTION_INDEX % watchDirections.size();
                setDirection(watchDirections.get(dirIndex));
            }

            aimTimer = 0;
        }
    }


    // Corridor detection (only fires if player is in LOS in the current direction)
    private boolean playerInWatchedCorridor(Player player) {
        Direction dir = getDirection();
        int[][] maze = getMaze();
        int rows = getMazeRows();
        float cellSize = 2f / rows;

        // Convert positions to grid
        int er = (int)((1 - getY()) / cellSize);
        int ec = (int)((getX() + 1) / cellSize);
        int pr = (int)((1 - player.getY()) / cellSize);
        int pc = (int)((player.getX() + 1) / cellSize);

        switch (dir) {
            case UP:
                if (pc != ec) return false;
                for (int r = er - 1; r >= 0; r--) {
                    if (maze[r][ec] == 1) return false;
                    if (r == pr) return true;
                }
                break;
            case DOWN:
                if (pc != ec) return false;
                for (int r = er + 1; r < rows; r++) {
                    if (maze[r][ec] == 1) return false;
                    if (r == pr) return true;
                }
                break;
            case LEFT:
                if (pr != er) return false;
                for (int c = ec - 1; c >= 0; c--) {
                    if (maze[er][c] == 1) return false;
                    if (c == pc) return true;
                }
                break;
            case RIGHT:
                if (pr != er) return false;
                for (int c = ec + 1; c < rows; c++) {
                    if (maze[er][c] == 1) return false;
                    if (c == pc) return true;
                }
                break;
        }
        return false;
    }

    // ✅ Custom sniper bullet
    private Bullet createSniperBullet() {
        return new Bullet(getX(), getY(), getDirection(), getMaze(), getMazeRows(), GameConfig.SNIPER_BULLET_SPEED, GameConfig.SNIPER_BULLET_SIZE);
    }
}


