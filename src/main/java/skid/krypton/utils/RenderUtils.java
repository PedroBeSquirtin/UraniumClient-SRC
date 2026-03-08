package com.uranium.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.VertexSorter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import com.uranium.UraniumClient;
import com.uranium.module.modules.client.Uranium;

import java.awt.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class RenderUtils {
    
    public static VertexSorter vertexSorter;
    public static boolean rendering3D = true;

    public static Vec3d getCameraPos() {
        return getCamera().getPos();
    }

    public static Camera getCamera() {
        return UraniumClient.mc.getBlockEntityRenderDispatcher().camera;
    }

    public static double deltaTime() {
        return UraniumClient.mc.getCurrentFps() > 0 ? 1.0 / UraniumClient.mc.getCurrentFps() : 1.0;
    }

    public static float fast(float current, float target, float speed) {
        return (1.0f - MathHelper.clamp((float) (deltaTime() * speed), 0.0f, 1.0f)) * current + 
               MathHelper.clamp((float) (deltaTime() * speed), 0.0f, 1.0f) * target;
    }

    public static Vec3d getPlayerLookVec(PlayerEntity player) {
        float cos = MathHelper.cos(player.getYaw() * 0.017453292f - 3.1415927f);
        float sin = MathHelper.sin(player.getYaw() * 0.017453292f - 3.1415927f);
        float cos2 = MathHelper.cos(player.getPitch() * 0.017453292f);
        return new Vec3d(sin * cos2, MathHelper.sin(player.getPitch() * 0.017453292f), cos * cos2).normalize();
    }

    public static void unscaledProjection() {
        vertexSorter = RenderSystem.getVertexSorting();
        RenderSystem.setProjectionMatrix(
            new Matrix4f().setOrtho(
                0.0f, 
                UraniumClient.mc.getWindow().getFramebufferWidth(),
                UraniumClient.mc.getWindow().getFramebufferHeight(), 
                0.0f, 
                1000.0f, 
                21000.0f
            ), 
            VertexSorter.BY_Z
        );
        rendering3D = false;
    }

    public static void scaledProjection() {
        RenderSystem.setProjectionMatrix(
            new Matrix4f().setOrtho(
                0.0f, 
                UraniumClient.mc.getWindow().getFramebufferWidth() / UraniumClient.mc.getWindow().getScaleFactor(),
                UraniumClient.mc.getWindow().getFramebufferHeight() / UraniumClient.mc.getWindow().getScaleFactor(), 
                0.0f, 
                1000.0f, 
                21000.0f
            ), 
            vertexSorter
        );
        rendering3D = true;
    }

    public static void renderRoundedQuad(MatrixStack matrices, Color color, double x, double y, double width, double height, double radius, double thickness) {
        renderRoundedQuad(matrices, color, x, y, x + width, y + height, radius, radius, radius, radius, thickness);
    }

    public static void renderRoundedQuad(MatrixStack matrices, Color color, double x1, double y1, double x2, double y2, double tl, double tr, double bl, double br, double thickness) {
        int rgb = color.getRGB();
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        
        renderRoundedQuadInternal(
            matrix, 
            (rgb >> 16 & 0xFF) / 255.0f, 
            (rgb >> 8 & 0xFF) / 255.0f, 
            (rgb & 0xFF) / 255.0f, 
            (rgb >> 24 & 0xFF) / 255.0f,
            x1, y1, x2, y2, tl, tr, bl, br, thickness
        );
        
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    private static void renderRoundedQuadInternal(Matrix4f matrix, float r, float g, float b, float a, 
                                                 double x1, double y1, double x2, double y2, 
                                                 double tl, double tr, double bl, double br, double thickness) {
        BufferBuilder builder = Tessellator.getInstance().begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
        
        double[][] corners = new double[][] {
            {x2 - br, y2 - br, br}, // bottom right
            {x1 + bl, y2 - bl, bl}, // bottom left
            {x1 + tl, y1 + tl, tl}, // top left
            {x2 - tr, y1 + tr, tr}  // top right
        };

        for (int i = 0; i < 4; i++) {
            double[] corner = corners[i];
            double radius = corner[2];
            
            for (double angle = i * 90.0; angle < 90.0 + i * 90.0; angle += 90.0 / thickness) {
                double rad = Math.toRadians(angle);
                double sin = Math.sin(rad);
                double cos = Math.cos(rad);
                
                builder.vertex(matrix, (float) (corner[0] + sin * radius), (float) (corner[1] + cos * radius), 0.0f)
                       .color(r, g, b, a);
            }
            double rad = Math.toRadians(90.0 + i * 90.0);
            builder.vertex(matrix, (float) (corner[0] + Math.sin(rad) * radius), (float) (corner[1] + Math.cos(rad) * radius), 0.0f)
                   .color(r, g, b, a);
        }
        
        BufferRenderer.drawWithGlobalProgram(builder.end());
    }

    public static void setScissorRegion(int x, int y, int x2, int y2) {
        double scale = UraniumClient.mc.getWindow().getScaleFactor();
        int scissorY = UraniumClient.mc.currentScreen != null ? 
                      UraniumClient.mc.currentScreen.height - y2 : y2;
        
        GL11.glScissor(
            (int) (x * scale),
            (int) (scissorY * scale),
            (int) ((x2 - x) * scale),
            (int) ((y2 - y) * scale)
        );
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
    }

    public static void renderCircle(MatrixStack matrices, Color color, double x, double y, double radius, int segments) {
        int clamped = MathHelper.clamp(segments, 4, 360);
        int rgb = color.getRGB();
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        
        BufferBuilder builder = Tessellator.getInstance().begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
        
        for (int i = 0; i < 360; i += Math.min(360 / clamped, 360 - i)) {
            double rad = Math.toRadians(i);
            builder.vertex(matrix, (float) (x + Math.sin(rad) * radius), (float) (y + Math.cos(rad) * radius), 0.0f)
                   .color(
                       (rgb >> 16 & 0xFF) / 255.0f,
                       (rgb >> 8 & 0xFF) / 255.0f,
                       (rgb & 0xFF) / 255.0f,
                       (rgb >> 24 & 0xFF) / 255.0f
                   );
        }
        
        BufferRenderer.drawWithGlobalProgram(builder.end());
        RenderSystem.disableBlend();
    }

    public static void renderLine(MatrixStack matrices, Color color, Vec3d start, Vec3d end) {
        matrices.push();
        
        if (Uranium.enableMSAA.getValue()) {
            GL11.glEnable(GL11.GL_POLYGON_SMOOTH);
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        }
        
        GL11.glDepthFunc(GL11.GL_ALWAYS);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableBlend();
        
        BufferBuilder builder = Tessellator.getInstance().begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
        builder.vertex(matrices.peek().getPositionMatrix(), (float) start.x, (float) start.y, (float) start.z)
               .color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
        builder.vertex(matrices.peek().getPositionMatrix(), (float) end.x, (float) end.y, (float) end.z)
               .color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
        BufferRenderer.drawWithGlobalProgram(builder.end());
        
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        RenderSystem.disableBlend();
        
        if (Uranium.enableMSAA.getValue()) {
            GL11.glDisable(GL11.GL_LINE_SMOOTH);
            GL11.glDisable(GL11.GL_POLYGON_SMOOTH);
        }
        
        matrices.pop();
    }

    public static void drawItem(DrawContext context, ItemStack stack, int x, int y, float scale, int z) {
        if (stack.isEmpty()) return;
        
        float scaled = scale / 16.0f;
        MatrixStack matrices = context.getMatrices();
        matrices.push();
        matrices.translate(x, y, z);
        matrices.scale(scaled, scaled, 1.0f);
        context.drawItem(stack, 0, 0);
        matrices.pop();
    }

    public static MatrixStack matrixFrom(double x, double y, double z) {
        MatrixStack matrices = new MatrixStack();
        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw() + 180.0f));
        matrices.translate(x - camera.getPos().x, y - camera.getPos().y, z - camera.getPos().z);
        return matrices;
    }
}
