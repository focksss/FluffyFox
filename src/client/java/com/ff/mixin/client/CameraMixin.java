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
            setRotation(Freelook.yaw, Freelook.pitch);

            Vec3d pos = focusedEntity.getLerpedPos(tickProgress);

            double yawRad = Math.toRadians(Freelook.yaw);
            double pitchRad = -Math.toRadians(Freelook.pitch);
            setPos(
                pos.x + (Freelook.distance * Math.cos(pitchRad) * Math.sin(yawRad)),
                pos.y + focusedEntity.getStandingEyeHeight() - (Freelook.distance * Math.sin(pitchRad)),
                pos.z - (Freelook.distance * Math.cos(pitchRad) * Math.cos(yawRad))
            );
        }
    }
}
