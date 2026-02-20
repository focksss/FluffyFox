package com.ff;

import com.ff.feature.features.Freelook;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class Keybinds {
    public static KeyBinding toggleFreelook;

    public static void register() {
        toggleFreelook = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.ff.toggle_freelook",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_GRAVE_ACCENT,
            KeyBinding.Category.create(Identifier.of("ff", "controls"))
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleFreelook.wasPressed()) {
                Freelook.INSTANCE.toggle();
            }
        });
    }
}
