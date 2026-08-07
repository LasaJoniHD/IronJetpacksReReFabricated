package com.blakebr0.ironjetpacks.item;

import com.blakebr0.cucumber.iface.IColored;
import com.blakebr0.cucumber.iface.IComponentInitializer;
import com.blakebr0.cucumber.item.BaseArmorItem;
import com.blakebr0.cucumber.util.Formatting;
import com.blakebr0.ironjetpacks.config.ModConfigs;
import com.blakebr0.ironjetpacks.lib.ModArmorMaterials;
import com.blakebr0.ironjetpacks.lib.ModTooltips;
import com.blakebr0.ironjetpacks.util.JetpackUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.enchantment.Enchantable;
import net.minecraft.world.item.equipment.ArmorType;

import java.util.function.Consumer;

public class JetpackItem extends BaseArmorItem implements IColored, IComponentInitializer {
    public JetpackItem(Identifier id) {
        super(id, ModArmorMaterials.JETPACK, ArmorType.CHESTPLATE, p -> p
                .stacksTo(1)
                .component(DataComponents.UNBREAKABLE, Unit.INSTANCE)
        );
    }

    @Override
    public Component getName(ItemStack stack) {
        var jetpack = JetpackUtils.getJetpack(stack);
        return Component.translatable("item.ironjetpacks.jetpack", jetpack.getDisplayName());
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        var energy = JetpackUtils.getEnergyStorage(stack);
        var capacity = (int) energy.getCapacity();
        if (capacity <= 0) return 0;
        var stored = capacity - (int) energy.getAmount();
        return Math.round(13.0F - stored * 13.0F / capacity);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        var energy = JetpackUtils.getEnergyStorage(stack);
        var capacity = (int) energy.getCapacity();
        if (capacity <= 0) return Mth.hsvToRgb(0.0F, 1.0F, 1.0F);
        float f = Math.max(0.0F, (float) (int) energy.getAmount() / capacity);
        return Mth.hsvToRgb(f / 3.0F, 1.0F, 1.0F);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return !JetpackUtils.getJetpack(stack).creative;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag flag) {
        builder.accept(Component.translatable("itemGroup.ironjetpacks").withStyle(ChatFormatting.BLUE));

        var jetpack = JetpackUtils.getJetpack(stack);

        if (flag.isAdvanced()) {
            builder.accept(ModTooltips.JETPACK_ID.args(jetpack.getId().toString()).color(ChatFormatting.DARK_GRAY).toComponent());
            builder.accept(Component.literal(" "));
        }

        if (!jetpack.creative) {
            var energy = JetpackUtils.getEnergyStorage(stack);
            builder.accept(Formatting.number(energy.getAmount()).append(" / ").append(Formatting.energy(energy.getCapacity())).withStyle(ChatFormatting.GRAY));
        } else {
            builder.accept(ModTooltips.INFINITE.toComponent().append(" FE"));
        }

        var tier = ModTooltips.TIER.color(jetpack.rarity.color()).args(jetpack.creative ? "C" : jetpack.tier).toComponent();
        var engine = ModTooltips.ENGINE.color(JetpackUtils.isEngineOn(stack) ? ChatFormatting.GREEN : ChatFormatting.RED).toComponent();
        var hover = ModTooltips.HOVER.color(JetpackUtils.isHovering(stack) ? ChatFormatting.GREEN : ChatFormatting.RED).toComponent();
        var hud = ModTooltips.HUD.color(JetpackUtils.isHUDEnabled(stack) ? ChatFormatting.GREEN : ChatFormatting.RED).toComponent();

        builder.accept(Component.empty()
                .append(tier).append(" | ").withStyle(ChatFormatting.GRAY)
                .append(engine).append(" | ").withStyle(ChatFormatting.GRAY)
                .append(hover).append(" | ").withStyle(ChatFormatting.GRAY)
                .append(hud));

        builder.accept(ModTooltips.THROTTLE.args(Component.literal((int) (JetpackUtils.getThrottle(stack) * 100) + "%")).toComponent()
                .withStyle(ChatFormatting.GRAY));

    }

    @Override
    public int getColor(int i, ItemStack stack) {
        var jetpack = JetpackUtils.getJetpack(stack);
        return i == 1 ? jetpack.color : -1;
    }

    @Override
    public void initialize(ItemStack stack) {
        var jetpack = JetpackUtils.getJetpack(stack);
        stack.set(DataComponents.ATTRIBUTE_MODIFIERS, jetpack.createAttributeModifiers());

        if (stack.isEnchanted()) {
            var rarity = switch (jetpack.rarity) {
                case COMMON, UNCOMMON -> Rarity.RARE;
                case RARE -> Rarity.EPIC;
                case EPIC -> jetpack.rarity;
            };
            stack.set(DataComponents.RARITY, rarity);
        } else {
            stack.set(DataComponents.RARITY, jetpack.rarity);
        }

        if (ModConfigs.ENCHANTABLE_JETPACKS.get()) {
            stack.set(DataComponents.ENCHANTABLE, new Enchantable(jetpack.enchantablilty));
        }
    }
}
