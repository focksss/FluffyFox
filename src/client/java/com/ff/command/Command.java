package com.ff.command;

import com.ff.feature.Feature;
import com.ff.feature.Manager;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class Command {
    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess) -> register(dispatcher)
        );
    }

    private static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        var root = literal("ff");

        for (Feature feature : Manager.FEATURES) {
            root.then(feature.buildCommand(feature.getName()));
            root.then(feature.buildCommand(feature.getAlias()));
        }

        dispatcher.register(root);
    }
}