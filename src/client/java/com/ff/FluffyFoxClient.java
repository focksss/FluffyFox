package com.ff;

import com.ff.feature.*;
import com.ff.feature.features.*;
import com.ff.ipc.IpcManager;
import net.fabricmc.api.ClientModInitializer;
import com.ff.command.Command;
import net.minecraft.client.MinecraftClient;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;


public class FluffyFoxClient implements ClientModInitializer {
	public static MinecraftClient MC = MinecraftClient.getInstance();

	@Override
	public void onInitializeClient() {
		IpcManager.init();

		Keybinds.register();

		Manager.register(Test.INSTANCE);
		Manager.register(AntiAfk.INSTANCE);
		Manager.register(Freelook.INSTANCE);
		Manager.register(AntiScroll.INSTANCE);
		Manager.register(Travel.INSTANCE);
		Manager.register(FindItem.INSTANCE);
		Manager.register(UthMacro.INSTANCE);
		Manager.register(IcelessSnakes.INSTANCE);
		Manager.register(Antiblind.INSTANCE);
		Manager.register(DiscoSwap.INSTANCE);
		Manager.register(FixSkinRendering.INSTANCE);

		Command.register();

		ClientTickEvents.END_CLIENT_TICK.register(client ->
			Manager.tick()
		);
	}
}