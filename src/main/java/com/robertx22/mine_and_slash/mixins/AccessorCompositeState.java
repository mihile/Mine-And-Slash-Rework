package com.robertx22.mine_and_slash.mixins;

import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderType.CompositeState.class)
public interface AccessorCompositeState {
    @Accessor("textureState")
    RenderStateShard.EmptyTextureStateShard neat_textureState();
}
