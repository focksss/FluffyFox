package com.ff.feature.features;

import com.ff.config.ConfigManager;
import com.ff.feature.Feature;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class Antiblind extends Feature {
    public static Antiblind INSTANCE = new Antiblind();

    public Antiblind() { super("antiblind", "ab"); }

    @Override
    public void onTick() {

    }

    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> buildCommand(String commandRoot) {
        return literal(commandRoot)
            .then(literal("toggle")
                .executes(ctx -> {
                    toggle();
                    ctx.getSource().sendFeedback(Text.literal("Antiblind: " + (enabled ? "ON" : "OFF")));
                    ConfigManager.get().antiBlindEnabled = enabled;
                    ConfigManager.save();
                    return 1;
                })
            );
    }
}
