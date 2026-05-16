package com.robertx22.mine_and_slash.a_libraries.dmg_number_particle;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.robertx22.mine_and_slash.a_libraries.neat.NeatRenderType;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.util.HashSet;
import java.util.Set;

public class DamageParticleRenderer {

    public static Set<DamageParticle> PARTICLES = new HashSet<>();

    public static void renderParticles(PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, Camera camera) {
        for (DamageParticle p : PARTICLES) {
            renderParticle(poseStack, bufferSource, p, camera);
        }
    }

    private static void renderParticle(PoseStack matrix, MultiBufferSource.BufferSource bufferSource, DamageParticle particle, Camera camera) {
        float scaleToGui = 0.025f;

        if (particle.packet.iscrit) {
            scaleToGui *= 1.5f; // 크리티컬 시 크기 증가
        }

        Minecraft client = Minecraft.getInstance();
        float tickDelta = client.getTimer().getGameTimeDeltaPartialTick(false);

        // 보간된 절대 좌표 계산
        double x = Mth.lerp((double) tickDelta, particle.xPrev, particle.x);
        double y = Mth.lerp((double) tickDelta, particle.yPrev, particle.y);
        double z = Mth.lerp((double) tickDelta, particle.zPrev, particle.z);

        // 카메라 좌표
        Vec3 camPos = camera.getPosition();
        double camX = camPos.x;
        double camY = camPos.y;
        double camZ = camPos.z;

        matrix.pushPose();
        
        // 카메라를 기준으로 상대 좌표로 이동 (Identity matrix 가정)
        matrix.translate(x - camX, y - camY, z - camZ);
        
        // 카메라를 항상 바라보게 회전
        matrix.mulPose(client.getEntityRenderDispatcher().cameraOrientation());
        
        // 중요: Y축 반전 및 크기 조절 (일반 네임태그 렌더링 방식)
        matrix.scale(-scaleToGui, -scaleToGui, scaleToGui);

        Font font = client.font;
        String text = particle.renderString;
        float f2 = (float) (-font.width(text) / 2);

        Matrix4f matrix4f = matrix.last().pose();
        int color = particle.packet.format.getColor() != null ? particle.packet.format.getColor() : 0xFFFFFF;

        float bgOpacity = client.options.getBackgroundOpacity(0.25F);
        int bgColor = (int)(bgOpacity * 255.0F) << 24;

        MultiBufferSource textBuffers = new HudTextBufferSource(bufferSource);
        RenderSystem.enableDepthTest();
        RenderSystem.depthFunc(GL11.GL_ALWAYS);
        try {
            font.drawInBatch(text, f2, 0, color, false, matrix4f, textBuffers, Font.DisplayMode.SEE_THROUGH, bgColor, 15728880);
            bufferSource.endBatch();
        } finally {
            RenderSystem.depthFunc(GL11.GL_LEQUAL);
        }

        matrix.popPose();
    }

    private record HudTextBufferSource(MultiBufferSource delegate) implements MultiBufferSource {
        @Override
        public VertexConsumer getBuffer(RenderType renderType) {
            return delegate.getBuffer(NeatRenderType.getHudTextType(renderType));
        }
    }
}
