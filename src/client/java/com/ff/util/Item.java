package com.ff.util;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;

import java.util.ArrayList;
import java.util.List;

public class Item {
    String name;
    List<Text> lore;

    List<Stat> stats = new ArrayList<>();
    Rarity rarity;

    boolean tradable = true;
    boolean questItem = false;

    public Item(ItemStack stack) {
        name = stack.getName().getString();
        LoreComponent lore = stack.get(DataComponentTypes.LORE);
        if (lore != null) {
            List<Text> lines = lore.lines();
            this.lore = lines;
            int rarityIndex = -1;
            int emptyLines = 0;
            String loreSection = "Base Stats";
            for (int i = 0; i < lines.size(); i++) {
                Text line = lines.get(i);
                String string = line.getString();

                if (string.contains("Mythic Item")) {
                    rarityIndex = i;
                    rarity = Rarity.MYTHIC;
                    break;
                } else if (string.contains("Crafted ")) {
                    rarityIndex = i;
                    rarity = Rarity.CRAFTED;
                    break;
                } else if (string.contains("Legendary Item")) {
                    rarityIndex = i;
                    rarity = Rarity.LEGENDARY;
                    break;
                } else if (string.contains("Rare Item")) {
                    rarityIndex = i;
                    rarity = Rarity.RARE;
                    break;
                } else if (string.contains("Unique Item")) {
                    rarityIndex = i;
                    rarity = Rarity.UNIQUE;
                    break;
                } else if (string.contains("Fabled Item")) {
                    rarityIndex = i;
                    rarity = Rarity.FABLED;
                    break;
                } else if (string.contains(" Set ")) {
                    rarityIndex = i;
                    rarity = Rarity.SET;
                    break;
                } else if (string.contains("Normal Item")) {
                    rarityIndex = i;
                    rarity = Rarity.NORMAL;
                    break;
                }

                if (string.contains(":")) continue;

                boolean percent = string.contains("%");
                if (string.contains(" Walk Speed")) {
                    double value = Double.parseDouble(string.replaceAll("[^\\d.-]", ""));
                    stats.add(new Stat(Stat.StatType.WALK_SPEED, value));
                } else if (string.contains(" Intelligence")) {
                    double value = Double.parseDouble(string.replaceAll("[^\\d.-]", ""));
                    stats.add(new Stat(Stat.StatType.INTELLIGENCE, value));
                } else if (string.contains(" Defence")) { // Â§7î€
                    double value = Double.parseDouble(string.replaceAll("[^\\d.-]", ""));
                    stats.add(new Stat(Stat.StatType.DEFENCE, value));
                } else if (string.contains(" Agility")) {
                    double value = Double.parseDouble(string.replaceAll("[^\\d.-]", ""));
                    stats.add(new Stat(Stat.StatType.AGILITY, value));
                } else if (string.contains(" Strength")) {
                    double value = Double.parseDouble(string.replaceAll("[^\\d.-]", ""));
                    stats.add(new Stat(Stat.StatType.STRENGTH, value));
                } else if (string.contains(" Dexterity")) {
                    double value = Double.parseDouble(string.replaceAll("[^\\d.-]", ""));
                    stats.add(new Stat(Stat.StatType.DEXTERITY, value));
                } else if (string.contains(" Healing Efficiency")) {
                    double value = Double.parseDouble(string.replaceAll("[^\\d.-]", ""));
                    stats.add(new Stat(Stat.StatType.HEALING_EFFICIENCY, value));
                } else if (string.contains(" Health")) {
                    double value = Double.parseDouble(string.replaceAll("[^\\d.-]", ""));
                    stats.add(new Stat(Stat.StatType.HEALTH, value));
                } else if (string.contains(" Health Regen")) {
                    double value = Double.parseDouble(string.replaceAll("[^\\d.-]", ""));
                    stats.add(new Stat(percent ? Stat.StatType.PERCENT_HEALTH_REGEN : Stat.StatType.RAW_HEALTH_REGEN, value));
                } else if (string.contains(" Air Defence")) {
                    double value = Double.parseDouble(string.replaceAll("[^\\d.-]", ""));
                    stats.add(new Stat(Stat.StatType.AIR_DEFENCE, value));
                } else if (string.contains(" Fire Defence")) {
                    double value = Double.parseDouble(string.replaceAll("[^\\d.-]", ""));
                    stats.add(new Stat(Stat.StatType.FIRE_DEFENCE, value));
                } else if (string.contains(" Thunder Defence")) {
                    double value = Double.parseDouble(string.replaceAll("[^\\d.-]", ""));
                    stats.add(new Stat(Stat.StatType.THUNDER_DEFENCE, value));
                } else if (string.contains(" Water Defence")) {
                    double value = Double.parseDouble(string.replaceAll("[^\\d.-]", ""));
                    stats.add(new Stat(Stat.StatType.WATER_DEFENCE, value));
                } else if (string.contains(" Earth Defence")) {
                    double value = Double.parseDouble(string.replaceAll("[^\\d.-]", ""));
                    stats.add(new Stat(Stat.StatType.EARTH_DEFENCE, value));
                } else if (string.contains(" Max Mana")) {
                    double value = Double.parseDouble(string.replaceAll("[^\\d.-]", ""));
                    stats.add(new Stat(Stat.StatType.MAX_MANA, value));
                } else if (string.contains(" Earth Damage")) {
                    double value = Double.parseDouble(string.replaceAll("[^\\d.-]", ""));
                    stats.add(new Stat(percent ? Stat.StatType.PERCENT_EARTH_DAMAGE : Stat.StatType.RAW_EARTH_DAMAGE, value));
                } else if (string.contains(" Water Damage")) {
                    double value = Double.parseDouble(string.replaceAll("[^\\d.-]", ""));
                    stats.add(new Stat(percent ? Stat.StatType.PERCENT_WATER_DAMAGE : Stat.StatType.RAW_WATER_DAMAGE, value));
                } else if (string.contains(" Air Damage")) {
                    double value = Double.parseDouble(string.replaceAll("[^\\d.-]", ""));
                    stats.add(new Stat(percent ? Stat.StatType.PERCENT_AIR_DAMAGE : Stat.StatType.RAW_AIR_DAMAGE, value));
                } else if (string.contains(" Thunder Damage")) {
                    double value = Double.parseDouble(string.replaceAll("[^\\d.-]", ""));
                    stats.add(new Stat(percent ? Stat.StatType.PERCENT_THUNDER_DAMAGE : Stat.StatType.RAW_THUNDER_DAMAGE, value));
                } else if (string.contains(" Fire Damage")) {
                    double value = Double.parseDouble(string.replaceAll("[^\\d.-]", ""));
                    stats.add(new Stat(percent ? Stat.StatType.PERCENT_FIRE_DAMAGE : Stat.StatType.RAW_FIRE_DAMAGE, value));
                } else if (string.contains(" Spell Damage")) {
                    double value = Double.parseDouble(string.replaceAll("[^\\d.-]", ""));
                    stats.add(new Stat(percent ? Stat.StatType.PERCENT_SPELL_DAMAGE : Stat.StatType.RAW_SPELL_DAMAGE, value));
                } else if (string.contains(" tier Attack Speed")) {
                    double value = Double.parseDouble(string.replaceAll("[^\\d.-]", ""));
                    stats.add(new Stat(Stat.StatType.ATTACK_SPEED, value));
                } else if (string.contains(" Critical Damage Bonus")) {
                    double value = Double.parseDouble(string.replaceAll("[^\\d.-]", ""));
                    stats.add(new Stat(Stat.StatType.CRITICAL_DAMAGE_BONUS, value));
                } else if (string.contains(" XP Bonus")) {
                    double value = Double.parseDouble(string.replaceAll("[^\\d.-]", ""));
                    stats.add(new Stat(Stat.StatType.XP_BONUS, value));
                } else if (string.contains(" Loot Bonus")) {
                    double value = Double.parseDouble(string.replaceAll("[^\\d.-]", ""));
                    stats.add(new Stat(Stat.StatType.LOOT_BONUS, value));
                } else if (string.contains(" Exploding")) {
                    double value = Double.parseDouble(string.replaceAll("[^\\d.-]", ""));
                    stats.add(new Stat(Stat.StatType.EXPLODING, value));
                } else if (string.contains(" Exploding")) {
                    double value = Double.parseDouble(string.replaceAll("[^\\d.-]", ""));
                    stats.add(new Stat(Stat.StatType.EXPLODING, value));
                } else if (string.contains("Fire Main Attack Damage")) {
                    double value = Double.parseDouble(string.replaceAll("[^\\d.-]", ""));
                    stats.add(new Stat(percent ? Stat.StatType.PERCENT_MAIN_ATTACK_FIRE_DAMAGE : Stat.StatType.RAW_MAIN_ATTACK_FIRE_DAMAGE, value));
                } else if (string.contains("Water Main Attack Damage")) {
                    double value = Double.parseDouble(string.replaceAll("[^\\d.-]", ""));
                    stats.add(new Stat(percent ? Stat.StatType.PERCENT_MAIN_ATTACK_WATER_DAMAGE : Stat.StatType.RAW_MAIN_ATTACK_WATER_DAMAGE, value));
                } else if (string.contains("Thunder Main Attack Damage")) {
                    double value = Double.parseDouble(string.replaceAll("[^\\d.-]", ""));
                    stats.add(new Stat(percent ? Stat.StatType.PERCENT_MAIN_ATTACK_THUNDER_DAMAGE : Stat.StatType.RAW_MAIN_ATTACK_THUNDER_DAMAGE, value));
                } else if (string.contains("Earth Main Attack Damage")) {
                    double value = Double.parseDouble(string.replaceAll("[^\\d.-]", ""));
                    stats.add(new Stat(percent ? Stat.StatType.PERCENT_MAIN_ATTACK_EARTH_DAMAGE : Stat.StatType.RAW_MAIN_ATTACK_EARTH_DAMAGE, value));
                } else if (string.contains("Air Main Attack Damage")) {
                    double value = Double.parseDouble(string.replaceAll("[^\\d.-]", ""));
                    stats.add(new Stat(percent ? Stat.StatType.PERCENT_MAIN_ATTACK_AIR_DAMAGE : Stat.StatType.RAW_MAIN_ATTACK_AIR_DAMAGE, value));
                } else if (string.contains("Main Attack Damage")) {
                    double value = Double.parseDouble(string.replaceAll("[^\\d.-]", ""));
                    stats.add(new Stat(percent ? Stat.StatType.PERCENT_MAIN_ATTACK_DAMAGE : Stat.StatType.RAW_MAIN_ATTACK_DAMAGE, value));
                } else if (string.contains("Elemental Defence")) {
                    double value = Double.parseDouble(string.replaceAll("[^\\d.-]", ""));
                    stats.add(new Stat(Stat.StatType.ELEMENTAL_DEFENCE, value));
                }
            }
            if (rarityIndex == lines.size() - 1) return;
            Text next = lines.get(rarityIndex + 1);
            String nextString = next.getString();
            if (nextString.toLowerCase().contains("untradable item")) {
                tradable = false;
                return;
            } else if (nextString.toLowerCase().contains("quest item")) {
                questItem = true;
                tradable = false;
                return;
            }
            TextColor nextColor = next.getStyle().getColor();
            if (nextColor != null && nextColor.getHexCode().equals("#555555")) return;

            rarity = Rarity.NONE;
            stats.clear();
        }
    }

