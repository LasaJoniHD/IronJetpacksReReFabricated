package com.blakebr0.cucumber.item;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import java.util.function.Function;

public class BaseArmorItem extends BaseItem {
    public BaseArmorItem(Identifier id, ArmorMaterial material, ArmorType type) {
        this(id, material, type, Function.identity());
    }

    public BaseArmorItem(Identifier id, ArmorMaterial material, ArmorType type, Function<Properties, Properties> properties) {
        super(id, properties.compose(p -> p.humanoidArmor(material, type)));
    }
}
