package com.uranium.auth;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;

public class Encryption {
    
    private static final String ALGORITHM = "AES";
    private static final byte[] KEY = "UraniumSecureKey".getBytes();
    
    public static String encrypt(String data) {
        try {
            Key key = new SecretKeySpec(KEY, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encrypted = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            return data;
        }
    }
    
    public static String decrypt(String encryptedData) {
        try {
            Key key = new SecretKeySpec(KEY, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decoded = Base64.getDecoder().decode(encryptedData);
            return new String(cipher.doFinal(decoded));
        } catch (Exception e) {
            return encryptedData;
        }
    }
    
    // Runtime string protection
    public static class SecureString {
        private final byte[] encrypted;
        
        public SecureString(String plaintext) {
            this.encrypted = encrypt(plaintext).getBytes();
        }
        
        public String get() {
            return decrypt(new String(encrypted));
        }
        
        @Override
        public String toString() {
            return "[PROTECTED]";
        }
    }
}
