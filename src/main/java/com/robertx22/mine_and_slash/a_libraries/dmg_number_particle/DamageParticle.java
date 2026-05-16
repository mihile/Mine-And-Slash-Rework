package com.robertx22.mine_and_slash.a_libraries.dmg_number_particle;

import com.robertx22.mine_and_slash.vanilla_mc.packets.DmgNumPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;

public class DamageParticle {

    public double x = 0;
    public double y = 0;
    public double z = 0;
    public double xPrev = 0;
    public double yPrev = 0;
    public double zPrev = 0;

    public int age = 0;

    public double ax = 0.00;
    public double ay = -0.005;
    public double az = 0.00;

    public double vx = 0;
    public double vy = 0;
    public double vz = 0;

    public String renderString = "";

    DmgNumPacket packet;

    public DamageParticle(Entity entity, DmgNumPacket packet) {
        this.packet = packet;

        this.renderString = packet.format + packet.string;
        if (packet.iscrit) {
            this.renderString += "!";
        }


        Minecraft mc = Minecraft.getInstance();

        age = 0;
        
        // 엔티티 대신 패킷에서 직접 서버 동기화 좌표를 가져옵니다.
        x = packet.x + (mc.level.random.nextGaussian() * 0.5);
        y = packet.y + (mc.level.random.nextGaussian() * 0.5);
        z = packet.z + (mc.level.random.nextGaussian() * 0.5);

        vx = mc.level.random.nextGaussian() * 0.01;
        vy = 0.05 + (mc.level.random.nextGaussian() * 0.01);
        vz = mc.level.random.nextGaussian() * 0.01;

        xPrev = x;
        yPrev = y;
        zPrev = z;
    }

    public void tick() {

        age++;


        xPrev = x;
        yPrev = y;
        zPrev = z;
        x += vx;
        y += vy;
        z += vz;
        vx += ax;
        vy += ay;
        vz += az;
    }

}