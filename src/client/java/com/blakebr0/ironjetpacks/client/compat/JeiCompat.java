package com.blakebr0.ironjetpacks.client.compat;

import com.blakebr0.ironjetpacks.IronJetpacks;
import com.blakebr0.ironjetpacks.init.ModDataComponentTypes;
import com.blakebr0.ironjetpacks.init.ModItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.resources.Identifier;

@JeiPlugin
public final class JeiCompat implements IModPlugin {
    private static final Identifier UID = IronJetpacks.id("jei_plugin");

    @Override
    public Identifier getPluginUid() {
        return UID;
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.registerFromDataComponentTypes(
                ModItems.JETPACK,
                ModDataComponentTypes.JETPACK_ID
        );
        registration.registerFromDataComponentTypes(
                ModItems.CELL,
                ModDataComponentTypes.JETPACK_ID
        );
        registration.registerFromDataComponentTypes(
                ModItems.CAPACITOR,
                ModDataComponentTypes.JETPACK_ID
        );
        registration.registerFromDataComponentTypes(
                ModItems.THRUSTER,
                ModDataComponentTypes.JETPACK_ID
        );
    }
}
