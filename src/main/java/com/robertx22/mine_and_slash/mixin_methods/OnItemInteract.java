package com.robertx22.mine_and_slash.mixin_methods;

import com.robertx22.addons.orbs_of_crafting.currency.IItemAsCurrency;
import com.robertx22.library_of_exile.utils.SoundUtils;
import com.robertx22.orbs_of_crafting.register.ExileCurrency;
import com.robertx22.mine_and_slash.config.forge.ServerContainer;
import com.robertx22.mine_and_slash.database.data.auto_item.AutoItem;
import com.robertx22.mine_and_slash.database.data.profession.items.CraftedSoulItem;
import com.robertx22.mine_and_slash.itemstack.ExileStack;
import com.robertx22.mine_and_slash.itemstack.StackKeys;
import com.robertx22.mine_and_slash.mmorpg.ForgeEvents;
import com.robertx22.mine_and_slash.mmorpg.registers.common.items.SlashItems;
import com.robertx22.mine_and_slash.saveclasses.item_classes.GearItemData;
import com.robertx22.mine_and_slash.saveclasses.stat_soul.SavedGearSoul;
import com.robertx22.mine_and_slash.saveclasses.stat_soul.StatSoulData;
import com.robertx22.mine_and_slash.saveclasses.stat_soul.StatSoulItem;
import com.robertx22.mine_and_slash.uncommon.datasaving.StackSaving;
import com.robertx22.mine_and_slash.uncommon.localization.Chats;
import com.robertx22.mine_and_slash.uncommon.utilityclasses.PlayerUtils;
import com.robertx22.mine_and_slash.vanilla_mc.items.SoulExtractorItem;
import com.robertx22.mine_and_slash.vanilla_mc.items.misc.RarityStoneItem;
import com.robertx22.orbs_of_crafting.misc.LocReqContext;
import net.minecraft.ChatFormatting;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.ItemStackedOnOtherEvent;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.ArrayList;
import java.util.List;

public class OnItemInteract {

    private static class Result {

        public boolean can;

        public Result(boolean can) {
            this.can = can;
        }

        private boolean doDing = false;

        public Result ding() {
            this.doDing = true;
            return this;
        }
    }

    private abstract static class ClickFeature {
        public abstract Result tryApply(Player player, ItemStack craftedStack, ItemStack currency, Slot slot);
    }

    static List<ClickFeature> CLICKS = new ArrayList<>();


