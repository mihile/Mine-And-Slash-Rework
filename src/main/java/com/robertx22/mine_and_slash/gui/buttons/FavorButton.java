package com.robertx22.mine_and_slash.gui.buttons;

import com.robertx22.mine_and_slash.uncommon.datasaving.Load;
import com.robertx22.library_of_exile.utils.TextUTIL;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import com.robertx22.mine_and_slash.compat.OldImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.resources.ResourceLocation;

public class FavorButton extends OldImageButton {

    public static int FAVOR_BUTTON_SIZE_X = 34;
    public static int FAVOR_BUTTON_SIZE_Y = 34;

    Minecraft mc = Minecraft.getInstance();

    public FavorButton(int xPos, int yPos) {
        super(xPos, yPos, FAVOR_BUTTON_SIZE_X, FAVOR_BUTTON_SIZE_Y, 0, 0, FAVOR_BUTTON_SIZE_Y, ResourceLocation.parse("empty"), (button) -> {
        });

    }

    @Override
    public void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float delta) {
        ResourceLocation tex = Load.player(mc.player).favor.getTexture();
        gui.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        gui.blit(tex, getX(), getY(), FAVOR_BUTTON_SIZE_X, FAVOR_BUTTON_SIZE_X, FAVOR_BUTTON_SIZE_X, FAVOR_BUTTON_SIZE_X, FAVOR_BUTTON_SIZE_X, FAVOR_BUTTON_SIZE_X);

        if (this.isHovered()) {
            setModTooltip();
        } else {
            this.setTooltip(null);
        }
    }

    public void setModTooltip() {
        this.setTooltip(Tooltip.create(TextUTIL.mergeList(Load.player(mc.player).favor.getTooltip())));

    }


}

