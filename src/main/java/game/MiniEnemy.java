package game;


public class MiniEnemy extends Enemy {

    public MiniEnemy(float x, float y, int[][] maze) {
         super(
            x, y, maze,
            1,                      // health: weak
            0.45f,                    // speed: fast
            4f * (2f / maze.length),       // chase range
            3f * (2f / maze.length)        // shoot range
        );

        this.setShootInterval(0.7f);  // half the normal cooldown
        this.setCustomSize(0.7f);        // Visually smaller for clarification
    }

}
