package com.blakebr0.ironjetpacks.client.handler;

import com.blakebr0.cucumber.lib.Tooltips;
import com.blakebr0.cucumber.util.Formatting;
import com.blakebr0.ironjetpacks.config.ModConfigs;
import com.blakebr0.ironjetpacks.item.JetpackItem;
import com.blakebr0.ironjetpacks.lib.ModTooltips;
import com.blakebr0.ironjetpacks.registry.Jetpack;
import com.blakebr0.ironjetpacks.util.JetpackUtils;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.ArrayList;
import java.util.List;

public final class JetpackTooltipHandler {
    private static final String CHEST_MODIFIER_KEY = "item.modifiers.chest";

    private JetpackTooltipHandler() {
    }

    public static void initialize() {
        ItemTooltipCallback.EVENT.register(JetpackTooltipHandler::appendAdvancedInfo);
    }

    private static void appendAdvancedInfo(ItemStack stack, Item.TooltipContext context, TooltipFlag flag, List<Component> lines) {
        if (!(stack.getItem() instanceof JetpackItem)) return;

        if (!ModConfigs.ENABLE_ADVANCED_INFO_TOOLTIPS.get()) return;

        var jetpack = JetpackUtils.getJetpack(stack);
        var advanced = new ArrayList<Component>();

        if (!Minecraft.getInstance().hasShiftDown()) {
            advanced.add(Tooltips.HOLD_SHIFT_FOR_INFO.toComponent().withStyle(ChatFormatting.GRAY));
            advanced.add(Component.literal(" "));
        } else {
            addAdvancedStats(advanced, jetpack);
        }

        int insertAt = findChestModifierHeader(lines);
        if (insertAt < 0) {
            lines.addAll(advanced);
        } else {
            lines.addAll(insertAt, advanced);
        }
    }

    private static int findChestModifierHeader(List<Component> lines) {
        String header = Component.translatable(CHEST_MODIFIER_KEY).getString();
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).getString();
            if (line.equals(header) || line.equals(header + ":")) return i;
        }
        return -1;
    }

    private static void addAdvancedStats(List<Component> lines, Jetpack jetpack) {
        lines.add(ModTooltips.FUEL_USAGE.args(Formatting.energyPerTick(jetpack.usage)).toComponent().withStyle(ChatFormatting.GRAY));
        lines.add(ModTooltips.VERTICAL_SPEED.args(jetpack.speedVert).toComponent().withStyle(ChatFormatting.GRAY));
        lines.add(ModTooltips.VERTICAL_ACCELERATION.args(jetpack.accelVert).toComponent().withStyle(ChatFormatting.GRAY));
        lines.add(ModTooltips.HORIZONTAL_SPEED.args(jetpack.speedSide).toComponent().withStyle(ChatFormatting.GRAY));
        lines.add(ModTooltips.HOVER_SPEED.args(jetpack.speedHoverSlow).toComponent().withStyle(ChatFormatting.GRAY));
        lines.add(ModTooltips.HOVER_ASCEND_SPEED.args(jetpack.speedHoverAscend).toComponent().withStyle(ChatFormatting.GRAY));
        lines.add(ModTooltips.HOVER_DESCEND_SPEED.args(jetpack.speedHoverDescend).toComponent().withStyle(ChatFormatting.GRAY));
        lines.add(ModTooltips.SPRINT_MODIFIER.args(jetpack.sprintSpeed).toComponent().withStyle(ChatFormatting.GRAY));
        lines.add(ModTooltips.SPRINT_VERTICAL_MODIFIER.args(jetpack.sprintSpeedVert).toComponent().withStyle(ChatFormatting.GRAY));
        lines.add(ModTooltips.SPRINT_FUEL_MODIFIER.args(jetpack.sprintFuel).toComponent().withStyle(ChatFormatting.GRAY));
        lines.add(Component.literal(" "));
    }
}
