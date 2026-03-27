package com.ff.feature.features;
import com.ff.util.RenderingUtil;
import com.ff.config.ConfigManager;
import com.ff.feature.Feature;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

import static com.ff.FluffyFoxClient.MC;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class BulbHolderWaypoints extends Feature {

    public static final BulbHolderWaypoints INSTANCE = new BulbHolderWaypoints();

    private static final float R = 1.00f;
    private static final float G = 0.82f;
    private static final float B = 0.10f;

    private static final float BEAM_HEIGHT = 64f;
    private static final float BEAM_HALF_WIDTH = 0.15f;

    private static final int LIFETIME = 5000; // ms

    private final List<Waypoint> waypoints = new ArrayList<>();

    private BulbHolderWaypoints() {
        super("bulbHolderWaypoints", "bhw");
    }

    public void onGoatHorn(Vec3d pos, long tick) {
        if (!isEnabled()) return;

        MC.inGameHud.setTitle(Text.literal("BULB").styled(s -> s.withColor(0xFF2200)));
        MC.inGameHud.setTitleTicks(2, 6, 3);

        waypoints.add(new Waypoint(pos, tick));
    }

    @Override
    public void onRender(WorldRenderContext ctx) {
        if (!enabled) return;

        if (MC.player == null) return;

        if (MC.player.getEyePos().getHorizontal().distanceTo(new Vec3d(23477, 0, -22034)) > 300.0) return;

        long now = System.currentTimeMillis();

        waypoints.removeIf(waypoint -> waypoint.isExpired(now));


        if (waypoints.isEmpty()) return;


        Vec3d cam = MC.gameRenderer.getCamera().getCameraPos();
        VertexConsumerProvider.Immediate vcp =
                MC.getBufferBuilders().getEntityVertexConsumers();

        ctx.matrices().push();
//        ctx.matrices().translate(-cam.x, -cam.y, -cam.z);


        for (Waypoint wp : waypoints) {
            float alpha = wp.alpha(now);
            if (alpha <= 0f) continue;

            RenderingUtil.drawBeam(
                ctx.matrices(), vcp, cam, wp.pos,
                BEAM_HEIGHT, BEAM_HALF_WIDTH,
                R, G, B, alpha * 0.55f
            );

            RenderingUtil.drawTracer(
                ctx.matrices(), vcp, cam, wp.pos,
                R, G, B, alpha
            );
        }

        vcp.draw();
        ctx.matrices().pop();
    }

    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> buildCommand(String commandRoot) {
        return literal(commandRoot)
                .then(literal("toggle")
                        .executes(ctx -> {
                            toggle();
                            if (!enabled) waypoints.clear();
                            ctx.getSource().sendFeedback(Text.literal(
                                    enabled ? "BulbHolderWaypoints enabled."
                                            : "BulbHolderWaypoints disabled."));
                            ConfigManager.get().bulbHolderWaypointsEnabled = enabled;
                            ConfigManager.save();
                            return 1;
                        })
                );
    }

    private record Waypoint(Vec3d pos, long creationTime) {
        boolean isExpired(long now) {
            return now - creationTime >= LIFETIME;
        }

        float alpha(long now) {
            float remaining = LIFETIME - (now - creationTime);
            return Math.max(0f, Math.min(1f, remaining / LIFETIME));
        }
    }
}