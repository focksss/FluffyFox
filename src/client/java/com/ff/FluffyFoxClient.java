package com.ff;

import com.ff.features.AntiScroll;
import com.ff.features.Freelook;
import net.fabricmc.api.ClientModInitializer;
import com.ff.command.Command;
import net.minecraft.client.MinecraftClient;

import com.ff.features.AntiAfk;
import com.ff.features.Manager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import java.io.FileReader;
import java.security.Key;


public class FluffyFoxClient implements ClientModInitializer {
	public static MinecraftClient MC = MinecraftClient.getInstance();

	@Override
	public void onInitializeClient() {
		Keybinds.register();

		Manager.register(AntiAfk.INSTANCE);
		Manager.register(Freelook.INSTANCE);
		Manager.register(AntiScroll.INSTANCE);

		Command.register();

		ClientTickEvents.END_CLIENT_TICK.register(client ->
			Manager.tick()
		);
	}
}