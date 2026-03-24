package com.ff.feature.features;

import com.ff.config.ConfigManager;
import com.ff.feature.Feature;
import com.ff.feature.State;
import com.ff.ipc.IpcManager;
import com.ff.util.InventoryUtil;
import com.ff.util.MovementUtil;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.option.GameOptions;
import net.minecraft.command.CommandSource;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.common.ClientOptionsC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.util.List;

import static com.ff.FluffyFoxClient.MC;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class FixSkinRendering extends Feature {
    public static FixSkinRendering INSTANCE = new FixSkinRendering();

    public FixSkinRendering() {
        super("fixskinrendering", "fsr");
    }

    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> buildCommand(String commandRoot) {
        return literal(commandRoot)
            .then(literal("toggle")
                    .executes(ctx -> {
                        toggle();
                        ctx.getSource().sendFeedback(Text.literal((enabled ? "All skin layers will be force loaded." : "Skin layers will no longer be force loaded")));
                        ConfigManager.get().fixSkinRenderingEnabled = enabled;
                        ConfigManager.save();
                        return 1;
                    })
            )
            .then(literal("check")
                .executes(ctx -> {
                    assert MC.player != null;

                    GameOptions options = MC.options;

                    boolean hatEnabled = MC.player.isModelPartVisible(PlayerModelPart.HAT);
                    boolean capeEnabled = MC.player.isModelPartVisible(PlayerModelPart.CAPE);
                    ctx.getSource().sendFeedback(Text.literal("hat layer internally enabled: " + hatEnabled));
                    ctx.getSource().sendFeedback(Text.literal("cape internally enabled: " + capeEnabled));

                    boolean original = options.isPlayerModelPartEnabled(PlayerModelPart.HAT);
                    options.setPlayerModelPart(PlayerModelPart.HAT, !original);
                    options.setPlayerModelPart(PlayerModelPart.HAT, original);

                    ctx.getSource().sendFeedback(Text.literal((!hatEnabled || !capeEnabled) ? "One or more layers are internally disabled, try enabling the feature if this didn't fix it." : "Layers should be rendering properly."));
                    return 1;
                })
            );
    }
}
