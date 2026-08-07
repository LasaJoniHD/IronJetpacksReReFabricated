package com.blakebr0.ironjetpacks.client.handler;

import com.blakebr0.ironjetpacks.IronJetpacks;
import com.blakebr0.ironjetpacks.client.model.JetpackModel;
import com.blakebr0.ironjetpacks.item.JetpackItem;
import com.blakebr0.ironjetpacks.util.JetpackUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public final class JetpackArmorRenderer implements ArmorRenderer {
    private static final Identifier BASE_TEXTURE = IronJetpacks.id("textures/entity/equipment/humanoid/jetpack.png");
    private static final Identifier OVERLAY_TEXTURE = IronJetpacks.id("textures/entity/equipment/humanoid/jetpack_overlay.png");

    private final JetpackModel[] models;

    public JetpackArmorRenderer(net.minecraft.client.renderer.entity.EntityRendererProvider.Context context) {
        this.models = new JetpackModel[6];
        for (int i = 0; i < this.models.length; i++) {
            this.models[i] = new JetpackModel(context.bakeLayer(ModelHandler.JETPACK_LAYER), i);
        }
    }

    @Override
    public void render(PoseStack poseStack, SubmitNodeCollector collector, ItemStack stack,
                       HumanoidRenderState state, EquipmentSlot slot, int light,
                       HumanoidModel<HumanoidRenderState> contextModel) {
        if (slot != EquipmentSlot.CHEST || !(stack.getItem() instanceof JetpackItem)) return;

        var jetpack = JetpackUtils.getJetpack(stack);
        int barState = jetpack.creative ? 5 : energyBarState(stack);
        var model = models[Math.clamp(barState, 0, models.length - 1)];

        int color = jetpack.color;
        ArmorRenderer.submitTransformCopyingModel(
                contextModel, state, model, state, true, collector, poseStack,
                model.renderType(BASE_TEXTURE), light, OverlayTexture.NO_OVERLAY,
                color, null, 0, null
        );
        ArmorRenderer.submitTransformCopyingModel(
                contextModel, state, model, state, true, collector, poseStack,
                model.renderType(OVERLAY_TEXTURE), light, OverlayTexture.NO_OVERLAY,
                0xFFFFFFFF, null, 0, null
        );
    }

    private static int energyBarState(ItemStack stack) {
        var energy = JetpackUtils.getEnergyStorage(stack);
        long capacity = energy.getCapacity();
        if (capacity <= 0) return 5;
        return (int) Math.clamp(energy.getAmount() * 6L / capacity, 0L, 5L);
    }
}