    @Override
    public String toString() {
        return "Item{" +
            "\nname=" + name +
            ",\nlore=" + lore +
            ",\nstats=" + stats +
            ",\nrarity=" + rarity +
            ",\ntradable=" + tradable +
            ",\nquestItem=" + questItem +
            '}';
    }

    public enum Rarity {
        UNIQUE,
        RARE,
        LEGENDARY,
        FABLED,
        MYTHIC,
        SET,
        CRAFTED,
        NORMAL,
        NONE
    }
    public static class Stat {
        public enum StatType {
            STRENGTH,
            DEXTERITY,
            INTELLIGENCE,
            DEFENCE,
            AGILITY,

            RAW_DAMAGE,
            RAW_MAIN_ATTACK_DAMAGE,
            RAW_FIRE_DAMAGE,
            RAW_MAIN_ATTACK_FIRE_DAMAGE,
            RAW_AIR_DAMAGE,
            RAW_MAIN_ATTACK_AIR_DAMAGE,
            RAW_EARTH_DAMAGE,
            RAW_MAIN_ATTACK_EARTH_DAMAGE,
            RAW_WATER_DAMAGE,
            RAW_MAIN_ATTACK_WATER_DAMAGE,
            RAW_THUNDER_DAMAGE,
            RAW_MAIN_ATTACK_THUNDER_DAMAGE,

