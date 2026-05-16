package com.robertx22.mine_and_slash.capability.player;

import com.robertx22.library_of_exile.components.ICap;
import net.neoforged.neoforge.common.util.INBTSerializable;
import com.robertx22.library_of_exile.main.Packets;
import com.robertx22.library_of_exile.packets.SyncPlayerCapToClient;
import com.robertx22.library_of_exile.utils.LoadSave;
import com.robertx22.mine_and_slash.a_libraries.curios.MyCurioUtils;
import com.robertx22.mine_and_slash.a_libraries.curios.RefCurio;
import com.robertx22.mine_and_slash.capability.DirtySync;
import com.robertx22.mine_and_slash.capability.entity.SummonedData;
import com.robertx22.mine_and_slash.capability.player.data.*;
import com.robertx22.mine_and_slash.capability.player.helper.GemInventoryHelper;
import com.robertx22.mine_and_slash.capability.player.helper.JewelInvHelper;
import com.robertx22.mine_and_slash.capability.player.helper.MyInventory;
import com.robertx22.mine_and_slash.characters.CharStorageData;
import com.robertx22.mine_and_slash.database.data.omen.OmenData;
import com.robertx22.mine_and_slash.database.data.spells.components.Spell;
import com.robertx22.mine_and_slash.event_hooks.my_events.CachedPlayerStats;
import com.robertx22.mine_and_slash.gui.screens.stat_gui.StatCalcInfoData;
import com.robertx22.mine_and_slash.mmorpg.SlashRef;
import com.robertx22.mine_and_slash.prophecy.PlayerProphecies;
import com.robertx22.mine_and_slash.saveclasses.perks.TalentsData;
import com.robertx22.mine_and_slash.saveclasses.spells.SpellCastingData;
import com.robertx22.mine_and_slash.saveclasses.spells.SpellSchoolsData;
import com.robertx22.mine_and_slash.saveclasses.unit.Unit;
import com.robertx22.mine_and_slash.saveclasses.unit.stat_calc.StatCalculation;
import com.robertx22.mine_and_slash.uncommon.datasaving.Load;
import com.robertx22.mine_and_slash.uncommon.datasaving.StackSaving;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import com.robertx22.library_of_exile.compat.capability.Capability;
import com.robertx22.library_of_exile.compat.capability.CapabilityManager;
import com.robertx22.library_of_exile.compat.capability.CapabilityToken;
import com.robertx22.library_of_exile.compat.capability.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PlayerData implements ICap, INBTSerializable<CompoundTag> {


    public static final ResourceLocation RESOURCE = ResourceLocation.fromNamespaceAndPath(SlashRef.MODID, "player_data");
    public static Capability<PlayerData> INSTANCE = CapabilityManager.get(new CapabilityToken<>() {
    });

    public static PlayerData get(LivingEntity entity) {
        return entity instanceof Player player
                ? player.getData(com.robertx22.mine_and_slash.mmorpg.registers.common.SlashAttachments.PLAYER_DATA.get()).init(player)
                : null;
    }

    /**
     * NeoForge Attachment 기본 생성자입니다.
     */
    public PlayerData() {
        // player는 init()에서 나중에 주입됩니다
    }

    /**
     * Attachment에서 꺼낸 후 player를 주입합니다.
     * 처음 init() 호출 시 ATTACHMENT_LOOKUP에 클라이언트 역직렬화 함수도 등록합니다.
     */
    public PlayerData init(Player player) {
        this.player = player;
        if (this.cachedStats == null) {
            this.cachedStats = new CachedPlayerStats(player);
        } else {
            this.cachedStats.p = player;
        }
        return this;
    }

    @Override
    public CompoundTag serializeNBT(net.minecraft.core.HolderLookup.Provider provider) {

        CompoundTag nbt = new CompoundTag();

        LoadSave.Save(team, nbt, TEAM_DATA);
        LoadSave.Save(talents, nbt, TALENTS_DATA);
        LoadSave.Save(prophecy, nbt, PROPHECY);
        LoadSave.Save(statPoints, nbt, STAT_POINTS);
        LoadSave.Save(ascClass, nbt, ASC);
        LoadSave.Save(spellCastingData, nbt, CAST);
        LoadSave.Save(config, nbt, CONFIG);
        LoadSave.Save(favor, nbt, FAVOR);
        LoadSave.Save(professions, nbt, PROFESSIONS);
        LoadSave.Save(buff, nbt, BUFFS);
        LoadSave.Save(rested_xp, nbt, RESTED_XP);
        LoadSave.Save(characters, nbt, CHARACTERS);
        LoadSave.Save(points, nbt, POINTS);
        LoadSave.Save(miscInfo, nbt, MISC_INFO);
        LoadSave.Save(summonedData, nbt, SUMMONED);

        nbt.put(GEMS, skillGemInv.createTag(provider));
        nbt.put(AURAS, auraInv.createTag(provider));
        nbt.put(JEWELS, jewelsInv.createTag(provider));

        nbt.putInt(BONUS_TALENTS, bonusTalents);
        nbt.putInt(OMENS_FILLED, omensFilled);

        return nbt;
    }

    @Override
    public void deserializeNBT(net.minecraft.core.HolderLookup.Provider provider, CompoundTag nbt) {

        this.team = loadOrBlank(TeamData.class, new TeamData(), nbt, TEAM_DATA, new TeamData());
        this.prophecy = loadOrBlank(PlayerProphecies.class, new PlayerProphecies(), nbt, PROPHECY, new PlayerProphecies());
        this.talents = loadOrBlank(TalentsData.class, new TalentsData(), nbt, TALENTS_DATA, new TalentsData());
        this.statPoints = loadOrBlank(StatPointsData.class, new StatPointsData(), nbt, STAT_POINTS, new StatPointsData());
        this.ascClass = loadOrBlank(SpellSchoolsData.class, new SpellSchoolsData(), nbt, ASC, new SpellSchoolsData());
        this.spellCastingData = loadOrBlank(SpellCastingData.class, new SpellCastingData(), nbt, CAST, new SpellCastingData());
        this.config = loadOrBlank(PlayerConfigData.class, new PlayerConfigData(), nbt, CONFIG, new PlayerConfigData());
        this.favor = loadOrBlank(DeathFavorData.class, new DeathFavorData(), nbt, FAVOR, new DeathFavorData());
        this.professions = loadOrBlank(PlayerProfessionsData.class, new PlayerProfessionsData(), nbt, PROFESSIONS, new PlayerProfessionsData());
        this.buff = loadOrBlank(PlayerBuffData.class, new PlayerBuffData(), nbt, BUFFS, new PlayerBuffData());
        this.rested_xp = loadOrBlank(RestedExpData.class, new RestedExpData(), nbt, RESTED_XP, new RestedExpData());
        this.points = loadOrBlank(PlayerPointsData.class, new PlayerPointsData(), nbt, POINTS, new PlayerPointsData());
        this.characters = loadOrBlank(CharStorageData.class, new CharStorageData(), nbt, CHARACTERS, new CharStorageData());
        this.miscInfo = loadOrBlank(MiscSyncData.class, new MiscSyncData(), nbt, MISC_INFO, new MiscSyncData());
        this.summonedData = loadOrBlank(SummonedData.class, new SummonedData(), nbt, SUMMONED, new SummonedData());
        skillGemInv.fromTag(nbt.getList(GEMS, 10), provider);
        auraInv.fromTag(nbt.getList(AURAS, 10), provider);
        jewelsInv.fromTag(nbt.getList(JEWELS, 10), provider);


        this.bonusTalents = nbt.getInt(BONUS_TALENTS);
        if (bonusTalents < 0) {
            bonusTalents = 0;
        }
        this.omensFilled = nbt.getInt(OMENS_FILLED);

        // 클라이언트에서 역직렬화 후 player/cachedStats 참조 복구
        // (player는 init()을 통해 나중에 주입됨, cachedStats만 null 체크)
        if (this.player != null && this.cachedStats == null) {
            this.cachedStats = new CachedPlayerStats(this.player);
        }

    }

    @Override
    public CompoundTag serializeNBT() {
        return serializeNBT(player != null ? player.level().registryAccess() : null);
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        deserializeNBT(player != null ? player.level().registryAccess() : null, nbt);
    }


    private static final String TEAM_DATA = "teams";
    private static final String PROPHECY = "proph";
    private static final String TALENTS_DATA = "tals";
    private static final String STAT_POINTS = "stats";
    private static final String DEATH_STATS = "death";
    private static final String GEMS = "gems";
    private static final String AURAS = "auras";
    private static final String MAP = "map";
    private static final String ASC = "asc";
    private static final String CAST = "casting";
    private static final String CONFIG = "config";
    private static final String JEWELS = "jewels";
    private static final String FAVOR = "favor";
    private static final String PROFESSIONS = "profs";
    private static final String BUFFS = "buffs";
    private static final String RESTED_XP = "rxp";
    private static final String NAME = "name";
    private static final String CHARACTERS = "chars";
    private static final String BONUS_TALENTS = "btal";
    private static final String POINTS = "points";
    private static final String MISC_INFO = "minfo";
    private static final String OMENS_FILLED = "ofi";
    private static final String SUMMONED = "summoned";

    public DirtySync playerDataSync = new DirtySync("playerdata_sync", x -> syncData());

    public transient Player player;


    public transient StatCalcInfoData ctxs = new StatCalcInfoData();

    public TeamData team = new TeamData();
    public TalentsData talents = new TalentsData();
    public StatPointsData statPoints = new StatPointsData();
    public SpellSchoolsData ascClass = new SpellSchoolsData();
    public PlayerProphecies prophecy = new PlayerProphecies();
    public SpellCastingData spellCastingData = new SpellCastingData();
    public PlayerConfigData config = new PlayerConfigData();
    public DeathFavorData favor = new DeathFavorData();
    public PlayerProfessionsData professions = new PlayerProfessionsData();
    public PlayerBuffData buff = new PlayerBuffData();
    public RestedExpData rested_xp = new RestedExpData();
    public PlayerPointsData points = new PlayerPointsData();
    public MiscSyncData miscInfo = new MiscSyncData();

    private MyInventory skillGemInv = new MyInventory(GemInventoryHelper.TOTAL_SLOTS);
    private MyInventory auraInv = new MyInventory(GemInventoryHelper.TOTAL_AURAS);
    private MyInventory jewelsInv = new MyInventory(9);

    public CharStorageData characters = new CharStorageData();

    public List<String> aurasOn = new ArrayList<>();
    SummonedData summonedData = new SummonedData();


    public int bonusTalents = 0;


    public int omensFilled = 0;

    public PlayerData(Player player) {
        this.player = player;
        this.cachedStats = new CachedPlayerStats(player);
    }


    public CachedPlayerStats cachedStats;

    public JewelInvHelper getJewels() {
        return new JewelInvHelper(jewelsInv);
    }


    private void syncData() {
        if (player == null) return;
        try {
            // PlayerCapabilities 레거시 맵 대신 직접 NBT를 직렬화해서 전송합니다.
            Packets.sendToClient(player, new SyncPlayerCapToClient(this.getCapIdForSyncing(), this.serializeNBT()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    transient HashMap<String, Unit> spellUnits = new HashMap<>();


    // todo cache this maybe too
    public void recalcOmensFilled() {
        try {
            omensFilled = 0;
            ItemStack stack = MyCurioUtils.get(RefCurio.OMEN, player, 0);
            if (StackSaving.OMEN.has(stack)) {
                var omen = StackSaving.OMEN.loadFrom(stack);
                this.omensFilled = omen.calcPiecesEquipped(player);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public OmenData getOmen() {
        try {
            ItemStack stack = MyCurioUtils.get(RefCurio.OMEN, player, 0);
            if (StackSaving.OMEN.has(stack)) {
                var omen = StackSaving.OMEN.loadFrom(stack);
                return omen;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Unit getSpellUnitStats(Player p, Spell spell) {

        if (!spellUnits.containsKey(spell.GUID())) {
            int key = keyOf(spell);
            if (spell.config.usesSupportGemsFromAnotherSpell()) {
                key = keyOf(spell.config.getSpellUsedForSuppGems());
            }
            var unit = calcSpellUnit(spell, key);
            spellUnits.put(spell.GUID(), unit);
        }
        if (!spellUnits.containsKey(spell.GUID())) {
            return Load.Unit(p).getUnit();
        }
        return spellUnits.get(spell.GUID());
    }

    public void setSpellUnitsDirty() {
        spellUnits = new HashMap<>();
    }

    public int keyOf(Spell spell) {
        int key = this.spellCastingData.keyOfSpell(spell.GUID());
        return key;
    }

    private Unit calcSpellUnit(Spell spell, int key) {
        var unit = new Unit();
        StatCalculation.calc(unit, this.cachedStats.allStatsWithoutSuppGems, player, spell, key);
        return unit;
    }

    public GemInventoryHelper getSkillGemInventory() {
        return new GemInventoryHelper(player, skillGemInv, auraInv);
    }

    public SummonedData getSummonedData() {
        return summonedData;
    }

    public void addSummonedType(String spellId, int amount) {
        this.summonedData.addSummonedType(spellId, amount);
        this.playerDataSync.setDirty();
    }

    public void setSummonedData(Map<String, Integer> summonedTypes) {
        summonedData.setSummonedType(summonedTypes);
        this.playerDataSync.setDirty();
    }

    public static <OBJ> OBJ loadOrBlank(Class theclass, OBJ newobj, CompoundTag nbt, String loc, OBJ blank) {
        try {
            OBJ data = LoadSave.Load(theclass, newobj, nbt, loc);
            if (data == null) {
                return blank;
            } else {
                return data;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return blank;
    }

    @Override
    public String getCapIdForSyncing() {
        return "rpg_player_data";
    }

    /**
     * 클라이언트가 SyncPlayerCapToClient 패킷을 수신해 deserializeNBT()를 완료한 직후 호출됩니다.
     * player 참조 주입 및 cachedStats 복구를 보장합니다.
     */
    @Override
    public void onClientReceived(net.minecraft.world.entity.player.Player player) {
        this.player = player;
        if (this.cachedStats == null) {
            this.cachedStats = new CachedPlayerStats(player);
        } else {
            this.cachedStats.p = player;
        }
    }

}
