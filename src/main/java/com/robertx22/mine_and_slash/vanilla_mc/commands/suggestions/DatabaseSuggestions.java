package com.robertx22.mine_and_slash.vanilla_mc.commands.suggestions;

import com.mojang.brigadier.Message;
import com.robertx22.library_of_exile.command_wrapper.CommandSuggestions;
import com.robertx22.library_of_exile.registry.Database;
import com.robertx22.library_of_exile.registry.ExileRegistryType;
import com.robertx22.library_of_exile.registry.IGUID;
import com.robertx22.mine_and_slash.uncommon.interfaces.IAutoLocName;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DatabaseSuggestions extends CommandSuggestions {

    ExileRegistryType type;
    String extraSuggestionOption;

    public DatabaseSuggestions(ExileRegistryType type, String extraSuggestionOption) {
        this.type = type;
        this.extraSuggestionOption = extraSuggestionOption;
    }

    @Override
    public List<String> suggestions() {

        if(extraSuggestionOption != null) {
            return registrySuggestionsWithExtraOption(extraSuggestionOption);
        }

        return registrySuggestions();
    }

    @Override
    public Map<String, Message> suggestionsWithTooltips() {
        Map<String, Message> map = new LinkedHashMap<>();
        for (Object item : Database.getRegistry(type).getList()) {
            if (item instanceof IGUID g) {
                if (item instanceof IAutoLocName loc) {
                    map.put(g.GUID(), loc.locName());
                } else {
                    map.put(g.GUID(), Component.literal(g.GUID()));
                }
            }
        }
        if (extraSuggestionOption != null) {
            map.put(extraSuggestionOption, Component.literal(extraSuggestionOption));
        }
        return map;
    }

    private List<String> registrySuggestions() {
        return Database.getRegistry(type)
                .getList()
                .stream()
                .map(x -> {
                    IGUID g = (IGUID) x;
                    return g.GUID();
                })
                .toList();
    }

    private List<String> registrySuggestionsWithExtraOption(String option) {
        List<String> registrySuggestions = new ArrayList<>(registrySuggestions());
        registrySuggestions.add(option);

        return registrySuggestions;
    }

}
