package com.uranium.gui;

import com.uranium.UraniumClient;
import com.uranium.module.Category;
import com.uranium.module.Module;
import com.uranium.utils.RenderUtils;
import com.uranium.utils.TextRenderer;
import com.uranium.utils.animation.Animation;
import com.uranium.utils.animation.Easing;
import net.minecraft.client.gui.DrawContext;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryPanel {
    private final Category category;
    private final List<ModuleButton> moduleButtons = new ArrayList<>();
    private int x, y;
    private final int width;
    private int height;
    private boolean dragging = false;
    private int dragX, dragY;
    private float expandAnimation = 1.0f;
    private float hoverAnimation = 0.0f;
    private boolean expanded = true;
    
    private static final int HEADER_HEIGHT = 45;
    private static final int MODULE_HEIGHT = 35;
    
    // Glowing Green Theme Colors
    private static final Color HEADER_COLOR = new Color(20, 35, 20);
    private static final Color HEADER_GLOW = new Color(50, 255, 50, 40);
    private static final Color BG_COLOR = new Color(15, 25, 15);
    private static final Color BORDER_COLOR = new Color(30, 80, 30);
    private static final Color ACCENT_COLOR = new Color(50, 255, 50);
    private static final Color TEXT_COLOR = new Color(200, 255, 200);
    
    public CategoryPanel(Category category, int x, int y, int width) {
        this.category = category;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = HEADER_HEIGHT;
        
        int offset = HEADER_HEIGHT;
        for (Module module : UraniumClient.getInstance().getModuleManager().getModulesByCategory(category)) {
            moduleButtons.add(new ModuleButton(this, module, offset));
            offset += MODULE_HEIGHT;
        }
        this.height = offset;
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        updateAnimations(mouseX, mouseY, delta);
        
        // Panel glow
        RenderUtils.renderGlow(context.getMatrices(), 
            new Color(50, 255, 50, (int)(20 * hoverAnimation)), 
            x + width / 2, y + 20, 150);
        
        // Panel background
        RenderUtils.renderRoundedQuad(context.getMatrices(), BG_COLOR,
            x, y, x + width, y + (int)(height * expandAnimation), 12, 12, 12, 12, 20);
        RenderUtils.renderOuterGlow(context.getMatrices(), ACCENT_COLOR,
            x, y, x + width, y + (int)(height * expandAnimation), 12, 2);
        
        // Header
        renderHeader(context, mouseX, mouseY);
        
        // Modules
        if (expanded) {
            int renderY = y + HEADER_HEIGHT;
            for (ModuleButton button : moduleButtons) {
                button.render(context, mouseX, mouseY, delta, renderY);
                renderY += MODULE_HEIGHT;
            }
        }
    }

    private void renderHeader(DrawContext context, int mouseX, int mouseY) {
        boolean hovered = mouseX >= x && mouseX <= x + width && 
                         mouseY >= y && mouseY <= y + HEADER_HEIGHT;
        
        // Header background with glow
        Color headerBg = hovered ? new Color(25, 45, 25) : HEADER_COLOR;
        RenderUtils.renderRoundedQuad(context.getMatrices(), headerBg,
            x, y, x + width, y + HEADER_HEIGHT, 12, 12, 0, 0, 20);
        
        if (hovered) {
            RenderUtils.renderOuterGlow(context.getMatrices(), 
                new Color(50, 255, 50, 60), x, y, x + width, y + HEADER_HEIGHT, 12, 4);
        }
        
        // Category name with glow
        float pulse = (float)(Math.sin(System.currentTimeMillis() * 0.003) * 0.2 + 0.8);
        Color textColor = new Color(50, (int)(255 * pulse), 50);
        
        TextRenderer.drawString(category.name().toUpperCase(), context,
            x + 15, y + 15, textColor.getRGB());
        
        // Expand/collapse indicator
        String indicator = expanded ? "▼" : "▶";
        TextRenderer.drawString(indicator, context,
            x + width - 25, y + 15, ACCENT_COLOR.getRGB());
        
        // Animated accent line
        float lineWidth = hoverAnimation * 30;
        context.fill(x, y + HEADER_HEIGHT - 2, 
            x + (int)(width * hoverAnimation), y + HEADER_HEIGHT - 1, 
            ACCENT_COLOR.getRGB());
    }

    private void updateAnimations(int mouseX, int mouseY, float delta) {
        boolean hovered = mouseX >= x && mouseX <= x + width && 
                         mouseY >= y && mouseY <= y + HEADER_HEIGHT;
        
        hoverAnimation += (hovered ? 0.1f : -0.1f) * delta * 2;
        hoverAnimation = Math.max(0, Math.min(1, hoverAnimation));
        
        if (dragging) {
            x += (mouseX - dragX - x) * 0.2f;
            y += (mouseY - dragY - y) * 0.2f;
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (mouseX >= x && mouseX <= x + width && 
            mouseY >= y && mouseY <= y + HEADER_HEIGHT) {
            if (button == 0) {
                dragging = true;
                dragX = (int)mouseX - x;
                dragY = (int)mouseY - y;
            } else if (button == 1) {
                expanded = !expanded;
            }
            return true;
        }
        
        if (expanded) {
            int buttonY = y + HEADER_HEIGHT;
            for (ModuleButton button1 : moduleButtons) {
                if (button1.mouseClicked(mouseX, mouseY, button, buttonY)) {
                    return true;
                }
                buttonY += MODULE_HEIGHT;
            }
        }
        return false;
    }

    public void mouseReleased(double mouseX, double mouseY, int button) {
        dragging = false;
        for (ModuleButton button1 : moduleButtons) {
            button1.mouseReleased(mouseX, mouseY, button);
        }
    }

    public void mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (dragging && button == 0) {
            x = (int)mouseX - dragX;
            y = (int)mouseY - dragY;
        }
    }

    public void updateAnimation(float delta) {
        for (ModuleButton button : moduleButtons) {
            button.updateAnimation(delta);
        }
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
}
