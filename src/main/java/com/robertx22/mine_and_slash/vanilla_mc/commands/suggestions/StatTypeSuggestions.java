package com.robertx22.mine_and_slash.vanilla_mc.commands.suggestions;

import com.mojang.brigadier.Message;
import com.robertx22.library_of_exile.command_wrapper.CommandSuggestions;
import com.robertx22.mine_and_slash.uncommon.enumclasses.ModType;
import net.minecraft.network.chat.Component;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StatTypeSuggestions extends CommandSuggestions {

    @Override
    public List<String> suggestions() {

        return Arrays.stream(ModType.values())
            .map(x -> x.name())
            .collect(Collectors.toList());

    }

    @Override
    public Map<String, Message> suggestionsWithTooltips() {
        Map<String, Message> map = new LinkedHashMap<>();
        for (ModType type : ModType.values()) {
            map.put(type.name(), Component.literal(type.name())); // ModType doesn't have a desc but we could add one if needed
        }
        return map;
    }

}

