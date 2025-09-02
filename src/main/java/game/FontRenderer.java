package game;

import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBTruetype.*;

public class FontRenderer {
    private static final int BITMAP_W = 512, BITMAP_H = 512;
    private final STBTTBakedChar.Buffer cdata;
    private final int texId;
    private final float bakeH = 32f;

    public FontRenderer() throws IOException {
        InputStream in = getClass().getClassLoader()
            .getResourceAsStream("fonts/roboto.ttf");
        if (in == null) throw new IOException("Missing fonts/roboto.ttf");
        byte[] bytes = in.readAllBytes();
        ByteBuffer ttf = BufferUtils.createByteBuffer(bytes.length);
ttf.put(bytes);
ttf.flip();


        ByteBuffer bmp = BufferUtils.createByteBuffer(BITMAP_W * BITMAP_H);
        cdata = STBTTBakedChar.malloc(96);
        if (stbtt_BakeFontBitmap(ttf, bakeH, bmp, BITMAP_W, BITMAP_H, 32, cdata) <= 0)
            throw new IOException("stbtt_BakeFontBitmap failed");

        texId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texId);
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_ALPHA, BITMAP_W, BITMAP_H,
                     0, GL_ALPHA, GL_UNSIGNED_BYTE, bmp);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    }

    public void renderText(String text, float x, float y, float fontPx) {
        glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, texId);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        //glColor3f(1,1,1);

        float scale = fontPx / bakeH;
        try (MemoryStack st = MemoryStack.stackPush()) {
            FloatBuffer px = st.floats(x), py = st.floats(y);
            glBegin(GL_QUADS);
            for (char c : text.toCharArray()) {
                if (c < 32 || c > 126) continue;
                STBTTAlignedQuad q = STBTTAlignedQuad.malloc(st);
                stbtt_GetBakedQuad(cdata, BITMAP_W, BITMAP_H,
                                  c - 32, px, py, q, false);
                float x0 = q.x0()*scale, y0 = q.y0()*scale;
                float x1 = q.x1()*scale, y1 = q.y1()*scale;
                glTexCoord2f(q.s0(), q.t0()); glVertex2f(x0, y0);
                glTexCoord2f(q.s1(), q.t0()); glVertex2f(x1, y0);
                glTexCoord2f(q.s1(), q.t1()); glVertex2f(x1, y1);
                glTexCoord2f(q.s0(), q.t1()); glVertex2f(x0, y1);
            }
            glEnd();
        }

        glDisable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
    }

    /**
     * Precisely measures the pixel width of `text` at height = fontPx
     * by walking each quad’s x0/x1.
     */
    public float measureText(String text, float fontPx) {
        float scale = fontPx / bakeH;
        float maxX = 0f;
        try (MemoryStack st = MemoryStack.stackPush()) {
            FloatBuffer px = st.floats(0f), py = st.floats(0f);
            for (char c : text.toCharArray()) {
                if (c < 32 || c > 126) continue;
                STBTTAlignedQuad q = STBTTAlignedQuad.malloc(st);
                stbtt_GetBakedQuad(cdata, BITMAP_W, BITMAP_H,
                                  c - 32, px, py, q, false);
                maxX = Math.max(maxX, q.x1() * scale);
            }
        }
        return maxX;
    }
}





