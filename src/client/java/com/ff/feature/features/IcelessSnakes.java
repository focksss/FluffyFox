package com.ff.feature.features;

import com.ff.feature.Feature;
import com.ff.ipc.IpcManager;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

import static com.ff.FluffyFoxClient.MC;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class IcelessSnakes extends Feature {
    public static IcelessSnakes INSTANCE = new IcelessSnakes();

    public IcelessSnakes() { super("icelesssnakes", "is"); }

    @Override
    public void onTick() {
        PlayerEntity player = MC.player;
        World world = MC.world;
        if (player == null || world == null) return;

        Vec3d center = MC.player.getEyePos();

        double radius = 50.0;
        Box box = new Box(
            center.getX() - radius, center.getY() - radius, center.getZ() - radius,
            center.getX() + radius, center.getY() + radius, center.getZ() + radius
        );

        List<ArmorStandEntity> entities = MC.world.getEntitiesByClass(
            ArmorStandEntity.class,
            box,
            entity -> {
                ItemStack item = entity.getEquippedStack(EquipmentSlot.HEAD);
                return item.getItem() ==Items.DIAMOND_SWORD && item.getDamage() == 4;
            }
        );

        for (ArmorStandEntity entity : entities) {
            System.out.println("!!!!!");
            System.out.println("!!!!!");
            System.out.println("!!!!!");
            System.out.println(entity.getEquippedStack(EquipmentSlot.HEAD).getName());
            System.out.println(entity.getEquippedStack(EquipmentSlot.HEAD).getComponents().get(DataComponentTypes.CUSTOM_MODEL_DATA));
            System.out.println(entity.getEquippedStack(EquipmentSlot.HEAD).getComponents().get(DataComponentTypes.ITEM_MODEL));
            System.out.println(entity.getEquippedStack(EquipmentSlot.HEAD).getComponents().get(DataComponentTypes.CUSTOM_NAME));
            System.out.println(entity.getEquippedStack(EquipmentSlot.HEAD).getComponents().get(DataComponentTypes.LORE));
            System.out.println(entity.getEquippedStack(EquipmentSlot.HEAD).getComponents().get(DataComponentTypes.CUSTOM_DATA));
        }
    }

    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> buildCommand(String commandRoot) {
        return literal(commandRoot)
            .then(literal("toggle")
                .executes(ctx -> {
                    toggle();
                    ctx.getSource().sendFeedback(Text.literal("IcelessSnakes: " + (enabled ? "ON" : "OFF")));
                    return 1;
                })
            );
    }
}
