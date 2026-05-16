package com.robertx22.mine_and_slash.capability.player.container;

import com.mojang.blaze3d.platform.InputConstants;
import com.robertx22.mine_and_slash.capability.player.data.Backpacks;
import com.robertx22.mine_and_slash.mixin_ducks.MouseHandlerDuck;
import com.robertx22.mine_and_slash.mmorpg.SlashRef;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.event.ScreenEvent;

public class BackpackScreen extends AbstractContainerScreen<BackpackMenu> {


    public static final ResourceLocation BACKGROUND_LOCATION = ResourceLocation.fromNamespaceAndPath(SlashRef.MODID, "textures/gui/master_bag.png");

    public static double iMouseX = (double)(Minecraft.getInstance().getWindow().getScreenWidth() / 2);
    public static double iMouseY = (double)(Minecraft.getInstance().getWindow().getHeight() / 2);

    public BackpackScreen(BackpackMenu pMenu, Inventory pPlayerInventory, Component txt) {
        super(pMenu, pPlayerInventory, Component.literal(""));
        this.imageWidth = 199;
        this.imageHeight = 222;


    }

    @Override
    protected void init() {
        super.init();

        int x = leftPos + 175;
        int y = topPos + 18;

        for (Backpacks.BackpackType type : Backpacks.BackpackType.values()) {
            this.addRenderableWidget(new BackpackButton(type, x, y));
            y += 18;
        }

        MouseHandlerDuck mouseHandler = (MouseHandlerDuck) Minecraft.getInstance().mouseHandler;
        //init() will be invoked when this screen be set to Minecraft.screen after the releaseMouse(), see setScreen();
        mouseHandler.setXPos(iMouseX);
        mouseHandler.setYPos(iMouseY);
        //from MouseHandler.class releaseMouse()
        InputConstants.grabOrReleaseMouse(this.minecraft.getWindow().getWindow(), 212993, iMouseX, iMouseY);
    }

    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.renderTooltip(pGuiGraphics, pMouseX, pMouseY);

    }

    @Override
    public void onClose() {
        super.onClose();
        //reset position
        iMouseX = (double)(Minecraft.getInstance().getWindow().getScreenWidth() / 2);
        iMouseY = (double)(Minecraft.getInstance().getWindow().getHeight() / 2);
    }



    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        pGuiGraphics.blit(BACKGROUND_LOCATION, i, j, 0, 0, this.imageWidth, this.imageHeight);

    }

}
