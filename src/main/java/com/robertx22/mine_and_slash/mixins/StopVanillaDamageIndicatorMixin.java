package com.robertx22.mine_and_slash.mixins;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Player.class)
public class StopVanillaDamageIndicatorMixin {

    @Redirect(
            method = "attack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;sendParticles(Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDD)I"
            )
    )
    private int mine_and_slash$skipDamageIndicator(ServerLevel level, ParticleOptions particle, double x, double y, double z, int count, double xDist, double yDist, double zDist, double speed) {
        if (particle.getType() == ParticleTypes.DAMAGE_INDICATOR) {
            return 0;
        }
        return level.sendParticles(particle, x, y, z, count, xDist, yDist, zDist, speed);
    }
}
