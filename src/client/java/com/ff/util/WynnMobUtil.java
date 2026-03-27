package com.ff.util;

import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.text.Text;

public class WynnMobUtil {
    public static String getMobName(DisplayEntity.TextDisplayEntity textDisplay) {
        Text text = textDisplay.getText();

        for (Text sibling : text.getSiblings()) {
            String s = sibling.getString();
            if (s != null && !s.isBlank()) {
                int cutoff = s.length();
                for (int i = 0; i < s.length(); i++) {
                    char c = s.charAt(i);
                    if (c >= 0xE000 || c == '\n') {
                        cutoff = i;
                        break;
                    }
                }
                String name = s.substring(0, cutoff).trim();
                if (!name.isEmpty()) return name;
            }
        }

        String plain = text.getString();
        StringBuilder name = new StringBuilder();
        for (char c : plain.toCharArray()) {
            if (c >= 0xE000 || c == '\n') break;
            name.append(c);
        }
        return name.toString().trim();
    }
}
