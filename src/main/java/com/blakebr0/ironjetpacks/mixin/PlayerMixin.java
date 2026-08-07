package com.blakebr0.ironjetpacks.mixin;

import com.blakebr0.ironjetpacks.handler.PlayerTickHandler;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin {
    @Inject(method = "aiStep", at = @At("TAIL"))
    private void ironjetpacks$tickJetpack(CallbackInfo info) {
        // LocalPlayer has its own client-only hook. Keep this common hook
        // authoritative for dedicated/server-side players only.
        Player player = (Player) (Object) this;
        if (!player.level().isClientSide()) PlayerTickHandler.onPlayerTick(player);
    }
}
