package com.ff.mixin.client;

import com.ff.feature.features.AntiScroll;
import com.ff.feature.features.Freelook;
import net.minecraft.client.Mouse;
import net.minecraft.entity.player.PlayerInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
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

    @Redirect(
        method = "onMouseScroll",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/player/PlayerInventory;setSelectedSlot(I)V"
        )
    )
    private void preventHotbarScroll(PlayerInventory inventory, int slot) {
        if (AntiScroll.INSTANCE.isEnabled()) {
            return;
        }
        inventory.setSelectedSlot(slot);
    }
}
