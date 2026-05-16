package com.robertx22.mine_and_slash.event_hooks.damage_hooks.util;

import com.robertx22.mine_and_slash.saveclasses.item_classes.GearItemData;
import com.robertx22.mine_and_slash.uncommon.datasaving.StackSaving;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class WeaponFinderUtil {

    public static ItemStack getWeapon(LivingEntity source, Entity sourceEntity) {

        if (!(source instanceof LivingEntity)) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = source.getMainHandItem();
        GearItemData gear = StackSaving.GEARS.loadFrom(stack);


        if (gear == null) {
            if (sourceEntity != null && source instanceof Player) {
                ItemStack wep = getWeaponStackFromThrownEntity(sourceEntity);
                if (wep != null) {
                    gear = StackSaving.GEARS.loadFrom(wep);
                    if (gear != null) {
                        return wep;
                    }
                }
            }
        }

        if (gear != null) {
            return stack;
        } else {
            return ItemStack.EMPTY;
        }
    }


    private static ItemStack getWeaponStackFromThrownEntity(Entity en) {
        try {
            for (SynchedEntityData.DataValue<?> entry : en.getEntityData().getNonDefaultValues()
            ) {
                if (entry.value() instanceof ItemStack) {
                    GearItemData gear = StackSaving.GEARS.loadFrom((ItemStack) entry.value());
                    if (gear != null) {
                        return (ItemStack) entry.value();
                    }
                }
            }
        } catch (Exception e) {
        }

        try {
            CompoundTag nbt = new CompoundTag();
            en.saveWithoutId(nbt);

            ItemStack stack = ItemStack.EMPTY;

            for (String key : nbt.getAllKeys()) {
                if (stack == null || stack.isEmpty()) {
                    try {
                        Tag tag = nbt.get(key);
                        ItemStack s = tryGetStackFromNbt(en.registryAccess(), tag);

                        if (!s.isEmpty() && StackSaving.GEARS.has(s)) {
                            return s;
                        }

                        if (tag instanceof CompoundTag nbt2) {
                            for (String key2 : nbt2.getAllKeys()) {
                                ItemStack s2 = tryGetStackFromNbt(en.registryAccess(), nbt2.get(key2));
                                if (!s2.isEmpty() && StackSaving.GEARS.has(s2)) {
                                    return s2;

                                }
                            }

                        }
                    } catch (Exception e) {
                    }
                }

            }

            if (stack == null) {
                stack = ItemStack.EMPTY;
            }

            return stack;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ItemStack.EMPTY;
    }

    private static ItemStack tryGetStackFromNbt(HolderLookup.Provider provider, Tag nbt) {
        if (nbt instanceof CompoundTag compound && compound.contains("id", Tag.TAG_STRING)) {
            ItemStack s = ItemStack.parseOptional(provider, compound);
            if (s != null && !s.isEmpty()) {
                return s;

            }
        }

        return ItemStack.EMPTY;
    }


}
