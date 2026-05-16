package com.robertx22.mine_and_slash.database.data.profession;

import com.robertx22.mine_and_slash.database.data.profession.all.Professions;
import com.robertx22.mine_and_slash.database.registry.ExileDB;
import com.robertx22.mine_and_slash.mmorpg.ForgeEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.event.entity.living.BabyEntitySpawnEvent;
import net.neoforged.neoforge.event.entity.player.ItemFishedEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;

import java.util.List;
import java.util.stream.Collectors;

public class ProfessionEvents {

    public static void init() {

        ForgeEvents.registerForgeEvent(BlockDropsEvent.class, x -> {
            if (!(x.getBreaker() instanceof Player p)) return;
            if (PlayerUTIL.isFake(p)) return;

            BlockState state = x.getState();
            Block block = state.getBlock();
            List<ItemStack> existingDrops = x.getDrops().stream()
                    .map(ItemEntity::getItem)
                    .collect(Collectors.toList());

            // Mining: skip silk touch (block drops itself)
            boolean silkTouch = x.getDrops().stream()
                    .anyMatch(ie -> ie.getItem().getItem() == block.asItem());
            if (!silkTouch) {
                List<ItemStack> miningDrops = ExileDB.Professions().get(Professions.MINING).onMineGetBonusDrops(p, existingDrops, state);
                for (ItemStack drop : miningDrops) {
                    if (!drop.isEmpty()) {
                        BlockPos pos = x.getPos();
                        x.getDrops().add(new ItemEntity(x.getLevel(), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, drop.copy()));
                    }
                }
            }

            // Farming
            List<ItemStack> farmingDrops = ExileDB.Professions().get(Professions.FARMING).onMineGetBonusDrops(p, existingDrops, state);
            for (ItemStack drop : farmingDrops) {
                if (!drop.isEmpty()) {
                    BlockPos pos = x.getPos();
                    x.getDrops().add(new ItemEntity(x.getLevel(), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, drop.copy()));
                }
            }
        });

        // Right-click harvest mods don't call Block.dropResources(), so BlockDropsEvent doesn't fire.
        // FARMING profession registers crops by drop item (Items.WHEAT, etc.), not by block.
        // We must compute the would-be drops via Block.getDrops() so onMineGetBonusDrops can match by item.
        ForgeEvents.registerForgeEvent(PlayerInteractEvent.RightClickBlock.class, x -> {
            Player p = x.getEntity();
            if (p.level().isClientSide()) return;
            if (PlayerUTIL.isFake(p)) return;

            BlockPos pos = x.getPos();
            BlockState state = p.level().getBlockState(pos);

            // Only compute drops for crops (blocks with AGE property) to avoid loot-table cost on every click.
            if (!state.getOptionalValue(net.minecraft.world.level.block.CropBlock.AGE).isPresent()
                    && !state.getOptionalValue(net.minecraft.world.level.block.NetherWartBlock.AGE).isPresent()) {
                return;
            }

            ServerLevel level = (ServerLevel) p.level();
            List<ItemStack> drops = Block.getDrops(state, level, pos, null, p, p.getMainHandItem());

            List<ItemStack> bonusDrops = ExileDB.Professions().get(Professions.FARMING)
                    .onMineGetBonusDrops(p, drops, state);
            for (ItemStack drop : bonusDrops) {
                if (!drop.isEmpty()) {
                    level.addFreshEntity(new ItemEntity(level,
                            pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, drop.copy()));
                }
            }
        }, EventPriority.HIGHEST);

        ForgeEvents.registerForgeEvent(ItemFishedEvent.class, x -> {
            Player p = x.getEntity();
            if (p != null) {
                if (!p.level().isClientSide) {
                    if (PlayerUTIL.isFake(p)) {
                        return;
                    }
                    var drops = ExileDB.Professions().get(Professions.FISHING).onFish(p);

                    for (ItemStack drop : drops) {
                        var en = p.spawnAtLocation(drop);
                    }
                }
            }
        });


        ForgeEvents.registerForgeEvent(BabyEntitySpawnEvent.class, x -> {
            Player p = x.getCausedByPlayer();
            if (p != null) {
                if (!p.level().isClientSide) {
                    if (PlayerUTIL.isFake(p)) {
                        return;
                    }
                    if (x.getChild() != null) {
                        var drops = ExileDB.Professions().get(Professions.HUSBANDRY).onBreedAnimal(p, x.getChild());
                        for (ItemStack drop : drops) {
                            x.getParentA().spawnAtLocation(drop);
                        }
                    }
                }
            }
        });

    }
}
