package com.uranium.inject;

import java.lang.instrument.Instrumentation;

public class Agent {
    
    public static void premain(String args, Instrumentation inst) {
        System.out.println("[Uranium] Agent loaded");
        inst.addTransformer(new UraniumTransformer());
    }
    
    public static void agentmain(String args, Instrumentation inst) {
        System.out.println("[Uranium] Agent attached to running JVM");
        inst.addTransformer(new UraniumTransformer(), true);
        try {
            Class[] classes = inst.getAllLoadedClasses();
            for (Class clazz : classes) {
                if (clazz.getName().contains("Minecraft")) {
                    inst.retransformClasses(clazz);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
