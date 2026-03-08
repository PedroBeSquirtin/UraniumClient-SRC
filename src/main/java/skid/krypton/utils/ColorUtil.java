package com.uranium.utils;

import java.awt.*;

public final class ColorUtil {
    
    public static Color rainbow(int index, int alpha) {
        Color hsbColor = Color.getHSBColor(
            (System.currentTimeMillis() * 3L + index * 175) % 7200L / 7200.0f, 
            0.6f, 
            1.0f
        );
        return new Color(hsbColor.getRed(), hsbColor.getGreen(), hsbColor.getBlue(), alpha);
    }

    public static Color breathing(Color color, int speed, int offset) {
        float[] hsbvals = new float[3];
        Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsbvals);
        
        float animation = (System.currentTimeMillis() % 2000L / 1000.0f + offset / (float) speed * 2.0f) % 2.0f;
        hsbvals[2] = 0.25f + 0.75f * Math.abs(animation - 1.0f);
        
        int rgb = Color.HSBtoRGB(hsbvals[0], hsbvals[1], hsbvals[2]);
        return new Color(rgb >> 16 & 0xFF, rgb >> 8 & 0xFF, rgb & 0xFF, color.getAlpha());
    }

    public static Color interpolate(float factor, Color start, Color end) {
        return new Color(
            (int) MathUtil.linearInterpolate(factor, start.getRed(), end.getRed()),
            (int) MathUtil.linearInterpolate(factor, start.getGreen(), end.getGreen()),
            (int) MathUtil.linearInterpolate(factor, start.getBlue(), end.getBlue())
        );
    }

    public static Color alphaInterpolate(float factor, int targetAlpha, Color color) {
        return new Color(
            color.getRed(), 
            color.getGreen(), 
            color.getBlue(), 
            (int) MathUtil.linearInterpolate(factor, color.getAlpha(), targetAlpha)
        );
    }

    public static Color blend(Color color1, Color color2, float ratio) {
        int r = (int) (color1.getRed() + ratio * (color2.getRed() - color1.getRed()));
        int g = (int) (color1.getGreen() + ratio * (color2.getGreen() - color1.getGreen()));
        int b = (int) (color1.getBlue() + ratio * (color2.getBlue() - color1.getBlue()));
        int a = (int) (color1.getAlpha() + ratio * (color2.getAlpha() - color1.getAlpha()));
        
        return new Color(
            clamp(r, 0, 255),
            clamp(g, 0, 255),
            clamp(b, 0, 255),
            clamp(a, 0, 255)
        );
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
