package game.gameplay;

import java.util.*;

public class PathFinder {
    // Returns a list of (row,col) pairs from start → goal, inclusive.
    public static List<int[]> findPath(int[][] maze, int startR, int startC, int goalR, int goalC) {
        int rows = maze.length, cols = maze[0].length;
        Node[][] nodes = new Node[rows][cols];
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                nodes[r][c] = new Node(r, c);

        PriorityQueue<Node> open = new PriorityQueue<>(Comparator.comparingInt(n -> n.f));
        boolean[][] closed = new boolean[rows][cols];
        Node start = nodes[startR][startC];
        Node goal  = nodes[goalR][goalC];

        start.g = 0;
        start.h = Math.abs(startR - goalR) + Math.abs(startC - goalC);
        start.f = start.h;
        open.add(start);

        int[][] DIRS = {{1,0},{-1,0},{0,1},{0,-1}};
        while (!open.isEmpty()) {
            Node cur = open.poll();
            if (cur == goal) break;
            closed[cur.r][cur.c] = true;

            for (int[] d : DIRS) {
                int nr = cur.r + d[0], nc = cur.c + d[1];
                if (nr<0||nr>=rows||nc<0||nc>=cols) continue;
                if (maze[nr][nc]==1 || closed[nr][nc]) continue;

                Node nxt = nodes[nr][nc];
                int ng = cur.g + 1;
                if (ng < nxt.g) {
                    nxt.g = ng;
                    nxt.h = Math.abs(nr - goalR) + Math.abs(nc - goalC);
                    nxt.f = nxt.g + nxt.h;
                    nxt.parent = cur;
                    if (!open.contains(nxt)) open.add(nxt);
                }
            }
        }

        // Reconstruct path
        List<int[]> path = new ArrayList<>();
        for (Node n = goal; n != null; n = n.parent) {
            path.add(new int[]{n.r, n.c});
        }
        Collections.reverse(path);
        return path;
    }

    private static class Node {
        int r,c;
        int g = Integer.MAX_VALUE, h = 0, f = Integer.MAX_VALUE;
        Node parent = null;
        Node(int r,int c){ this.r=r; this.c=c; }
    }
}

