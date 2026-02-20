package com.ff;

import net.fabricmc.api.ClientModInitializer;
import com.ff.command.Command;
import net.minecraft.client.MinecraftClient;

import com.ff.features.AntiAfk;
import com.ff.features.Manager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;


public class FluffyFoxClient implements ClientModInitializer {
	public static MinecraftClient MC = MinecraftClient.getInstance();

	@Override
	public void onInitializeClient() {
		Manager.register(AntiAfk.INSTANCE);

		Command.register();

		ClientTickEvents.END_CLIENT_TICK.register(client ->
				Manager.tick()
		);
	}
}