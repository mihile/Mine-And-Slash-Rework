package com.robertx22.mine_and_slash.vanilla_mc.commands.suggestions;

import com.mojang.brigadier.Message;
import com.robertx22.library_of_exile.command_wrapper.CommandSuggestions;
import com.robertx22.mine_and_slash.database.data.stats.Stat;
import com.robertx22.mine_and_slash.database.registry.ExileDB;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StatSuggestions extends CommandSuggestions {

    @Override
    public List<String> suggestions() {

        List<String> list = new ArrayList();
        for (Stat item : ExileDB.Stats()
            .getAll()
            .values()) {
            list.add(item.GUID());
        }

        return list;

    }

    @Override
    public Map<String, Message> suggestionsWithTooltips() {
        Map<String, Message> map = new LinkedHashMap<>();
        for (Stat item : ExileDB.Stats().getAll().values()) {
            map.put(item.GUID(), item.locName());
        }
        return map;
    }

}

