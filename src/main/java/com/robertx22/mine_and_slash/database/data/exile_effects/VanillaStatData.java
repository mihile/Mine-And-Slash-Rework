package com.robertx22.mine_and_slash.database.data.exile_effects;

import com.robertx22.mine_and_slash.uncommon.enumclasses.ModType;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.UUID;

public class VanillaStatData {

    float val;
    String uuid;
    String id;
    ModType type;

    public static VanillaStatData create(Holder<Attribute> attri, float val, ModType type, UUID uuid) {
        VanillaStatData data = new VanillaStatData();
        data.id = BuiltInRegistries.ATTRIBUTE.getKey(attri.value())
                .toString();
        data.uuid = uuid.toString();
        data.type = type;
        data.val = val;
        return data;
    }

    public Holder<Attribute> getAttribute() {
        return BuiltInRegistries.ATTRIBUTE.getHolder(ResourceLocation.parse(id)).orElseThrow();
    }

    public void applyVanillaStats(LivingEntity en, int stacks) {

        AttributeModifier mod = new AttributeModifier(ResourceLocation.fromNamespaceAndPath("mmorpg", "effect/" + uuid), val * stacks, type.operation);
        Holder<Attribute> attri = getAttribute();

        this.removeVanillaStats(en);

        if (en.getAttribute(attri) != null) {
            if (!en.getAttribute(attri)
                    .hasModifier(mod.id())) {
                en.getAttribute(attri)
                        .addTransientModifier(mod);
            }
        }

    }

    public void removeVanillaStats(LivingEntity en) {
        AttributeModifier mod = new AttributeModifier(ResourceLocation.fromNamespaceAndPath("mmorpg", "effect/" + uuid), val, type.operation);
        Holder<Attribute> attri = getAttribute();

        if (en.getAttribute(attri) != null) {
            if (en.getAttribute(attri)
                    .hasModifier(mod.id())) {
                en.getAttribute(attri)
                        .removeModifier(mod.id());
            }
        }
    }
}
