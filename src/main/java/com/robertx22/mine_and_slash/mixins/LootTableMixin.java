package com.robertx22.mine_and_slash.mixins;

import com.robertx22.mine_and_slash.mixin_methods.AddSpawnerExtraLootMethod;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(LootTable.class)
public class LootTableMixin {

    @org.spongepowered.asm.mixin.injection.Inject(method = "fill", at = @org.spongepowered.asm.mixin.injection.At(value = "HEAD"))
    public void onLootGen(net.minecraft.world.Container pContainer, net.minecraft.world.level.storage.loot.LootParams pParams, long pSeed, org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
        System.out.println("MineAndSlash: LootTable.fill triggered for container " + pContainer.getClass().getName());
        try {
            com.robertx22.library_of_exile.mixin_methods.ChestGenLootMixin.onLootGen(pContainer, pParams);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Inject(method = "getRandomItems(Lnet/minecraft/world/level/storage/loot/LootParams;)Lit/unimi/dsi/fastutil/objects/ObjectArrayList;", at = @At(value = "RETURN"))
    public void hookLoot(LootParams params, CallbackInfoReturnable<List<ItemStack>> ci) {
       
        try {
            var context = new net.minecraft.world.level.storage.loot.LootContext.Builder(params).create(java.util.Optional.empty());
            LootTable lootTable = (LootTable) (Object) this;

            AddSpawnerExtraLootMethod.hookLoot(context, ci);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
