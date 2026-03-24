package com.ff.mixin.client;

import com.ff.feature.features.FixSkinRendering;
import net.minecraft.entity.PlayerLikeEntity;
import net.minecraft.entity.player.PlayerModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerLikeEntity.class)
public class PlayerLikeEntityMixin {
    @Inject(method = "isModelPartVisible", at = @At("HEAD"), cancellable = true)
    private void forceAllPartsVisible(PlayerModelPart part, CallbackInfoReturnable<Boolean> cir) {
        if (!FixSkinRendering.INSTANCE.isEnabled()) return;
        cir.setReturnValue(true);
    }
}
