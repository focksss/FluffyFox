package com.ff.mixin.client;

import com.ff.feature.features.Fullbright;
import net.minecraft.client.option.GameOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameOptions.class)
public class GameOptionsMixin {
    @Inject(method = "getGamma", at = @At("HEAD"), cancellable = true)
    private void fullbright(CallbackInfoReturnable<Double> cir) {
        if (Fullbright.INSTANCE.isEnabled()) cir.setReturnValue(Fullbright.gammaOverride);
    }
}
