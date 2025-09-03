package game.render;

import static org.lwjgl.opengl.GL11.*;

public class LoseRenderer {
    private final FontRenderer font;
    private final int W = 800, H = 800;

    public LoseRenderer(FontRenderer fontRenderer) {
        this.font = fontRenderer;
    }

    public void render() {
        glLoadIdentity();

        // 1) red background
        glColor3f(1f, 0f, 0f);
        glBegin(GL_QUADS);
          glVertex2f(0,   0);
          glVertex2f(W,   0);
          glVertex2f(W,   H);
          glVertex2f(0,   H);
        glEnd();

        // 2) black text
        glColor3f(0f, 0f, 0f);

        // ─── Title: “YOU LOST! Try Again?” ─────────────────────────────────
        String title = "YOU LOST! Try Again?";
        float  tSize = 64f;
        float  tW    = font.measureText(title, tSize);
        float  tX    = (W - tW) / 2f;
        float  fudge = -70f;
        float  tY    = 115f;
        font.renderText(title, tX + fudge, tY, tSize);

        // ─── “Press SPACE to Play Again” ─────────────────────────────────
        String opt1  = "Press SPACE to Play Again";
        float  oSize = 32f;
        float  oW1   = font.measureText(opt1, oSize);
        float  oX1   = (W - oW1) / 2f;
        float  oY1   = H - 250f;
        font.renderText(opt1, oX1, oY1, oSize);

        // ─── “Press ESC to Quit” ─────────────────────────────────────────
        String opt2  = "Press ESC to Quit";
        float  oW2   = font.measureText(opt2, oSize);
        float  oX2   = (W - oW2) / 2f;
        float  oY2   = H - 160f;
        font.renderText(opt2, oX2, oY2, oSize);
    }
}
