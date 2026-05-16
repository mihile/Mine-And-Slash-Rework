package com.robertx22.mine_and_slash.database.data.spells.components.actions.vanity;

import com.robertx22.mine_and_slash.database.data.spells.components.MapHolder;
import com.robertx22.mine_and_slash.database.data.spells.components.actions.SpellAction;
import com.robertx22.mine_and_slash.database.data.spells.components.packets.ParticlesPacket;
import com.robertx22.mine_and_slash.database.data.spells.spell_classes.SpellCtx;
import com.robertx22.mine_and_slash.uncommon.effectdatas.rework.EventData;
import com.robertx22.library_of_exile.main.Packets;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.Arrays;
import java.util.Collection;

import static com.robertx22.mine_and_slash.database.data.spells.map_fields.MapField.*;

// TODO MAKE THIS A PACKET AGAIN, 1 PACKET > SUMMON INFINITE PARTICLES
public class ParticleInRadiusAction extends SpellAction {

    public ParticleInRadiusAction() {
        super(Arrays.asList(PARTICLE_TYPE, RADIUS, PARTICLE_COUNT));
    }

    @Override
    public void tryActivate(Collection<LivingEntity> targets, SpellCtx ctx, MapHolder data) {

        if (!ctx.world.isClientSide) {

            ParticleShape shape = data.getParticleShape();

            //SimpleParticleType particle = data.getParticle();

            float radius = data.get(RADIUS).floatValue();

            radius *= ctx.calculatedSpellData.data.getNumber(EventData.AREA_MULTI, 1F).number;

            float height = data.getOrDefault(HEIGHT, 0D).floatValue();
            int amount = data.get(PARTICLE_COUNT).intValue();
            amount *= ctx.calculatedSpellData.data.getNumber(EventData.AREA_MULTI, 1F).number;

         
            ParticleMotion motion = null;

            try {
                motion = ParticleMotion.valueOf(data.get(MOTION));
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                motion = ParticleMotion.None;
            }

            float yrand = data.getOrDefault(Y_RANDOM, 0D).floatValue();

            float motionMulti = data.getOrDefault(MOTION_MULTI, 1D).floatValue();

            Vec3 pos = ctx.getPos();
            Vec3 vel = ctx.getPositionEntity().getDeltaMovement();


            ParticlesPacket.Data saved = new ParticlesPacket.Data();
            saved.particle = data.get(PARTICLE_TYPE);
            saved.pos = pos;
            saved.motion = motion;
            saved.shape = shape;
            saved.casterAngle = ctx.caster.getLookAngle();
            saved.amount = amount;
            saved.radius = radius;
            saved.height = height;
            saved.yrand = yrand;
            saved.motionMulti = motionMulti;
            saved.vel = vel;

            Packets.sendToTracking(new ParticlesPacket(saved), ctx.getBlockPos(), ctx.world);

        }
    }


    public MapHolder create(SimpleParticleType particle, Double count, Double radius) {
        return create(particle, count, radius, ParticleMotion.None);
    }

    public MapHolder create(SimpleParticleType particle, Double count, Double radius, ParticleMotion motion) {
        MapHolder dmg = new MapHolder();
        dmg.type = GUID();
        dmg.put(RADIUS, radius);
        dmg.put(PARTICLE_COUNT, count);
        dmg.put(PARTICLE_TYPE, BuiltInRegistries.PARTICLE_TYPE.getKey(particle)
                .toString());
        dmg.put(MOTION, motion.name());
        return dmg;
    }

    @Override
    public String GUID() {
        return "particles_in_radius";
    }
}
