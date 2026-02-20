package com.ff.util;

import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.List;
import java.util.concurrent.LinkedTransferQueue;

import static com.ff.FluffyFoxClient.MC;

public class InventoryUtil {
    public static void switchToSlot(int slotIndex) {
        if (MC.player != null && (MC.currentScreen == null || MC.currentScreen instanceof ChatScreen)) {
            MC.player.getInventory().setSelectedSlot(slotIndex);
        }
    }

    public static String[] getHotbarItemNames() {
        String[] ret = new String[9];
        if (MC.player == null) { return ret; }
        for (int i = 0; i < 9; i++) {
            ItemStack stack = MC.player.getInventory().getStack(i);
            ret[i] = "";
            if (stack != null) {
                ret[i] = stack.getName().getString();
            }
        }
        return ret;
    }

    public static String[][] getHotbarItemLores() {
        String[][] ret = new String[9][];
        if (MC.player == null) { return ret; }
        for (int i = 0; i < 9; i++) {
            ItemStack stack = MC.player.getInventory().getStack(i);
            if (stack != null) {
                LoreComponent lore = stack.get(DataComponentTypes.LORE);
                if (lore != null) {
                    List<net.minecraft.text.Text> lines = lore.lines();
                    ret[i] = new String[lines.size()];

                    for (int j = 0; j < lines.size(); j++) {
                        ret[i][j] = lines.get(j).getString();
                    }
                }
            }
        }
        return ret;
    }

    public static int getSlotOfItemWithWord(String key) {
        String[] hotbarItemNames = getHotbarItemNames();
        for (int i = 0; i < 9; i++) {
            String[] titleWords = hotbarItemNames[i].split(" ");
            for (String word : titleWords) {
                if (word.equalsIgnoreCase(key)) {
                    return i;
                }
            }
        }
        return -1;
    }
    public static int getSlotOfItemWithString(String key) {
        String[] hotbarItemNames = getHotbarItemNames();
        for (int i = 0; i < 9; i++) {
            if (hotbarItemNames[i].toLowerCase().contains(key.toLowerCase())) return i;
        }
        return -1;
    }
    public static int getSlotOfItemWithLoreString(String key) {
        String[][] hotbarItemLores = getHotbarItemLores();
        for (int i = 0; i < 9; i++) {
            for (String line : hotbarItemLores[i]) {
                if (line.toLowerCase().contains(key.toLowerCase())) return i;
            }
        }
        return -1;
    }

    public static int getSlotOfAnyItemWithFirstWordOf(String[] keys) {
        for (String key : keys) {
            int slot = getSlotOfItemWithWord(key);
            if (slot > -1) {
                return slot;
            }
        }
        return -1;
    }
    public static int getSlotOfAnyItemWithFirstStringOf(String[] keys) {
        for (String key : keys) {
            int slot = getSlotOfItemWithString(key);
            if (slot > -1) {
                return slot;
            }
        }
        return -1;
    }
    public static int getSlotOfAnyItemWithFirstLoreStringOf(String[] keys) {
        for (String key : keys) {
            int slot = getSlotOfItemWithLoreString(key);
            if (slot > -1) {
                return slot;
            }
        }
        return -1;
    }
}