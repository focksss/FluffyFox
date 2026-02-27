package com.ff;

import com.ff.feature.features.DiscoSwap;
import com.ff.feature.features.Freelook;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class Keybinds {
    public static KeyBinding.Category category = KeyBinding.Category.create(Identifier.of("ff", "controls"));
    public static KeyBinding toggleFreelook;
    public static KeyBinding discoSwap;


    public static void register() {
        toggleFreelook = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "Toggle Freelook",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_GRAVE_ACCENT,
            category
        ));
        discoSwap = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "Disco Swap",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_N,
            category
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleFreelook.wasPressed()) {
                Freelook.INSTANCE.toggle();
            }
            while (discoSwap.wasPressed()) {
                DiscoSwap.INSTANCE.execute();
            }
        });
    }
}
