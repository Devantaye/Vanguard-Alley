package game.gameplay.enemies;

import game.app.GameConfig;

public class MiniEnemy extends Enemy {
    public MiniEnemy(float x, float y, int[][] maze) {
        super(
            x, y, maze,
            GameConfig.ENEMY_MINI_HEALTH,
            GameConfig.ENEMY_MINI_SPEED,
            GameConfig.cellsToWorld(GameConfig.ENEMY_MINI_CHASE_CELLS),
            GameConfig.cellsToWorld(GameConfig.ENEMY_MINI_SHOOT_CELLS)
        );
        this.setShootInterval((float) GameConfig.ENEMY_MINI_FIRE_COOLDOWN);
        this.setCustomSize(GameConfig.ENEMY_MINI_SIZE_SCALE);
    }
}

