// src/main/java/game/gameplay/enemies/EnemySpawning.java
package game.gameplay.enemies;

public final class EnemySpawning {
  private EnemySpawning() {}

  public static Enemy create(EnemyType kind, float x, float y, int[][] maze) {
    switch (kind) {
      case NORMAL: return new Enemy(x, y, maze);
      case TANK:   return new TankEnemy(x, y, maze);
      case MINI:   return new MiniEnemy(x, y, maze);
      case SNIPER: return new SniperEnemy(x, y, maze);
      default: throw new IllegalArgumentException("Unknown kind: " + kind);
    }
  }
}


