package com.uranium.utils;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import com.uranium.UraniumClient;
import com.uranium.mixin.ClientPlayerInteractionManagerAccessor;

import java.util.function.Predicate;

public final class InventoryUtil {
    
    public static void swap(int selectedSlot) {
        if (selectedSlot < 0 || selectedSlot > 8) {
            return;
        }
        UraniumClient.mc.player.getInventory().selectedSlot = selectedSlot;
        ((ClientPlayerInteractionManagerAccessor) UraniumClient.mc.interactionManager).syncSlot();
    }

    public static boolean swapStack(Predicate<ItemStack> predicate) {
        PlayerInventory inventory = UraniumClient.mc.player.getInventory();
        for (int i = 0; i < 9; ++i) {
            if (predicate.test(inventory.getStack(i))) {
                inventory.selectedSlot = i;
                return true;
            }
        }
        return false;
    }

    public static boolean swapItem(Predicate<Item> predicate) {
        PlayerInventory inventory = UraniumClient.mc.player.getInventory();
        for (int i = 0; i < 9; ++i) {
            if (predicate.test(inventory.getStack(i).getItem())) {
                inventory.selectedSlot = i;
                return true;
            }
        }
        return false;
    }

    public static boolean swap(Item item) {
        return swapItem(item2 -> item2 == item);
    }

    public static int getSlotCount(Item item) {
        ScreenHandler handler = UraniumClient.mc.player.currentScreenHandler;
        if (handler instanceof GenericContainerScreenHandler) {
            int count = 0;
            int slots = ((GenericContainerScreenHandler) handler).getRows() * 9;
            for (int i = 0; i < slots; ++i) {
                if (handler.getSlot(i).getStack().getItem().equals(item)) {
                    count++;
                }
            }
            return count;
        }
        return 0;
    }
    
    public static int findSlot(Item item) {
        for (int i = 0; i < 9; i++) {
            if (UraniumClient.mc.player.getInventory().getStack(i).isOf(item)) {
                return i;
            }
        }
        return -1;
    }
    
    public static boolean hasItem(Item item) {
        return findSlot(item) != -1;
    }
}
