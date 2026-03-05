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

/// Ice snake metadaa: [14:54:16] [Render thread/INFO]: [STDOUT]: {Damage:4s,HideFlags:6,Unbreakable:1b,"VV|Protocol1_20_3To1_20_5":1b}
/// Beta ice snake metadata

public class IcelessSnakes extends Feature {
    public static IcelessSnakes INSTANCE = new IcelessSnakes();

    public IcelessSnakes() { super("icelesssnakes", "is"); }

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
