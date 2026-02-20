package com.ff.feature.features;

import com.ff.feature.Feature;
import com.ff.feature.State;
import com.ff.util.InventoryUtil;
import com.ff.util.Item;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import static com.ff.FluffyFoxClient.MC;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;

import net.minecraft.command.CommandSource;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;

public class FindItem extends Feature {
    public static final Feature INSTANCE = new FindItem();

    private static String targetName;
    private static Item.Rarity targetRarity = null;

    private static Vec3d nearestEnderchest = null;

    private static class StartState extends State {
        public StartState() {
            super(FindItem.INSTANCE);
        }
        @Override
        public void onTick() {
            if (checkInventory()) {
                feature.setState(new CompletedState());
            } else {
                feature.setState(new SwitchSpeedItemState());
            };
        }
    }
    private static class SwitchSpeedItemState extends State {
        public SwitchSpeedItemState() {
            super(FindItem.INSTANCE);
        }
        @Override
        public void onTick() {

        }
    }
    private static class CompletedState extends State {
        public CompletedState() {
            super(FindItem.INSTANCE);
        }
    }

    public FindItem() {
        super("finditem", "fi");
    }

    @Override
    public void onEnable() {
        state = new StartState();
    }

    private static boolean checkInventory() {
        assert MC.player != null;

        ScreenHandler handler = MC.player.currentScreenHandler;

        for (Slot slot : handler.slots) {
            ItemStack stack = slot.getStack();
            if (stack.isEmpty()) continue;

            Item.Rarity stackRarity = InventoryUtil.getRarity(stack);

            if (stack.getName().getString().toLowerCase().contains(targetName) && (targetRarity == null || targetRarity == stackRarity)) {
                String area = InventoryUtil.getSlotArea(slot, handler);

                int inventoryIndex = slot.getIndex();
                int handlerIndex = slot.id;

                Text message = Text.literal(
                    "Found item in " + area +
                        " | inventory slot: " + inventoryIndex +
                        " | handler slot: " + handlerIndex
                ).formatted(Formatting.RED, Formatting.BOLD);

                MC.execute(() ->
                        MC.inGameHud.getChatHud().addMessage(message)
                );

                return true;
            }
        }
        return false;
    }

    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> buildCommand(String commandRoot) {
        return literal(commandRoot)
            .then(literal("test")
                .executes(ctx -> {
                    for (int i = 0; i < 9; i++) {
                        ItemStack stack = MC.player.getInventory().getStack(i);
                        if (stack != null) {
                            ctx.getSource().sendFeedback(Text.literal(new Item(stack).toString()));
                        }
                    }
                    return 1;
                })
            )
            .then(literal("toggle")
                .executes(ctx -> {
                    toggle();
                    ctx.getSource().sendFeedback(Text.literal("FindItem: " + (enabled ? "ON" : "OFF")));
                    return 1;
                })
            )
            .then(literal("named")
                .then(argument("name_filter", StringArgumentType.string())
                    .executes(ctx -> {
                        targetName = getString(ctx, "name_filter").toLowerCase();
                        ctx.getSource().sendFeedback(Text.literal("Target item name set to: " + targetName));
                        return 1;
                    })
                )
            )
            .then(literal("rarity")
                .then(argument("rarity_filter", StringArgumentType.word())
                    .suggests((context, builder) ->
                        CommandSource.suggestMatching(
                            new String[]{
                                "mythic", "legendary", "rare", "unique",
                                "fabled", "crafted", "set", "normal", "none"
                            },
                            builder
                        )
                    )
                    .executes(ctx -> {
                        String rarityInput = StringArgumentType.getString(ctx, "rarity_filter").toUpperCase();

                        if (rarityInput.equalsIgnoreCase("any")) {
                            targetRarity = null;
                            return 1;
                        }
                        try {
                            targetRarity = Item.Rarity.valueOf(rarityInput);
                            ctx.getSource().sendFeedback(
                                Text.literal("Target item rarity set to: " + rarityInput)
                            );
                        } catch (IllegalArgumentException e) {
                            ctx.getSource().sendError(
                                Text.literal("Invalid rarity.")
                            );
                        }
                        return 1;
                    })
                )
            );
    }
}
