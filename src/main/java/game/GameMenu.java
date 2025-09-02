package game;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.Timer;

public class GameMenu extends JPanel implements ActionListener, SettingsChangeListener 
{
    private Timer timer;
    private JButton playButton, settingsButton, rulesButton, creditsButton, exitButton;
    private MusicPlayer music = new MusicPlayer();
    private static GameMenu mainMenu;
        
    
 // Maze definition (28 rows x 36 cols, 1 = wall, 0 = path)
    private final int[][] maze = 
    {
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,1,1,1,1,1,0,1,0,1,1,1,1,0,1,0,1,0,1,0,1,0,1,1,1,1,0,1,0,1,1,1,1,0,1},
        {1,0,0,0,0,0,1,0,0,0,1,0,0,0,0,1,0,0,0,1,0,1,0,0,0,0,1,0,1,0,0,0,0,0,0,1},
        {1,1,1,1,1,0,1,1,1,0,1,1,1,1,0,1,1,1,0,1,0,1,1,1,1,0,1,0,1,1,1,1,1,1,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,1,1,1,1,1,1,0,1,1,1,1,1,0,1,1,1,0,1,1,1,1,1,1,0,1,1,1,1,0,1,1,1,1,1},
        {1,0,0,0,0,0,0,1,0,0,0,0,0,0,0,1,0,0,0,1,0,0,0,0,0,0,1,0,0,0,0,1,0,0,0,1},
        {1,1,1,1,1,1,0,1,1,1,1,1,0,1,1,1,1,1,0,1,1,1,1,1,0,1,1,1,1,1,0,1,1,1,0,1},
        {1,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,1,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,1,0,1},
        {1,0,1,1,0,1,1,1,1,1,0,1,1,1,1,1,0,1,1,1,0,1,1,1,1,1,0,1,1,1,1,1,0,1,0,1},
        {1,0,0,1,0,0,0,0,0,1,0,0,0,0,0,0,0,1,0,0,0,1,0,0,0,0,0,0,0,0,0,1,0,0,0,1},
        {1,1,0,1,1,1,1,1,0,1,0,1,1,1,1,1,0,1,0,1,0,1,1,1,1,1,0,1,1,1,1,1,0,1,1,0,1},
        {1,0,0,0,0,0,0,1,0,0,0,0,0,0,0,1,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,1},
        {1,0,1,1,1,1,0,1,1,1,1,1,1,1,0,1,1,1,1,1,1,1,1,1,1,1,0,1,1,1,0,1,1,1,1,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
        // Add extra rows here (like a repeat or new paths) to reach ~28 rows
        {1,0,1,1,1,1,1,1,0,1,1,1,1,1,0,1,1,1,0,1,1,1,1,1,1,0,1,1,1,1,0,1,1,1,1,1},
        {1,0,0,0,0,0,0,1,0,0,0,0,0,0,0,1,0,0,0,1,0,0,0,0,0,0,1,0,0,0,0,1,0,0,0,1},
        {1,1,1,1,1,1,0,1,1,1,1,1,0,1,1,1,1,1,0,1,1,1,1,1,0,1,1,1,1,1,0,1,1,1,0,1},
        {1,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,1,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,1,0,1},
        {1,0,1,1,0,1,1,1,1,1,0,1,1,1,1,1,0,1,1,1,0,1,1,1,1,1,0,1,1,1,1,1,0,1,0,1},
        {1,0,0,1,0,0,0,0,0,1,0,0,0,0,0,0,0,1,0,0,0,1,0,0,0,0,0,0,0,0,0,1,0,0,0,1},
        {1,1,0,1,1,1,1,1,0,1,0,1,1,1,1,1,0,1,0,1,0,1,1,1,1,1,0,1,1,1,1,1,0,1,1,0,1},
        {1,0,0,0,0,0,0,1,0,0,0,0,0,0,0,1,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,1},
        {1,0,1,1,1,1,0,1,1,1,1,1,1,1,0,1,1,1,1,1,1,1,1,1,1,1,0,1,1,1,0,1,1,1,1,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
    };


    private final int cellSize = 30;
    private Tank player;
    private java.util.List<Tank> enemies = new ArrayList<>();
    private Random rand = new Random();

    private int brightnessLevel = 10;

    public GameMenu() 
    {
        setLayout(null);
        
        if (mainMenu == null)
        {
            mainMenu = this;
        }
        
        //music file
        music.init("res/menu_music.wav");

        // Buttons
        playButton     = createButton("Play",     300, 200);
        settingsButton = createButton("Settings", 300, 270);
        rulesButton    = createButton("Rules",    300, 340);
        creditsButton  = createButton("Credits",  300, 410);
        exitButton     = createButton("Exit",     300, 480);

        
        add(playButton);
        add(settingsButton);
        add(rulesButton);
        add(creditsButton);
        add(exitButton);

        // Tanks
        player = new Tank(1, 1, Color.WHITE, true);
        spawnEnemy();

        // Timer for animation
        timer = new Timer(200, this);
        timer.start();
    }

    private JButton createButton(String text, int x, int y) 
    {
        JButton btn = new JButton(text);
        btn.setBounds(x, y, 200, 50);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Monospaced", Font.BOLD, 20));
        btn.addActionListener(e -> handleButtonClick(text));
        return btn;
    }

    private void handleButtonClick(String buttonText) 
    {
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);

        switch (buttonText) 
        {
            case "Play":
                System.out.println("Play the game!");
                break;

            case "Settings":
                SettingsDialog dlg = new SettingsDialog(parent, this);
                dlg.pack();
                dlg.setLocationRelativeTo(parent);
                dlg.setVisible(true);
                break;

            case "Rules":
                Rules rulesPanel = new Rules(e -> 
                {
                    // Go back to the menu
                    parent.getContentPane().removeAll();
                    parent.add(mainMenu);
                    parent.revalidate();
                    parent.repaint();
                });

                parent.getContentPane().removeAll();
                parent.add(rulesPanel);
                parent.revalidate();
                parent.repaint();
                break;
                
             case "Credits":
                Credits creditsPanel = new Credits(e -> 
                {
                    // Go back to the menu
                    parent.getContentPane().removeAll();
                    parent.revalidate();
                    parent.add(mainMenu);
                    parent.repaint();
                });

                parent.getContentPane().removeAll();
                parent.add(creditsPanel);
                parent.revalidate();
                parent.repaint();
                break;

            case "Exit":
                System.exit(0);
                break;
        }
    }
    
