package com.ff.features;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class AntiScroll extends Feature {
    public static final AntiScroll INSTANCE = new AntiScroll();

    public AntiScroll() {
        super("nohotbarscroll", "nhs");
    }

    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> buildCommand(String commandRoot) {
        return literal(commandRoot)
            .then(literal("toggle")
                .executes(ctx -> {
                    toggle();
                    ctx.getSource().sendFeedback(Text.literal("NoHotbarScroll: " + (enabled ? "ON" : "OFF")));
                    return 1;
                })
            )
            .then(literal("t")
                .executes(ctx -> {
                    toggle();
                    ctx.getSource().sendFeedback(Text.literal("NoHotbarScroll: " + (enabled ? "ON" : "OFF")));
                    return 1;
                })
            );
    }
}
