package game;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

public class BrightnessPanel extends JPanel {
    private final JSlider slider;

    public BrightnessPanel(SettingsChangeListener listener) {
        setLayout(new BorderLayout(10,10));
        setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        JLabel label = new JLabel("Change Brightness:");
        label.setFont(new Font("Arial", Font.PLAIN, 16));

        slider = new JSlider(1, 10, 5);
        slider.setMajorTickSpacing(1);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.addChangeListener(e ->
            listener.onBrightnessChanged(slider.getValue())
        );

        add(label, BorderLayout.NORTH);
        add(slider, BorderLayout.CENTER);
    }
}
