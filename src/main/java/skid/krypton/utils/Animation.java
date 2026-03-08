package com.uranium.utils;

import com.uranium.module.modules.client.Uranium;

public class Animation {
    private double value;
    private final double end;

    public Animation(double end) {
        this.value = end;
        this.end = end;
    }

    public void animate(double speed, double target) {
        if (Uranium.animationMode.isMode(Uranium.AnimationMode.NORMAL)) {
            this.value = MathUtil.approachValue((float) speed, this.value, target);
        } else if (Uranium.animationMode.isMode(Uranium.AnimationMode.POSITIVE)) {
            this.value = MathUtil.smoothStep(speed, this.value, target);
        } else {
            this.value = target;
        }
    }

    public double getAnimation() {
        return this.value;
    }

    public void setAnimation(double factor) {
        this.value = MathUtil.smoothStep(factor, this.value, this.end);
    }
}
