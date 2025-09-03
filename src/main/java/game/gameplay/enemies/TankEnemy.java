package game.gameplay.enemies;

import java.util.List;

import game.app.GameConfig;
import game.gameplay.Bullet;
import game.gameplay.Player;
import game.gameplay.Player.Direction;

public class TankEnemy extends Enemy {

    public TankEnemy(float x, float y, int[][] maze) {
        super(
            x, y, maze,
            GameConfig.ENEMY_TANK_HEALTH,
            GameConfig.ENEMY_TANK_SPEED,
            GameConfig.cellsToWorld(GameConfig.ENEMY_TANK_CHASE_CELLS),
            GameConfig.cellsToWorld(GameConfig.ENEMY_TANK_SHOOT_CELLS)
        );
        this.setShootInterval((float) GameConfig.ENEMY_TANK_FIRE_COOLDOWN);
        this.setCustomSize(GameConfig.ENEMY_TANK_SIZE_SCALE);
    }

    @Override
    protected void shoot(float dt, Player player, List<Bullet> outBullets) {
        shootCooldown -= dt;
        if (shootCooldown <= 0f) {
            shootCooldown = getShootInterval();

            Direction shootDir = getDirectionToPlayer(player);

            // ✅ Fires a big, slow shell (0.5x speed, 2x size)
            outBullets.add(new Bullet(getX(), getY(), shootDir, getMaze(), getMazeRows(), 0.5f, 2.0f));
        }
    }

    private Direction getDirectionToPlayer(Player player) {
        var diff = new org.joml.Vector2f(player.getPosition()).sub(getPosition()).normalize();
        return Math.abs(diff.x) > Math.abs(diff.y)
                ? (diff.x > 0 ? Player.Direction.RIGHT : Player.Direction.LEFT)
                : (diff.y > 0 ? Player.Direction.UP    : Player.Direction.DOWN);
    }
}