    public static void register() {


        CLICKS.add(new ClickFeature() {
            @Override
            public Result tryApply(Player player, ItemStack craftedStack, ItemStack currency, Slot slot) {

                if (StackSaving.JEWEL.has(craftedStack)) {
                    var data = StackSaving.JEWEL.loadFrom(craftedStack);

                    if (data.uniq.isCraftableUnique()) {
                        ItemStack cost = data.uniq.getStackNeededForUpgrade();

                        if (cost.getItem() == currency.getItem()) {
                            if (currency.getCount() >= cost.getCount()) {
                                if (data.uniq.getCraftedTier().canUpgradeMore()) {
                                    data.uniq.upgradeUnique(data);

                                    StackSaving.JEWEL.saveTo(craftedStack, data);
                                    currency.shrink(cost.getCount());

                                    return new Result(true).ding();
                                }
                            }
                        }
                    }
                }

                return new Result(false);
            }
        });


        // todo replace repair stones with datapack currencies
        CLICKS.add(new ClickFeature() {
            @Override
            public Result tryApply(Player player, ItemStack craftedStack, ItemStack currency, Slot slot) {
                if (craftedStack.isDamaged() && currency.getItem() instanceof RarityStoneItem) {

                    if (!StackSaving.GEARS.has(craftedStack) && !StackSaving.OMEN.has(craftedStack) && !StackSaving.TOOL.has(craftedStack)) {
                        player.sendSystemMessage(Chats.NOT_GEAR_OR_LACKS_SOUL.locName().withStyle(ChatFormatting.RED));
                        return new Result(true);
                    }

                    RarityStoneItem essence = (RarityStoneItem) currency.getItem();

                    SoundUtils.playSound(player, SoundEvents.ANVIL_USE, 1, 1);

                    int repair = essence.getTotalRepair();

                    craftedStack.setDamageValue(craftedStack.getDamageValue() - repair);

                    currency.shrink(1);
                    return new Result(true).ding();
                }
                return new Result(false);
            }
        });

        CLICKS.add(new ClickFeature() {
            @Override
            public Result tryApply(Player player, ItemStack craftedStack, ItemStack currency, Slot slot) {
                if (currency.getItem() instanceof StatSoulItem || currency.getItem() instanceof CraftedSoulItem) {
                    StatSoulData data = StackSaving.STAT_SOULS.loadFrom(currency);
                    if (currency.getItem() instanceof CraftedSoulItem cs) {
                        data = cs.getSoul(currency);
                    }
                    if (data != null) {
                        var res = data.canInsertIntoStack(craftedStack);

                        if (res.can) {
                            if (craftedStack.getCount() == 1) {
                                ItemStack result = data.insertAsUnidentifiedOn(craftedStack, player);
                                craftedStack.shrink(1);
                                slot.set(result);
                                currency.shrink(1);
                                return new Result(true).ding();
                            }
                        } else {
                            if (res.answer != null) {
                                if (!player.level().isClientSide) player.sendSystemMessage(res.answer);
                            }
                            return new Result(true);
                        }
                    }

                }
                return new Result(false);
            }
        });


        CLICKS.add(new ClickFeature() {
            @Override
            public Result tryApply(Player player, ItemStack craftedStack, ItemStack currency, Slot slot) {
                if (currency.getItem() instanceof IItemAsCurrency) {
                    LocReqContext ctx = new LocReqContext(player, craftedStack, currency);

                    if (!craftedStack.isEmpty()) {
                        if (ctx.Currency.getItem() instanceof IItemAsCurrency c) {
                            var effect = c.currencyEffect(ctx.Currency);
                            var can = effect.canItemBeModified(ctx);
                            if (can.can) {
                                var modifyResult = effect.modifyItem(ctx);
                                ItemStack result = modifyResult.stack;
                                if (result == null || result.isEmpty()) {
                                    // modifyItem returned empty - do not consume items
                                    return new Result(true);
                                }
                                result = result.copy();
                                craftedStack.shrink(1); // seems the currency creates a copy of a new item, so we delete the old one
                                currency.shrink(1);
                                // PlayerUtils.giveItem(result, player);
                                slot.set(result);
                                return new Result(true);
                            } else {
                                if (!player.level().isClientSide) player.sendSystemMessage(can.answer);
                                return new Result(true);
                            }
                        }
                    }
                }
                return new Result(false);
            }
        });


        CLICKS.add(new ClickFeature() {
            @Override
            public Result tryApply(Player player, ItemStack craftedStack, ItemStack currency, Slot slot) {
                if (craftedStack.isEmpty()) return new Result(false);
                if (!(currency.getItem() instanceof IItemAsCurrency)) {
                    var exileCur = ExileCurrency.get(currency);
                    if (exileCur.isPresent()) {
                        ExileCurrency cur = exileCur.get();
                        LocReqContext ctx = new LocReqContext(player, craftedStack, currency);
                        var can = cur.canItemBeModified(ctx);
                        if (can.can) {
                            ItemStack result = cur.modifyItem(ctx).stack.copy();
                            craftedStack.shrink(1);
                            currency.shrink(1);

                            if (craftedStack.isEmpty()) {
                                slot.set(result);
                            } else {
                                if (!player.getInventory().add(result)) {
                                    player.drop(result, false);
                                }
                            }
                            return new Result(true);
                        }
 else {
                            if (!player.level().isClientSide) player.sendSystemMessage(can.answer);
                            return new Result(true);
                        }
                    }
                }
                return new Result(false);
            }
        });

        CLICKS.add(new ClickFeature() {
            @Override
            public Result tryApply(Player player, ItemStack craftedStack, ItemStack currency, Slot slot) {
                if (currency.getItem() instanceof SoulExtractorItem se) {

                    GearItemData gear = StackSaving.GEARS.loadFrom(craftedStack);

                    if (gear != null) {
                        try {

                            if (se.canExtract(gear.getRarity())) {
                                StatSoulData soul = new StatSoulData();
                                soul.slot = gear.GetBaseGearType().getGearSlot().GUID();
                                var ex = ExileStack.of(craftedStack);

                                soul.rar = gear.rar;

                                soul.gear = new SavedGearSoul(ex.get(StackKeys.GEAR).get(), ex.get(StackKeys.POTENTIAL).getOrCreate(), ex.get(StackKeys.CUSTOM).getOrCreate());

                                ItemStack soulstack = soul.toStack();

                                SoundUtils.playSound(player, SoundEvents.EXPERIENCE_ORB_PICKUP);

                                craftedStack.shrink(1);
                                currency.shrink(1);
                                PlayerUtils.giveItem(soulstack, player);
                                return new Result(true).ding();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
                return new Result(false);
            }
        });
        CLICKS.add(new ClickFeature() {
            @Override
            public Result tryApply(Player player, ItemStack craftedStack, ItemStack currency, Slot slot) {
                if (currency.is(SlashItems.SOUL_CLEANER.get())) {

                    GearItemData gear = StackSaving.GEARS.loadFrom(craftedStack);

                    if (gear != null && !ServerContainer.get().isSoulCleanBanned(craftedStack.getItem())) {
                        try {
                            StackSaving.GEARS.saveTo(craftedStack, null);
                            currency.shrink(1);
                            return new Result(true).ding();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                return new Result(false);
            }
        });


        ForgeEvents.registerForgeEvent(ItemStackedOnOtherEvent.class, x -> {
            Player player = x.getPlayer();

            if (player.level().isClientSide) {
                // return; // removed to fix client desync (ghost items)
            }
            if (x.getClickAction() != ClickAction.SECONDARY) {
                return; // ONLY allow Secondary Click (Right Click) for orbs/gems to avoid conflict with sort mods
            }

            // currency MUST be the carried item (held by mouse)
            // craftedStack MUST be the one in the slot
            ItemStack currency = x.getCarriedItem();
            ItemStack craftedStack = x.getStackedOnItem();


            for (ClickFeature click : CLICKS) {
                // check if the currency is actually a currency item before trying to apply
                boolean isCurrency = currency.getItem() instanceof IItemAsCurrency 
                                     || ExileCurrency.get(currency).isPresent()
                                     || currency.getItem() instanceof RarityStoneItem
                                     || currency.getItem() instanceof SoulExtractorItem
                                     || currency.is(SlashItems.SOUL_CLEANER.get())
                                     || currency.getItem() instanceof com.robertx22.mine_and_slash.saveclasses.stat_soul.StatSoulItem
                                     || currency.getItem() instanceof com.robertx22.mine_and_slash.database.data.profession.items.CraftedSoulItem;
                
                if (!isCurrency) continue;

                var result = click.tryApply(player, craftedStack, currency, x.getSlot());

                if (result.doDing) {
                    SoundUtils.ding(player.level(), player.blockPosition());
                    SoundUtils.playSound(player.level(), player.blockPosition(), SoundEvents.ANVIL_USE, 1, 1);
                }

                if (result.can) {
                    x.setCanceled(true);
                    break;
                }
            }


        });

        ForgeEvents.registerForgeEvent(PlayerEvent.ItemCraftedEvent.class, x -> {
            try {
                if (!x.getEntity().level().isClientSide) {
                    ItemStack stack = x.getCrafting();
                    AutoItem.tryInsertTo(stack, x.getEntity());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        ForgeEvents.registerForgeEvent(ItemEntityPickupEvent.Post.class, x -> {
            try {
                if (!x.getPlayer().level().isClientSide) {
                    ItemStack stack = x.getOriginalStack();
                    AutoItem.tryInsertTo(stack, x.getPlayer());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


}
