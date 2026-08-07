package com.blakebr0.ironjetpacks.mixin;

import com.blakebr0.cucumber.iface.IComponentInitializer;
import net.minecraft.core.Holder;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Inject(method = "<init>(Lnet/minecraft/core/Holder;ILnet/minecraft/core/component/PatchedDataComponentMap;)V", at = @At("RETURN"))
    private void ironjetpacks$initializeComponents(Holder<Item> item, int count, PatchedDataComponentMap components, CallbackInfo info) {
        if (item.value() instanceof IComponentInitializer initializer) {
            initializer.initialize((ItemStack) (Object) this);
        }
    }
}
