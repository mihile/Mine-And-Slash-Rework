package com.robertx22.mine_and_slash.a_libraries.neat;

import com.mojang.blaze3d.vertex.VertexFormat;
import com.robertx22.mine_and_slash.mixins.AccessorCompositeRenderType;
import com.robertx22.mine_and_slash.mixins.AccessorCompositeState;
import com.robertx22.mine_and_slash.mixins.AccessorRenderType;
import com.robertx22.mine_and_slash.mixins.AccessorTextureStateShard;
import com.robertx22.mine_and_slash.mmorpg.SlashRef;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP;

public class NeatRenderType extends RenderStateShard {

    //https://github.com/UpcraftLP/Orderly/blob/master/src/main/resources/assets/orderly/textures/ui/default_health_bar.png
    public static final ResourceLocation HEALTH_BAR_TEXTURE = ResourceLocation.fromNamespaceAndPath(SlashRef.MODID, "textures/gui/health_bar_texture.png");

    public static final RenderType BAR_TEXTURE_TYPE = getHealthBarType();
    private static final Map<ResourceLocation, RenderType> HUD_TEXT_TYPES = new ConcurrentHashMap<>();

    private NeatRenderType(String string, Runnable r, Runnable r1) {
        super(string, r, r1);
    }

    private static RenderType getHealthBarType() {
        RenderType.CompositeState renderTypeState = RenderType.CompositeState.builder()
                .setShaderState(POSITION_COLOR_TEX_LIGHTMAP_SHADER)
                .setTextureState(new TextureStateShard(NeatRenderType.HEALTH_BAR_TEXTURE, false, false))
                .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                .setLightmapState(LIGHTMAP)
                .setCullState(NO_CULL)
                .setDepthTestState(LEQUAL_DEPTH_TEST)
                .setWriteMaskState(COLOR_WRITE)
                .createCompositeState(false);
        return AccessorRenderType.neat_create("neat_health_bar", POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 256, true, true, renderTypeState);
    }

    public static RenderType getHudTextType(RenderType original) {
        Optional<ResourceLocation> texture = getTexture(original);
        return texture.map(NeatRenderType::getHudTextType).orElse(original);
    }

    private static Optional<ResourceLocation> getTexture(RenderType renderType) {
        if (!(renderType instanceof AccessorCompositeRenderType composite)) {
            return Optional.empty();
        }
        try {
            RenderStateShard.EmptyTextureStateShard textureState =
                    ((AccessorCompositeState) (Object) composite.neat_state()).neat_textureState();
            return ((AccessorTextureStateShard) (Object) textureState).neat_texture();
        } catch (RuntimeException ignored) {
        }
        return Optional.empty();
    }

    private static RenderType getHudTextType(ResourceLocation texture) {
        return HUD_TEXT_TYPES.computeIfAbsent(texture, key -> {
            RenderType.CompositeState state = RenderType.CompositeState.builder()
                    .setShaderState(RENDERTYPE_TEXT_SEE_THROUGH_SHADER)
                    .setTextureState(new TextureStateShard(key, false, false))
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setLightmapState(LIGHTMAP)
                    .setDepthTestState(NO_DEPTH_TEST)
                    .setWriteMaskState(COLOR_WRITE)
                    .createCompositeState(false);
            return AccessorRenderType.neat_create("neat_hud_text", POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 1536, false, true, state);
        });
    }
}
