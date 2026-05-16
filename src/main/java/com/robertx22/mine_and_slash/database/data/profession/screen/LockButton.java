package com.robertx22.mine_and_slash.database.data.profession.screen;

import com.robertx22.mine_and_slash.mmorpg.SlashRef;
import com.robertx22.mine_and_slash.uncommon.localization.Gui;
import com.robertx22.mine_and_slash.vanilla_mc.packets.LockTogglePacket;
import com.robertx22.library_of_exile.main.Packets;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import com.robertx22.mine_and_slash.compat.OldImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class LockButton extends OldImageButton {

    public static int XS = 18;
    public static int YS = 18;
    private static final WidgetSprites SPRITES = new WidgetSprites(SlashRef.guiId("lockbutton"), SlashRef.guiId("lockbutton"));


    Minecraft mc = Minecraft.getInstance();
    CraftingStationScreen s;

    public LockButton(int xPos, int yPos, CraftingStationScreen s) {
        super(xPos, yPos, XS, YS, SPRITES, (button) -> {
            Packets.sendToServer(new LockTogglePacket(s.getSyncedData().getBlockPos()));
        }, Component.empty());
        this.s = s;
    }

    @Override
    public void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float delta) {
        setModTooltip();
        ResourceLocation tex = SlashRef.guiId("lockbutton");
        gui.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        gui.blit(tex, getX(), getY(), 0, s.getSyncedData().recipe_locked ? 18 : 0, 18, 18);
    }

    public void setModTooltip() {
        if (s.getSyncedData().recipe_locked)
            this.setTooltip(Tooltip.create(Gui.STATION_UNLOCK_RECIPE.locName().withStyle(ChatFormatting.DARK_AQUA)));
        else
            this.setTooltip(Tooltip.create(Gui.STATION_LOCK_RECIPE.locName().withStyle(ChatFormatting.DARK_AQUA)));
    }

}

