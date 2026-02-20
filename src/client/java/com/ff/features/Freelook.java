package com.ff.features;

import com.ff.util.MovementUtil;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.network.ClientPlayerEntity;

import static com.ff.FluffyFoxClient.MC;

public class Freelook extends Feature {
    public static final Freelook INSTANCE = new Freelook();

    public static float pitch = 0.0F;
    public static float yaw = 0.0F;

    public static float distance = 3.0F;

    public Freelook() {
        super("freelook", "fl");
    }

    public static void onMouseMove(double dx, double dy) {
        if (!Freelook.INSTANCE.isEnabled() || MC.player == null) return;

        float sense = MC.options.getMouseSensitivity().getValue().floatValue() * 0.6F + 0.2F;
        float factor = sense * sense * sense * 8.0F;

        yaw += (float)(dx * factor * 0.15F);
        pitch -= (float)(dy * factor * 0.15F);
        pitch = Math.max(-90.0F, Math.min(90.0F, pitch));
    }

    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> buildCommand() {
        return null;
    }
}