package com.ff.feature.features;

import com.ff.config.ConfigManager;
import com.ff.feature.Feature;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class GlowingShadowlings extends Feature {
    public static final GlowingShadowlings INSTANCE = new GlowingShadowlings();

    public GlowingShadowlings() {
        super("glowingShadowlings", "gs");
    }

    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> buildCommand(String commandRoot) {
        return literal(commandRoot).then(literal("toggle")
                .executes(ctx -> {
                    toggle();
                    ctx.getSource().sendFeedback(Text.literal("GlowingShadowlings: " + (enabled ? "ON" : "OFF")));
                    ConfigManager.get().glowingShadowlingsEnabled = enabled;
                    ConfigManager.save();
                    return 1;
                })
        );
    }
}
