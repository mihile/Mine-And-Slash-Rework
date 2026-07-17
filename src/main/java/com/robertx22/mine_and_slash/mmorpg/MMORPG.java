package com.robertx22.mine_and_slash.mmorpg;

import com.robertx22.addons.dungeon_realm.DungeonAddonEvents;
import com.robertx22.addons.orbs_of_crafting.currency.reworked.addon.OrbAddonEvents;
import com.robertx22.library_of_exile.events.base.EventConsumer;
import com.robertx22.library_of_exile.events.base.ExileEvents;
import com.robertx22.library_of_exile.localization.ExileLangFile;
import com.robertx22.library_of_exile.localization.ExileTranslation;
import com.robertx22.library_of_exile.localization.TranslationBuilder;
import com.robertx22.library_of_exile.compat.SimpleChannel;
import com.robertx22.library_of_exile.registry.ExileRegistryType;
import com.robertx22.library_of_exile.registry.helpers.OrderedModConstructor;
import com.robertx22.library_of_exile.registry.register_info.HardcodedRegistration;
import com.robertx22.library_of_exile.registry.register_info.ModRequiredRegisterInfo;
import com.robertx22.library_of_exile.registry.register_info.SeriazableRegistration;
import com.robertx22.library_of_exile.registry.util.ExileRegistryUtil;
import com.robertx22.library_of_exile.utils.Watch;
import com.robertx22.mine_and_slash.a_libraries.curios.CurioEvents;
import com.robertx22.mine_and_slash.a_libraries.curios.RefCurio;
import com.robertx22.mine_and_slash.a_libraries.neat.NeatForgeConfig;
import com.robertx22.mine_and_slash.aoe_data.database.stat_conditions.StatConditions;
import com.robertx22.mine_and_slash.aoe_data.database.stat_effects.StatEffects;
import com.robertx22.mine_and_slash.aoe_data.database.stats.Stats;
import com.robertx22.mine_and_slash.aoe_data.datapacks.lang_file.CreateLangFile;
import com.robertx22.mine_and_slash.characters.PlayerStats;
import com.robertx22.mine_and_slash.config.forge.ClientConfigs;
import com.robertx22.mine_and_slash.config.forge.ServerContainer;
import com.robertx22.mine_and_slash.config.forge.compat.CompatConfig;
import com.robertx22.mine_and_slash.database.data.profession.ProfessionEvents;
import com.robertx22.mine_and_slash.database.data.profession.all.ProfessionRecipes;
import com.robertx22.mine_and_slash.database.data.spells.components.conditions.EffectCondition;
import com.robertx22.mine_and_slash.database.data.spells.map_fields.MapField;
import com.robertx22.mine_and_slash.database.data.stats.layers.StatLayers;
import com.robertx22.mine_and_slash.database.data.stats.priority.StatPriority;
import com.robertx22.mine_and_slash.maps.MapEvents;
import com.robertx22.mine_and_slash.mixin_ducks.tooltip.ItemTooltipsRegister;
import com.robertx22.mine_and_slash.mmorpg.event_registers.CommonEvents;
import com.robertx22.mine_and_slash.mmorpg.init.ClientRegistration;
import com.robertx22.mine_and_slash.mmorpg.registers.client.S2CPacketRegister;
import com.robertx22.mine_and_slash.mmorpg.registers.common.C2SPacketRegister;
import com.robertx22.mine_and_slash.mmorpg.registers.common.SlashAttachments;
import com.robertx22.mine_and_slash.mmorpg.registers.common.SlashCapabilities;
import com.robertx22.mine_and_slash.mmorpg.registers.common.SlashItemTags;
import com.robertx22.mine_and_slash.mmorpg.registers.common.items.SlashItems;
import com.robertx22.mine_and_slash.tags.ModTags;
import com.robertx22.mine_and_slash.uncommon.coins.Coin;
import com.robertx22.mine_and_slash.uncommon.datasaving.StackSaving;
import com.robertx22.mine_and_slash.uncommon.effectdatas.rework.action.StatEffect;
import com.robertx22.mine_and_slash.uncommon.effectdatas.rework.condition.StatCondition;
import com.robertx22.mine_and_slash.uncommon.interfaces.data_items.VanillaRarities;
import com.robertx22.test.test2.SchemaTest;
import net.minecraft.ChatFormatting;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import net.neoforged.fml.ModContainer;
import top.theillusivec4.curios.api.SlotTypeMessage;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.function.Consumer;

@Mod(SlashRef.MODID)
public class MMORPG {

    // DISABLE WHEN PUBLIC BUILD
    public static boolean RUN_DEV_TOOLS = false;

    public static SeriazableRegistration SERIAZABLE_REGISTRATION_INFO = new SeriazableRegistration(SlashRef.MODID);
    public static HardcodedRegistration HARDCODED_REGISTRATION_INFO = new HardcodedRegistration(SlashRef.MODID);