            PERCENT_DAMAGE,
            PERCENT_MAIN_ATTACK_DAMAGE,
            PERCENT_FIRE_DAMAGE,
            PERCENT_MAIN_ATTACK_FIRE_DAMAGE,
            PERCENT_AIR_DAMAGE,
            PERCENT_MAIN_ATTACK_AIR_DAMAGE,
            PERCENT_EARTH_DAMAGE,
            PERCENT_MAIN_ATTACK_EARTH_DAMAGE,
            PERCENT_WATER_DAMAGE,
            PERCENT_MAIN_ATTACK_WATER_DAMAGE,
            PERCENT_THUNDER_DAMAGE,
            PERCENT_MAIN_ATTACK_THUNDER_DAMAGE,

            RAW_SPELL_DAMAGE,
            PERCENT_SPELL_DAMAGE,

            ATTACK_SPEED,
            MAIN_ATTACK_RANGE,
            KNOCKBACK,
            CRITICAL_DAMAGE_BONUS,

            HEALTH,
            RAW_HEALTH_REGEN,
            PERCENT_HEALTH_REGEN,
            LIFE_STEAL,
            HEALING_EFFICIENCY,
            MANA_REGEN,
            MANA_STEAL,
            MAX_MANA,

            EARTH_DEFENCE,
            THUNDER_DEFENCE,
            WATER_DEFENCE,
            FIRE_DEFENCE,
            AIR_DEFENCE,
            ELEMENTAL_DEFENCE,

