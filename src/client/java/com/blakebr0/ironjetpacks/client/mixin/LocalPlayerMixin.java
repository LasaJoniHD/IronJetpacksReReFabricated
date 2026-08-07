package com.blakebr0.ironjetpacks.client.mixin;

import com.blakebr0.ironjetpacks.handler.PlayerTickHandler;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin {
    @Inject(method = "aiStep", at = @At("TAIL"))
    private void ironjetpacks$tickJetpack(CallbackInfo info) {
        PlayerTickHandler.onPlayerTick((LocalPlayer) (Object) this);
    }
}
