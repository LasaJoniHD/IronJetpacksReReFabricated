package com.blakebr0.ironjetpacks.lib;

import com.blakebr0.ironjetpacks.IronJetpacks;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;
import java.util.EnumMap;

public final class ModArmorMaterials {
    private static final ResourceKey<EquipmentAsset> JETPACK_ASSET = ResourceKey.create(EquipmentAssets.ROOT_ID, IronJetpacks.id("jetpack"));
    public static final ArmorMaterial JETPACK = new ArmorMaterial(
            0,
            Util.make(new EnumMap<>(ArmorType.class), map -> map.put(ArmorType.CHESTPLATE, 0)),
            10, SoundEvents.ARMOR_EQUIP_GENERIC, 0.0F, 0.0F, ModTags.NONE, JETPACK_ASSET
    );
}
