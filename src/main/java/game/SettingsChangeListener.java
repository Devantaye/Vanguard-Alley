package game;

public interface SettingsChangeListener {
    /**
     * Volume changes from 1–10
     * @param level Volume level (1-10)
     */
    void onVolumeChanged(int level);

    /**
     * Brightness varies from 1–10
     * @param level Brightness level (1-10)
     */
    void onBrightnessChanged(int level);
}