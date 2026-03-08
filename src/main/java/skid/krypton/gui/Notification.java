package com.uranium.gui;

import com.uranium.utils.RenderUtils;
import com.uranium.utils.TextRenderer;
import net.minecraft.client.gui.DrawContext;

import java.awt.*;

public class Notification {
    private final String title;
    private final String message;
    private final long startTime;
    public int life = 100;
    public Animation animation = new Animation(1.0);
    
    private static final Color BG_COLOR = new Color(15, 25, 15, 230);
    private static final Color ACCENT_COLOR = new Color(50, 255, 50);
    private static final Color TEXT_COLOR = new Color(220, 255, 220);
    
    public Notification(String title, String message) {
        this.title = title;
        this.message = message;
        this.startTime = System.currentTimeMillis();
    }

    public void render(DrawContext context, int y, float delta) {
        int width = context.getScaledWindowWidth();
        int notifWidth = 300;
        int x = width - notifWidth - 20;
        
        float lifeProgress = life / 100f;
        float fadeIn = Math.min(1, (System.currentTimeMillis() - startTime) / 200f);
        float fadeOut = Math.min(1, Math.max(0, life - 20) / 80f);
        float alpha = Math.min(fadeIn, fadeOut) * 0.8f;
        
        // Background
        Color bgColor = new Color(BG_COLOR.getRed(), BG_COLOR.getGreen(), 
            BG_COLOR.getBlue(), (int)(BG_COLOR.getAlpha() * alpha));
        RenderUtils.renderRoundedQuad(context.getMatrices(), bgColor,
            x, y, x + notifWidth, y + 40, 8, 8, 8, 8, 20);
        
        // Accent line
        context.fill(x, y, x + (int)(notifWidth * lifeProgress), y + 2, 
            new Color(ACCENT_COLOR.getRed(), ACCENT_COLOR.getGreen(), 
                ACCENT_COLOR.getBlue(), (int)(200 * alpha)).getRGB());
        
        // Content
        TextRenderer.drawString(title, context, x + 15, y + 8, 
            new Color(TEXT_COLOR.getRed(), TEXT_COLOR.getGreen(), 
                TEXT_COLOR.getBlue(), (int)(255 * alpha)).getRGB());
        TextRenderer.drawString(message, context, x + 15, y + 23, 
            new Color(TEXT_COLOR.getRed(), TEXT_COLOR.getGreen(), 
                TEXT_COLOR.getBlue(), (int)(200 * alpha)).getRGB());
    }

    public void update(float delta) {
        life -= delta * 0.5f;
    }
}
