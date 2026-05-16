package com.robertx22.mine_and_slash.gui.screens.character_screen;

import com.robertx22.mine_and_slash.gui.bases.BaseScreen;
import com.robertx22.mine_and_slash.mmorpg.SlashRef;
import com.robertx22.mine_and_slash.uncommon.datasaving.Load;
import com.robertx22.mine_and_slash.uncommon.localization.Gui;
import com.robertx22.library_of_exile.gui.ItemSlotButton;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import com.robertx22.mine_and_slash.compat.OldImageButton;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class PlayerGearButton extends OldImageButton {

    public static int xSize = 99;
    public static int ySize = 80;

    static ResourceLocation TEX = ResourceLocation.fromNamespaceAndPath(SlashRef.MODID, "textures/gui/player_gear.png");
    BaseScreen screen;
    Player player;

    public PlayerGearButton(Player player, BaseScreen screen, int xPos, int yPos) {
        super(xPos, yPos, xSize, ySize, 0, 0, ySize + 1, TEX, (button) -> {
        });
        this.player = player;
        this.screen = screen;

        // todo why is this broken
        // addItemButton(player.getEquippedStack(EquipmentSlot.MAINHAND), 58, 69);
        //addItemButton(player.getEquippedStack(EquipmentSlot.OFFHAND), 179, 69);

    }

    @Override
    public void renderWidget(GuiGraphics gui, int x, int y, float ticks) {
        super.renderWidget(gui, x, y, ticks);

        MutableComponent str = Gui.MAINHUB_LEVEL.locName().append(String.valueOf(Load.Unit(player).getLevel()));

        Minecraft mc = Minecraft.getInstance();


        // player 3d view
        InventoryScreen.renderEntityInInventoryFollowsMouse(gui, this.getX() + 20, this.getY() + 18, this.getX() + 80, this.getY() + 77, 30, 0.0625F, (float) (getX() + 51) - x, (float) (getY() + 25) - y, player);

        gui.drawString(mc.font, str, this.getX() + xSize / 2 - mc.font.width(str) / 2, this.getY() + 6, ChatFormatting.YELLOW.getColor());


    }

    private void addItemButton(ItemStack stack, int x, int y) {
        screen.publicAddButton(new ItemSlotButton(stack, this.getX() + x, this.getY() + y));
    }

}

