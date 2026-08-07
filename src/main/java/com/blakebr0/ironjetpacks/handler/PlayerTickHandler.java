package com.blakebr0.ironjetpacks.handler;

import com.blakebr0.ironjetpacks.item.JetpackItem;
import com.blakebr0.ironjetpacks.mixin.ServerGamePacketListenerAccessor;
import com.blakebr0.ironjetpacks.util.JetpackUtils;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public final class PlayerTickHandler {
    public static void onPlayerTick(Player player) {
        var level = player.level();
        var stack = JetpackUtils.getEquippedJetpack(player);
        if (stack.isEmpty()) return;

        if (stack.getItem() instanceof JetpackItem && JetpackUtils.isEngineOn(stack)) {
            var hover = JetpackUtils.isHovering(stack);
            boolean ascending = InputHandler.isHoldingUp(player);
            boolean descending = InputHandler.isHoldingDown(player);
            boolean hoveringInAir = hover && !player.onGround();
            if (ascending || descending || hoveringInAir) {
                var jetpack = JetpackUtils.getJetpack(stack);
                double motionY = player.getDeltaMovement().y();
                double hoverSpeed = descending ? jetpack.speedHoverDescend : jetpack.speedHoverSlow;
                double currentAccel = jetpack.accelVert * (motionY < 0.3D ? 2.5D : 1.0D);
                double currentSpeedVertical = jetpack.speedVert * (player.isInWater() ? 0.4D : 1.0D);
                double usage = player.isSprinting() || InputHandler.isHoldingSprint(player) ? jetpack.usage * jetpack.sprintFuel : jetpack.usage;
                boolean creative = jetpack.creative;
                var energy = JetpackUtils.getEnergyStorage(stack);
                if (!player.isCreative() && !creative) {
                    long requiredEnergy = Math.max(1L, (long) Math.ceil(usage));
                    if (level.isClientSide()) {
                        // Client prediction must stop when its synchronized copy
                        // cannot pay this tick's cost.
                        if (energy.getAmount() < requiredEnergy) return;
                    } else {
                        // The server is authoritative: do not apply movement
                        // unless the complete tick cost was actually extracted.
                        try (var tx = Transaction.openOuter()) {
                            if (energy.extract(requiredEnergy, tx) < requiredEnergy) return;
                            tx.commit();
                        }
                    }
                }

                if (hover && player.isFallFlying()) player.stopFallFlying();

                {
                    double throttle = JetpackUtils.getThrottle(stack);
                    double verticalSprintMulti = motionY >= 0 && InputHandler.isHoldingSprint(player) ? jetpack.sprintSpeedVert : 1.0D;
                    if (ascending) {
                        if (!hover) fly(player, Math.min(motionY + currentAccel, currentSpeedVertical) * throttle * verticalSprintMulti);
                        else if (InputHandler.isHoldingDown(player)) fly(player, Math.min(motionY + currentAccel, -jetpack.speedHoverSlow));
                        else fly(player, Math.min(motionY + currentAccel, jetpack.speedHoverAscend) * throttle * verticalSprintMulti);
                    } else if (descending) {
                        fly(player, Math.min(motionY + currentAccel, -jetpack.speedHoverDescend));
                    } else {
                        fly(player, Math.min(motionY + currentAccel, -hoverSpeed));
                    }

                    double speedSideways = (player.isCrouching() ? jetpack.speedSide * 0.5F : jetpack.speedSide) * throttle;
                    double speedForward = (player.isSprinting() ? speedSideways * jetpack.sprintSpeed : speedSideways) * throttle;
                    if (!player.isFallFlying()) {
                        if (InputHandler.isHoldingForwards(player)) player.moveRelative(1, new Vec3(0, 0, speedForward));
                        if (InputHandler.isHoldingBackwards(player)) player.moveRelative(1, new Vec3(0, 0, -speedSideways * 0.8F));
                        if (InputHandler.isHoldingLeft(player)) player.moveRelative(1, new Vec3(speedSideways, 0, 0));
                        if (InputHandler.isHoldingRight(player)) player.moveRelative(1, new Vec3(-speedSideways, 0, 0));
                    }
                    if (!level.isClientSide()) {
                        player.fallDistance = 0.0F;
                        if (player instanceof ServerPlayer serverPlayer) {
                            ((ServerGamePacketListenerAccessor) serverPlayer.connection).ironjetpacks$setAboveGroundTickCount(0);
                        }
                    }
                }
            }
        }
    }

    private static void fly(Player player, double y) {
        var motion = player.getDeltaMovement();
        player.setDeltaMovement(motion.x, y, motion.z);
    }
}
