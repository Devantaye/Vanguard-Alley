package game.ui.menu;

public interface SettingsChangeListener 
{
    /**
     * Volume changes from 1–10
     * @param level Volume level (1-10)
     */
    void onVolumeChanged(int level);
}
