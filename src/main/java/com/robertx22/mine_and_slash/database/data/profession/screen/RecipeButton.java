package com.robertx22.mine_and_slash.database.data.profession.screen;

import com.robertx22.library_of_exile.main.Packets;
import com.robertx22.library_of_exile.tooltip.ExileTooltipUtils;
import com.robertx22.mine_and_slash.a_libraries.jei.LockRecipePacket;
import com.robertx22.mine_and_slash.database.data.profession.ProfessionRecipe;
import com.robertx22.mine_and_slash.mmorpg.SlashRef;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import com.robertx22.mine_and_slash.compat.OldImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.TooltipFlag;

import java.util.ArrayList;
import java.util.List;

public class RecipeButton extends OldImageButton {

    public static int XS = 18;
    public static int YS = 19;
    private static final WidgetSprites SPRITES = new WidgetSprites(SlashRef.guiId("empty"), SlashRef.guiId("empty"));

    Minecraft mc = Minecraft.getInstance();


    ProfessionRecipe recipe;

    public RecipeButton(CraftingStationScreen screen, ProfessionRecipe recipe, int xPos, int yPos) {
        super(xPos, yPos, XS, YS, SPRITES, (button) -> {
            Packets.sendToServer(new LockRecipePacket(recipe.GUID()));
            screen.refreshRequiredMats(recipe);
        }, Component.empty());
        this.recipe = recipe;
    }

    @Override
    public void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float delta) {
        setModTooltip();

        gui.renderFakeItem(recipe.toResultStackForJei(), getX(), getY());

    }

    public void setModTooltip() {

        List<MutableComponent> list = new ArrayList<>();
        for (Component l : recipe.toResultStackForJei().getTooltipLines(net.minecraft.world.item.Item.TooltipContext.EMPTY, mc.player, TooltipFlag.NORMAL)) {
            list.add((MutableComponent) l);
        }

        this.setTooltip(Tooltip.create(ExileTooltipUtils.joinMutableComps(list.listIterator(), Component.literal("\n"))));
    }

}

