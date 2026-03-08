package com.uranium.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;

import java.awt.*;

public class RenderUtils {
    
    public static void renderRoundedQuad(MatrixStack matrices, Color color,
                                         double x1, double y1, double x2, double y2,
                                         double tl, double tr, double bl, double br, double segments) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        float r = color.getRed() / 255f;
        float g = color.getGreen() / 255f;
        float b = color.getBlue() / 255f;
        float a = color.getAlpha() / 255f;
        
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        
        BufferBuilder builder = Tessellator.getInstance().begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
        
        double[][] corners = {
            {x2 - br, y2 - br, br}, // bottom right
            {x1 + bl, y2 - bl, bl}, // bottom left
            {x1 + tl, y1 + tl, tl}, // top left
            {x2 - tr, y1 + tr, tr}  // top right
        };
        
        for (int i = 0; i < 4; i++) {
            double[] corner = corners[i];
            double radius = corner[2];
            
            for (double angle = i * 90.0; angle < 90.0 + i * 90.0; angle += 90.0 / segments) {
                double rad = Math.toRadians(angle);
                double sin = Math.sin(rad);
                double cos = Math.cos(rad);
                
                builder.vertex(matrix, (float)(corner[0] + sin * radius), (float)(corner[1] + cos * radius), 0)
                       .color(r, g, b, a);
            }
        }
        
        BufferRenderer.drawWithGlobalProgram(builder.end());
        RenderSystem.disableBlend();
    }

    public static void renderGlow(MatrixStack matrices, Color color, int centerX, int centerY, int radius) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        float r = color.getRed() / 255f;
        float g = color.getGreen() / 255f;
        float b = color.getBlue() / 255f;
        float a = color.getAlpha() / 255f;
        
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        
        BufferBuilder builder = Tessellator.getInstance().begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
        
        builder.vertex(matrix, centerX, centerY, 0).color(r, g, b, a);
        
        for (int i = 0; i <= 36; i++) {
            double angle = Math.toRadians(i * 10);
            int x = (int)(centerX + Math.cos(angle) * radius);
            int y = (int)(centerY + Math.sin(angle) * radius);
            builder.vertex(matrix, x, y, 0).color(r, g, b, 0);
        }
        
        BufferRenderer.drawWithGlobalProgram(builder.end());
        RenderSystem.disableBlend();
    }

    public static void renderOuterGlow(MatrixStack matrices, Color color,
                                       double x1, double y1, double x2, double y2,
                                       double radius, double thickness) {
        // Inner glow
        renderRoundedQuad(matrices, new Color(color.getRed(), color.getGreen(), color.getBlue(), 40),
            x1 - thickness, y1 - thickness, x2 + thickness, y2 + thickness,
            radius + thickness, radius + thickness, radius + thickness, radius + thickness, 20);
        
        // Outer glow
        renderRoundedQuad(matrices, new Color(color.getRed(), color.getGreen(), color.getBlue(), 20),
            x1 - thickness * 2, y1 - thickness * 2, x2 + thickness * 2, y2 + thickness * 2,
            radius + thickness * 2, radius + thickness * 2, radius + thickness * 2, radius + thickness * 2, 20);
    }

    public static void renderCircle(MatrixStack matrices, Color color,
                                    double centerX, double centerY, double radius, int segments) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        float r = color.getRed() / 255f;
        float g = color.getGreen() / 255f;
        float b = color.getBlue() / 255f;
        float a = color.getAlpha() / 255f;
        
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        
        BufferBuilder builder = Tessellator.getInstance().begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
        
        builder.vertex(matrix, (float)centerX, (float)centerY, 0).color(r, g, b, a);
        
        for (int i = 0; i <= segments; i++) {
            double angle = Math.toRadians(i * (360.0 / segments));
            double x = centerX + Math.sin(angle) * radius;
            double y = centerY + Math.cos(angle) * radius;
            builder.vertex(matrix, (float)x, (float)y, 0).color(r, g, b, 0);
        }
        
        BufferRenderer.drawWithGlobalProgram(builder.end());
        RenderSystem.disableBlend();
    }

    public static void renderGradient(MatrixStack matrices,
                                      double x1, double y1, double x2, double y2,
                                      Color color1, Color color2) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        
        BufferBuilder builder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        
        builder.vertex(matrix, (float)x1, (float)y1, 0)
               .color(color1.getRed() / 255f, color1.getGreen() / 255f, color1.getBlue() / 255f, color1.getAlpha() / 255f);
        builder.vertex(matrix, (float)x1, (float)y2, 0)
               .color(color1.getRed() / 255f, color1.getGreen() / 255f, color1.getBlue() / 255f, color1.getAlpha() / 255f);
        builder.vertex(matrix, (float)x2, (float)y2, 0)
               .color(color2.getRed() / 255f, color2.getGreen() / 255f, color2.getBlue() / 255f, color2.getAlpha() / 255f);
        builder.vertex(matrix, (float)x2, (float)y1, 0)
               .color(color2.getRed() / 255f, color2.getGreen() / 255f, color2.getBlue() / 255f, color2.getAlpha() / 255f);
        
        BufferRenderer.drawWithGlobalProgram(builder.end());
        RenderSystem.disableBlend();
    }
}
