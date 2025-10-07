
package game.ui.menu;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Frame;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

/**
 *
 * @author rocka
 */
public class SettingsDialog extends JDialog 
{
    public SettingsDialog(Frame owner, SettingsChangeListener listener) 
    {
        super(owner, "Settings", true);
        setSize(400, 300);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        JPanel top = new JPanel();
        JButton volBtn = new JButton("Volume");
        top.add(volBtn);

        JPanel cards = new JPanel(new CardLayout());
        volumepanel volPanel = new volumepanel(listener);
        cards.add(volPanel, "VOLUME");

        volBtn.addActionListener(e ->
            ((CardLayout)cards.getLayout()).show(cards, "VOLUME"));

        add(top, BorderLayout.NORTH);
        add(cards, BorderLayout.CENTER);
        ((CardLayout)cards.getLayout()).show(cards, "VOLUME");
    }
}
