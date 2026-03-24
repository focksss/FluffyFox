package com.ff.feature.features;

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
import net.minecraft.command.CommandSource;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.item.ItemStack;
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

public class Test extends Feature {
    public static Test INSTANCE = new Test();

    public Test() {
        super("test", "test");
    }

    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> buildCommand(String commandRoot) {
        return literal(commandRoot)
            .then(literal("run")
                    .executes(ctx -> {
                        assert MC.player != null;
                        ctx.getSource().sendFeedback(Text.literal("hat enabled: " + MC.player.isModelPartVisible(PlayerModelPart.HAT)));
                        ctx.getSource().sendFeedback(Text.literal("cape enabled: " + MC.player.isModelPartVisible(PlayerModelPart.CAPE)));
                        return 1;
                    })
            );
    }
}
