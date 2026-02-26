package com.ff.feature.features;

import com.ff.feature.Feature;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class Fullbright extends Feature {
    public static Fullbright INSTANCE = new Fullbright();
    public static double gammaOverride = 1000.0;

    public Fullbright() { super("fullbright", "fb"); }

    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> buildCommand(String commandRoot) {
        return literal(commandRoot)
            .then(literal("toggle")
                .executes(ctx -> {
                    toggle();
                    ctx.getSource().sendFeedback(Text.literal("Fullbright: " + (enabled ? "ON" : "OFF")));
                    return 1;
                })
            )
            .then(literal("gammaOverride")
                .then(argument("gamma", DoubleArgumentType.doubleArg(0.0))
                    .executes(ctx -> {
                        gammaOverride = DoubleArgumentType.getDouble(ctx, "gamma");
                        ctx.getSource().sendFeedback(Text.literal("Gamma override set too: " + gammaOverride));
                        return 1;
                    })
                )
            );
    }
}
