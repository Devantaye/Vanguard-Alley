
package game;


import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Frame;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;


public class SettingsDialog extends JDialog {
    public SettingsDialog(Frame owner, SettingsChangeListener listener) {
        super(owner, "Settings", true);
        setSize(400, 300);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        JPanel top = new JPanel();
        JButton volBtn = new JButton("Volume");
        JButton briBtn = new JButton("Brightness");
        top.add(volBtn);
        top.add(briBtn);

        JPanel cards = new JPanel(new CardLayout());
        volumepanel volPanel = new volumepanel(listener);
        BrightnessPanel briPanel = new BrightnessPanel(listener);
        cards.add(volPanel, "VOLUME");
        cards.add(briPanel, "BRIGHT");

        volBtn.addActionListener(e ->
            ((CardLayout)cards.getLayout()).show(cards, "VOLUME"));
        briBtn.addActionListener(e ->
            ((CardLayout)cards.getLayout()).show(cards, "BRIGHT"));

        add(top, BorderLayout.NORTH);
        add(cards, BorderLayout.CENTER);
        ((CardLayout)cards.getLayout()).show(cards, "VOLUME");
    }
}