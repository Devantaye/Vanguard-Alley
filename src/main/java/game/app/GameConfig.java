package game.app;

public final class GameConfig {
  private GameConfig() {}

  // ── Window / Maze ─────────────────────────────────────────────── 
  public static final int   WINDOW_WIDTH  = 800;
  public static final int   WINDOW_HEIGHT = 800;
  public static final int   MAZE_ROWS     = 21;   // must be odd 
  public static final float SPAWN_MARGIN_CELLS = 6f;

  // ── Gameplay timing ───────────────────────────────────────────── 
  public static final double MOVE_INTERVAL   = 0.10; // sec between grid steps
  public static final double SHOOT_COOLDOWN  = 0.50; // sec between player shots

  // ── Levels & difficulty ───────────────────────────────────────── 
  public static final int MAX_LEVEL                 = 5;
  public static final int BASE_ENEMY_COUNT          = 2;   // enemies = base + level
  public static final int FINAL_LEVEL_BONUS_COUNT   = 11;  // custom count for final 

  // ── Feature flags ─────────────────────────────────────────────── 
  public static final boolean ENABLE_GESTURES = true;
  public static final boolean SOUND_ON        = true;      

  // ── Theme ─────────────────────────────────────────────────────── 
  public static final float[][] BG_COLORS = {
      {0.10f, 0.10f, 0.10f},
      {0.05f, 0.10f, 0.20f},
      {0.10f, 0.05f, 0.15f},
      {0.10f, 0.05f, 0.02f},
      {0.02f, 0.10f, 0.05f}
  };

  // ── Assets (classpath: src/main/resources) ────────────────────── 
  public static final String MUSIC_PATH      = "audio/music.wav";
  public static final String MENU_MUSIC_PATH = "audio/menu_music.wav"; 
  public static final String CLICK_SFX_PATH  = "audio/click.wav"; 
  public static final String FONT_PRIMARY    = "fonts/roboto.ttf"; 

  // multipliers (1.0f = default bullet speed/size as per Bullet class)
  public static final float PLAYER_BULLET_SPEED = 1.0f;
  public static final float ENEMY_BULLET_SPEED  = 0.85f;

  // ── Enemy: NORMAL (Default) ───────────────────────────────────── 
  public static final int    ENEMY_NORMAL_HEALTH        = 1;
  public static final float  ENEMY_NORMAL_SPEED         = 0.12f;
  public static final float  ENEMY_NORMAL_CHASE_CELLS   = 4f;   // ranges in cells
  public static final float  ENEMY_NORMAL_SHOOT_CELLS   = 3f;
  public static final double ENEMY_NORMAL_FIRE_COOLDOWN = 0.90; // sec
  public static final float  ENEMY_NORMAL_SIZE_SCALE    = 1.00f;

  // ── Enemy: TANK ───────────────────────────────────────────────── 
  public static final int    ENEMY_TANK_HEALTH        = 3;
  public static final float  ENEMY_TANK_SPEED         = 0.05f;
  public static final float  ENEMY_TANK_CHASE_CELLS   = 4.5f;
  public static final float  ENEMY_TANK_SHOOT_CELLS   = 3.5f;
  public static final double ENEMY_TANK_FIRE_COOLDOWN = 1.10;
  public static final float  ENEMY_TANK_SIZE_SCALE    = 1.15f;

  // ── Enemy: MINI ───────────────────────────────────────────────── 
  public static final int    ENEMY_MINI_HEALTH        = 1;
  public static final float  ENEMY_MINI_SPEED         = 0.22f;
  public static final float  ENEMY_MINI_CHASE_CELLS   = 4f;
  public static final float  ENEMY_MINI_SHOOT_CELLS   = 3f;
  public static final double ENEMY_MINI_FIRE_COOLDOWN = 0.70;
  public static final float  ENEMY_MINI_SIZE_SCALE    = 0.70f;

  // ── Enemy: SNIPER ─────────────────────────────────────────────── 
  public static final int    ENEMY_SNIPER_HEALTH        = 2;
  public static final float  ENEMY_SNIPER_SPEED         = 0.10f;
  public static final float  ENEMY_SNIPER_CHASE_CELLS   = 6f;
  public static final float  ENEMY_SNIPER_SHOOT_CELLS   = 10f;
  public static final double ENEMY_SNIPER_FIRE_COOLDOWN = 1.40;
  public static final float  ENEMY_SNIPER_SIZE_SCALE    = 1.00f;

  public static final float SNIPER_BULLET_SPEED = 4.2f;
  public static final float SNIPER_BULLET_SIZE  = 0.6f;

  // Global scan state (unchanged)
    public static float GLOBAL_SCAN_TIMER = 0f;
    public static final float SCAN_INTERVAL = 22f;
    public static int GLOBAL_DIRECTION_INDEX = 0;

  // ── Helpers (kept minimal; not classes) ───────────────────────── 
  public static float cellSize()            { return 2f / MAZE_ROWS; }
  public static float cellsToWorld(float c) { return c * cellSize(); }
}

