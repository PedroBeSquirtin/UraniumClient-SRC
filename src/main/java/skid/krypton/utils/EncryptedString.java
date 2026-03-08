package com.uranium.utils;

import org.jetbrains.annotations.NotNull;

import java.nio.CharBuffer;
import java.security.SecureRandom;
import java.util.Arrays;

public final class EncryptedString implements AutoCloseable, CharSequence {
    private final char[] key;
    private final char[] value;
    private final int length;
    private static final SecureRandom random = new SecureRandom();
    private boolean closed;

    public EncryptedString(String string) {
        this.closed = false;
        if (string == null) {
            throw new IllegalArgumentException("Input string cannot be null");
        }
        this.length = string.length();
        this.key = generateRandomKey(Math.min(this.length, 128));
        this.value = new char[this.length];
        string.getChars(0, this.length, this.value, 0);
        applyXorEncryption(this.value, this.key, 0, this.length);
    }

    public EncryptedString(char[] encrypted, char[] encryptionKey) {
        this.closed = false;
        if (encrypted == null || encryptionKey == null) {
            throw new IllegalArgumentException("Neither encrypted value nor key can be null");
        }
        if (encryptionKey.length == 0) {
            throw new IllegalArgumentException("Encryption key cannot be empty");
        }
        this.length = encrypted.length;
        this.value = Arrays.copyOf(encrypted, encrypted.length);
        this.key = Arrays.copyOf(encryptionKey, encryptionKey.length);
    }

    public static EncryptedString of(String s) {
        return new EncryptedString(s);
    }

    public static EncryptedString of(String encrypted, String key) {
        if (encrypted == null || key == null) {
            throw new IllegalArgumentException("Neither encrypted data nor key can be null");
        }
        return new EncryptedString(encrypted.toCharArray(), key.toCharArray());
    }

    private static char[] generateRandomKey(int length) {
        char[] array = new char[length];
        for (int i = 0; i < length; ++i) {
            array[i] = (char) random.nextInt(65536);
        }
        return array;
    }

    private static void applyXorEncryption(char[] data, char[] key, int offset, int length) {
        for (int i = 0; i < length; ++i) {
            data[offset + i] ^= key[i % key.length];
        }
    }

    @Override
    public int length() {
        checkClosed();
        return this.length;
    }

    @Override
    public char charAt(int index) {
        checkClosed();
        if (index < 0 || index >= this.length) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + this.length);
        }
        return (char) (this.value[index] ^ this.key[index % this.key.length]);
    }

    @NotNull
    @Override
    public CharSequence subSequence(int start, int end) {
        checkClosed();
        if (start < 0 || end > this.length || start > end) {
            throw new IndexOutOfBoundsException("Invalid subsequence range: " + start + " to " + end);
        }
        int subLength = end - start;
        char[] subValue = new char[subLength];
        char[] subKey = new char[subLength];
        
        System.arraycopy(this.value, start, subValue, 0, subLength);
        for (int i = 0; i < subLength; ++i) {
            subKey[i] = this.key[(start + i) % this.key.length];
        }
        
        applyXorEncryption(subValue, this.key, 0, subLength);
        applyXorEncryption(subValue, subKey, 0, subLength);
        
        return new EncryptedString(subValue, subKey);
    }

    @NotNull
    @Override
    public String toString() {
        checkClosed();
        char[] decrypted = new char[this.length];
        for (int i = 0; i < this.length; ++i) {
            decrypted[i] = this.charAt(i);
        }
        String result = new String(decrypted);
        Arrays.fill(decrypted, '\0');
        return result;
    }

    public String decrypt() {
        return this.toString();
    }

    public CharBuffer toCharBuffer() {
        checkClosed();
        CharBuffer buffer = CharBuffer.allocate(this.length);
        for (int i = 0; i < this.length; ++i) {
            buffer.put(i, this.charAt(i));
        }
        buffer.flip();
        return buffer.asReadOnlyBuffer();
    }

    @Override
    public void close() {
        if (!this.closed) {
            Arrays.fill(this.value, '\0');
            Arrays.fill(this.key, '\0');
            this.closed = true;
        }
    }

    private void checkClosed() {
        if (this.closed) {
            throw new IllegalStateException("This EncryptedString has been closed and cannot be used");
        }
    }

    @Override
    public boolean equals(Object obj) {
        checkClosed();
        if (this == obj) return true;
        if (!(obj instanceof CharSequence)) return false;
        
        CharSequence other = (CharSequence) obj;
        if (this.length != other.length()) return false;
        
        for (int i = 0; i < this.length; ++i) {
            if (this.charAt(i) != other.charAt(i)) return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        checkClosed();
        int hash = 0;
        for (int i = 0; i < this.length; ++i) {
            hash = 31 * hash + this.charAt(i);
        }
        return hash;
    }
}
