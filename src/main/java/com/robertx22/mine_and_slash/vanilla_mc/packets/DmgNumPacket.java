package com.robertx22.mine_and_slash.vanilla_mc.packets;

import com.robertx22.library_of_exile.main.MyPacket;
import com.robertx22.library_of_exile.packets.ExilePacketContext;
import com.robertx22.mine_and_slash.a_libraries.dmg_number_particle.DamageParticleAdder;
import com.robertx22.mine_and_slash.config.forge.ClientConfigs;
import com.robertx22.mine_and_slash.mmorpg.SlashRef;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class DmgNumPacket extends MyPacket<DmgNumPacket> {

    public String string;
    public int id;
    public boolean iscrit = false;
    public ChatFormatting format = ChatFormatting.RED;
    public double x, y, z;

    public DmgNumPacket() {

    }

    public DmgNumPacket(LivingEntity entity, String str, boolean iscrit, ChatFormatting format) {
        string = str;
        this.id = entity.getId();
        this.iscrit = iscrit;
        this.format = format;
        this.x = entity.getX();
        this.y = entity.getY() + entity.getBbHeight();
        this.z = entity.getZ();
    }

    @Override
    public ResourceLocation getIdentifier() {
        return ResourceLocation.fromNamespaceAndPath(SlashRef.MODID, "dmgnum");
    }

    @Override
    public void loadFromData(FriendlyByteBuf tag) {
        string = tag.readUtf(500);
        id = tag.readInt();
        this.iscrit = tag.readBoolean();
        this.format = ChatFormatting.getByName(tag.readUtf(100));
        this.x = tag.readDouble();
        this.y = tag.readDouble();
        this.z = tag.readDouble();
    }

    @Override
    public void saveToData(FriendlyByteBuf tag) {
        tag.writeUtf(string);
        tag.writeInt(id);
        tag.writeBoolean(iscrit);
        tag.writeUtf(format.getName());
        tag.writeDouble(x);
        tag.writeDouble(y);
        tag.writeDouble(z);
    }

    @Override
    public void onReceived(ExilePacketContext ctx) {
        if (ClientConfigs.getConfig().ENABLE_FLOATING_DMG.get().getReal()) {
            DamageParticleAdder.displayParticle(null, this);
        }
    }

    @Override
    public MyPacket<DmgNumPacket> newInstance() {
        return new DmgNumPacket();
    }
}
