package com.uranium.auth;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Enumeration;

public class HardwareAuth {
    
    // Generate unique hardware fingerprint
    public static String generateHWID() {
        try {
            String hwid = getProcessorID() + 
                         getMotherboardSerial() + 
                         getMACAddress() + 
                         getVolumeSerial();
            
            return hashString(hwid);
        } catch (Exception e) {
            return fallbackHWID();
        }
    }
    
    private static String getProcessorID() {
        try {
            Process process = Runtime.getRuntime().exec(
                System.getProperty("os.name").toLowerCase().contains("windows") ?
                "wmic cpu get processorid" : "dmidecode -t processor | grep ID"
            );
            process.waitFor();
            java.io.BufferedReader reader = new java.io.BufferedReader(
                new java.io.InputStreamReader(process.getInputStream())
            );
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().length() > 0 && !line.contains("ProcessorId")) {
                    return line.trim();
                }
            }
        } catch (Exception ignored) {}
        return "UNKNOWN_CPU";
    }
    
    private static String getMotherboardSerial() {
        try {
            Process process = Runtime.getRuntime().exec(
                "wmic baseboard get serialnumber"
            );
            process.waitFor();
            java.io.BufferedReader reader = new java.io.BufferedReader(
                new java.io.InputStreamReader(process.getInputStream())
            );
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().length() > 0 && !line.contains("SerialNumber")) {
                    return line.trim();
                }
            }
        } catch (Exception ignored) {}
        return "UNKNOWN_MB";
    }
    
    private static String getMACAddress() {
        try {
            Enumeration<NetworkInterface> networks = 
                NetworkInterface.getNetworkInterfaces();
            while (networks.hasMoreElements()) {
                NetworkInterface network = networks.nextElement();
                byte[] mac = network.getHardwareAddress();
                if (mac != null && mac.length > 0) {
                    StringBuilder sb = new StringBuilder();
                    for (byte b : mac) {
                        sb.append(String.format("%02X", b));
                    }
                    return sb.toString();
                }
            }
        } catch (Exception ignored) {}
        return "UNKNOWN_MAC";
    }
    
    private static String getVolumeSerial() {
        try {
            File cDrive = new File("C:");
            return Long.toHexString(cDrive.getTotalSpace()).substring(0, 8);
        } catch (Exception ignored) {}
        return "UNKNOWN_VOL";
    }
    
    private static String hashString(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString().substring(0, 32).toUpperCase();
        } catch (Exception e) {
            return input;
        }
    }
    
    private static String fallbackHWID() {
        return hashString(
            System.getProperty("user.name") + 
            System.getProperty("os.version") + 
            Runtime.getRuntime().availableProcessors()
        );
    }
}
