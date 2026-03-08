package com.uranium.gui;

public class Animation {
    private double value;
    private double target;
    private double speed = 0.1;
    
    public Animation(double initial) {
        this.value = initial;
        this.target = initial;
    }
    
    public void update(float delta) {
        value += (target - value) * speed * delta * 60;
        if (Math.abs(target - value) < 0.001) {
            value = target;
        }
    }
    
    public void setTarget(double target) {
        this.target = target;
    }
    
    public double getValue() {
        return value;
    }
    
    public void setSpeed(double speed) {
        this.speed = speed;
    }
}
