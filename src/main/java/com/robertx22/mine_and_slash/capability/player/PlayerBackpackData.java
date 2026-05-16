package com.robertx22.mine_and_slash.capability.player;

import com.robertx22.mine_and_slash.capability.player.data.Backpacks;
import com.robertx22.mine_and_slash.mmorpg.SlashRef;
import com.robertx22.library_of_exile.components.ICap;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import com.robertx22.library_of_exile.compat.capability.Capability;
import com.robertx22.library_of_exile.compat.capability.CapabilityManager;
import com.robertx22.library_of_exile.compat.capability.CapabilityToken;
import com.robertx22.library_of_exile.compat.capability.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerBackpackData implements ICap, INBTSerializable<CompoundTag> {


    public static final ResourceLocation RESOURCE = ResourceLocation.fromNamespaceAndPath(SlashRef.MODID, "backpacks");
    public static Capability<PlayerBackpackData> INSTANCE = CapabilityManager.get(new CapabilityToken<>() {
    });

    public static PlayerBackpackData get(LivingEntity entity) {
        return entity instanceof Player player
                ? player.getData(com.robertx22.mine_and_slash.mmorpg.registers.common.SlashAttachments.PLAYER_BACKPACK_DATA.get()).init(player)
                : null;
    }

    /**
     * NeoForge Attachment 기본 생성자입니다.
     */
    public PlayerBackpackData() {
        this.data = new Backpacks(null);
    }

    /**
     * Attachment에서 꺼낸 후 player를 주입합니다.
     */
    public PlayerBackpackData init(Player player) {
        this.player = player;
        this.data.setPlayer(player);
        return this;
    }

    @Override
    public CompoundTag serializeNBT(net.minecraft.core.HolderLookup.Provider provider) {
        CompoundTag nbt = new CompoundTag();

        if (provider == null && player != null) {
            provider = player.level().registryAccess();
        }

        for (Backpacks.BackpackType type : Backpacks.BackpackType.values()) {
            try {
                nbt.put(type.id, data.getInv(type).createTag(provider));
            } catch (Exception e) {
                // throw new RuntimeException(e);
            }
        }

        return nbt;
    }

    @Override
    public void deserializeNBT(net.minecraft.core.HolderLookup.Provider provider, CompoundTag nbt) {
        if (provider == null && player != null) {
            provider = player.level().registryAccess();
        }
        for (Backpacks.BackpackType type : Backpacks.BackpackType.values()) {
            try {
                if (nbt.contains(type.id)) {
                    data.getInv(type).fromTag(nbt.getList(type.id, 10), provider);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
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

    transient final LazyOptional<PlayerBackpackData> supp = LazyOptional.of(() -> this);

    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == INSTANCE) {
            return supp.cast();
        }
        return LazyOptional.empty();

    }


    transient Player player;
    private Backpacks data;

    public PlayerBackpackData(Player player) {
        this.player = player;
        this.data = new Backpacks(player);
    }

    public Backpacks getBackpacks() {
        return data;
    }

    @Override
    public void syncToClient(Player player) {
        // dont sync backpacks to client
    }


    @Override
    public String getCapIdForSyncing() {
        return "backpack_data";
    }

}
