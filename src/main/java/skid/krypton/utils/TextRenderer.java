package com.uranium.utils;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import com.uranium.UraniumClient;
import com.uranium.font.Fonts;
import com.uranium.module.modules.client.Uranium;

public final class TextRenderer {
    
    public static void drawString(CharSequence text, DrawContext context, int x, int y, int color) {
        if (Uranium.useCustomFont.getValue()) {
            Fonts.FONT.drawString(context.getMatrices(), text, x, y, color);
        } else {
            drawVanilla(text, context, x, y, color);
        }
    }

    public static int getWidth(CharSequence text) {
        if (Uranium.useCustomFont.getValue()) {
            return Fonts.FONT.getStringWidth(text);
        }
        return UraniumClient.mc.textRenderer.getWidth(text.toString()) * 2;
    }

    public static void drawCenteredString(CharSequence text, DrawContext context, int x, int y, int color) {
        if (Uranium.useCustomFont.getValue()) {
            Fonts.FONT.drawString(
                context.getMatrices(), 
                text, 
                x - Fonts.FONT.getStringWidth(text) / 2, 
                y, 
                color
            );
        } else {
            drawCenteredVanilla(text, context, x, y, color);
        }
    }

    private static void drawVanilla(CharSequence text, DrawContext context, int x, int y, int color) {
        MatrixStack matrices = context.getMatrices();
        matrices.push();
        matrices.scale(2.0f, 2.0f, 2.0f);
        context.drawText(UraniumClient.mc.textRenderer, text.toString(), x / 2, y / 2, color, false);
        matrices.pop();
    }

    private static void drawCenteredVanilla(CharSequence text, DrawContext context, int x, int y, int color) {
        MatrixStack matrices = context.getMatrices();
        matrices.push();
        matrices.scale(2.0f, 2.0f, 2.0f);
        context.drawText(
            UraniumClient.mc.textRenderer, 
            text.toString(), 
            x / 2 - UraniumClient.mc.textRenderer.getWidth(text.toString()) / 2, 
            y / 2, 
            color, 
            false
        );
        matrices.pop();
    }
    
    public static void drawStringWithShadow(CharSequence text, DrawContext context, int x, int y, int color) {
        drawString(text, context, x + 1, y + 1, 0xAA000000);
        drawString(text, context, x, y, color);
    }
}
