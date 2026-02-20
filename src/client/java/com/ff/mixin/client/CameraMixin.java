package com.ff.mixin.client;

import com.ff.features.Freelook;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.ff.FluffyFoxClient.MC;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Shadow protected abstract void setRotation(float yaw, float pitch);

    @Shadow protected abstract void setPos(double x, double y, double z);

    @Inject(method = "update", at = @At("TAIL"))
    private void onUpdate(World area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickProgress, CallbackInfo ci) {
        if (Freelook.INSTANCE.isEnabled()) {
            PlayerEntity player = MC.player;
            if (player == null) return;
            setRotation(Freelook.yaw, Freelook.pitch);
            setPos(
                player.getX() + (Freelook.distance * Math.cos(Freelook.yaw) * Math.cos(Freelook.pitch)),
                player.getY() + (Freelook.distance * Math.sin(Freelook.pitch)),
                player.getZ() + (Freelook.distance * Math.sin(Freelook.yaw) * Math.cos(Freelook.pitch))
            );
        }
    }
}
