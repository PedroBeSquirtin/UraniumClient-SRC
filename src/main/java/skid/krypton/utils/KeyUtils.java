package com.uranium.utils;

import org.apache.commons.lang3.StringUtils;
import org.lwjgl.glfw.GLFW;
import com.uranium.UraniumClient;

public final class KeyUtils {

    public static CharSequence getKeyName(int key) {
        switch (key) {
            case -1: return EncryptedString.of("None");
            case 0: return EncryptedString.of("LMB");
            case 1: return EncryptedString.of("RMB");
            case 2: return EncryptedString.of("MMB");
            case 32: return EncryptedString.of("Space");
            case 39: return EncryptedString.of("Apostrophe");
            case 44: return EncryptedString.of("Comma");
            case 59: return EncryptedString.of("Semicolon");
            case 61: return EncryptedString.of("Equals");
            case 91: return EncryptedString.of("Left Bracket");
            case 92: return EncryptedString.of("Backslash");
            case 93: return EncryptedString.of("Right Bracket");
            case 96: return EncryptedString.of("Grave Accent");
            case 161: return EncryptedString.of("World 1");
            case 162: return EncryptedString.of("World 2");
            case 256: return EncryptedString.of("Escape");
            case 257: return EncryptedString.of("Enter");
            case 258: return EncryptedString.of("Tab");
            case 259: return EncryptedString.of("Backspace");
            case 260: return EncryptedString.of("Insert");
            case 261: return EncryptedString.of("Delete");
            case 262: return EncryptedString.of("Right");
            case 263: return EncryptedString.of("Left");
            case 264: return EncryptedString.of("Down");
            case 265: return EncryptedString.of("Up");
            case 266: return EncryptedString.of("Page Up");
            case 267: return EncryptedString.of("Page Down");
            case 268: return EncryptedString.of("Home");
            case 269: return EncryptedString.of("End");
            case 280: return EncryptedString.of("Caps Lock");
            case 281: return EncryptedString.of("Scroll Lock");
            case 282: return EncryptedString.of("Num Lock");
            case 283: return EncryptedString.of("Print Screen");
            case 284: return EncryptedString.of("Pause");
            case 290: return EncryptedString.of("F1");
            case 291: return EncryptedString.of("F2");
            case 292: return EncryptedString.of("F3");
            case 293: return EncryptedString.of("F4");
            case 294: return EncryptedString.of("F5");
            case 295: return EncryptedString.of("F6");
            case 296: return EncryptedString.of("F7");
            case 297: return EncryptedString.of("F8");
            case 298: return EncryptedString.of("F9");
            case 299: return EncryptedString.of("F10");
            case 300: return EncryptedString.of("F11");
            case 301: return EncryptedString.of("F12");
            case 335: return EncryptedString.of("Numpad Enter");
            case 340: return EncryptedString.of("Left Shift");
            case 341: return EncryptedString.of("Left Control");
            case 342: return EncryptedString.of("Left Alt");
            case 343: return EncryptedString.of("Left Super");
            case 344: return EncryptedString.of("Right Shift");
            case 345: return EncryptedString.of("Right Control");
            case 346: return EncryptedString.of("Right Alt");
            case 347: return EncryptedString.of("Right Super");
            case 348: return EncryptedString.of("Menu");
            default:
                if (key >= 48 && key <= 57) {
                    return EncryptedString.of(String.valueOf((char) key));
                }
                if (key >= 65 && key <= 90) {
                    return EncryptedString.of(String.valueOf((char) key));
                }
                if (key >= 320 && key <= 329) {
                    return EncryptedString.of("F" + (key - 319));
                }
        }
        String keyName = GLFW.glfwGetKeyName(key, 0);
        if (keyName == null) {
            return EncryptedString.of("Key " + key);
        }
        return EncryptedString.of(StringUtils.capitalize(keyName));
    }

    public static boolean isKeyPressed(int key) {
        if (key <= 2) {
            return GLFW.glfwGetMouseButton(UraniumClient.mc.getWindow().getHandle(), key) == 1;
        }
        if (key <= 0) return false;
        return GLFW.glfwGetKey(UraniumClient.mc.getWindow().getHandle(), key) == 1;
    }
    
    public static boolean isKeyDown(int key) {
        return isKeyPressed(key);
    }
}
