package com.robertx22.mine_and_slash.database.data.profession;

import com.robertx22.library_of_exile.util.ExplainedResult;
import com.robertx22.mine_and_slash.database.data.profession.all.Professions;
import com.robertx22.mine_and_slash.database.data.profession.screen.CraftingStationMenu;
import com.robertx22.mine_and_slash.database.registry.ExileDB;
import com.robertx22.mine_and_slash.itemstack.ExileStack;
import com.robertx22.mine_and_slash.mmorpg.ModErrors;
import com.robertx22.mine_and_slash.mmorpg.registers.common.SlashBlockEntities;
import com.robertx22.mine_and_slash.mmorpg.registers.common.items.SlashItems;
import com.robertx22.mine_and_slash.uncommon.datasaving.Load;
import com.robertx22.mine_and_slash.uncommon.interfaces.data_items.ICommonDataItem;
import com.robertx22.mine_and_slash.uncommon.interfaces.data_items.ISalvagable;
import com.robertx22.mine_and_slash.uncommon.localization.Chats;
import com.robertx22.mine_and_slash.uncommon.localization.Words;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

public class ProfessionBlockEntity extends BlockEntity {

    public static MergedContainer.Inventory INPUTS = new MergedContainer.Inventory("INPUTS", 9, Direction.UP);
    public static MergedContainer.Inventory OUTPUTS = new MergedContainer.Inventory("OUTPUTS", 9, Direction.DOWN);

    public MergedContainer inventory = new MergedContainer(Arrays.asList(INPUTS, OUTPUTS), this);

    public SimpleContainer show = new SimpleContainer(1);

    public Boolean recipe_locked = false;
    public ProfessionRecipe last_recipe;
    public Crafting_State craftingState = Crafting_State.STOPPED;
    public UUID ownerUUID = null;


