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

    /**
     * Interpolates the camera towards the target rotation given parameters.
     *
     * @param pitchMin pitch clamp min when rotating camera
     * @param pitchMax pitch clamp max when rotating camera
     * @param pitchOffset pitch offset when rotating camera
     * @param pitchTolerance tolerance for pitch before inducing a rotation
     * @param yawTolerance tolerance for yaw before inducing a rotation
     * @param absoluteYawTolerance tolerance for yaw before returning a value of 0, indicating that remaining yaw delta is negligible
     * @param snapThreshold tolerance for a delta in pitch or yaw before simulating a large mouse swipe to get near it
     * @return the remaining yaw delta (for strafe correction). Returns 0 if within absoluteYawTolerance
     */
    public static double updateCamera(
            double pitchMin,
            double pitchMax,
            double pitchOffset,
            double pitchTolerance,
            double yawTolerance,
            double absoluteYawTolerance,
            double snapThreshold
    ) {
        PlayerEntity player = MC.player;
        if (player == null) return 0;
        if (!active) return 0;

        long elapsed = System.currentTimeMillis() - startTime;
        float totalDelta = (float) Math.sqrt(
            Math.pow(wrapDegrees(targetYaw - startYaw), 2) + Math.pow(targetPitch - startPitch, 2)
        );
        float effectiveDuration = duration;
        if (totalDelta > snapThreshold) {
            effectiveDuration = (float) (duration * (snapThreshold / totalDelta));
        }
        float t = Math.min(1.0f, (float) elapsed / effectiveDuration);

        // yaw
        float interpolatedYaw = lerpAngle(startYaw, targetYaw, t);

        float yawDelta = wrapDegrees(interpolatedYaw - currentYaw);

        if (Math.abs(yawDelta) <= absoluteYawTolerance) {
            if (t >= 1.0f) active = false;
            return 0;
        }

        if (Math.abs(yawDelta) > yawTolerance) {
            currentYaw = interpolatedYaw;
        }

        // pitch
        float thisTargetPitch = (float) (targetPitch + pitchOffset);
        float pitchDelta = thisTargetPitch - currentPitch;

        if (Math.abs(pitchDelta) > pitchTolerance) {
            currentPitch = (float) clamp(
                lerp(startPitch, thisTargetPitch, t),
                pitchMin,
                pitchMax
            );
        }

        // submit
        player.setYaw(currentYaw);
        player.setPitch(currentPitch);

        if (t >= 1.0f) {
            active = false;
        }

        return yawDelta;
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
