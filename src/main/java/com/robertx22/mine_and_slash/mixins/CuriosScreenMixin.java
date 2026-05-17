package com.robertx22.mine_and_slash.mixins;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.theillusivec4.curios.client.gui.CuriosScreen;
import top.theillusivec4.curios.common.inventory.CurioSlot;

import java.util.List;
import java.util.Optional;

@Mixin(CuriosScreen.class)
public class CuriosScreenMixin {

    @Redirect(
            method = "renderTooltip",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;renderTooltip(Lnet/minecraft/client/gui/Font;Ljava/util/List;Ljava/util/Optional;II)V"
            )
    )
    private void renderTooltipWithStack(GuiGraphics guiGraphics, Font font, List<Component> components, Optional<TooltipComponent> tooltipComponent, int mouseX, int mouseY) {
        CuriosScreen screen = (CuriosScreen) (Object) this;
        Slot slot = screen.getSlotUnderMouse();
        ItemStack stack = slot.getItem();
        if (slot instanceof CurioSlot curioSlot) {
            stack = curioSlot.getSlotExtension().getDisplayStack(curioSlot.getSlotContext(), stack);
        }
        guiGraphics.renderTooltip(font, components, tooltipComponent, stack, mouseX, mouseY);
    }
}
