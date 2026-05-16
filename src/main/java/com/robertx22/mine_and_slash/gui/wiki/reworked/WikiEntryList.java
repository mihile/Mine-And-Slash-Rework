package com.robertx22.mine_and_slash.gui.wiki.reworked;

import com.robertx22.mine_and_slash.gui.wiki.BestiaryEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WikiEntryList extends ObjectSelectionList<WikiEntry> {

    public static int WIDTH = 200;
    public static int HEIGHT = 50;
    NewWikiScreen screen;

    public WikiEntryList(NewWikiScreen screen, Minecraft mc, int pWidth, int pHeight, int pY0, int pY1, int pItemHeight) {
        super(mc, pWidth, pY1 - pY0, pY0, pItemHeight);
        this.screen = screen;

        tryFilter("");
    }

    private List<BestiaryEntry> currentlyDisplayedLevels = new ArrayList<>();

    String filter = "donutuse";

    private void reloadAllEntries() {

        this.currentlyDisplayedLevels = new ArrayList<>();
        this.currentlyDisplayedLevels.addAll(screen.group.getAll(1));

    }

    public void tryFilter(String pFilter) {
        if (this.currentlyDisplayedLevels != null && !pFilter.equals(this.filter)) {
            this.forceFilter(pFilter);
        }
        this.filter = pFilter;
    }

    public void forceFilter(String cFilter) {
        this.clearEntries();
        reloadAllEntries();
        cFilter = cFilter.toLowerCase(Locale.ROOT);

        for (BestiaryEntry cur : this.currentlyDisplayedLevels) {
            if (hasString(cur, cFilter)) {
                if (screen.filter.values().stream().allMatch(x -> x.isValid(cur))) {
                    this.addEntry(new WikiEntry(this, cur));
                }
            }
        }
    }

    boolean hasString(BestiaryEntry<?> en, String s) {
        String str = s.toLowerCase(Locale.ROOT);

        boolean has = en.getName().toLowerCase(Locale.ROOT).contains(str);

        if (has) {
            return true;
        }

        if (screen.searchTooltipsCheckbox.selected()) {
            return en.getTooltip()
                    .stream()
                    .map(x -> x.getString().toLowerCase(Locale.ROOT))
                    .anyMatch(x -> x.contains(str));
        }

        return false;
    }

}
