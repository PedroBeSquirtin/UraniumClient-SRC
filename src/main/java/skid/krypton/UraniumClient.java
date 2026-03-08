package com.uranium;

import com.uranium.auth.LicenseValidator;
import com.uranium.gui.ClickGUI;
import com.uranium.module.ModuleManager;
import com.uranium.utils.*;
import net.minecraft.client.MinecraftClient;

public class UraniumClient {
    
    public static UraniumClient INSTANCE;
    public static MinecraftClient mc;
    
    public ModuleManager moduleManager;
    public ClickGUI clickGUI;
    public ConfigManager configManager;
    public EventManager eventBus;
    
    public String version = "1.0.0";
    public boolean authenticated = false;
    
    public UraniumClient() {
        INSTANCE = this;
        mc = MinecraftClient.getInstance();
        
        // Check license
        if (!checkLicense()) {
            System.err.println("[Uranium] License validation failed");
            return;
        }
        
        this.eventBus = new EventManager();
        this.moduleManager = new ModuleManager();
        this.clickGUI = new ClickGUI();
        this.configManager = new ConfigManager();
        
        this.configManager.load();
        
        System.out.println("[Uranium] Client initialized v" + version);
    }
    
    private boolean checkLicense() {
        String licenseKey = System.getProperty("uranium.license");
        if (licenseKey == null) {
            licenseKey = loadLicenseFromFile();
        }
        
        LicenseValidator.LicenseStatus status = 
            LicenseValidator.validateLicense(licenseKey);
        
        this.authenticated = status == LicenseValidator.LicenseStatus.VALID;
        return this.authenticated;
    }
    
    private String loadLicenseFromFile() {
        // Load from uranium/license.key
        try {
            File file = new File("uranium/license.key");
            if (file.exists()) {
                return new String(java.nio.file.Files.readAllBytes(file.toPath()));
            }
        } catch (Exception e) {}
        return null;
    }
    
    // Called from injected bytecode
    public static void inject() {
        new UraniumClient();
    }
}
