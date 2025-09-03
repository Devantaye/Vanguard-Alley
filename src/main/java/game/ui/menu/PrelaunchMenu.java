package game.ui.menu;

import javax.swing.*;

import java.util.concurrent.CountDownLatch;
import game.app.GameConfig;
import game.audio.AudioPlayer;


public final class PrelaunchMenu {
    private PrelaunchMenu() {}
    private static AudioPlayer menuMusic;


    public static boolean showAndWait() {
        CountDownLatch latch = new CountDownLatch(1);
        final boolean[] start = {false};

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Vanguard Alley");
            frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

            GameMenu panel = new GameMenu(() -> {
                start[0] = true;      // Play clicked
                frame.dispose();
            });

            frame.setContentPane(panel);
            frame.setSize(800, 600);
            frame.setResizable(false);
            frame.setLocationRelativeTo(null);

            frame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override public void windowClosed(java.awt.event.WindowEvent e) {
                    panel.stopAudio();
                    latch.countDown();
                }
                @Override public void windowClosing(java.awt.event.WindowEvent e) {
                    panel.stopAudio();
                    latch.countDown();
                }
            });

            frame.setVisible(true);
        });

        try { latch.await(); } catch (InterruptedException ignored) {}
        return start[0];
    }
}