    public static ModRequiredRegisterInfo REGISTER_INFO = new ModRequiredRegisterInfo(SlashRef.MODID);

    public static String formatNumber(float num) {

        if (num < ClientConfigs.getConfig().SHOW_DECIMALS_ON_NUMBER_SMALLER_THAN.get()) {
            return DECIMAL_FORMAT.format(num);
        } else {
            return ((int) num) + "";
        }
    }

    // idk how else to pass arguments to a method reference
    public static void createMnsLangFile() {
        TranslationBuilder.of(SlashRef.MODID).name(ExileTranslation.item(SlashItems.RELIC.get(), ChatFormatting.GREEN + "Dungeon Relic")).build();

        // todo eventually i'll replace the entire old CreateLangFile class
        ExileLangFile.createFile(SlashRef.MODID, CreateLangFile.create());
    }

    // todo test
    public static String formatBigNumber(float num) {
        return NumberFormat.getInstance().format(num);
    }


    public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");
    public static IEventBus MOD_BUS;
    public static ModContainer MOD_CONTAINER;


    public static boolean RUN_DEV_TOOLS_REMOVE_WHEN_DONE = RUN_DEV_TOOLS; // this exists to stop me from making dumb mistakes when testing and forgetting about it


    public static final SimpleChannel NETWORK = new SimpleChannel();

    public MMORPG(IEventBus bus, ModContainer modContainer) {
        com.robertx22.library_of_exile.main.ExileLog.get().onlyInConsole("MMORPG: Starting Mine and Slash...");
        SchemaTest.run();

        MOD_BUS = bus;
        MOD_CONTAINER = modContainer;
        OrderedModConstructor.register(new MnsConstructor(SlashRef.MODID), bus);

        // NeoForge Data Attachment ????깅줉 (?ㅽ꺈, ?덈꺼, ?뚮젅?댁뼱 ?곗씠?????곸냽?붾? ?꾪빐 ?꾩닔)
        SlashAttachments.init(bus);


        if (MMORPG.RUN_DEV_TOOLS) {
            ExileRegistryUtil.setCurrentRegistarMod(SlashRef.MODID);
        }

        Watch watch = new Watch();


        modContainer.registerConfig(ModConfig.Type.SERVER, ServerContainer.spec, NeatForgeConfig.defaultConfigName(ModConfig.Type.SERVER, "mine_and_slash"));


        ExileEvents.CHECK_IF_DEV_TOOLS_SHOULD_RUN.register(new EventConsumer<ExileEvents.OnCheckIsDevToolsRunning>() {
            @Override
            public void accept(ExileEvents.OnCheckIsDevToolsRunning event) {
                event.run = true;
            }
        });

        VanillaRarities.init();
        Coin.init();
        StatPriority.init();
        StackSaving.init();
        StatEffect.init();
        StatCondition.loadclass();

        bus.addListener(SimpleChannel::registerPayloadHandlers);


        if (FMLEnvironment.dist == Dist.CLIENT) {
            ClientRegistration.register(bus, modContainer);
        }


        bus.addListener(this::commonSetupEvent);
        bus.addListener(this::interMod);

        ItemTooltipsRegister.init();

        CurioEvents.reg();


        ModTags.init();

        StatLayers.init();
        StatEffects.addSerializers();
        StatConditions.loadClass();
        Stats.loadClass();

        //ExileDBInit.initRegistries();
        //SpecialStats.init();


        MapField.init();
        EffectCondition.init();
        SlashItemTags.init();
        CommonEvents.register();

        C2SPacketRegister.register();
        S2CPacketRegister.register();

        LifeCycleEvents.register();

        bus.addListener(EventPriority.LOW, new Consumer<GatherDataEvent>() {
            @Override
            public void accept(GatherDataEvent x) {
                for (ExileRegistryType type : ExileRegistryType.getAllInRegisterOrder()) {
                    x.getGenerator().addProvider(true, type.getDatapackGenerator());
                }
            }
        });

        MapEvents.init();
        ProfessionEvents.init();
        OrbAddonEvents.register();

        PlayerStats.register();
        PlayerStats.initialize();

        DerivedRegistries.init();

        DungeonAddonEvents.init();

        watch.print("Mine and slash mod initialization ");


    }


    public void interMod(InterModEnqueueEvent event) {


        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> new SlotTypeMessage.Builder(RefCurio.RING).size(2).build());
        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> new SlotTypeMessage.Builder(RefCurio.NECKLACE).size(1).build());
        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> new SlotTypeMessage.Builder(RefCurio.OMEN).size(1).build());

        MOD_CONTAINER.registerConfig(ModConfig.Type.SERVER, CompatConfig.spec, NeatForgeConfig.defaultConfigName(ModConfig.Type.SERVER, "mine_and_slash_compatibility"));

    }

    public void commonSetupEvent(FMLCommonSetupEvent event) {

        ProfessionRecipes.init();


        SlashCapabilities.register();

    }


}
