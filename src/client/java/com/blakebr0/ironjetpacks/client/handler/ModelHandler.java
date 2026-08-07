package com.blakebr0.ironjetpacks.client.handler;

import com.blakebr0.ironjetpacks.client.model.JetpackModel;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.Identifier;

public final class ModelHandler {
    public static final ModelLayerLocation JETPACK_LAYER = new ModelLayerLocation(Identifier.fromNamespaceAndPath("ironjetpacks", "jetpack"), "main");

    private ModelHandler() {
    }

    public static LayerDefinition createLayer() {
        return JetpackModel.createArmorLayer();
    }
}
