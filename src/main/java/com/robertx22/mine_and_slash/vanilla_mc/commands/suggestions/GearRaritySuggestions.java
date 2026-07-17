package com.robertx22.mine_and_slash.vanilla_mc.commands.suggestions;

import com.mojang.brigadier.Message;
import com.robertx22.library_of_exile.command_wrapper.CommandSuggestions;
import com.robertx22.mine_and_slash.database.registry.ExileDB;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GearRaritySuggestions extends CommandSuggestions {

    @Override
    public List<String> suggestions() {
        List<String> list = new ArrayList();

        ExileDB.GearRarities()
                .getList()
                .forEach(x -> {
                    list.add(x.GUID());
                
                });
        list.add("random");

        return list;
    }

    @Override
    public Map<String, Message> suggestionsWithTooltips() {
        Map<String, Message> map = new LinkedHashMap<>();
        ExileDB.GearRarities().getList().forEach(x -> {
            map.put(x.GUID(), x.locName());
        });
        map.put("random", Component.literal("random"));
        return map;
    }

}

