package game;

import static org.lwjgl.opengl.GL11.*;

public class PauseRenderer {
    private FontRenderer fontRenderer;
    
    public PauseRenderer(FontRenderer fontRenderer) {
        this.fontRenderer = fontRenderer;
    }
    
    public void render(float volume, float brightness) {
        glClearColor(0.05f, 0.05f, 0.1f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT);
        
       
        glColor3f(1.0f, 1.0f, 1.0f);
        
        
        fontRenderer.renderText("GAME PAUSED", 175f, 150f, 40f);
        
       
        glColor3f(0.8f, 0.8f, 1.0f);
        fontRenderer.renderText("Volume: " + (int)(volume * 100) + "%", 300f, 230f, 32f);
        
       
        fontRenderer.renderText("Brightness: " + (int)(brightness * 100) + "%", 280f, 280f, 32f);
        
       
        fontRenderer.renderText("Press +/- to Adjust Volume", 250f, 330f, 28f);
        
        
        fontRenderer.renderText("Press Up/Down to Adjust Brightness", 220f, 370f, 28f);
        
       
        fontRenderer.renderText("Press E to Resume Game", 270f, 420f, 28f);
        fontRenderer.renderText("Press ESC to Return to Menu", 240f, 460f, 28f);
        
        
        drawVolumeBar(300f, 500f, 200f, 20f, volume);
        
        
        drawBrightnessBar(300f, 540f, 200f, 20f, brightness);
        
      
        glColor3f(0.3f, 0.3f, 0.5f);
        glBegin(GL_QUADS);
        glVertex2f(240, 190);
        glVertex2f(560, 190);
        glVertex2f(560, 200);
        glVertex2f(240, 200);
        glEnd();
    }
    
    private void drawVolumeBar(float x, float y, float width, float height, float volume) {
      
        glColor3f(0.2f, 0.2f, 0.3f);
        glBegin(GL_QUADS);
        glVertex2f(x, y);
        glVertex2f(x + width, y);
        glVertex2f(x + width, y + height);
        glVertex2f(x, y + height);
        glEnd();
        
        
        glColor3f(0.2f, 0.6f, 1.0f);
        glBegin(GL_QUADS);
        glVertex2f(x, y);
        glVertex2f(x + width * volume, y);
        glVertex2f(x + width * volume, y + height);
        glVertex2f(x, y + height);
        glEnd();
        
        
        glColor3f(0.8f, 0.8f, 1.0f);
        glLineWidth(2f);
        glBegin(GL_LINE_LOOP);
        glVertex2f(x, y);
        glVertex2f(x + width, y);
        glVertex2f(x + width, y + height);
        glVertex2f(x, y + height);
        glEnd();
    }
    
    private void drawBrightnessBar(float x, float y, float width, float height, float brightness) {
        
        glColor3f(0.2f, 0.2f, 0.3f);
        glBegin(GL_QUADS);
        glVertex2f(x, y);
        glVertex2f(x + width, y);
        glVertex2f(x + width, y + height);
        glVertex2f(x, y + height);
        glEnd();
        
       
        glColor3f(1.0f, 0.8f, 0.2f);
        glBegin(GL_QUADS);
        glVertex2f(x, y);
        glVertex2f(x + width * brightness, y);
        glVertex2f(x + width * brightness, y + height);
        glVertex2f(x, y + height);
        glEnd();
        
        
        glColor3f(0.8f, 0.8f, 1.0f);
        glLineWidth(2f);
        glBegin(GL_LINE_LOOP);
        glVertex2f(x, y);
        glVertex2f(x + width, y);
        glVertex2f(x + width, y + height);
        glVertex2f(x, y + height);
        glEnd();
    }
}