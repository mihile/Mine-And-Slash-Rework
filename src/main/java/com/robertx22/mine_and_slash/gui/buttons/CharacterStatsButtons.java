package com.robertx22.mine_and_slash.gui.buttons;

import com.robertx22.library_of_exile.utils.TextUTIL;
import com.robertx22.library_of_exile.wrappers.ExileText;
import com.robertx22.mine_and_slash.capability.entity.EntityData;
import com.robertx22.mine_and_slash.database.data.stats.IUsableStat;
import com.robertx22.mine_and_slash.database.data.stats.Stat;
import com.robertx22.mine_and_slash.gui.screens.character_screen.MainHubScreen;
import com.robertx22.mine_and_slash.saveclasses.unit.StatData;
import com.robertx22.mine_and_slash.uncommon.datasaving.Load;
import com.robertx22.mine_and_slash.uncommon.utilityclasses.NumberUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import com.robertx22.mine_and_slash.compat.OldImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class CharacterStatsButtons extends OldImageButton {

    public static int BUTTON_SIZE_X = 16;
    public static int BUTTON_SIZE_Y = 16;

    MainHubScreen.StatType type;

    public CharacterStatsButtons(MainHubScreen.StatType type, int xPos, int yPos) {
        super(xPos, yPos, BUTTON_SIZE_X, BUTTON_SIZE_Y, 0, 0, BUTTON_SIZE_Y, type.getIcon(), (button) -> {

        });
        this.type = type;
    }
    protected ClientTooltipPositioner createTooltipPositioner() {
        return DefaultTooltipPositioner.INSTANCE;
    }

    @Override
    public void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float delta) {
        gui.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        gui.blit(type.getIcon(), getX(), getY(), 0, 0, BUTTON_SIZE_X, BUTTON_SIZE_Y, BUTTON_SIZE_X, BUTTON_SIZE_Y);

        if (this.isHovered()) {
            setModTooltip();
        } else {
            this.setTooltip(null);
        }

    }

    public void setModTooltip() {

        List<Component> tooltip = new ArrayList<>();

        Minecraft mc = Minecraft.getInstance();

        for (List<Stat> list : MainHubScreen.STAT_MAP.get(type)) {
            if (!tooltip.isEmpty()) {
                tooltip.add(ExileText.emptyLine().get());
            }

            for (Stat stat : list) {
                tooltip.add(stat.locName().append(": " + getStatString(stat, Load.Unit(mc.player))));
            }
        }


        this.setTooltip(Tooltip.create(TextUTIL.mergeList(tooltip)));


    }


    public static String getStatString(Stat stat, EntityData unitdata) {
        StatData data = unitdata.getUnit().getCalculatedStat(stat);

        String str = "";
        String v1 = NumberUtils.formatForTooltip(data.get());

        if (stat instanceof IUsableStat usable) {
            String value = NumberUtils.format(usable.getUsableValue(unitdata.getUnit(), (int) data.get(), unitdata.getLevel()) * 100);

            str += value + "%";

            if (stat.IsPercent()) {
                str += " (" + v1 + "%)";
            } else {
                str += " (" + v1 + ")";
            }
        } else {
            str += v1;

            if (stat.IsPercent()) {
                str += '%';
            }
        }
        return str;

    }


    public static String getHubStatString(Stat stat, EntityData unitdata) {
        StatData data = unitdata.getUnit().getCalculatedStat(stat);

        String str = "";
        String v1 = NumberUtils.formatForTooltip(data.get());

        if (stat instanceof IUsableStat usable) {
            String value = NumberUtils.format(usable.getUsableValue(unitdata.getUnit(), (int) data.get(), unitdata.getLevel()) * 100);

            str += value;
        } else {
            str += v1;
        }
        return str;

    }

}



