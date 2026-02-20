package com.ff.mixin.client;

import com.ff.features.Freelook;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseMixin {
    @Shadow private double cursorDeltaX;
    @Shadow private double cursorDeltaY;

    @Inject(method = "updateMouse", at = @At("HEAD"), cancellable = true)
    private void onUpdateMouse(CallbackInfo ci) {
        if (Freelook.INSTANCE.isEnabled()) {
            Freelook.onMouseMove(cursorDeltaX, cursorDeltaY);
            cursorDeltaX = 0;
            cursorDeltaY = 0;
            ci.cancel();
        }
    }
}
