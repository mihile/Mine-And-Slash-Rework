package com.robertx22.mine_and_slash.capability.chunk;

import net.neoforged.neoforge.common.util.INBTSerializable;

import com.robertx22.library_of_exile.components.ICap;
import com.robertx22.library_of_exile.main.ExileLog;
import com.robertx22.mine_and_slash.mmorpg.MMORPG;
import com.robertx22.mine_and_slash.mmorpg.SlashRef;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import com.robertx22.library_of_exile.compat.capability.Capability;
import com.robertx22.library_of_exile.compat.capability.CapabilityManager;
import com.robertx22.library_of_exile.compat.capability.CapabilityToken;
import com.robertx22.library_of_exile.compat.capability.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChunkCap implements ICap, INBTSerializable<CompoundTag> {


    public static final ResourceLocation RESOURCE = ResourceLocation.fromNamespaceAndPath(SlashRef.MODID, "chunk");
    public static Capability<ChunkCap> INSTANCE = CapabilityManager.get(new CapabilityToken<>() {
    });

    transient final LazyOptional<ChunkCap> supp = LazyOptional.of(() -> this);

    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == INSTANCE) {
            return supp.cast();
        }
        return LazyOptional.empty();

    }


    transient LevelChunk chunk;

    public boolean generatedMobs = false;
    public boolean generatedTerrain = false;

    /**
     * NeoForge Attachment 기본 생성자입니다.
     */
    public ChunkCap() {
    }

    /**
     * Attachment에서 꺼낸 후 chunk을 주입합니다.
     */
    public ChunkCap init(LevelChunk chunk) {
        if (this.chunk == null) {
            this.chunk = chunk;
        }
        return this;
    }

    @Override
    public CompoundTag serializeNBT(net.minecraft.core.HolderLookup.Provider provider) {
        return serializeNBT();
    }

    @Override
    public void deserializeNBT(net.minecraft.core.HolderLookup.Provider provider, CompoundTag nbt) {
        deserializeNBT(nbt);
    }

    public ChunkCap(LevelChunk chunk) {
        this.chunk = chunk;
    }


    List<CompoundTag> savedMobs = new ArrayList<>();

    public void tryLoadMobs(Level world) {
        if (savedMobs.isEmpty()) return;

        try {
            var saved = List.copyOf(savedMobs);
            savedMobs.clear();
            mobIds.clear();

            for (CompoundTag nbt : saved) {
                var en = EntityType.loadEntityRecursive(nbt, world, x -> x);
                if (en != null) {
                    world.addFreshEntity(en);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<UUID> mobIds = new ArrayList<>();

    public void trySaveMob(LivingEntity en) {

        if (en instanceof Player || mobIds.contains(en.getUUID())) {
            return;
        }

        if (savedMobs.size() > 30) {
            if (MMORPG.RUN_DEV_TOOLS) {
                ExileLog.get().warn("Saved too many mobs in 1 chunk, stopping just in case");
            }
            return;
        }

        mobIds.add(en.getUUID());

        var nbt = en.serializeNBT(en.registryAccess());

        savedMobs.add(nbt);
    }

    @Override
    public CompoundTag serializeNBT() {

        CompoundTag nbt = new CompoundTag();

        try {
            nbt.putBoolean("gen", generatedTerrain);
            nbt.putBoolean("genmobs", generatedMobs);

            nbt.putInt("mobs", savedMobs.size());

            for (int i = 0; i < savedMobs.size(); i++) {
                nbt.put(i + "", savedMobs.get(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
            ExileLog.get().warn("Mob Unloading/Loading Error");
        }

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {

        try {
            this.generatedTerrain = nbt.getBoolean("gen");
            this.generatedMobs = nbt.getBoolean("genmobs");

            int mobs = nbt.getInt("mobs");

            this.savedMobs = new ArrayList<>();
            this.mobIds = new ArrayList<>();

            for (int i = 0; i < mobs; i++) {
                var mobnbt = nbt.getCompound(i + "");
                var id = mobnbt.getUUID("UUID");
                if (id != null) {
                    savedMobs.add(mobnbt);
                    mobIds.add(id);
                }
                // todo test if game still freezes
            }
        } catch (Exception e) {
            e.printStackTrace();
            ExileLog.get().warn("Mob Unloading/Loading Error");
        }

    }

    @Override
    public String getCapIdForSyncing() {
        return "chunk_data";
    }

}
