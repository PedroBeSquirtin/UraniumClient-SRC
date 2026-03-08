package com.uranium.gui;

import com.uranium.module.Module;
import com.uranium.utils.RenderUtils;
import com.uranium.utils.TextRenderer;
import net.minecraft.client.gui.DrawContext;

import java.awt.*;

public class ModuleButton {
    private final CategoryPanel parent;
    private final Module module;
    private float hoverAnimation = 0.0f;
    private float enabledAnimation = 0.0f;
    
    private static final Color BG_COLOR = new Color(20, 30, 20);
    private static final Color HOVER_COLOR = new Color(30, 50, 30);
    private static final Color ENABLED_COLOR = new Color(50, 255, 50);
    private static final Color DISABLED_COLOR = new Color(100, 150, 100);
    private static final Color TEXT_COLOR = new Color(200, 255, 200);
    
    public ModuleButton(CategoryPanel parent, Module module, int offset) {
        this.parent = parent;
        this.module = module;
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta, int y) {
        int x = parent.getX();
        int width = parent.getWidth();
        
        boolean hovered = mouseX >= x && mouseX <= x + width && 
                         mouseY >= y && mouseY <= y + 35;
        
        hoverAnimation += (hovered ? 0.15f : -0.15f) * delta * 2;
        hoverAnimation = Math.max(0, Math.min(1, hoverAnimation));
        
        enabledAnimation += (module.isEnabled() ? 0.1f : -0.1f) * delta * 2;
        enabledAnimation = Math.max(0, Math.min(1, enabledAnimation));
        
        // Background
        Color bgColor = interpolateColor(BG_COLOR, HOVER_COLOR, hoverAnimation);
        RenderUtils.renderRoundedQuad(context.getMatrices(), bgColor,
            x + 2, y, x + width - 2, y + 33, 6, 6, 6, 6, 20);
        
        if (hovered) {
            RenderUtils.renderOuterGlow(context.getMatrices(),
                new Color(50, 255, 50, (int)(40 * hoverAnimation)),
                x + 2, y, x + width - 2, y + 33, 6, 2);
        }
        
        // Module name
        Color textColor = module.isEnabled() ? 
            new Color(50, (int)(255 * (0.8 + enabledAnimation * 0.2)), 50) : 
            new Color(150, 200, 150);
        TextRenderer.drawString(module.getName(), context,
            x + 15, y + 10, textColor.getRGB());
        
        // Status indicator
        int indicatorSize = 8;
        int indicatorX = x + width - 25;
        int indicatorY = y + 13;
        
        if (module.isEnabled()) {
            // Pulsing glow for enabled modules
            float pulse = (float)(Math.sin(System.currentTimeMillis() * 0.005) * 0.3 + 0.7);
            RenderUtils.renderGlow(context.getMatrices(),
                new Color(50, 255, 50, (int)(40 * pulse)),
                indicatorX + 4, indicatorY + 4, 20);
        }
        
        RenderUtils.renderCircle(context.getMatrices(),
            module.isEnabled() ? ENABLED_COLOR : DISABLED_COLOR,
            indicatorX + 4, indicatorY + 4, indicatorSize / 2, 16);
    }

    private Color interpolateColor(Color c1, Color c2, float factor) {
        return new Color(
            (int)(c1.getRed() + (c2.getRed() - c1.getRed()) * factor),
            (int)(c1.getGreen() + (c2.getGreen() - c1.getGreen()) * factor),
            (int)(c1.getBlue() + (c2.getBlue() - c1.getBlue()) * factor),
            (int)(c1.getAlpha() + (c2.getAlpha() - c1.getAlpha()) * factor)
        );
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button, int y) {
        int x = parent.getX();
        int width = parent.getWidth();
        
        if (mouseX >= x && mouseX <= x + width && 
            mouseY >= y && mouseY <= y + 35) {
            if (button == 0) {
                module.toggle();
            }
            return true;
        }
        return false;
    }

    public void mouseReleased(double mouseX, double mouseY, int button) {}
    public void updateAnimation(float delta) {}
}
