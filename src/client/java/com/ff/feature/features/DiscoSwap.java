package com.ff.feature.features;

import com.ff.feature.Feature;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

import static com.ff.FluffyFoxClient.MC;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class DiscoSwap extends Feature {
    public static DiscoSwap INSTANCE = new DiscoSwap();
    private boolean isEquipped = false;
    private ItemStack originalChestplate = ItemStack.EMPTY;

    private static int discoSlot = -1;

    public DiscoSwap() { super("discoswap", "ds"); }


    /**
     * - Find disco in inventory, if any. If none is present, return and print message.
     * - If disco is equipped, then check if there is a previously equipped slot.
     * - - If there is no previously equipped slot, return and print message.
     * - - If there is a previously equipped slot, swap
     * - If disco not equipped, find it and switch, set previously equipped slot to the slot it was in.
     */
    public void execute() {
        PlayerEntity player = MC.player;
        if (player == null) return;

        if (!isEquipped) {
            // Only search for the slot when we're about to equip
            if (discoSlot == -1 || !isDiscoverer(player.getInventory().getStack(discoSlot))) {
                discoSlot = findDiscovererSlot(player);
                if (discoSlot == -1) {
                    player.sendMessage(Text.literal("No Discoverer chestplate found!"), false);
                    return;
                }
            }

            ItemStack equipped = player.getEquippedStack(EquipmentSlot.CHEST);
            originalChestplate = equipped.copy();

            ItemStack discoverer = player.getInventory().getStack(discoSlot);
            player.equipStack(EquipmentSlot.CHEST, discoverer.copy());
            player.getInventory().setStack(discoSlot, equipped);

            isEquipped = true;
        } else {
            // Don't re-check discoSlot here — Discoverer is equipped, not in inventory
            ItemStack discoverer = player.getEquippedStack(EquipmentSlot.CHEST).copy();

            player.equipStack(EquipmentSlot.CHEST, originalChestplate.copy());
            player.getInventory().setStack(discoSlot, discoverer);

            isEquipped = false;
        }
    }

    private boolean isDiscoverer(ItemStack stack) {
        return !stack.isEmpty()
                && stack.getItem() == Items.LEATHER_CHESTPLATE
                && stack.getName().getString().contains("Discoverer");
    }
    private int findDiscovererSlot(PlayerEntity player) {
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (isDiscoverer(stack)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> buildCommand(String commandRoot) {
        return literal(commandRoot)
            .then(literal("toggle")
                .executes(ctx -> {
                    toggle();
                    ctx.getSource().sendFeedback(Text.literal("Antiblind: " + (enabled ? "ON" : "OFF")));
                    return 1;
                })
            );
    }
}
