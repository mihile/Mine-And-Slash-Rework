package com.robertx22.mine_and_slash.vanilla_mc.commands.suggestions;

import com.mojang.brigadier.Message;
import com.robertx22.library_of_exile.command_wrapper.CommandSuggestions;
import com.robertx22.mine_and_slash.uncommon.testing.CommandTests;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CommandsSuggestions extends CommandSuggestions {

    @Override
    public List<String> suggestions() {
        List<String> list = new ArrayList<>(CommandTests.tests.keySet());
        return list;
    }

    @Override
    public Map<String, Message> suggestionsWithTooltips() {
        Map<String, Message> map = new LinkedHashMap<>();
        for (String test : CommandTests.tests.keySet()) {
            map.put(test, Component.literal("Run test: " + test));
        }
        return map;
    }

}
