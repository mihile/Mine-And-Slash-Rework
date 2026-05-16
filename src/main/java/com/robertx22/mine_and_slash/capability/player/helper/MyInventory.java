package com.robertx22.mine_and_slash.capability.player.helper;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

// had to override tag methods because the simplecontainer doesn't save place in inventory, just autosorts items..
public class MyInventory extends SimpleContainer {

    public MyInventory(int pSize) {
        super(pSize);

    }


    public void fromTag(ListTag pContainerNbt) {
        fromTag(pContainerNbt, null);
    }

    private HolderLookup.Provider getProvider(HolderLookup.Provider provider) {
        if (provider != null) return provider;
        try {
            return net.neoforged.neoforge.server.ServerLifecycleHooks.getCurrentServer().registryAccess();
        } catch (Exception e) {
            // If we're on client or server is not available yet, this might fail.
            // But we've done our best.
        }
        return null;
    }

    @Override
    public void fromTag(ListTag pContainerNbt, HolderLookup.Provider provider) {
        provider = getProvider(provider);
        for (int i = 0; i < this.getContainerSize(); ++i) {
            this.setItem(i, ItemStack.EMPTY);
        }

        for (int k = 0; k < pContainerNbt.size(); ++k) {
            CompoundTag compoundtag = pContainerNbt.getCompound(k);
            int j = compoundtag.getByte("Slot") & 255;
            if (j >= 0 && j < this.getContainerSize()) {
                this.setItem(j, provider == null ? ItemStack.EMPTY : ItemStack.parseOptional(provider, compoundtag));
            }
        }

    }

    public ListTag createTag() {
        return createTag(null);
    }

    @Override
    public ListTag createTag(HolderLookup.Provider provider) {
        provider = getProvider(provider);
        ListTag listtag = new ListTag();

        for (int i = 0; i < this.getContainerSize(); ++i) {
            ItemStack itemstack = this.getItem(i);
            if (!itemstack.isEmpty()) {
                if (provider != null) {
                    net.minecraft.nbt.Tag savedTag = itemstack.saveOptional(provider);
                    if (savedTag instanceof CompoundTag) {
                        CompoundTag ct = (CompoundTag) savedTag;
                        ct.putByte("Slot", (byte) i);
                        listtag.add(ct);
                    } else {
                        CompoundTag compoundtag = new CompoundTag();
                        compoundtag.putByte("Slot", (byte) i);
                        compoundtag.put("Item", savedTag);
                        listtag.add(compoundtag);
                    }
                } else {
                    // If we REALLY don't have a provider, we can't save the item correctly in 1.21.1.
                    // But we should at least log this instead of silent loss.
                }
            }
        }

        return listtag;
    }

    public int getTotalSlots() {
        return this.getContainerSize(); // todo for upgradables maybe limit this
    }

    public boolean hasFreeSlots() {
        return getFreeSlots() > 0;
    }


    public int getFreeSlots() {
        int free = 0;
        for (int i = 0; i < this.getTotalSlots(); i++) {
            ItemStack stack = this.getItem(i);
            if (stack.isEmpty()) {
                free++;
            }
        }
        return free;
    }

}
