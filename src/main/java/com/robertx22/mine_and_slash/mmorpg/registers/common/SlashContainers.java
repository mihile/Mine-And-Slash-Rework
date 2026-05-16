package com.robertx22.mine_and_slash.mmorpg.registers.common;

import com.robertx22.library_of_exile.deferred.RegObj;
import com.robertx22.mine_and_slash.capability.player.container.BackpackMenu;
import com.robertx22.mine_and_slash.capability.player.container.SkillGemsMenu;
import com.robertx22.mine_and_slash.database.data.profession.all.Professions;
import com.robertx22.mine_and_slash.database.data.profession.screen.CraftingStationMenu;
import com.robertx22.mine_and_slash.mmorpg.registers.deferred_wrapper.Def;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;

import java.util.HashMap;
import java.util.function.Supplier;

public class SlashContainers {
    public static HashMap<String, RegObj<MenuType<CraftingStationMenu>>> STATIONS = new HashMap<>();

    public static void init() {


        addStation(Professions.SALVAGING, () -> IMenuTypeExtension.create((x, y, z) -> new CraftingStationMenu(Professions.SALVAGING, x, y)));
        addStation(Professions.GEAR_CRAFTING, () -> IMenuTypeExtension.create((x, y, z) -> new CraftingStationMenu(Professions.GEAR_CRAFTING, x, y)));
        addStation(Professions.ALCHEMY, () -> IMenuTypeExtension.create((x, y, z) -> new CraftingStationMenu(Professions.ALCHEMY, x, y)));
        addStation(Professions.COOKING, () -> IMenuTypeExtension.create((x, y, z) -> new CraftingStationMenu(Professions.COOKING, x, y)));
        addStation(Professions.INFUSING, () -> IMenuTypeExtension.create((x, y, z) -> new CraftingStationMenu(Professions.INFUSING, x, y)));
    }

    static void addStation(String prof, Supplier<MenuType<CraftingStationMenu>> sup) {
        STATIONS.put(prof, Def.container(prof, sup));
    }


    public static RegObj<MenuType<SkillGemsMenu>> SKILL_GEMS = Def.container("runeword", () -> IMenuTypeExtension.create((x, y, z) -> new SkillGemsMenu(x, y)));
    public static RegObj<MenuType<BackpackMenu>> BACKPACK = Def.container("backpack", () -> IMenuTypeExtension.create((id, pInv, buf) -> new BackpackMenu(id, pInv)));

}
