package com.robertx22.mine_and_slash.compat;

import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class OldImageButton extends ImageButton {
    public ResourceLocation resourceLocation;
    public int xTexStart;
    public int yTexStart;
    public int yDiffTex;
    public int textureWidth = 256;
    public int textureHeight = 256;

    public OldImageButton(int x, int y, int width, int height, WidgetSprites sprites, OnPress onPress) {
        super(x, y, width, height, sprites, onPress);
    }

    public OldImageButton(int x, int y, int width, int height, WidgetSprites sprites, OnPress onPress, Component message) {
        super(x, y, width, height, sprites, onPress, message);
    }

    public OldImageButton(int x, int y, int width, int height, int xTexStart, int yTexStart, int yDiffTex, ResourceLocation texture, OnPress onPress) {
        super(x, y, width, height, new WidgetSprites(texture, texture), onPress, Component.empty());
        this.resourceLocation = texture;
        this.xTexStart = xTexStart;
        this.yTexStart = yTexStart;
        this.yDiffTex = yDiffTex;
    }

    public OldImageButton(int x, int y, int width, int height, int xTexStart, int yTexStart, int yDiffTex, ResourceLocation texture, OnPress onPress, Component message) {
        super(x, y, width, height, new WidgetSprites(texture, texture), onPress, message);
        this.resourceLocation = texture;
        this.xTexStart = xTexStart;
        this.yTexStart = yTexStart;
        this.yDiffTex = yDiffTex;
    }

    public OldImageButton(int x, int y, int width, int height, int xTexStart, int yTexStart, int yDiffTex, ResourceLocation texture, int textureWidth, int textureHeight, OnPress onPress, Component message) {
        super(x, y, width, height, new WidgetSprites(texture, texture), onPress, message);
        this.resourceLocation = texture;
        this.xTexStart = xTexStart;
        this.yTexStart = yTexStart;
        this.yDiffTex = yDiffTex;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }

    public OldImageButton(int x, int y, int width, int height, int xTexStart, int yTexStart, int yDiffTex, ResourceLocation texture, int textureWidth, int textureHeight, OnPress onPress) {
        this(x, y, width, height, xTexStart, yTexStart, yDiffTex, texture, textureWidth, textureHeight, onPress, Component.empty());
    }

    @Override
    public void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        if (this.resourceLocation == null) {
            super.renderWidget(gui, mouseX, mouseY, partialTick);
            return;
        }

        int yStart = this.yTexStart;
        if (this.isHovered()) {
            yStart += this.yDiffTex;
        }
        if (yStart + this.getHeight() > this.textureHeight) {
            yStart = this.yTexStart;
        }
        this.renderTexture(gui, this.resourceLocation, this.getX(), this.getY(), this.xTexStart, yStart, this.yDiffTex, this.getWidth(), this.getHeight(), this.textureWidth, this.textureHeight);
    }

    public void renderTexture(net.minecraft.client.gui.GuiGraphics gui, ResourceLocation texture, int x, int y, int xTexStart, int yTexStart, int yDiffTex, int width, int height, int textureWidth, int textureHeight) {
        gui.blit(texture, x, y, xTexStart, yTexStart, width, height, textureWidth, textureHeight);
    }
}


