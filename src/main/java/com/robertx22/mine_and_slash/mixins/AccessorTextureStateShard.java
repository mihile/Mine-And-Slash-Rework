package com.robertx22.mine_and_slash.mixins;

import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Optional;

@Mixin(RenderStateShard.TextureStateShard.class)
public interface AccessorTextureStateShard {
    @Accessor("texture")
    Optional<ResourceLocation> neat_texture();
}
