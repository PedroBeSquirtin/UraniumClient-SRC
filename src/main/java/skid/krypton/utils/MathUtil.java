package com.uranium.utils;

public final class MathUtil {
    
    public static double roundToNearest(double value, double step) {
        return step * Math.round(value / step);
    }

    public static double smoothStep(double factor, double start, double end) {
        double clamped = Math.max(0.0, Math.min(1.0, factor));
        return start + (end - start) * (clamped * clamped * (3.0 - 2.0 * clamped));
    }

    public static double approachValue(float speed, double current, double target) {
        double step = Math.ceil(Math.abs(target - current) * speed);
        if (current < target) {
            return Math.min(current + step, target);
        }
        return Math.max(current - step, target);
    }

    public static double linearInterpolate(double factor, double start, double end) {
        return start + (end - start) * factor;
    }

    public static double exponentialInterpolate(double start, double end, double base, double exponent) {
        return linearInterpolate(1.0f - (float) Math.pow(base, exponent), start, end);
    }

    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(value, max));
    }

    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(value, max));
    }
    
    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(value, max));
    }
    
    public static double map(double value, double start1, double end1, double start2, double end2) {
        return start2 + (end2 - start2) * ((value - start1) / (end1 - start1));
    }
    
    public static double getDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }
    
    public static double getDistance3D(double x1, double y1, double z1, double x2, double y2, double z2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2) + Math.pow(z2 - z1, 2));
    }
}
