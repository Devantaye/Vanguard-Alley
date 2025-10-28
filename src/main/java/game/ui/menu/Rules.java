package game.ui.menu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import javax.imageio.ImageIO;

public class Rules extends JPanel 
{
    private JButton backButton;

    // Gesture images
    private BufferedImage upGesture, downGesture, leftGesture, rightGesture, shootGesture;

    // Keyboard key images
    private BufferedImage upKey, downKey, leftKey, rightKey, shootKey;

    public Rules(ActionListener onBack) 
    {
        setPreferredSize(new Dimension(600, 550));
        setLayout(new BorderLayout());

        // Load images
        try 
        {
            // Gesture icons
            upGesture = ImageIO.read(new File("res/upgesture.png"));
            downGesture = ImageIO.read(new File("res/downgesture.png"));
            leftGesture = ImageIO.read(new File("res/leftgesture.png"));
            rightGesture = ImageIO.read(new File("res/rightgesture.png"));
            shootGesture = ImageIO.read(new File("res/shootgesture.png"));

            // Keyboard key icons
            upKey = ImageIO.read(new File("res/upkey.png"));
            downKey = ImageIO.read(new File("res/downkey.png"));
            leftKey = ImageIO.read(new File("res/leftkey.png"));
            rightKey = ImageIO.read(new File("res/rightkey.png"));
            shootKey = ImageIO.read(new File("res/shootkey.png"));
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }

        // Back button
        backButton = new JButton("Back to Menu");
        backButton.addActionListener(onBack);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    @Override
    protected void paintComponent(Graphics g) 
    {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Background
        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(0, 0, getWidth(), getHeight());

        // ===== Header =====
        g2.setColor(Color.GREEN);
        g2.setFont(new Font("Monospaced", Font.BOLD, 28));
        String header = "RULES";
        FontMetrics fmHeader = g2.getFontMetrics();
        int headerX = (getWidth() - fmHeader.stringWidth(header)) / 2;
        g2.drawString(header, headerX, 50);

        // ===== Sub-headers =====
        int middleX = getWidth() / 2;

        // Left sub-header — Hand Gesture Controls
        g2.setFont(new Font("Arial", Font.BOLD, 20));
        g2.setColor(Color.CYAN);
        String gestureHeader = "Hand Gesture Controls";
        FontMetrics fmSub1 = g2.getFontMetrics();
        int gestureHeaderX = (middleX - fmSub1.stringWidth(gestureHeader)) / 2;
        g2.drawString(gestureHeader, gestureHeaderX, 85);

        // Line under left sub-header
        g2.setColor(Color.LIGHT_GRAY);
        g2.fillRect(gestureHeaderX, 90, fmSub1.stringWidth(gestureHeader), 2);

        // Right sub-header — Keyboard Controls
        g2.setFont(new Font("Arial", Font.BOLD, 20));
        g2.setColor(Color.CYAN);
        String keyboardHeader = "Keyboard Controls";
        FontMetrics fmSub2 = g2.getFontMetrics();
        int keyboardHeaderX = middleX + (middleX - fmSub2.stringWidth(keyboardHeader)) / 2;
        g2.drawString(keyboardHeader, keyboardHeaderX, 85);

        // Line under right sub-header
        g2.setColor(Color.LIGHT_GRAY);
        g2.fillRect(keyboardHeaderX, 90, fmSub2.stringWidth(keyboardHeader), 2);

        // ===== Divider line (middle) =====
        g2.setColor(Color.LIGHT_GRAY);
        g2.fillRect(middleX - 2, 80, 4, getHeight() - 160); // vertical divider

        // ===== Rules setup =====
        List<String> rules = Arrays.asList
        (
                "To move UP",
                "To move DOWN",
                "To move LEFT",
                "To move RIGHT",
                "To SHOOT"
        );

        int imgSize = 50;
        int gap = 20;
        int startY = 130; //slightly lower to make room for headers
        int leftX = 50;
        int rightX = middleX + 70;

        g2.setFont(new Font("Arial", Font.BOLD, 16));
        g2.setColor(Color.WHITE);

        for (String rule : rules) 
        {
            BufferedImage gestureImg = null;
            BufferedImage keyImg = null;

            if (rule.toLowerCase().contains("up")) 
            {
                gestureImg = upGesture;
                keyImg = upKey;
            } 
            else if (rule.toLowerCase().contains("down")) 
            {
                gestureImg = downGesture;
                keyImg = downKey;
            } 
            else if (rule.toLowerCase().contains("left")) 
            {
                gestureImg = leftGesture;
                keyImg = leftKey;
            } 
            else if (rule.toLowerCase().contains("right")) 
            {
                gestureImg = rightGesture;
                keyImg = rightKey;
            } 
            else if (rule.toLowerCase().contains("shoot")) 
            {
                gestureImg = shootGesture;
                keyImg = shootKey;
            }

            // Left side (gesture + text)
            if (gestureImg != null)
                g2.drawImage(gestureImg, leftX, startY - imgSize + 20, imgSize, imgSize, null);

            g2.drawString(rule, leftX + imgSize + gap, startY);

            // Right side (keyboard + text closer)
            if (keyImg != null)
                g2.drawImage(keyImg, rightX, startY - imgSize + 20, imgSize, imgSize, null);

            g2.drawString(rule, rightX + imgSize + 10, startY); //text closer to keyboard

            startY += 70;
        }

        // ===== Bottom text =====
        g2.setFont(new Font("Arial", Font.BOLD, 18));
        g2.setColor(Color.YELLOW);
        String extraRule = "Kill all enemies and proceed to the white checkpoint";
        FontMetrics fmExtra = g2.getFontMetrics();
        int extraX = (getWidth() - fmExtra.stringWidth(extraRule)) / 2;
        int extraY = getHeight() - 50; // slightly lower
        g2.drawString(extraRule, extraX, extraY);
}


    // For testing
    public static void main(String[] args) 
    {
        JFrame frame = new JFrame("Rules");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new Rules(e -> System.out.println("Back clicked")));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
