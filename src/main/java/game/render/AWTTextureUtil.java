package game.render;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

public class AWTTextureUtil {
    public static ByteBuffer getImageBuffer(BufferedImage img) {
        int[] pixels = new int[img.getWidth() * img.getHeight()];
        img.getRGB(0, 0, img.getWidth(), img.getHeight(), pixels, 0, img.getWidth());

        ByteBuffer buf = ByteBuffer.allocateDirect(img.getWidth() * img.getHeight() * 4);
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int pixel = pixels[y * img.getWidth() + x];
                buf.put((byte) ((pixel >> 16) & 0xFF)); // R
                buf.put((byte) ((pixel >> 8) & 0xFF));  // G
                buf.put((byte) (pixel & 0xFF));         // B
                buf.put((byte) ((pixel >> 24) & 0xFF)); // A
            }
        }
        buf.flip();
        return buf;
    }
}

