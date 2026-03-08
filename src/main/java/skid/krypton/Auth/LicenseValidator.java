package com.uranium.auth;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class LicenseValidator {
    
    private static final String AUTH_SERVER = "https://your-auth-server.com/api/";
    private static final byte[] SECRET_KEY = "URANIUM_SECRET_2026".getBytes();
    
    public enum LicenseStatus {
        VALID, INVALID, EXPIRED, HARDWARE_MISMATCH, OFFLINE
    }
    
    public static LicenseStatus validateLicense(String licenseKey) {
        try {
            String hwid = HardwareAuth.generateHWID();
            String encryptedData = encryptData(licenseKey + ":" + hwid);
            
            URL url = new URL(AUTH_SERVER + "verify");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            
            String jsonInput = "{\"license\":\"" + licenseKey + 
                               "\",\"hwid\":\"" + hwid + 
                               "\",\"data\":\"" + encryptedData + "\"}";
            
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInput.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    String response = br.readLine();
                    return parseResponse(response);
                }
            } else if (responseCode == 403) {
                return LicenseStatus.HARDWARE_MISMATCH;
            } else if (responseCode == 404) {
                return LicenseStatus.INVALID;
            }
            
        } catch (Exception e) {
            // Offline fallback - check local cache
            return checkOfflineCache(licenseKey);
        }
        return LicenseStatus.INVALID;
    }
    
    private static String encryptData(String data) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY, "AES");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] encrypted = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            return data;
        }
    }
    
    private static LicenseStatus parseResponse(String response) {
        if (response.contains("\"status\":\"valid\"")) {
            return LicenseStatus.VALID;
        } else if (response.contains("\"status\":\"expired\"")) {
            return LicenseStatus.EXPIRED;
        }
        return LicenseStatus.INVALID;
    }
    
    private static LicenseStatus checkOfflineCache(String licenseKey) {
        // Implement offline cache validation
        File cacheFile = new File("uranium/license.cache");
        if (cacheFile.exists()) {
            // Check if cache is less than 7 days old
            if (System.currentTimeMillis() - cacheFile.lastModified() < 604800000) {
                return LicenseStatus.VALID;
            }
        }
        return LicenseStatus.OFFLINE;
    }
}
