package com.robertx22.mine_and_slash.capability.world;

import com.robertx22.dungeon_realm.main.DungeonMain;
import com.robertx22.library_of_exile.components.ICap;
import com.robertx22.library_of_exile.dimension.MapDataFinder;
import com.robertx22.library_of_exile.dimension.MapDimensionInfo;
import com.robertx22.library_of_exile.utils.LoadSave;
import com.robertx22.mine_and_slash.maps.MapData;
import com.robertx22.mine_and_slash.maps.MnsMapDataHolder;
import com.robertx22.mine_and_slash.mmorpg.SlashRef;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import com.robertx22.library_of_exile.compat.capability.Capability;
import com.robertx22.library_of_exile.compat.capability.CapabilityManager;
import com.robertx22.library_of_exile.compat.capability.CapabilityToken;
import com.robertx22.library_of_exile.compat.capability.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class WorldData extends SavedData implements ICap {

    public static final ResourceLocation RESOURCE = ResourceLocation.fromNamespaceAndPath(SlashRef.MODID, "world");
    public static Capability<WorldData> INSTANCE = CapabilityManager.get(new CapabilityToken<>() {
    });

    transient final LazyOptional<WorldData> supp = LazyOptional.of(() -> this);

    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == INSTANCE) {
            return supp.cast();
        }
        return LazyOptional.empty();
    }

    private static final String SAVE_KEY = SlashRef.MODID + "_world_data";

    /** 클라이언트 사이드용 인메모리 캐시 (서버에서만 SavedData 사용) */
    private static final Map<Level, WorldData> CLIENT_CACHE = new HashMap<>();

    /**
     * WorldData를 가져옵니다.
     * - 서버: SavedData(영속적 저장소)에서 로드
     * - 클라이언트: 인메모리 캐시 사용
     */
    public static WorldData get(Level level) {
        if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
            return serverLevel.getServer().overworld().getDataStorage().computeIfAbsent(
                    new SavedData.Factory<>(WorldData::new, WorldData::load, null),
                    SAVE_KEY
            );
        }
        // 클라이언트 사이드는 캐시 사용
        return CLIENT_CACHE.computeIfAbsent(level, WorldData::new);
    }

    private static final String MAP = "mapdata";

    transient Level level;

    public MnsMapDataHolder map = new MnsMapDataHolder();

    /** SavedData 기본 생성자 */
    public WorldData() {
    }

    public WorldData(Level level) {
        this.level = level;
    }

    /** SavedData 로드 팩토리 */
    public static WorldData load(CompoundTag nbt, HolderLookup.Provider provider) {
        WorldData data = new WorldData();
        data.deserializeNBT(nbt);
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag nbt, HolderLookup.Provider provider) {
        return serializeNBT();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        LoadSave.Save(map, nbt, MAP);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.map = loadOrBlank(MnsMapDataHolder.class, new MnsMapDataHolder(), nbt, MAP, new MnsMapDataHolder());
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
        return "world_data";
    }

    @Override
    public void syncToClient(net.minecraft.world.entity.player.Player player) {
        // WorldData는 클라이언트 동기화 없음
    }

    public static MapDataFinder<MapData> DATA_GETTER = new MapDataFinder<>() {
        @Override
        public MapData getData(Pos pos) {
            return get(pos.level).map.getData(this.getInfo().structure, pos.pos);
        }

        @Override
        public MapDimensionInfo getInfo() {
            return DungeonMain.MAP;
        }
    };
}
