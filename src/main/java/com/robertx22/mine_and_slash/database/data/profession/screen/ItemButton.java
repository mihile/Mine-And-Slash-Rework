package com.robertx22.mine_and_slash.database.data.profession.screen;

import com.robertx22.library_of_exile.utils.TextUTIL;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import com.robertx22.mine_and_slash.compat.OldImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.ArrayList;
import java.util.List;

public class ItemButton extends OldImageButton {
    public static int xSize = 16;
    public static int ySize = 16;
    static ResourceLocation buttonLoc = ResourceLocation.fromNamespaceAndPath("library_of_exile", "");
    private static final WidgetSprites SPRITES = new WidgetSprites(buttonLoc, buttonLoc);
    static ResourceLocation fancyBorderLoc = ResourceLocation.fromNamespaceAndPath("library_of_exile", "textures/gui/pretty_icon_border.png");
    static int FX = 20;
    static int FY = 20;
    ItemStack stack;
    Minecraft mc;
    public boolean renderFancyBorder;

    public List<Component> extraText = new ArrayList<>();

    public ItemButton(ItemStack stack, int xPos, int yPos) {
        this(stack, xPos, yPos, (button) -> {
        });
        this.stack = stack;
    }

    public ItemButton(ItemStack stack, int xPos, int yPos, Button.OnPress onclick) {
        super(xPos + 1, yPos + 1, xSize, ySize, SPRITES, onclick, Component.empty());
        this.mc = Minecraft.getInstance();
        this.renderFancyBorder = false;
        this.stack = stack;
    }

    @Override
    public void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.setTooltip(Tooltip.create(TextUTIL.mergeList(this.stack.getTooltipLines(Item.TooltipContext.EMPTY, this.mc.player, TooltipFlag.NORMAL))));
        pGuiGraphics.renderItem(stack, getX(), getY());
        pGuiGraphics.renderItemDecorations(mc.font, stack, getX(), getY());


        var tip = new ArrayList<Component>();
        tip.addAll(extraText);
        tip.addAll(stack.getTooltipLines(Item.TooltipContext.EMPTY, mc.player, TooltipFlag.NORMAL));

        this.setTooltip(Tooltip.create(TextUTIL.mergeList(tip)));

    }
}

