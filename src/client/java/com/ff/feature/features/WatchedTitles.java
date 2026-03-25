package com.ff.feature.features;

import com.ff.config.ConfigManager;
import com.ff.feature.Feature;
import com.ff.feature.State;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

import static com.ff.FluffyFoxClient.MC;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class WatchedTitles extends Feature {
    public static final WatchedTitles INSTANCE = new WatchedTitles();

    public static int telegraphStep = 0;
    public static long lastSoundTick = 0;
    public static final long RESET_TICKS = 80;

    public WatchedTitles() {
        super("watchedtitles", "wt");
    }

    public static void onTelegraphSound(int step) {
        if (!INSTANCE.isEnabled()) return;
        if (MC.player == null) return;

        String label = switch (step) {
            case 0 -> "2";
            case 1 -> "1";
            case 2 -> "0";
            default -> "";
        };
        int color = switch (step) {
            case 0 -> 0xFFFF00;
            case 1 -> 0xFF8800;
            case 2 -> 0xFF2200;
            default -> 0xFFFFFF;
        };

        MC.inGameHud.setTitle(Text.literal(label).styled(s -> s.withColor(color)));
        MC.inGameHud.setTitleTicks(2, 18, 5);
    }

    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> buildCommand(String commandRoot) {
        return literal(commandRoot)
                .then(literal("toggle")
                        .executes(ctx -> {
                            toggle();
                            ctx.getSource().sendFeedback(Text.literal(
                                    enabled ? "WatchedTitles enabled." : "WatchedTitles disabled."
                            ));
                            ConfigManager.get().watchedTitlesEnabled = enabled;
                            ConfigManager.save();
                            return 1;
                        })
                );
    }
}