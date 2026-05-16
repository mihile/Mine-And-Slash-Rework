package com.robertx22.mine_and_slash.prophecy.gui;

import com.robertx22.mine_and_slash.mmorpg.SlashRef;
import com.robertx22.mine_and_slash.prophecy.AcceptProphecyPacket;
import com.robertx22.mine_and_slash.prophecy.ProphecyData;
import com.robertx22.library_of_exile.main.Packets;
import com.robertx22.library_of_exile.utils.TextUTIL;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import com.robertx22.mine_and_slash.compat.OldImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.resources.ResourceLocation;

public class ProphecyButton extends OldImageButton {
    static ResourceLocation DEFAULT_ID = ResourceLocation.fromNamespaceAndPath(SlashRef.MODID, "textures/gui/prophecy/icon.png");

    ProphecyData data;
    ResourceLocation iconTexture;


    public ProphecyButton(ProphecyData data, boolean canTake, int x, int y) {
        super(x, y, 16, 16, 0, 0, 1, DEFAULT_ID, (action) -> {
            if (canTake) {
                Packets.sendToServer(new AcceptProphecyPacket(data.uuid));
                Minecraft.getInstance().setScreen(null);
            }
        });
        this.data = data;
        this.iconTexture = getIconForProphecyType(data);
    }

    private ResourceLocation getIconForProphecyType(ProphecyData data) {
        String iconPath = "textures/gui/prophecy/" + data.start + ".png";

        return ResourceLocation.fromNamespaceAndPath(SlashRef.MODID, iconPath);
    }

    @Override
    public void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        pGuiGraphics.blit(iconTexture, getX(), getY(), 0, 0, 16, 16, 16, 16);

        this.setTooltip(Tooltip.create(TextUTIL.mergeList(data.getTooltip())));

        super.renderWidget(pGuiGraphics, pMouseX, pMouseY, pPartialTick);


    }
}
