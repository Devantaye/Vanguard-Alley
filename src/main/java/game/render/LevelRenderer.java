package game.render;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glVertex2f;

public class LevelRenderer {
    private final FontRenderer font;
    private final int W = 800, H = 800;

    public LevelRenderer(FontRenderer fontRenderer) {
        this.font = fontRenderer;
    }

    public void render(int level) {
        glLoadIdentity();

        // 1) Background
        glColor3f(0.75f, 0.85f, 0.95f); 

        glBegin(GL_QUADS);
          glVertex2f(0,   0);
          glVertex2f(W,   0);
          glVertex2f(W,   H);
          glVertex2f(0,   H);
        glEnd();

        // 2) black text
        glColor3f(0f, 0f, 0f);

        // ─── Title: “You Win!” ───────────────────────────────────────────────
        String title     = "LEVEL " + level + " COMPLETE!";
        float  tSize     = 64f;
        float  tW        = font.measureText(title, tSize);
        float  tX        = (W - tW) / 2f;
        float winFudge = -70f;
        float  tY        = 115f;
        font.renderText(title, tX + winFudge, tY, tSize);

        // ─── “Press SPACE to play again” ────────────────────────────────────
        String opt1      = "Press SPACE to go to Level " + (level + 1);
        float  oSize     = 32f;
        float  oW1       = font.measureText(opt1, oSize);
        float  oX1       = (W - oW1) / 2f;
        float  oY1       = H - 250f;
        font.renderText(opt1, oX1, oY1, oSize);

        // ─── “Press ESC to Quit” ───────────────────────────────────────────
        String opt2      = "Press ESC to Quit";
        float  oW2       = font.measureText(opt2, oSize);
        float  oX2       = (W - oW2) / 2f;
        float  oY2       = H - 160f;
        font.renderText(opt2, oX2, oY2, oSize);
    }
}


