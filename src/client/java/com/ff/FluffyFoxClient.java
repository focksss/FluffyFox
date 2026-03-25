package com.ff;

import com.ff.config.ConfigManager;
import com.ff.feature.*;
import com.ff.feature.features.*;
import com.ff.ipc.IpcManager;
import net.fabricmc.api.ClientModInitializer;
import com.ff.command.Command;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
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
		Manager.register(WatchedTitles.INSTANCE);
		Manager.register(GlowingShadowlings.INSTANCE);
		Manager.register(BulbHolderWaypoints.INSTANCE);

		Command.register();

		ConfigManager.load();
		ConfigManager.get().updateInternal();

		WorldRenderEvents.END_MAIN.register(BulbHolderWaypoints::onRender);

		ClientTickEvents.END_CLIENT_TICK.register(client ->
			Manager.tick()
		);
	}
}