    @Override
    public void onVolumeChanged(int level) 
    {
        float vol = Math.max(0f, Math.min(1f, level / 10f));
        music.setVolume(vol);
    }

    @Override
    public void onBrightnessChanged(int level) 
    {
        brightnessLevel = Math.max(1, Math.min(10, level));
    }

@Override
protected void paintComponent(Graphics g) 
{
    super.paintComponent(g);
    
    Graphics2D g2 = (Graphics2D) g;

    // Background brightness
    float f = brightnessLevel / 10f;
    g2.setColor(new Color((int)(50 * f), (int)(50 * f), (int)(50 * f)));
    g2.fillRect(0, 0, getWidth(), getHeight());

    // Calculate dynamic cell size to fit the panel
    int rows = maze.length;
    int cols = maze[0].length;
    int cellWidth = getWidth() / cols;
    int cellHeight = getHeight() / rows;
    int cellSize = Math.min(cellWidth, cellHeight); // Keep cells square

    // Draw maze
    for (int r = 0; r < rows; r++) 
    {
        for (int c = 0; c < cols; c++) 
        {
            if (maze[r][c] == 1) 
            {
                g2.setColor(Color.DARK_GRAY);
            } 
            else 
            {
                g2.setColor(Color.BLACK);
            }
            g2.fillRect(c * cellSize, r * cellSize, cellSize, cellSize);
        }
    }

    // Draw tanks
    player.draw(g2, cellSize);
    for (Tank e : enemies) e.draw(g2, cellSize);

    // Draw Title
    String title = "VANGUARD ALLEY";
    Font font = new Font("Monospaced", Font.BOLD, 48);
    g2.setFont(font);
    g2.setColor(Color.GREEN.darker());

    FontMetrics fm = g2.getFontMetrics(font);
    int textWidth = fm.stringWidth(title);

    // Center horizontally
    int x = (getWidth() - textWidth) / 2;

    // Position vertically just above Play button
    int playButtonY = 150;   // <<< change this to your actual Play button Y
    int gap = 20;            // distance between title and Play button
    int y = playButtonY - gap;

    // Draw
    g2.drawString(title, x, y);

}



@Override
public void actionPerformed(ActionEvent e) 
{
    // Move player randomly
    player.randomMove();

    // Move enemies toward player
    for (Tank enemy : enemies) 
    {
        enemy.chase(player);
        // Check collision
        if (enemy.row == player.row && enemy.col == player.col) 
        {
            respawnPlayer();
            break; // only need to respawn once
        }
    }

    // Occasionally spawn new enemy
    if (rand.nextInt(30) == 0 && enemies.size() < 5) 
    {
        spawnEnemy();
    }

    repaint();
}

private void respawnPlayer() 
{
    int r, c;
    
    do 
    {
        r = rand.nextInt(maze.length);
        c = rand.nextInt(maze[0].length);
    } 
    while (maze[r][c] == 1);  // ensure not a wall
    
    player.row = r;
    player.col = c;
}



    private void spawnEnemy() 
    {
        int r, c;
        
        do 
        {
            r = rand.nextInt(maze.length);
            c = rand.nextInt(maze[0].length);
        } 
        while (maze[r][c] == 1 || (r == 1 && c == 1));
        
        enemies.add(new Tank(c, r, Color.RED, false));
    }

    public static void main(String[] args) 
    {
        JFrame frame = new JFrame("Vanguard Alley");
        GameMenu mainMenu = new GameMenu();
        frame.add(mainMenu);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    // ================= Tank Class =================
    private class Tank 
    {
        int col, row;
        Color color;
        boolean isPlayer;

        Tank(int col, int row, Color color, boolean isPlayer) 
        {
            this.col = col;
            this.row = row;
            this.color = color;
            this.isPlayer = isPlayer;
        }

        void draw(Graphics2D g2, int cellSize) 
        {
            g2.setColor(color);
            g2.fillRect(col * cellSize + cellSize / 6, row * cellSize + cellSize / 6, 
                        cellSize * 2 / 3, cellSize * 2 / 3);
        }

        void randomMove() 
        {
            int[][] dirs = {{0,-1},{1,0},{0,1},{-1,0}};

            Collections.shuffle(Arrays.asList(dirs));

            for (int[] d : dirs) 
            {
                int nr = row + d[1], nc = col + d[0];
                if (maze[nr][nc] == 0) { row = nr; col = nc; break; }
            }
        }

        void chase(Tank target) 
        {
            if (Math.abs(target.row - row) > Math.abs(target.col - col)) 
            {
                row += Integer.signum(target.row - row);
            } 
            else 
            {
                col += Integer.signum(target.col - col);
            }

            if (maze[row][col] == 1) 
            {
                // Undo if hit wall
                if (Math.abs(target.row - row) > Math.abs(target.col - col)) 
                {
                    row -= Integer.signum(target.row - row);
                } 
                else 
                {
                    col -= Integer.signum(target.col - col);
                }
            }
        }
    }
}