    public ProfessionBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(SlashBlockEntities.PROFESSION.get(), pPos, pBlockState);
    }

    public Profession getProfession() {
        var id = ((ProfessionBlock) getBlockState().getBlock()).profession;
        return ExileDB.Professions().get(id);
    }

    public Player getOwner(Level l) {
        if (ownerUUID != null) {
            return l.getPlayerByUUID(ownerUUID);
        }
        return null;
    }


    public void tryInputRecipe(ProfessionRecipe recipe, Player p) {

        if (!recipe.profession.equals(getProfession().GUID())) {
            return;
        }

        this.ownerUUID = p.getUUID();

        this.recipe_locked = true;
        this.last_recipe = recipe;


    }

    public void tryTakeMaterialsFromNearbyChests() {
        // Nearby chest intake is currently disabled; the old body returned before reading the config.
    }

    public List<Container> getNearbyInventories() {
        List<Container> list = new ArrayList<>();
        for (Direction dir : Arrays.asList(Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST)) {
            BlockPos pos = getBlockPos().offset(dir.getNormal());
            if (this.getLevel().getBlockEntity(pos) instanceof Container inv) {
                list.add(inv);
            }
        }

        return list;
    }

    public boolean onTryInsertItem(ItemStack stack) {

        if (this.recipe_locked) {
            craftingState = Crafting_State.ACTIVE;
        }

        return true;
    }

    public void onTickWhenPlayerWatching(Player p) {
        var recipe = this.getCurrentRecipe();

        if (recipe != null) {
            this.show.setItem(0, recipe.toResultStackForJei());
        } else {
            this.show.setItem(0, ItemStack.EMPTY);

        }
    }

    int lastInputs = 0;

    public void tick(Level level) {
        try {
            if (craftingState == Crafting_State.IDLE) {
                activateIdleCraftingIfInputsChanged();
            }

            if (craftingState == Crafting_State.ACTIVE) {
                if (stopCraftingIfNoValidInputs()) {
                    return;
                }
                
                Player p = getOwner(level);
                if (p != null && p.isAlive()) {
                    if (getProfession().GUID().equals(Professions.SALVAGING)) {
                        handleSalvageTick(p);
                    } else if (recipe_locked) {
                        handleLockedRecipeTick(p);
                    } else {
                        handleUnlockedRecipeTick(p);
                    }
                }
            }
        } catch (Exception e) {
            ModErrors.print(e);
        }
    }

    private void handleSalvageTick(Player p) {
        var rec = trySalvage(p);
        if (!rec.can && !recipe_locked)
            show.clearContent();
    }

    private void handleLockedRecipeTick(Player p) {
        var can = last_recipe.canCraft(getMats());
        if (can.can) {
            var can2 = tryRecipe(p);
            if (!can2.can && p.containerMenu instanceof CraftingStationMenu) {
                p.sendSystemMessage(can2.answer);
            }
        } else if (p.containerMenu instanceof CraftingStationMenu) {
            p.sendSystemMessage(can.answer);
            craftingState = Crafting_State.IDLE;
        }
    }

    private void handleUnlockedRecipeTick(Player p) {
        ProfessionRecipe recipe = getCurrentRecipe();
        if (recipe == null) {
            p.sendSystemMessage(Chats.PROF_RECIPE_NOT_SELECTED.locName().withStyle(ChatFormatting.RED, ChatFormatting.BOLD));
            craftingState = Crafting_State.IDLE;
            return;
        }
        int ownerLvl = Load.player(p).professions.getLevel(recipe.profession);
        if (recipe.getLevelRequirement() > ownerLvl) {
            p.sendSystemMessage(Chats.PROF_RECIPE_LEVEL_NOT_ENOUGH.locName(getProfession().locName(), recipe.getLevelRequirement(), ownerLvl).withStyle(ChatFormatting.RED, ChatFormatting.BOLD));
            craftingState = Crafting_State.IDLE;
            return;
        }
        var can = recipe.canCraft(getMats());

        if (can.can) {
            var rec = tryRecipe(p);
            if (!rec.can) {
                show.clearContent();
                craftingState = Crafting_State.IDLE;
            }
        } else {
            p.sendSystemMessage(can.answer);
        }
    }

    private void activateIdleCraftingIfInputsChanged() {
        var inv = this.inventory.getInventory(INPUTS);
        int inputs = 0;
        for (int i = 0; i < inv.getContainerSize(); i++) {
            if (inv.getItem(i).isEmpty()) {
                inputs++;
            }
        }
        if (inputs != lastInputs) {
            craftingState = Crafting_State.ACTIVE;
            lastInputs = inputs;
            this.tryTakeMaterialsFromNearbyChests();

        }
    }

    private boolean stopCraftingIfNoValidInputs() {
        if (this.inventory.getInventory(INPUTS).isEmpty() || hasOnlyDestroyOutputInputs()) {
            craftingState = Crafting_State.IDLE;
            return true;
        }
        return false;
    }

    private boolean hasOnlyDestroyOutputInputs() {
        return getMats().stream().filter(x -> !x.toString().equals(Blocks.AIR.asItem().getDefaultInstance().toString())).allMatch(x -> x.toString().equals(SlashItems.DESTROY_OUTPUT.get().getDefaultInstance().toString()));
    }

    public boolean hasAtLeastOneFreeOutputSlot() {
        var inv = this.inventory.getInventory(OUTPUTS);

        for (int i = 0; i < inv.getContainerSize(); i++) {
            if (inv.getItem(i).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public ExplainedResult tryRecipe(Player p) {
        ProfessionRecipe recipe = getRecipeForCrafting();

        if (recipe == null) {
            return ExplainedResult.failure(Chats.PROF_RECIPE_NOT_SELECTED.locName());
        }
        int ownerLvl = Load.player(p).professions.getLevel(getProfession().GUID());
        if (recipe.getLevelRequirement() > ownerLvl) {
            return ExplainedResult.failure(Chats.PROF_RECIPE_LEVEL_NOT_ENOUGH.locName(getProfession().locName(), recipe.getLevelRequirement(), ownerLvl));
        }

        boolean destroyOuput = shouldDestroyRecipeOutput();
        float expMulti = destroyOuput ? 2 : 1;

        int expGive = (int) (recipe.getExpReward(p, ownerLvl, getMats()) * expMulti);
        this.addExp(expGive);
        var output = recipe.craft(p, getMats());
        if (!destroyOuput && !tryPutToOutputs(output))
            return ExplainedResult.failure(Chats.PROF_OUTPUT_SLOT_NOT_EMPTY.locName());
        recipe.spendMaterials(getMats());
        this.setChanged();
        return ExplainedResult.success();

    }

    private ProfessionRecipe getRecipeForCrafting() {
        if (recipe_locked) {
            return last_recipe;
        } else {
            return getCurrentRecipe();
        }
    }

    private boolean shouldDestroyRecipeOutput() {
        return this.inventory.getInventory(INPUTS).countItem(SlashItems.DESTROY_OUTPUT.get()) > 0 && !getProfession().GUID().equals(Professions.SALVAGING);
    }

    public boolean tryPutToOutputs(List<ItemStack> stacks) {
        for (ItemStack stack : stacks) {
            if (!inventory.addStack(OUTPUTS, stack)) {
                ItemEntity itementity = new ItemEntity(level, getBlockPos().getX(), getBlockPos().getY() + 0.5, getBlockPos().getZ(), stack);
                itementity.setDefaultPickUpDelay();
                level.addFreshEntity(itementity);
            }
        }
        return true;
    }

    public ExplainedResult trySalvage(Player p) {

        if (!hasAtLeastOneFreeOutputSlot()) {
            return ExplainedResult.failure(Chats.PROF_OUTPUT_SLOT_NOT_EMPTY.locName());
        }

        if (!getProfession().GUID().equals(Professions.SALVAGING)) {
            return ExplainedResult.failure(Component.literal(""));
        }

        int ownerLvl = Load.player(p).professions.getLevel(getProfession().GUID());

        for (ItemStack stack : this.getMats()) {

            ExileStack ex = ExileStack.of(stack);
            ICommonDataItem data = ICommonDataItem.load(stack);
            ISalvagable sal = ISalvagable.load(stack);

            if (data == null || sal == null || !sal.isSalvagable(ex)) {
                dropUnsalvageableStack(p, stack);
                continue;
            }

            float multi = data.getRarity().item_value_multi;

            List<ItemStack> output = createSalvageOutput(p, ownerLvl, ex, sal);
            output.addAll(getProfession().getAllDrops(p, ownerLvl, data.getLevel(), multi));

            tryPutToOutputs(output);
            this.addExp(data.getSalvageExpReward());
            stack.shrink(1);
            this.setChanged();

            return ExplainedResult.success();
        }

        return ExplainedResult.failure(Component.literal(""));
    }

    private List<ItemStack> createSalvageOutput(Player p, int ownerLvl, ExileStack ex, ISalvagable sal) {
        List<ItemStack> output = new ArrayList<>();
        output.addAll(sal.getSalvageResult(ex));

        // Additional salvage attempts based on profession level
        // 1% chance per level to get extra salvage results
        float extraSalvageChance = ownerLvl * 0.01f; // 1% per level
        while (extraSalvageChance > 0) {
            if (extraSalvageChance >= 1.0f || p.level().random.nextFloat() < extraSalvageChance) {
                output.addAll(sal.getSalvageResult(ex));
            }
            extraSalvageChance -= 1.0f;
        }

        return output;
    }

    private void dropUnsalvageableStack(Player p, ItemStack stack) {
        if (stack != null && !stack.isEmpty()) {

            ItemStack copy = stack.copy();

            stack.shrink(stack.getCount());

            ItemEntity itementity = new ItemEntity(level, this.getBlockPos().getX(), getBlockPos().getY() + 1.5, getBlockPos().getZ(), copy);
            itementity.setDefaultPickUpDelay();
            level.addFreshEntity(itementity);

            p.sendSystemMessage(Words.UNSALVAGEABLE.locName().append(": ").append(copy.getDisplayName()));
        }
    }

    public void addExp(int xp) {

        Player p = getOwner(level);

        if (p != null) {
            Load.player(p).professions.addExp(p, getProfession().GUID(), xp);
        }

    }


    public List<ItemStack> getMats() {
        return inventory.getAllStacks(INPUTS);
    }

    public ProfessionRecipe getCurrentRecipe() {
        if (this.recipe_locked) {
            if (this.last_recipe != null) {
                return last_recipe;
            }
        }

        // Unlocked auto recipe lookup is currently disabled; the old body returned null before searching.
        return null;
    }

    public ListTag createTag(HolderLookup.Provider provider) {
        ListTag listtag = new ListTag();

        for (int i = 0; i < inventory.getContainerSize(); ++i) {
            ItemStack itemstack = inventory.getItem(i);
            if (itemstack.isEmpty())
                continue;
            
            net.minecraft.nbt.Tag savedTag = itemstack.saveOptional(provider);
            if (savedTag instanceof CompoundTag) {
                CompoundTag ct = (CompoundTag) savedTag;
                ct.putInt("slot", i);
                listtag.add(ct);
            } else {
                CompoundTag slot = new CompoundTag();
                slot.putInt("slot", i);
                slot.put("Item", savedTag);
                listtag.add(slot);
            }
        }
        return listtag;
    }

    public void fromTag(ListTag pContainerNbt, HolderLookup.Provider provider) {
        inventory.clearContent();
        for (int i = 0; i < pContainerNbt.size(); ++i) {
            CompoundTag tag = pContainerNbt.getCompound(i);
            int slot = tag.getInt("slot");
            tag.remove("slot");
            ItemStack itemstack = ItemStack.parseOptional(provider, tag);
            inventory.setItem(slot, itemstack);
        }
    }


    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider provider) {
        super.loadAdditional(pTag, provider);
        try {
            loadInventories(pTag, provider);
            loadCraftingState(pTag);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    private void loadInventories(CompoundTag pTag, HolderLookup.Provider provider) {
        fromTag(pTag.getList("inv", 10), provider);
        this.show.fromTag(pTag.getList("show", 10), provider);
    }

    private void loadCraftingState(CompoundTag pTag) {
        this.recipe_locked = pTag.getBoolean("locked");

        var state = pTag.getString("state");
        if (state.isEmpty()) {
            state = Crafting_State.IDLE.name();
        }
        this.craftingState = Crafting_State.valueOf(state);
        if (craftingState != Crafting_State.STOPPED) {
            if (pTag.contains("owner"))
                this.ownerUUID = pTag.getUUID("owner");
            else {
                this.ownerUUID = null;
                craftingState = Crafting_State.STOPPED;
            }
        }
        if (recipe_locked && pTag.contains("recipe")) {
            this.last_recipe = ExileDB.Recipes().get(pTag.getString("recipe"));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider provider) {
        super.saveAdditional(pTag, provider);

        try {
            saveInventories(pTag, provider);
            saveCraftingState(pTag);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveInventories(CompoundTag pTag, HolderLookup.Provider provider) {
        pTag.put("inv", createTag(provider));
        pTag.put("show", this.show.createTag(provider));
    }

    private void saveCraftingState(CompoundTag pTag) {
        if (recipe_locked) {
            if (last_recipe != null) {
                pTag.putBoolean("locked", recipe_locked);
                pTag.putString("recipe", last_recipe.GUID());
            } else {
                pTag.putBoolean("locked", false);
            }
        }
        if (craftingState != Crafting_State.STOPPED) {
            if (this.ownerUUID != null) {
                pTag.putUUID("owner", this.ownerUUID);
                pTag.putString("state", craftingState.name());
            } else {
                pTag.putString("state", Crafting_State.STOPPED.name());
            }
        }
    }

}

