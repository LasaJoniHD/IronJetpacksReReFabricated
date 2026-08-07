package com.blakebr0.ironjetpacks.client.handler;

import com.blakebr0.cucumber.helper.VecHelper;
import com.blakebr0.ironjetpacks.client.sound.JetpackSound;
import com.blakebr0.ironjetpacks.config.ModConfigs;
import com.blakebr0.ironjetpacks.item.JetpackItem;
import com.blakebr0.ironjetpacks.util.JetpackUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ParticleStatus;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.Vec3;
import java.util.concurrent.ThreadLocalRandom;

public final class JetpackClientHandler {
    private JetpackClientHandler() {
    }

    public static void tick() {
        var mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null || mc.isPaused()) return;
        var chest = JetpackUtils.getEquippedJetpack(mc.player);
        if (!(chest.getItem() instanceof JetpackItem) || !JetpackUtils.isFlying(mc.player)) return;

        if (ModConfigs.ENABLE_JETPACK_PARTICLES.get() && mc.options.particles().get() != ParticleStatus.MINIMAL) {
            var jetpack = JetpackUtils.getJetpack(chest);
            var playerPos = mc.player.position().add(0, 1.5, 0);
            float random = (ThreadLocalRandom.current().nextFloat() - 0.5F) * 0.1F;
            double[] sneakBonus = mc.player.isCrouching() ? new double[] {-0.30D, -0.10D} : new double[] {0.0D, 0.0D};

            // These are model-local nozzle offsets. VecHelper rotates them by
            // yBodyRot so the nozzles stay attached to the jetpack as the player
            // turns. Positive yaw (clockwise top-down) is used — matching MC's
            // convention — so the left/right nozzle positions are correct at all
            // orientations.
            var leftNozzle = VecHelper.rotate(
                    new Vec3(-0.18D, -0.90D + sneakBonus[1], -0.30D + sneakBonus[0]),
                    mc.player.yBodyRot, 0, 0);
            var rightNozzle = VecHelper.rotate(
                    new Vec3(0.18D, -0.90D + sneakBonus[1], -0.30D + sneakBonus[0]),
                    mc.player.yBodyRot, 0, 0);
            var inheritedMotion = mc.player.getDeltaMovement().scale(jetpack.speedSide);
            spawn(mc, playerPos.add(leftNozzle).add(inheritedMotion), random, Vec3.ZERO);
            spawn(mc, playerPos.add(rightNozzle).add(inheritedMotion), random, Vec3.ZERO);
        }
        if (ModConfigs.ENABLE_JETPACK_SOUNDS.get() && !JetpackSound.playing(mc.player.getId())) {
            mc.getSoundManager().play(new JetpackSound(mc.player));
        }
    }

    private static void spawn(Minecraft mc, Vec3 position, float random, Vec3 inheritedMotion) {
        mc.particleEngine.createParticle(ParticleTypes.FLAME, position.x, position.y, position.z,
                inheritedMotion.x + random, inheritedMotion.y - 0.2D, inheritedMotion.z + random);
        mc.particleEngine.createParticle(ParticleTypes.SMOKE, position.x, position.y, position.z,
                inheritedMotion.x + random, inheritedMotion.y - 0.2D, inheritedMotion.z + random);
    }
}
