package com.ff.feature.features;

import com.ff.config.ConfigManager;
import com.ff.feature.Feature;
import com.ff.feature.State;
import com.ff.util.RenderingUtil;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

import static com.ff.FluffyFoxClient.MC;
import static com.ff.util.WynnMobUtil.getMobName;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class FlyingAxonWarning extends Feature {
    public static final FlyingAxonWarning INSTANCE = new FlyingAxonWarning();

    public double warningRadius = 10.0;
    public double warningAngle = 35.0;

    private final List<Vec3d> targets = new ArrayList<>();

    public FlyingAxonWarning() {
        super("FlyingAxonWarning", "faw");
    }

    @Override
    public void onTick() {
        targets.clear();


        PlayerEntity player = MC.player;
        World world = MC.world;
        ClientPlayerInteractionManager interactionManager = MC.interactionManager;
        if (player == null || interactionManager == null || world == null) return;

        Vec3d center = player.getEyePos();
        if (center.getHorizontal().distanceTo(new Vec3d(11782, 0, 4642)) > 300.0) return;

        double radius = warningRadius;
        Box box = new Box(
                center.getX() - radius, center.getY() - radius, center.getZ() - radius,
                center.getX() + radius, center.getY() + radius, center.getZ() + radius
        );

        List<DisplayEntity.TextDisplayEntity> axons = world.getEntitiesByClass(
                DisplayEntity.TextDisplayEntity.class,
                box,
                entity -> getMobName(entity).contains("Onyx Axon")
        );

        for (DisplayEntity.TextDisplayEntity axon : axons) {
            targets.add(new Vec3d(axon.getX(), axon.getY(), axon.getZ()));
        }
        if (!targets.isEmpty()) {
            MC.inGameHud.setTitle(Text.literal("AXONS").styled(s -> s.withColor(0xFF2200)));
            MC.inGameHud.setTitleTicks(0, 1, 1);
        }
    }

    @Override
    public void onRender(WorldRenderContext ctx) {
        if (!enabled) return;

        if (MC.player == null) return;

        // if (MC.player.getEyePos().getHorizontal().distanceTo(new Vec3d(23477, 0, -22034)) > 300.0) return;

        if (targets.isEmpty()) return;

        Vec3d cam = MC.gameRenderer.getCamera().getCameraPos();
        VertexConsumerProvider.Immediate vcp =
                MC.getBufferBuilders().getEntityVertexConsumers();

        ctx.matrices().push();

        for (Vec3d t : targets) {
            RenderingUtil.drawTracer(
                    ctx.matrices(), vcp, cam, t,
                    1.0f, 0.0f, 0.0f, 1.0f
            );
        }

        vcp.draw();
        ctx.matrices().pop();
    }

    @Override
    public void onDisable() {
        targets.clear();
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
            )
            .then(literal("warningRadius")
                .then(argument("warningRadius", DoubleArgumentType.doubleArg(0))
                    .executes(ctx -> {
                        warningRadius = DoubleArgumentType.getDouble(ctx, "warningRadius");
                        ctx.getSource().sendFeedback(
                            Text.literal("FlyingAxonWarning radius threshold set to: " + warningRadius)
                        );
                        return 1;
                    })
                )
            )
            .then(literal("warningAngle")
                .then(argument("warningAngle", DoubleArgumentType.doubleArg(0))
                    .executes(ctx -> {
                        warningAngle = DoubleArgumentType.getDouble(ctx, "warningAngle");
                        ctx.getSource().sendFeedback(
                                Text.literal("FlyingAxonWarning visibility angle threshold set to: " + warningAngle)
                        );
                        return 1;
                    })
                )
            );
    }
}