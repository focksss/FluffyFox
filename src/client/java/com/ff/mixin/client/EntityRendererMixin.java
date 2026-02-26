package com.ff.mixin.client;

import com.ff.feature.features.IcelessSnakes;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {
    @Inject(
            method = "shouldRender",
            at = @At("HEAD"),
            cancellable = true
    )
    private void hideIceSpike(
            Entity entity, Frustum frustum, double x, double y, double z, CallbackInfoReturnable<Boolean> cir
    ) {
        if (!IcelessSnakes.INSTANCE.isEnabled()) return;
        if (entity instanceof ArmorStandEntity) {
            ItemStack item = ((ArmorStandEntity) entity).getEquippedStack(EquipmentSlot.HEAD);
            Vec3d vel = entity.getVelocity();
            if (item.getItem() == Items.DIAMOND_SWORD && vel.getHorizontal().length() == 0.0 && !entity.hasNoGravity() && item.getDamage() == 4) cir.setReturnValue(false);
        }
    }
}
