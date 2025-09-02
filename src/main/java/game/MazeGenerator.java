package game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Original recursive-backtracker from your upskilling project,
 * unchanged except for package name.
 *
 * rows & cols should both be odd.
 */
public class MazeGenerator {
    private final int rows;
    private final int cols;
    private final int[][] maze;
    private final Random rand = new Random();

    public MazeGenerator(int rows, int cols) {
        if (rows % 2 == 0 || cols % 2 == 0) {
            throw new IllegalArgumentException("rows and cols must be odd");
        }
        this.rows = rows;
        this.cols = cols;
        this.maze = new int[rows][cols];

        // fill with walls
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                maze[r][c] = 1;
            }
        }

        // carve starting at (1,1)
        generateMaze(1, 1);
    }

    private void generateMaze(int r, int c) {
        maze[r][c] = 0;
        List<int[]> dirs = new ArrayList<>();
        dirs.add(new int[]{0, -2});
        dirs.add(new int[]{0,  2});
        dirs.add(new int[]{-2, 0});
        dirs.add(new int[]{ 2, 0});
        Collections.shuffle(dirs, rand);

        for (int[] d : dirs) {
            int nr = r + d[0], nc = c + d[1];
            if (nr > 0 && nr < rows - 1 &&
                nc > 0 && nc < cols - 1 &&
                maze[nr][nc] == 1)
            {
                // knock down the wall between
                maze[r + d[0]/2][c + d[1]/2] = 0;
                generateMaze(nr, nc);
            }
        }
    }

    public int[][] getMaze() {
        return maze;
    }
}







