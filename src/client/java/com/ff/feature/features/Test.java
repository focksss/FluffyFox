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
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.command.CommandSource;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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
                        System.out.println("running test method");
                        /* enabled layers test
                        assert MC.player != null;
                        ctx.getSource().sendFeedback(Text.literal("hat enabled: " + MC.player.isModelPartVisible(PlayerModelPart.HAT)));
                        ctx.getSource().sendFeedback(Text.literal("cape enabled: " + MC.player.isModelPartVisible(PlayerModelPart.CAPE)));

                         */

                        assert MC.world != null;
                        Vec3d playerPosition = new Vec3d(MC.player.getX(), MC.player.getY() + 0.6, MC.player.getZ());
                        double radius = 30.0;
                        Box box = new Box(
                                playerPosition.getX() - radius, playerPosition.getY() - radius, playerPosition.getZ() - radius,
                                playerPosition.getX() + radius, playerPosition.getY() + radius, playerPosition.getZ() + radius
                        );
                        List<DisplayEntity.ItemDisplayEntity> entities = MC.world.getEntitiesByClass(DisplayEntity.ItemDisplayEntity.class, box, entity -> true);
                        for (Entity entity : entities) {
                            if (entity.getType().toString().contains("item_display")) {
                                System.out.println("FOUND ITEM DISPLAY: " + entity);

                                var itemDisplay = (DisplayEntity.ItemDisplayEntity) entity;

                                ItemStack stack = itemDisplay.getItemStack();

                                System.out.println("Item: " + stack.getItem());

                                var cmdComponent = stack.get(DataComponentTypes.CUSTOM_MODEL_DATA);

                                if (cmdComponent != null && !cmdComponent.floats().isEmpty()) {
                                    float cmd = cmdComponent.floats().get(0);

                                    System.out.println("CMD: " + cmd);
                                }
                            }
                        }

                        return 1;
                    })
            )
            .then(literal("summonTest")
                .then(argument("minID", IntegerArgumentType.integer(0))
                    .then(argument("maxID", IntegerArgumentType.integer(0))
                    .executes(ctx -> {

                        int minID = IntegerArgumentType.getInteger(ctx, "minID");
                        int maxID = IntegerArgumentType.getInteger(ctx, "maxID");

                        if (minID > maxID) {
                            ctx.getSource().sendError(Text.literal("min must be <= max"));
                            return 0;
                        }

                        ClientPlayerEntity player = MC.player;
                        if (player == null) return 0;

                        int GRID_WIDTH = 16;
                        float SPACING = 0.5f;

                        int count = 0;
                        for (int cmd = minID; cmd <= maxID; cmd++) {
                            int col = count % GRID_WIDTH;
                            int row = count / GRID_WIDTH;

                            float offsetX = col * SPACING;
                            float offsetZ = row * SPACING;

                            String command = String.format(
                                    "summon minecraft:item_display ~%.1f ~1.6 ~%.1f {item:{id:\"minecraft:oak_boat\",components:{\"minecraft:custom_model_data\":{floats:[%df]}}}}",
                                    offsetX, offsetZ, cmd
                            );

                            player.networkHandler.sendChatCommand(command);
                            count++;
                        }

                        return 1;
                    })
            )));
    }
}
