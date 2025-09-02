package game;

import static org.lwjgl.opengl.GL11.*;

public class MenuRenderer {
    private final FontRenderer font;
    private final int W = 800, H = 800;

    public MenuRenderer(FontRenderer fontRenderer) {
        this.font = fontRenderer;
    }

    public void render() {
        glLoadIdentity();

        // 1) White background
        glColor3f(1f, 1f, 1f);
        glBegin(GL_QUADS);
          glVertex2f(0,   0);
          glVertex2f(W,   0);
          glVertex2f(W,   H);
          glVertex2f(0,   H);
        glEnd();

        // 2) Black text for title and options
        glColor3f(0f, 0f, 0f);

        // ─── Title ──────────────────────────────────────────────────────────────
        String title = "TANK MAZE GAME";
        float tSize = 64f;
        float tW     = font.measureText(title, tSize);
        float tX     = (W - tW) / 2f;
        float titleFudge = -85f;   // original fudge to make title appear in centre
        float tY     = 115f;
        font.renderText(title, tX + titleFudge, tY, tSize);

        // ─── “Press SPACE to Start” ───────────────────────────────────────────
        String opt1 = "Press SPACE to Start";
        float oSize = 32f;
        float oW1  = font.measureText(opt1, oSize);
        float oX1  = (W - oW1) / 2f;
        float oY1  = H - 250f;
        font.renderText(opt1, oX1, oY1, oSize);

        // ─── “Press ESC to Quit” ───────────────────────────────────────────────
        String opt2 = "Press ESC to Quit";
        float oW2  = font.measureText(opt2, oSize);
        float oX2  = (W - oW2) / 2f;
        float oY2  = H - 160f;
        font.renderText(opt2, oX2, oY2, oSize);
    }
}
















