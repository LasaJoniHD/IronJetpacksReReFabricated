package com.blakebr0.cucumber.helper;

import net.minecraft.world.phys.Vec3;

public final class VecHelper {
    private VecHelper() {
    }

    public static Vec3 rotate(Vec3 vec, float yaw, float pitch, float roll) {
        double yawRadians = Math.toRadians(yaw);
        double pitchRadians = Math.toRadians(pitch);
        double rollRadians = Math.toRadians(roll);

        double cosYaw = Math.cos(yawRadians);
        double sinYaw = Math.sin(yawRadians);
        double x = vec.x * cosYaw - vec.z * sinYaw;
        double z = vec.x * sinYaw + vec.z * cosYaw;
        double y = vec.y;

        double cosPitch = Math.cos(pitchRadians);
        double sinPitch = Math.sin(pitchRadians);
        double rotatedY = y * cosPitch - z * sinPitch;
        double rotatedZ = y * sinPitch + z * cosPitch;

        double cosRoll = Math.cos(rollRadians);
        double sinRoll = Math.sin(rollRadians);
        double rotatedX = x * cosRoll - rotatedY * sinRoll;
        double finalY = x * sinRoll + rotatedY * cosRoll;

        return new Vec3(rotatedX, finalY, rotatedZ);
    }
}
