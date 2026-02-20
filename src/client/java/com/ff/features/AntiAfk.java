package com.ff.features;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static com.ff.FluffyFoxClient.MC;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;

public class AntiAfk extends Feature {
    public static final AntiAfk INSTANCE = new AntiAfk();

    private double delaySeconds = 60;
    private long lastJumpTime = 0;
    private boolean doGroundCheck = true;

    private AntiAfk() {
        super("antiafk","aa");
    }

    public double getDelay() {
        return delaySeconds;
    }

    public void setDelay(double seconds) {
        delaySeconds = seconds;
    }

    @Override
    public void onTick() {
        if (MC.player == null) return;
        if (MC.world == null) return;
        if (MC.currentScreen != null) return;

        long now = System.currentTimeMillis();
        long interval = (long) (delaySeconds * 1000);

        if (doGroundCheck && !MC.player.isOnGround()) return;

        if (now - lastJumpTime >= interval) {
            MC.player.jump();
            lastJumpTime = now;
        }
    }

    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> buildCommand() {
        LiteralArgumentBuilder<FabricClientCommandSource> mainNode = literal("antiafk")
            .then(literal("toggle")
                .executes(ctx -> {
                    toggle();
                    ctx.getSource().sendFeedback(
                            Text.literal("AntiAFK: "
                                    + (enabled ? "ON" : "OFF"))
                    );
                    return 1;
                })
            )
            .then(literal("t")
                .executes(ctx -> {
                    toggle();
                    ctx.getSource().sendFeedback(
                            Text.literal("AntiAFK: "
                                    + (enabled ? "ON" : "OFF"))
                    );
                    return 1;
                })
            )
            .then(literal("delay")
                .then(argument("seconds", DoubleArgumentType.doubleArg(0.1))
                    .executes(ctx -> {
                        double seconds = DoubleArgumentType.getDouble(ctx, "seconds");
                        setDelay(seconds);
                        ctx.getSource().sendFeedback(
                                Text.literal("AntiAFK delay set to " + seconds + "s")
                        );
                        return 1;
                    })
                )
            )
            .then(literal("d")
                .then(argument("seconds", DoubleArgumentType.doubleArg(0.1))
                    .executes(ctx -> {
                        double seconds = DoubleArgumentType.getDouble(ctx, "seconds");
                        setDelay(seconds);

                        ctx.getSource().sendFeedback(
                                Text.literal("AntiAFK delay set to " + seconds + "s")
                        );
                        return 1;
                    })
                )
            )
            .then(literal("groundcheck")
                .executes(ctx -> {
                    doGroundCheck = !doGroundCheck;
                    ctx.getSource().sendFeedback((!doGroundCheck ?
                            Text.literal("!!!DANGER!!!")
                                    .formatted(Formatting.RED, Formatting.BOLD, Formatting.UNDERLINE)
                                    .append(Text.literal(""))
                                    .append(Text.literal(" - Ground Check Disabled! This can cause flight at low jump delays!"))
                            : Text.literal("Ground Check: Enabled")
                    ));
                    return 1;
                })
            )
            .then(literal("gc")
                .executes(ctx -> {
                    doGroundCheck = !doGroundCheck;
                    ctx.getSource().sendFeedback((!doGroundCheck ?
                            Text.literal("!!!DANGER!!!")
                                    .formatted(Formatting.RED, Formatting.BOLD, Formatting.UNDERLINE)
                                    .append(Text.literal(""))
                                    .append(Text.literal(" - Ground Check Disabled! This can cause flight at low jump delays!"))
                            : Text.literal("Ground Check: Enabled")
                    ));
                    return 1;
                })
            );

        var aliasNode = literal("aa").redirect(mainNode.build());

        return mainNode.then(aliasNode);
    }
}