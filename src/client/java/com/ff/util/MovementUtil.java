package com.ff.util;

import net.minecraft.client.input.Input;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.StyleSpriteSource;
import net.minecraft.util.math.Vec3d;

import static com.ff.FluffyFoxClient.MC;

public class MovementUtil {
    public enum MovementState {
        NEGATIVE, ZERO, POSITIVE, OFF
    }

    public static float startYaw, startPitch;
    public static float targetYaw, targetPitch;
    public static float currentYaw, currentPitch;
    private static long startTime, duration;
    private static boolean active = false;
    private static boolean isSneakingForced = false;
    private static boolean isMining = false;
    public static Enum<MovementState> forceForward = MovementState.ZERO;
    public static Enum<MovementState> forceSideways = MovementState.ZERO;
    public static Enum<MovementState> forceVertical = MovementState.ZERO;

    public static void lookAtCoordinate(Vec3d coordinate, double timeToLook) {
        PlayerEntity player = MC.player;
        if (player == null) return;
        float currentYaw = player.getYaw();
        float currentPitch = player.getPitch();

        if (active) {
            // if already moving, start from the current interpolated orientation
            currentYaw = getCurrentYaw();
            currentPitch = getCurrentPitch();
        }

        startYaw = currentYaw;
        startPitch = currentPitch;

        // player position
        double dx = coordinate.x - player.getX();
        double dy = coordinate.y - player.getEyeY();
        double dz = coordinate.z - player.getZ();

        // target orientation
        double distXZ = Math.sqrt(dx * dx + dz * dz);
        targetYaw = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90F);
        targetPitch = (float) -(Math.toDegrees(Math.atan2(dy, distXZ)));

        // normalize yaw difference to the shortest path
        targetYaw = wrapDegrees(targetYaw);

        // reset timing
        startTime = System.currentTimeMillis();
        duration = (long) (timeToLook * 1000);
        active = true;
    }
    public static void toSpecificRotation(float pitch, float yaw, double timeToLook) {
        PlayerEntity player = MC.player;
        if (player == null) return;
        float currentYaw = player.getYaw();
        float currentPitch = player.getPitch();

        if (active) {
            currentYaw = getCurrentYaw();
            currentPitch = getCurrentPitch();
        }

        startYaw = currentYaw;
        startPitch = currentPitch;

        targetYaw = yaw;
        targetPitch = pitch;

        targetYaw = wrapDegrees(targetYaw);

        startTime = System.currentTimeMillis();
        duration = (long) (timeToLook * 1000);
        active = true;
    }

    public static void updateCamera(double pitchMin, double pitchMax) {
        PlayerEntity player = MC.player;
        if (player == null) return;
        if (!active) return;

        long elapsed = System.currentTimeMillis() - startTime;
        float t = Math.min(1.0f, (float) elapsed / duration);

        currentYaw = lerpAngle(startYaw, targetYaw, t);
        currentPitch = (float) clamp(lerp(startPitch, targetPitch, t), pitchMin, pitchMax);

        player.setYaw(currentYaw);
        player.setPitch(currentPitch);

        if (t >= 1.0f) {
            active = false;
        }
    }

    public static void updatePlayerMovement() {
        MC.options.forwardKey.setPressed(forceForward == MovementState.POSITIVE);
        MC.options.backKey.setPressed(forceForward == MovementState.NEGATIVE);

        MC.options.leftKey.setPressed(forceSideways == MovementState.POSITIVE);
        MC.options.rightKey.setPressed(forceSideways == MovementState.NEGATIVE);

        MC.options.jumpKey.setPressed(forceVertical == MovementState.POSITIVE);
        MC.options.sneakKey.setPressed(forceVertical == MovementState.NEGATIVE);
    }

    public static Vec3d getPlayerLookLocation(double dist) {
        if (MC.player == null) return null;
        Vec3d eyePos = MC.player.getEyePos();
        Vec3d dir = MC.player.getRotationVector();
        return eyePos.add(dir.x * dist, dir.y * dist, dir.z * dist);
    }

    private static Vec3d rotateYaw(Vec3d vec, double yawDeg) {
        double yaw = Math.toRadians(yawDeg);
        double cos = Math.cos(yaw);
        double sin = Math.sin(yaw);
        return new Vec3d(
            vec.x * cos - vec.z * sin,
            vec.y,
            vec.x * sin + vec.z * cos
        );
    }
    private static Vec3d rotatePitch(Vec3d vec, double pitchDeg) {
        double pitch = Math.toRadians(pitchDeg);
        double cos = Math.cos(pitch);
        double sin = Math.sin(pitch);
        return new Vec3d(
            vec.x,
            vec.y * cos - vec.z * sin,
            vec.y * sin + vec.z * cos
        );
    }

    private static float getCurrentYaw() {
        long elapsed = System.currentTimeMillis() - startTime;
        float t = Math.min(1.0f, (float) elapsed / duration);
        t = (float)(-0.5 * (Math.cos(Math.PI * t) - 1));
        return lerpAngle(startYaw, targetYaw, t);
    }

    private static float getCurrentPitch() {
        long elapsed = System.currentTimeMillis() - startTime;
        float t = Math.min(1.0f, (float) elapsed / duration);
        t = (float)(-0.5 * (Math.cos(Math.PI * t) - 1));
        return lerp(startPitch, targetPitch, t);
    }

    private static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    private static float lerpAngle(float a, float b, float t) {
        float diff = wrapDegrees(b - a);
        return a + diff * t;
    }

    private static float wrapDegrees(float angle) {
        angle = angle % 360.0f;
        if (angle >= 180.0f) angle -= 360.0f;
        if (angle < -180.0f) angle += 360.0f;
        return angle;
    }

    private static double clamp(double a, double min, double max) {
        return Math.max(Math.min(a, max), min);
    }
}
