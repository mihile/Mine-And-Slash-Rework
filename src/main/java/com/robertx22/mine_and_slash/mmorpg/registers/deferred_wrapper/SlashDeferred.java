package com.robertx22.mine_and_slash.mmorpg.registers.deferred_wrapper;

import com.robertx22.mine_and_slash.database.data.profession.all.ProfessionMatItems;
import com.robertx22.mine_and_slash.database.data.profession.all.ProfessionProductItems;
import com.robertx22.mine_and_slash.mmorpg.SlashRef;
import com.robertx22.mine_and_slash.mmorpg.registers.common.*;
import com.robertx22.mine_and_slash.mmorpg.registers.common.items.*;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;

public class SlashDeferred {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, SlashRef.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, SlashRef.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, SlashRef.MODID);
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, SlashRef.MODID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, SlashRef.MODID);
    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(BuiltInRegistries.MENU, SlashRef.MODID);
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, SlashRef.MODID);
    public static final DeferredRegister<MobEffect> POTIONS = DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, SlashRef.MODID);
    public static final DeferredRegister<CreativeModeTab> TAB = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SlashRef.MODID);

    public static void initContainers(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
        BLOCK_ENTITIES.register(bus);
        SOUNDS.register(bus);
        ENTITIES.register(bus);
        CONTAINERS.register(bus);
        PARTICLES.register(bus);
        POTIONS.register(bus);
        TAB.register(bus);
    }


    public static void registerEntries() {

        SlashTabs.init();
        SlashPotions.init();
        SlashRecipeTypes.init();
        SlashSounds.init();
        SlashRecipeSers.init();

        SlashParticles.init();
        SlashEntities.init();
        SlashBlocks.init();
        SlashBlockEntities.init();
        SlashContainers.init();

        //items
        RarityItems.init();
        SkillGemsItems.init();
        SlashItems.init();
        SlashItems.GearItems.init();
        RuneItems.init();
        GemItems.init();

        ProfessionMatItems.init();
        ProfessionProductItems.init();
        SlashFeatures.init();


    }

}
