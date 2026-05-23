package com.robertx22.mine_and_slash.mixins;

import com.robertx22.mine_and_slash.event_hooks.damage_hooks.LivingHurtUtils;
import com.robertx22.mine_and_slash.event_hooks.damage_hooks.util.DmgSourceUtils;
import com.robertx22.mine_and_slash.mixin_ducks.LivingEntityAccesor;
import com.robertx22.mine_and_slash.mixin_methods.CanEntityHavePotionMixin;
import com.robertx22.mine_and_slash.uncommon.utilityclasses.HealthUtils;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.robertx22.mine_and_slash.capability.entity.EntityData;
import com.robertx22.mine_and_slash.saveclasses.unit.ResourceType;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements LivingEntityAccesor {

    @ModifyVariable(method = "hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z", at = @At(value = "HEAD"), argsOnly = true, ordinal = 0)
    private float absorbMagicShield(float amount, DamageSource source) {
        LivingEntity en = (LivingEntity) (Object) this;
        if (en.level().isClientSide || amount <= 0) return amount;
        if (DmgSourceUtils.isMyDmgSource(source)) return amount;

        EntityData data = EntityData.get(en);
        float shield = data.getResources().getMagicShield();
        float health = HealthUtils.getCurrentHealth(en);

        if (shield > 0 && health > 0) {
            float realDamage = amount * HealthUtils.getMaxHealth(en) / en.getMaxHealth();
            float shieldDamage = Math.min(shield, realDamage * (shield / (health + shield)));
            data.getResources().spend(en, ResourceType.magic_shield, shieldDamage);

            return HealthUtils.realToVanilla(en, realDamage - shieldDamage);
        }
        
        return amount;
    }

    @Shadow
    protected abstract float getVoicePitch();

    @Shadow
    protected abstract void updateInvisibilityStatus();

    @Override
    @Invoker("blockedByShield")
    public abstract void myknockback(LivingEntity target);

    @Override
    @Invoker("getHurtSound")
    public abstract SoundEvent myGetHurtSound(DamageSource source);

    @Override
    @Invoker("getSoundVolume")
    public abstract float myGetHurtVolume();

    @Override
    @Invoker("getVoicePitch")
    public abstract float myGetHurtPitch();

    @ModifyVariable(method = "heal(F)V", at = @At(value = "HEAD"), argsOnly = true, ordinal = 0)
    public float reduceHealPerLevel(float amount, float arg) {
        LivingEntity en = (LivingEntity) (Object) this;

        return HealthUtils.realToVanilla(en, amount);
    }

    // ENSURE MY SPECIAL DAMAGE ISNT LOWERED BY ARMOR, ENCHANTS ETC
    @Inject(method = "getDamageAfterMagicAbsorb", at = @At(value = "HEAD"), cancellable = true)
    public void hookench(DamageSource source, float amount, CallbackInfoReturnable<Float> ci) {
        LivingEntity en = (LivingEntity) (Object) this;
        if (DmgSourceUtils.isMyDmgSource(source)) {
            ci.setReturnValue(amount);
        }

    } // ENSURE MY SPECIAL DAMAGE ISNT LOWERED BY ARMOR, ENCHANTS ETC


    @Inject(method = "getDamageAfterMagicAbsorb", at = @At(value = "RETURN"), cancellable = true)
    public void hookenchreturn(DamageSource source, float amount, CallbackInfoReturnable<Float> ci) {
        LivingEntity en = (LivingEntity) (Object) this;

        if (!source.is(DamageTypeTags.BYPASSES_ARMOR)) {
            LivingHurtUtils.damageCurioItems(en, amount);
        }
        if (DmgSourceUtils.isMyDmgSource(source)) {
            ci.setReturnValue(amount);
        }

    }

    @Inject(method = "getDamageAfterArmorAbsorb", at = @At(value = "HEAD"), cancellable = true)
    public void hookarmortodmg(DamageSource source, float amount, CallbackInfoReturnable<Float> ci) {
        LivingEntity en = (LivingEntity) (Object) this;
        if (DmgSourceUtils.isMyDmgSource(source)) {
            //damageArmor(source, MathHelper.clamp(amount, 2, 10));
            ci.setReturnValue(amount);
        }
    }
    // ENSURE MY SPECIAL DAMAGE ISNT LOWERED BY ARMOR, ENCHANTS ETC

    @Inject(method = "canBeAffected", at = @At(value = "HEAD"), cancellable = true)
    public void hook(MobEffectInstance effect, CallbackInfoReturnable<Boolean> ci) {
        try {
            LivingEntity en = (LivingEntity) (Object) this;
            CanEntityHavePotionMixin.hook(en, effect, ci);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
