package com.ff.util;

import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Rarity;

import java.util.List;

import static com.ff.FluffyFoxClient.MC;

public class InventoryUtil {
    public static Item.Rarity getRarity(ItemStack stack) {
        if (stack == null) return null;

        Text name = stack.get(DataComponentTypes.CUSTOM_NAME);
        assert name != null;
        Style style = name.getStyle();
        if (style.getColor() == null) return null;

        String color = style.getColor().getHexCode();
        return switch (color) {
            case "#AA00AA" -> Item.Rarity.MYTHIC;
            case "#55FFFF" -> Item.Rarity.LEGENDARY;
            case "#FF55FF" -> Item.Rarity.RARE;
            case "#FFFF55" -> Item.Rarity.UNIQUE;
            case "#55FF55" -> Item.Rarity.SET;
            case "#FF5555" -> Item.Rarity.FABLED;
            case "#00AAAA" -> Item.Rarity.CRAFTED;
            default -> Item.Rarity.NONE;
        };
    }

    public static String getSlotArea(Slot slot, ScreenHandler handler) {
        assert MC.player != null;
        if (slot.inventory == MC.player.getInventory()) {

            int index = slot.getIndex();

            if (index >= 36 && index <= 39) {
                return "Armor";
            }

            if (index == 40) {
                return "Offhand";
            }

            if (index >= 0 && index <= 8) {
                return "Hotbar";
            }

            if (index >= 9 && index <= 35) {
                return "Main Inventory";
            }

            return "Player Inventory";
        }

        return "Container (" + slot.inventory.getClass().getSimpleName() + ")";
    }

    public static void switchToSlot(int slotIndex) {
        if (slotIndex > -1 && MC.player != null && (MC.currentScreen == null || MC.currentScreen instanceof ChatScreen)) {
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