            EXPLODING,
            POISON,
            THORNS,
            REFLECTION,

            WALK_SPEED,
            SPRINT,
            SPRINT_REGEN,
            JUMP_HEIGHT,

            LOOT_BONUS,
            LOOT_QUALITY,
            STEALING,
            XP_BONUS,
            GATHER_XP_BONUS,
            GATHER_SPEED,

            PERCENT_1ST_SPELL_COST,
            PERCENT_2ND_SPELL_COST,
            PERCENT_3RD_SPELL_COST,
            PERCENT_4TH_SPELL_COST,
            RAW_1ST_SPELL_COST,
            RAW_2ND_SPELL_COST,
            RAW_3RD_SPELL_COST,
            RAW_4TH_SPELL_COST,
        }
        StatType type;
        double value;

        public Stat(StatType type, double value) {
            this.type = type;
            this.value = value;
        }

        @Override
        public String toString() {
            return "Stat{"
                + "\ntype=" + type.name()
                + ",\nvalue=" + value
                + "}";
        }
    }
    private Stat.StatType resolveStatType(String s) {
        boolean pct = s.contains("%");
        if (s.contains("Main Attack Range")) return Stat.StatType.MAIN_ATTACK_RANGE;
        if (s.contains("Attack Speed")) return Stat.StatType.ATTACK_SPEED;
        if (s.contains("Knockback")) return Stat.StatType.KNOCKBACK;
        if (s.contains("Critical Damage")) return Stat.StatType.CRITICAL_DAMAGE_BONUS;

        if (s.contains("Health Regen")) return pct ? Stat.StatType.PERCENT_HEALTH_REGEN : Stat.StatType.RAW_HEALTH_REGEN;
        if (s.contains("Life Steal")) return Stat.StatType.LIFE_STEAL;
        if (s.contains("Healing Efficiency")) return Stat.StatType.HEALING_EFFICIENCY;
        if (s.contains("Mana Steal")) return Stat.StatType.MANA_STEAL;
        if (s.contains("Mana Regen")) return Stat.StatType.MANA_REGEN;
        if (s.contains("Max Mana") || s.contains("Mana")) return Stat.StatType.MAX_MANA;
        if (s.contains("Health")) return Stat.StatType.HEALTH;

        if (s.contains("Earth Defence")) return Stat.StatType.EARTH_DEFENCE;
        if (s.contains("Thunder Defence")) return Stat.StatType.THUNDER_DEFENCE;
        if (s.contains("Water Defence")) return Stat.StatType.WATER_DEFENCE;
        if (s.contains("Fire Defence")) return Stat.StatType.FIRE_DEFENCE;
        if (s.contains("Air Defence")) return Stat.StatType.AIR_DEFENCE;
        if (s.contains("Elemental Defence")) return Stat.StatType.ELEMENTAL_DEFENCE;

        if (s.contains("Fire Damage")) return pct ? Stat.StatType.PERCENT_FIRE_DAMAGE : Stat.StatType.RAW_FIRE_DAMAGE;
        if (s.contains("Air Damage")) return pct ? Stat.StatType.PERCENT_AIR_DAMAGE : Stat.StatType.RAW_AIR_DAMAGE;
        if (s.contains("Earth Damage")) return pct ? Stat.StatType.PERCENT_AIR_DAMAGE : Stat.StatType.RAW_AIR_DAMAGE;
        if (s.contains("Water Damage")) return pct ? Stat.StatType.PERCENT_WATER_DAMAGE : Stat.StatType.RAW_WATER_DAMAGE;
        if (s.contains("Thunder Damage")) return pct ? Stat.StatType.PERCENT_THUNDER_DAMAGE : Stat.StatType.RAW_THUNDER_DAMAGE;
        if (s.contains("Damage")) return pct ? Stat.StatType.PERCENT_DAMAGE : Stat.StatType.RAW_DAMAGE;

        if (s.contains("Exploding")) return Stat.StatType.EXPLODING;
        if (s.contains("Poison")) return Stat.StatType.POISON;
        if (s.contains("Thorns")) return Stat.StatType.THORNS;
        if (s.contains("Reflection")) return Stat.StatType.REFLECTION;

        if (s.contains("Sprint Regen")) return Stat.StatType.SPRINT_REGEN;
        if (s.contains("Sprint")) return Stat.StatType.SPRINT;
        if (s.contains("Walk Speed")) return Stat.StatType.WALK_SPEED;
        if (s.contains("Jump Height")) return Stat.StatType.JUMP_HEIGHT;

        if (s.contains("Loot Quality")) return Stat.StatType.LOOT_QUALITY;
        if (s.contains("Loot Bonus")) return Stat.StatType.LOOT_BONUS;
        if (s.contains("Stealing")) return Stat.StatType.STEALING;
        if (s.contains("XP Bonus")) return Stat.StatType.XP_BONUS;
        if (s.contains("Gather XP Bonus")) return Stat.StatType.GATHER_XP_BONUS;
        if (s.contains("Gather Speed")) return Stat.StatType.GATHER_SPEED;

        if (s.contains("1st Spell Cost")) return pct ? Stat.StatType.PERCENT_1ST_SPELL_COST : Stat.StatType.RAW_1ST_SPELL_COST;
        if (s.contains("2nd Spell Cost")) return pct ? Stat.StatType.PERCENT_2ND_SPELL_COST : Stat.StatType.RAW_2ND_SPELL_COST;
        if (s.contains("3rd Spell Cost")) return pct ? Stat.StatType.PERCENT_3RD_SPELL_COST : Stat.StatType.RAW_3RD_SPELL_COST;
        if (s.contains("4th Spell Cost")) return pct ? Stat.StatType.PERCENT_4TH_SPELL_COST : Stat.StatType.RAW_4TH_SPELL_COST;

        return null;
    }
}
