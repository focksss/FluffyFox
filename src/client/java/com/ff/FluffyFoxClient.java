package com.ff;

import com.ff.feature.*;
import com.ff.feature.features.*;
import net.fabricmc.api.ClientModInitializer;
import com.ff.command.Command;
import net.minecraft.client.MinecraftClient;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;


public class FluffyFoxClient implements ClientModInitializer {
	public static MinecraftClient MC = MinecraftClient.getInstance();

	@Override
	public void onInitializeClient() {
		Keybinds.register();

		Manager.register(AntiAfk.INSTANCE);
		Manager.register(Freelook.INSTANCE);
		Manager.register(AntiScroll.INSTANCE);
		Manager.register(Travel.INSTANCE);
		Manager.register(FindItem.INSTANCE);

		Command.register();

		ClientTickEvents.END_CLIENT_TICK.register(client ->
			Manager.tick()
		);
	}
}