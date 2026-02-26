package com.ff.mixin.client;

import com.ff.feature.features.Antiblind;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.fog.BlindnessEffectFogModifier;
import net.minecraft.client.render.fog.FogData;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlindnessEffectFogModifier.class)
public class BlindnessEffectFogModifierMixin {

    @Inject(method = "applyStartEndModifier", at = @At("TAIL"))
    private void noBlindnessFog(FogData data, Camera camera, ClientWorld clientWorld, float f, RenderTickCounter renderTickCounter, CallbackInfo ci) {
        if (Antiblind.INSTANCE.isEnabled()) {
            data.environmentalStart = 0.25F * f;
            data.environmentalEnd = f;
            data.skyEnd = f * 0.8F;
            data.cloudEnd = f * 0.8F;
        }
    }

    @Inject(method = "applyDarknessModifier", at = @At("HEAD"), cancellable = true)
    private void noBlindnessDarkness(LivingEntity cameraEntity, float darkness, float tickProgress, CallbackInfoReturnable<Float> cir) {
        if (Antiblind.INSTANCE.isEnabled()) cir.setReturnValue(0.0F);
    }
}
