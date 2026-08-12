package com.blakebr0.ironjetpacks.client.handler;

import com.blakebr0.cucumber.util.Formatting;
import com.blakebr0.ironjetpacks.IronJetpacks;
import com.blakebr0.ironjetpacks.config.ModConfigs;
import com.blakebr0.ironjetpacks.item.JetpackItem;
import com.blakebr0.ironjetpacks.lib.ModTooltips;
import com.blakebr0.ironjetpacks.util.JetpackUtils;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public final class HudHandler {
    private static final Identifier ID = IronJetpacks.id("jetpack_hud");
    // hud.png holds the HUD artwork at half resolution: the frame (28x156) and
    // energy fill (28x156) of the original 256x256 design are 14x78 each here.
    // The blits keep the 256x256 UV scale the art was authored for, which maps
    // exactly onto this file (u=28 -> x=14, v=156 -> y=78). Do NOT change the
    // texture dimensions to 128x128; it would sample the wrong regions.
    private static final Identifier HUD_TEXTURE = IronJetpacks.id("textures/gui/hud.png");

    // 0 = fully shown, 1 = fully hidden.
    private static double animationProgress = 0.0D;
    private static boolean wasHidden = false;

    private HudHandler() {
    }

    public static void initialize() {
        HudElementRegistry.addLast(ID, (graphics, delta) -> render(graphics));
    }

    private static void render(GuiGraphicsExtractor graphics) {
        var mc = Minecraft.getInstance();
        var player = mc.player;
        if (player == null || !isVisible(mc)) return;

        var chest = JetpackUtils.getEquippedJetpack(player);
        if (!(chest.getItem() instanceof JetpackItem)) {
            wasHidden = true;
            animationProgress = 1.0D;
            return;
        }

        boolean enabled = JetpackUtils.isHUDEnabled(chest);
        if (enabled && animationProgress > 0.0D) {
            wasHidden = false;
        } else if (!enabled && animationProgress < 1.0D) {
            wasHidden = true;
        }

        double animationStep = wasHidden
                ? ModConfigs.HUD_ANIMATION_SPEED.get()
                : -ModConfigs.HUD_ANIMATION_SPEED.get();
        animationProgress = Math.max(0.0D, Math.min(1.0D, animationProgress + animationStep));
        if (animationProgress >= 1.0D) return;

        var pos = getHudPos(animationProgress);
        if (pos == null) return;

        int xPos = (int) (pos.x / 0.33D) - 18;
        int yPos = (int) (pos.y / 0.33D) - 78;

        var pose = graphics.pose();
        pose.pushMatrix();
        pose.scale(0.33F, 0.33F);
        graphics.blit(RenderPipelines.GUI_TEXTURED, HUD_TEXTURE,
                xPos, yPos, 0, 0, 28, 156, 256, 256);
        int energyBar = getEnergyBarScaled(chest);
        graphics.blit(RenderPipelines.GUI_TEXTURED, HUD_TEXTURE,
                xPos, 166 - energyBar + yPos - 10,
                28, 156 - energyBar, 28, energyBar, 256, 256);
        pose.popMatrix();

        var fuel = getFuelComponent(chest);
        var throttle = getThrottleComponent(chest);
        var engine = getEngineComponent(chest);
        var hover = getHoverComponent(chest);
        if (pos.side == 1) {
            graphics.text(mc.font, fuel, pos.x - 8 - mc.font.width(fuel), pos.y - 21, 0xFF404040);
            graphics.text(mc.font, throttle, pos.x - 8 - mc.font.width(throttle), pos.y - 6, 0xFF404040);
            graphics.text(mc.font, engine, pos.x - 8 - mc.font.width(engine), pos.y + 4, 0xFF404040);
            graphics.text(mc.font, hover, pos.x - 8 - mc.font.width(hover), pos.y + 14, 0xFF404040);
        } else {
            graphics.text(mc.font, fuel, pos.x + 6, pos.y - 21, 0xFF404040);
            graphics.text(mc.font, throttle, pos.x + 6, pos.y - 6, 0xFF404040);
            graphics.text(mc.font, engine, pos.x + 6, pos.y + 4, 0xFF404040);
            graphics.text(mc.font, hover, pos.x + 6, pos.y + 14, 0xFF404040);
        }
    }

    private static HudPos getHudPos(double progress) {
        var window = Minecraft.getInstance().getWindow();
        int xOffset = ModConfigs.HUD_OFFSET_X.get();
        int yOffset = ModConfigs.HUD_OFFSET_Y.get();
        int width = window.getGuiScaledWidth();
        int height = window.getGuiScaledHeight();

        return switch (ModConfigs.HUD_POSITION.get()) {
            case 0 -> new HudPos(10 + xOffset, 30 + yOffset, 0, progress);
            case 1 -> new HudPos(10 + xOffset, height / 2 + yOffset, 0, progress);
            case 2 -> new HudPos(10 + xOffset, height - 30 + yOffset, 0, progress);
            case 3 -> new HudPos(width - 8 - xOffset, 30 + yOffset, 1, progress);
            case 4 -> new HudPos(width - 8 - xOffset, height / 2 + yOffset, 1, progress);
            case 5 -> new HudPos(width - 8 - xOffset, height - 30 + yOffset, 1, progress);
            default -> null;
        };
    }

    private static int getOffset(int coordinate, int side, double progress) {
        return side == 0
                ? (int) (coordinate - progress * 70.0D)
                : (int) (coordinate + progress * 70.0D);
    }

    private static int getEnergyBarScaled(ItemStack stack) {
        var jetpack = JetpackUtils.getJetpack(stack);
        if (jetpack.creative) return 156;

        var energy = JetpackUtils.getEnergyStorage(stack);
        long capacity = energy.getCapacity();
        long amount = energy.getAmount();
        return capacity != 0L && amount != 0L
                ? (int) (amount * 156L / capacity)
                : 0;
    }

    private static Component getFuelComponent(ItemStack stack) {
        var jetpack = JetpackUtils.getJetpack(stack);
        if (jetpack.creative) {
            return Component.literal(ModTooltips.INFINITE.toComponent().getString() + " FE")
                    .withStyle(ChatFormatting.GRAY);
        }
        return Component.literal(Formatting.formatEnergy(JetpackUtils.getEnergyStorage(stack).getAmount()))
                .withStyle(ChatFormatting.GRAY);
    }

    private static Component getThrottleComponent(ItemStack stack) {
        return Component.literal("T: " + (int) (JetpackUtils.getThrottle(stack) * 100) + "%")
                .withStyle(ChatFormatting.GRAY);
    }

    private static Component getEngineComponent(ItemStack stack) {
        return Component.literal("E: ")
                .append(ModTooltips.getStatusComponent(JetpackUtils.isEngineOn(stack)))
                .withStyle(ChatFormatting.GRAY);
    }

    private static Component getHoverComponent(ItemStack stack) {
        return Component.literal("H: ")
                .append(ModTooltips.getStatusComponent(JetpackUtils.isHovering(stack)))
                .withStyle(ChatFormatting.GRAY);
    }

    private static boolean isVisible(Minecraft mc) {
        return ModConfigs.ENABLE_HUD.get()				&& (ModConfigs.SHOW_HUD_OVER_CHAT.get() || !(mc.gui.screen() instanceof ChatScreen))
				&& !mc.gui.hud.isHidden()
                && !mc.getDebugOverlay().showDebugScreen();
    }

    private record HudPos(int x, int y, int side) {
        private HudPos(int x, int y, int side, double progress) {
            this(getOffset(x, side, progress), y, side);
        }
    }
}
