package com.robertx22.mine_and_slash.characters;

import com.robertx22.mine_and_slash.database.data.stats.types.resources.blood.Blood;
import com.robertx22.mine_and_slash.database.data.stats.types.resources.energy.Energy;
import com.robertx22.mine_and_slash.database.data.stats.types.resources.health.Health;
import com.robertx22.mine_and_slash.database.data.stats.types.resources.magic_shield.MagicShield;
import com.robertx22.mine_and_slash.database.data.stats.types.resources.mana.Mana;
import com.robertx22.mine_and_slash.mmorpg.MMORPG;
import com.robertx22.mine_and_slash.mmorpg.SlashRef;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerStats {
    public static final ResourceLocation LEVELS_GAINED = ResourceLocation.fromNamespaceAndPath(SlashRef.MODID, "levels_gained");
    public static final HashMap<String, ResourceLocation> REGISTERED_STATS = new HashMap<>();

    private static class Registrations {
        public final Set<ResourceLocation> customStats = new LinkedHashSet<>();

        @SubscribeEvent
        public void register(RegisterEvent event) {
            event.register(Registries.CUSTOM_STAT, helper -> customStats.forEach(it -> helper.register(it, it)));
        }
    }

    private static final Map<String, Registrations> registrations = new ConcurrentHashMap<>();

    public static void registerCustomStat(ResourceLocation identifier) {
        getActiveRegistrations().customStats.add(identifier);
    }

    public static void register() {
        MMORPG.MOD_BUS.register(getActiveRegistrations());
    }

    private static Registrations getActiveRegistrations() {
        return registrations.computeIfAbsent(ModLoadingContext.get().getActiveNamespace(), it -> new Registrations());
    }

    public static void addReg(String id) {
        REGISTERED_STATS.put(id, ResourceLocation.fromNamespaceAndPath(SlashRef.MODID, id));
    }

    public static void initialize() {
        addReg(Health.getInstance().GUID());
        addReg(Mana.getInstance().GUID());
        addReg(Blood.getInstance().GUID());
        addReg(Energy.getInstance().GUID());
        addReg(MagicShield.getInstance().GUID());
        registerCustomStat(LEVELS_GAINED);
        for (ResourceLocation rl : REGISTERED_STATS.values())
            registerCustomStat(rl);
    }

}
