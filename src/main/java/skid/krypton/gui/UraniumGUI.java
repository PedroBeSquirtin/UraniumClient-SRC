package com.uranium.gui;

import com.uranium.UraniumClient;
import com.uranium.module.Category;
import com.uranium.module.Module;
import com.uranium.utils.RenderUtils;
import com.uranium.utils.TextRenderer;
import com.uranium.utils.animation.Animation;
import com.uranium.utils.animation.Easing;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class UraniumGUI extends Screen {
    private static UraniumGUI INSTANCE;
    private final List<CategoryPanel> panels = new ArrayList<>();
    private final List<Notification> notifications = new ArrayList<>();
    private String searchQuery = "";
    private boolean searchFocused = false;
    private long lastSearchClick = 0;
    
    // Glowing Green Theme Colors
    private static final Color BG_PRIMARY = new Color(10, 12, 10, 240);
    private static final Color BG_SECONDARY = new Color(15, 20, 15, 230);
    private static final Color BG_TERTIARY = new Color(20, 30, 20, 220);
    private static final Color ACCENT_PRIMARY = new Color(50, 255, 50);
    private static final Color ACCENT_SECONDARY = new Color(100, 255, 100);
    private static final Color ACCENT_GLOW = new Color(50, 255, 50, 80);
    private static final Color ACCENT_PULSE = new Color(30, 255, 30, 40);
    private static final Color TEXT_PRIMARY = new Color(220, 255, 220);
    private static final Color TEXT_SECONDARY = new Color(160, 200, 160);
    private static final Color TEXT_DARK = new Color(80, 120, 80);
    private static final Color BORDER_COLOR = new Color(30, 80, 30);
    private static final Color GLOW_COLOR = new Color(50, 255, 50, 40);
    
    // Layout
    private static final int PANEL_WIDTH = 220;
    private static final int PANEL_SPACING = 15;
    private static final int HEADER_HEIGHT = 45;
    private static final int ANIMATION_SPEED = 200; // ms

    public UraniumGUI() {
        super(Text.literal("Uranium Client"));
        INSTANCE = this;
        initPanels();
    }

    private void initPanels() {
        panels.clear();
        int x = 20;
        for (Category category : Category.values()) {
            panels.add(new CategoryPanel(category, x, 20, PANEL_WIDTH));
            x += PANEL_WIDTH + PANEL_SPACING;
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        renderSearchBar(context, mouseX, mouseY);
        renderGlowEffects(context, mouseX, mouseY);
        
        // Update and render panels
        for (CategoryPanel panel : panels) {
            panel.updateAnimation(delta);
            panel.render(context, mouseX, mouseY, delta);
        }
        
        // Render notifications
        renderNotifications(context, delta);
        
        // Update notification animations
        notifications.removeIf(n -> n.animation.getValue() <= 0 && n.life <= 0);
        notifications.forEach(n -> n.update(delta));
        
        // Render version watermark
        String version = "URANIUM v1.0.0";
        TextRenderer.drawString(version, context, 
            client.getWindow().getScaledWidth() - TextRenderer.getWidth(version) - 10,
            client.getWindow().getScaledHeight() - 20,
            ACCENT_PRIMARY.getRGB());
    }

    private void renderBackground(DrawContext context) {
        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();
        
        // Dark green gradient overlay
        RenderUtils.renderGradient(context.getMatrices(), 
            0, 0, width, height,
            new Color(0, 20, 0, 200),
            new Color(5, 25, 5, 200));
        
        // Animated glowing particles
        long time = System.currentTimeMillis();
        for (int i = 0; i < 5; i++) {
            int x = (int)((Math.sin(time * 0.001 + i) * 0.5 + 0.5) * width);
            int y = (int)((Math.cos(time * 0.001 + i * 2) * 0.5 + 0.5) * height);
            int size = 100 + (int)(Math.sin(time * 0.002 + i) * 30);
            RenderUtils.renderGlow(context.getMatrices(), 
                new Color(50, 255, 50, 15), x, y, size);
        }
    }

    private void renderGlowEffects(DrawContext context, int mouseX, int mouseY) {
        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();
        
        // Mouse glow
        RenderUtils.renderGlow(context.getMatrices(), 
            new Color(50, 255, 50, 30), mouseX, mouseY, 150);
        
        // Corner glows
        RenderUtils.renderGlow(context.getMatrices(), ACCENT_GLOW, 0, 0, 200);
        RenderUtils.renderGlow(context.getMatrices(), ACCENT_GLOW, width, 0, 200);
        RenderUtils.renderGlow(context.getMatrices(), ACCENT_GLOW, 0, height, 200);
        RenderUtils.renderGlow(context.getMatrices(), ACCENT_GLOW, width, height, 200);
    }

    private void renderSearchBar(DrawContext context, int mouseX, int mouseY) {
        int width = client.getWindow().getScaledWidth();
        int centerX = width / 2;
        int searchWidth = 350;
        int searchX = centerX - searchWidth / 2;
        int searchY = 15;
        
        boolean hovered = mouseX >= searchX && mouseX <= searchX + searchWidth && 
                         mouseY >= searchY && mouseY <= searchY + 40;
        
        // Background with glow
        Color bgColor = hovered ? new Color(20, 35, 20) : new Color(15, 25, 15);
        
        // Outer glow
        RenderUtils.renderGlow(context.getMatrices(), 
            new Color(50, 255, 50, hovered ? 60 : 30), 
            searchX + searchWidth / 2, searchY + 20, 180);
        
        // Main box
        RenderUtils.renderRoundedQuad(context.getMatrices(), bgColor,
            searchX, searchY, searchX + searchWidth, searchY + 40, 12, 12, 12, 12, 20);
        RenderUtils.renderOuterGlow(context.getMatrices(), ACCENT_PRIMARY,
            searchX, searchY, searchX + searchWidth, searchY + 40, 12, 3);
        
        // Search icon (pulsing)
        float pulse = (float)(Math.sin(System.currentTimeMillis() * 0.005) * 0.3 + 0.7);
        RenderUtils.renderCircle(context.getMatrices(), 
            new Color(50, (int)(255 * pulse), 50), searchX + 25, searchY + 20, 6, 24);
        context.fill(searchX + 28, searchY + 23, searchX + 32, searchY + 27, 
            new Color(50, (int)(255 * pulse), 50).getRGB());
        
        String displayText = searchQuery.isEmpty() ? "Search modules..." : searchQuery;
        int textColor = searchQuery.isEmpty() ? TEXT_SECONDARY.getRGB() : TEXT_PRIMARY.getRGB();
        
        TextRenderer.drawString(displayText, context, searchX + 45, searchY + 14, textColor);
        
        if (searchFocused && (System.currentTimeMillis() % 1000 < 500)) {
            int cursorX = searchX + 45 + TextRenderer.getWidth(searchQuery);
            context.fill(cursorX, searchY + 12, cursorX + 2, searchY + 28, TEXT_PRIMARY.getRGB());
        }
    }

    private void renderNotifications(DrawContext context, float delta) {
        int y = 70;
        for (Notification notification : notifications) {
            notification.render(context, y, delta);
            y += 50;
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Handle search bar click
        int width = client.getWindow().getScaledWidth();
        int centerX = width / 2;
        int searchWidth = 350;
        int searchX = centerX - searchWidth / 2;
        int searchY = 15;
        
        if (mouseX >= searchX && mouseX <= searchX + searchWidth && 
            mouseY >= searchY && mouseY <= searchY + 40) {
            searchFocused = true;
            lastSearchClick = System.currentTimeMillis();
            return true;
        } else {
            searchFocused = false;
        }
        
        // Pass click to panels
        for (CategoryPanel panel : panels) {
            if (panel.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (CategoryPanel panel : panels) {
            panel.mouseReleased(mouseX, mouseY, button);
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        for (CategoryPanel panel : panels) {
            panel.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (searchFocused) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                searchFocused = false;
                return true;
            } else if (keyCode == GLFW.GLFW_KEY_BACKSPACE && !searchQuery.isEmpty()) {
                searchQuery = searchQuery.substring(0, searchQuery.length() - 1);
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (searchFocused && (Character.isLetterOrDigit(chr) || chr == ' ')) {
            searchQuery += chr;
            return true;
        }
        return super.charTyped(chr, modifiers);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void close() {
        UraniumClient.getInstance().getModuleManager().getModuleByClass(Uranium.class).disable();
        super.close();
    }

    public static UraniumGUI getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new UraniumGUI();
        }
        return INSTANCE;
    }
}
