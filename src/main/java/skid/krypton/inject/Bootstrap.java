package com.uranium.inject;

import com.sun.tools.attach.VirtualMachine;
import java.io.File;
import java.lang.management.ManagementFactory;

public class Bootstrap {
    
    public static void inject() {
        try {
            String pid = getPID();
            String agentPath = new File("uranium-agent.jar").getAbsolutePath();
            
            VirtualMachine vm = VirtualMachine.attach(pid);
            vm.loadAgent(agentPath);
            vm.detach();
            
            System.out.println("[Uranium] Injected successfully");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static String getPID() {
        String name = ManagementFactory.getRuntimeMXBean().getName();
        return name.split("@")[0];
    }
    
    // Standalone injector
    public static void main(String[] args) {
        inject();
    }
}
