package com.robertx22.mine_and_slash.mixins;

import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "net.minecraft.client.renderer.RenderType$CompositeRenderType")
public interface AccessorCompositeRenderType {
    @Accessor("state")
    RenderType.CompositeState neat_state();
}
