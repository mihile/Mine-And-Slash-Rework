package com.robertx22.mine_and_slash.gui.wiki.reworked;

import com.robertx22.library_of_exile.utils.TextUTIL;
import com.robertx22.mine_and_slash.gui.wiki.BestiaryEntry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class WikiEntry extends ObjectSelectionList.Entry<WikiEntry> {
    private static final ResourceLocation WIDGETS_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/widgets.png");

    BestiaryEntry entry;
    WikiEntryList list;

    public WikiEntry(WikiEntryList wikiEntryList, BestiaryEntry entry) {
        this.list = wikiEntryList;
        this.entry = entry;


    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {

      
        if (list.screen.selectedEntry == entry) {
            list.screen.selectedEntry = null;
        } else {
            this.list.screen.selectedEntry = entry;
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public Component getNarration() {
        return Component.empty();
    }

    private int getTextureY(int pMouseX, int pMouseY) {
        int i = 1;
        if (this.isMouseOver(pMouseX, pMouseY)) {
            i = 0;
        } else if (this.isFocused()) {
            i = 2;
        }
        return 46 + i * 20;
    }

    @Override
    public boolean isMouseOver(double pMouseX, double pMouseY) {
        // Only allow mouse over if it's within the list boundaries
        if (pMouseY < list.getY() || pMouseY > list.getBottom()) return false;
        
        // Sync interaction area with the expanded visual highlight (32px height)
        // The entry height in the list is 36px. We check if mouse is within the centered 32px.
        return super.isMouseOver(pMouseX, pMouseY);
    }

    @Override
    public void render(GuiGraphics gui, int pIndex, int pTop, int pLeft, int pWidth, int pHeight, int pMouseX, int pMouseY, boolean pHovering, float pPartialTick) {


        // expanded highlight range (32px height, centered in 36px slot)
        if (this.list.screen.selectedEntry == this.entry || this.isMouseOver(pMouseX, pMouseY)) {
            gui.fill(pLeft - 4, pTop + 2, pLeft + pWidth + 4, pTop + 34, 0x55222222);
        }

        if (this.isMouseOver(pMouseX, pMouseY)) {
            var tip = Tooltip.create(TextUTIL.mergeList(entry.getTooltip()));
            list.screen.setTooltipForNextRenderPass(tip, DefaultTooltipPositioner.INSTANCE, true);
        }

        var mc = Minecraft.getInstance();

        // adjusted positions to center within the larger highlight
        if (entry.icon == null) {
            gui.renderFakeItem(entry.stack, pLeft, pTop + 9);
        } else {
            gui.blit(entry.icon, pLeft, pTop + 9, 0, 0, 16, 16, 16, 16);
        }

        int xp = (int) (pLeft + 30);
        int yp = (int) pTop + 13;

        gui.drawString(mc.font, entry.getCutName(), xp, yp, ChatFormatting.GREEN.getColor());

    }
}
