package com.blakebr0.ironjetpacks.client.handler;

import com.blakebr0.ironjetpacks.IronJetpacks;
import com.blakebr0.ironjetpacks.handler.InputHandler;
import com.blakebr0.ironjetpacks.item.JetpackItem;
import com.blakebr0.ironjetpacks.lib.ModTooltips;
import com.blakebr0.ironjetpacks.network.payloads.*;
import com.blakebr0.ironjetpacks.util.JetpackUtils;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

public final class KeybindHandler {
    private static KeyMapping keyEngine, keyHover, keyHUD, keyAscend, keyDescend, keyIncrementThrottle, keyDecrementThrottle;
    private static boolean up, down, forwards, backwards, left, right, sprint;
    private static boolean connected;

    private KeybindHandler() {
    }

    public static void initialize() {
        var category = KeyMapping.Category.register(IronJetpacks.id("keybindings"));
        keyEngine = register(new KeyMapping("keybind.ironjetpacks.engine", GLFW.GLFW_KEY_V, category));
        keyHover = register(new KeyMapping("keybind.ironjetpacks.hover", GLFW.GLFW_KEY_H, category));
        keyHUD = register(new KeyMapping("keybind.ironjetpacks.hud", InputConstants.UNKNOWN.getValue(), category));
        keyAscend = register(new KeyMapping("keybind.ironjetpacks.ascend", GLFW.GLFW_KEY_SPACE, category));
        keyDescend = register(new KeyMapping("keybind.ironjetpacks.descend", GLFW.GLFW_KEY_LEFT_CONTROL, category));
        keyIncrementThrottle = register(new KeyMapping("keybind.ironjetpacks.increment_throttle", GLFW.GLFW_KEY_PERIOD, category));
        keyDecrementThrottle = register(new KeyMapping("keybind.ironjetpacks.decrement_throttle", GLFW.GLFW_KEY_COMMA, category));
        connected = false;
    }

    private static KeyMapping register(KeyMapping mapping) {
        return KeyMappingHelper.registerKeyMapping(mapping);
    }

    public static void tick() {
        var mc = Minecraft.getInstance();
        if (mc.player == null || mc.getConnection() == null) {
            connected = false;
            return;
        }
        boolean firstTickAfterConnect = !connected;
        connected = true;

        var settings = mc.options;
        boolean upNow = keyAscend.isUnbound() ? settings.keyJump.isDown() : keyAscend.isDown();
        boolean downNow = keyDescend.isUnbound() ? settings.keyShift.isDown() : keyDescend.isDown();
        boolean forwardsNow = settings.keyUp.isDown();
        boolean backwardsNow = settings.keyDown.isDown();
        boolean leftNow = settings.keyLeft.isDown();
        boolean rightNow = settings.keyRight.isDown();
        boolean sprintNow = settings.keySprint.isDown();

        boolean changed = firstTickAfterConnect || upNow != up || downNow != down || forwardsNow != forwards
                || backwardsNow != backwards || leftNow != left || rightNow != right || sprintNow != sprint;
        up = upNow; down = downNow; forwards = forwardsNow; backwards = backwardsNow;
        left = leftNow; right = rightNow; sprint = sprintNow;
        if (changed) {
            update(up, down, forwards, backwards, left, right, sprint);
        } else {
            // Keep local prediction responsive without sending a redundant packet.
            InputHandler.update(mc.player, up, down, forwards, backwards, left, right, sprint);
        }

        var stack = JetpackUtils.getEquippedJetpack(mc.player);
        if (stack.getItem() instanceof JetpackItem) handleInput(mc.player, stack);
    }

    public static void update(boolean up, boolean down, boolean forwards, boolean backwards, boolean left, boolean right, boolean sprint) {
        var player = Minecraft.getInstance().player;
        if (player == null) return;
        ClientPlayNetworking.send(new UpdateInputPayload(up, down, forwards, backwards, left, right, sprint));
        InputHandler.update(player, up, down, forwards, backwards, left, right, sprint);
    }

    private static void handleInput(Player player, ItemStack stack) {
        if (keyEngine.consumeClick()) {
            boolean on = JetpackUtils.toggleEngine(stack);
            ClientPlayNetworking.send(new ToggleEnginePayload(on));
            player.sendOverlayMessage(ModTooltips.TOGGLE_ENGINE.args(status(on)).toComponent());
        }
        if (keyHover.consumeClick()) {
            boolean on = JetpackUtils.toggleHover(stack);
            ClientPlayNetworking.send(new ToggleHoverPayload(on));
            player.sendOverlayMessage(ModTooltips.TOGGLE_HOVER.args(status(on)).toComponent());
        }
        if (keyHUD.consumeClick()) {
            boolean on = JetpackUtils.toggleHUD(stack);
            ClientPlayNetworking.send(new ToggleHUDPayload());
            player.sendOverlayMessage(ModTooltips.TOGGLE_HUD.args(status(on)).toComponent());
        }
        if (keyIncrementThrottle.consumeClick()) {
            double throttle = JetpackUtils.incrementThrottle(stack);
            ClientPlayNetworking.send(new IncrementThrottlePayload());
            player.sendOverlayMessage(ModTooltips.CHANGE_THROTTLE.args(Component.literal((int) (throttle * 100) + "%").withStyle(ChatFormatting.GREEN)).toComponent());
        }
        if (keyDecrementThrottle.consumeClick()) {
            double throttle = JetpackUtils.decrementThrottle(stack);
            ClientPlayNetworking.send(new DecrementThrottlePayload());
            player.sendOverlayMessage(ModTooltips.CHANGE_THROTTLE.args(Component.literal((int) (throttle * 100) + "%").withStyle(ChatFormatting.RED)).toComponent());
        }
    }

    private static Component status(boolean on) {
        return (on ? ModTooltips.ON : ModTooltips.OFF).color(on ? ChatFormatting.GREEN : ChatFormatting.RED).toComponent();
    }
}
