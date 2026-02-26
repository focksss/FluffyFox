package com.ff.mixin.client;

import com.ff.feature.features.IcelessSnakes;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.ArmorStandEntityRenderer;
import net.minecraft.client.render.entity.state.ArmorStandEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArmorStandEntityRenderer.class)
public abstract class ArmorStandRenderMixin {
    @Inject(
        method = "render(Lnet/minecraft/client/render/entity/state/ArmorStandEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/render/state/CameraRenderState;)V",
        at = @At("HEAD"),
        cancellable = true
    )
    private void hideIceSpike(
        ArmorStandEntityRenderState armorStandEntityRenderState,
        MatrixStack matrixStack,
        OrderedRenderCommandQueue orderedRenderCommandQueue,
        CameraRenderState cameraRenderState,
        CallbackInfo ci
    ) {
        if (!IcelessSnakes.INSTANCE.isEnabled()) return;
        System.out.println("ArmorStand renderer hit");
        ItemStack item = armorStandEntityRenderState.equippedHeadStack;
        ci.cancel();
        if (item.getItem() == Items.DIAMOND_SWORD) {
            System.out.println("RENDER ATTEMPT");
        }
    }
}
