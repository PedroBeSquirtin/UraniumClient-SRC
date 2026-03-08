package com.uranium.utils;

import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.Set;

public final class EnchantmentUtil {
    
    public static boolean hasEnchantment(ItemStack itemStack, RegistryKey<?> registryKey) {
        if (itemStack.isEmpty()) {
            return false;
        }
        Object2IntArrayMap<?> enchantmentMap = new Object2IntArrayMap<>();
        populateEnchantmentMap(itemStack, enchantmentMap);
        return containsEnchantment(enchantmentMap, registryKey);
    }

    private static boolean containsEnchantment(Object2IntMap<?> enchantmentMap, RegistryKey<?> registryKey) {
        for (Object enchantment : enchantmentMap.keySet()) {
            if (((RegistryEntry) enchantment).matchesKey(registryKey)) {
                return true;
            }
        }
        return false;
    }

    public static void populateEnchantmentMap(ItemStack itemStack, Object2IntMap enchantmentMap) {
        enchantmentMap.clear();
        if (!itemStack.isEmpty()) {
            Set<?> enchantments;
            if (itemStack.getItem() == Items.ENCHANTED_BOOK) {
                enchantments = itemStack.get(DataComponentTypes.STORED_ENCHANTMENTS).getEnchantmentEntries();
            } else {
                enchantments = itemStack.getEnchantments().getEnchantmentEntries();
            }
            for (Object enchantmentEntry : enchantments) {
                enchantmentMap.put(
                    ((Object2IntMap.Entry<?>) enchantmentEntry).getKey(), 
                    ((Object2IntMap.Entry<?>) enchantmentEntry).getIntValue()
                );
            }
        }
    }
}
