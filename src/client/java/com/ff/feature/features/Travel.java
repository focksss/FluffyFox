package com.ff.feature.features;

import com.ff.feature.Feature;
import com.ff.util.MovementUtil;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;

public class Travel extends Feature {
    public static Travel INSTANCE = new Travel();

    private enum MovementMode {
        SPELLS,
        WALK,
        HORSE
    }

    public static Vec3d target;

    public Travel() {
        super("travel", "go");
    }

    @Override
    public void onTick() {
        MovementUtil.lookAtCoordinate(target, 0.25);
        MovementUtil.forceForward = MovementUtil.MovementState.POSITIVE;
        MovementUtil.updatePlayerMovement();
        MovementUtil.updateCamera(-90, 90);
    }

    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> buildCommand(String commandRoot) {
        return literal(commandRoot)
            .then(literal("toggle")
                .executes(ctx -> {
                    toggle();
                    ctx.getSource().sendFeedback(Text.literal("Travel: " + (enabled ? "ON" : "OFF")));
                    return 1;
                })
            )
            .then(literal("t")
                .executes(ctx -> {
                    toggle();
                    ctx.getSource().sendFeedback(Text.literal("Travel: " + (enabled ? "ON" : "OFF")));
                    return 1;
                })
            )
            .then(literal("target")
                .then(argument("x", DoubleArgumentType.doubleArg())
                .then(argument("y", DoubleArgumentType.doubleArg())
                .then(argument("z", DoubleArgumentType.doubleArg())
                    .executes(ctx -> {
                        target = new Vec3d(
                            DoubleArgumentType.getDouble(ctx, "x"),
                            DoubleArgumentType.getDouble(ctx, "y"),
                            DoubleArgumentType.getDouble(ctx, "z")
                        );
                        ctx.getSource().sendFeedback(
                            Text.literal("Travel destination set to (" + target.x + ", " + target.y + ", " + target.z + ")")
                        );
                        return 1;
                    })
                )))
            )
            .then(literal("to")
                .then(argument("x", DoubleArgumentType.doubleArg())
                .then(argument("y", DoubleArgumentType.doubleArg())
                .then(argument("z", DoubleArgumentType.doubleArg())
                    .executes(ctx -> {
                        target = new Vec3d(
                            DoubleArgumentType.getDouble(ctx, "x"),
                            DoubleArgumentType.getDouble(ctx, "y"),
                            DoubleArgumentType.getDouble(ctx, "z")
                        );
                        ctx.getSource().sendFeedback(
                            Text.literal("Travel destination set to (" + target.x + ", " + target.y + ", " + target.z + ")")
                        );
                        return 1;
                    })
                )))
            );
    }
}
