package game.ui.menu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;

public class Credits extends JPanel {
    private JButton backButton;
    private Timer timer;

    // Phase control
    private boolean showThankYou = false;

    // Y offset for scrolling
    private int yOffset = -200; // start above screen

    private final List<String> creditsText = Arrays.asList(
            "Hand Gesture Control: Shayne and Yunsu",
            "Tank Game Creation: Devante and Leo",
            "Tank Game Menu Creation: Frank and Kanak"
    );

    private final String header = "CREDITS";
    private final String thankYouMessage = "Thank you for playing!";

    public Credits(ActionListener onBack) {
        setPreferredSize(new Dimension(800, 600));
        setLayout(null);

        // Back button
        backButton = new JButton("Back to Menu");
        backButton.setBounds(320, 500, 160, 40);
        backButton.addActionListener(onBack);
        add(backButton);

        // Animation timer
        timer = new Timer(33, e -> {
            yOffset += 2; // move downward

            if (!showThankYou) {
                // Total block height = header + credits lines
                int blockHeight = 60 + creditsText.size() * 40;
                int lastLineY = yOffset + blockHeight;

                // When last line has scrolled past bottom, switch to Thank You
                if (lastLineY > getHeight()) {
                    showThankYou = true;
                    yOffset = -50; // restart above screen for thank-you message
                }
            } else {
                // Stop once thank-you message reaches center
                int panelCenter = getHeight() / 2;
                if (yOffset >= panelCenter) {
                    yOffset = panelCenter;
                    timer.stop();
                }
            }

            repaint();
        });
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Background
        g2.setColor(new Color(70, 70, 70));
        g2.fillRect(0, 0, getWidth(), getHeight());

        // Steel plating effect
        g2.setColor(new Color(100, 100, 100));
        for (int x = 0; x < getWidth(); x += 80) {
            g2.fillRect(x, 0, 5, getHeight());
        }

        // Weapons silhouette
        g2.setColor(new Color(40, 40, 40));
        g2.fillRect(650, 300, 100, 20);
        g2.fillRect(730, 280, 20, 60);
        g2.fillRect(680, 250, 100, 20);

        if (!showThankYou) {
            // Header (scrolling with credits)
            g2.setFont(new Font("Monospaced", Font.BOLD, 36));
            g2.setColor(Color.GREEN);
            FontMetrics fmHeader = g2.getFontMetrics();
            int headerX = (getWidth() - fmHeader.stringWidth(header)) / 2;
            g2.drawString(header, headerX, yOffset);

            // Credits text
            g2.setFont(new Font("Arial", Font.PLAIN, 18));
            g2.setColor(Color.RED);
            FontMetrics fmCredits = g2.getFontMetrics();

            int y = yOffset + 60; // leave some gap after header
            for (String line : creditsText) {
                int textWidth = fmCredits.stringWidth(line);
                int x = (getWidth() - textWidth) / 2; // center horizontally
                g2.drawString(line, x, y);
                y += 40;
            }
        } else {
            // Thank You message
            g2.setFont(new Font("Arial", Font.BOLD, 24));
            g2.setColor(Color.YELLOW);
            FontMetrics fm2 = g2.getFontMetrics();
            int msgX = (getWidth() - fm2.stringWidth(thankYouMessage)) / 2;
            g2.drawString(thankYouMessage, msgX, yOffset);
        }
    }

    // For quick testing
    public static void main(String[] args) 
    {
        JFrame frame = new JFrame("Credits");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new Credits(e -> System.out.println("Back clicked")));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}