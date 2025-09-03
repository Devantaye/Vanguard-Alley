package game.gameplay;

import java.util.List;

public class TankEnemy extends Enemy {

    public TankEnemy(float x, float y, int[][] maze) {
        super(
            x, y, maze,
            3,                    // health: tanky
            0.2f,                  // speed: slow
            5f * (2f / maze.length),     // chase range
            4f * (2f / maze.length)      // shoot range
        );
        this.setCustomSize(1.5f);
    }

    @Override
    protected void shoot(float dt, Player player, List<Bullet> outBullets) {
        shootCooldown -= dt;
        if (shootCooldown <= 0f) {
            shootCooldown = getShootInterval();

            Player.Direction shootDir = getDirectionToPlayer(player);

            // ✅ Fires a big, slow shell (0.5x speed, 2x size)
            outBullets.add(new Bullet(getX(), getY(), shootDir, getMaze(), getMazeRows(), 0.5f, 2.0f));
        }
    }

    // ✅ Helper: determines facing direction toward player
    private Player.Direction getDirectionToPlayer(Player player) {
        var diff = new org.joml.Vector2f(player.getPosition()).sub(getPosition()).normalize();
        return Math.abs(diff.x) > Math.abs(diff.y)
                ? (diff.x > 0 ? Player.Direction.RIGHT : Player.Direction.LEFT)
                : (diff.y > 0 ? Player.Direction.UP    : Player.Direction.DOWN);
    }
}
