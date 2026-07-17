package com.robertx22.mine_and_slash.database.data.spells.summons.entity;

import com.robertx22.library_of_exile.utils.SoundUtils;
import com.robertx22.library_of_exile.utils.geometry.MyPosition;
import com.robertx22.mine_and_slash.capability.player.data.PlayerConfigData;
import com.robertx22.mine_and_slash.database.data.spells.components.ProjectileCastHelper;
import com.robertx22.mine_and_slash.database.data.spells.entities.AutoAimingProj;
import com.robertx22.mine_and_slash.mmorpg.registers.common.SlashEntities;
import com.robertx22.mine_and_slash.uncommon.datasaving.Load;
import com.robertx22.mine_and_slash.uncommon.utilityclasses.AllyOrEnemy;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class SummonEntity extends TamableAnimal implements RangedAttackMob {

    public SummonEntity(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }


    protected AbstractArrow getArrow(ItemStack pArrowStack, float pVelocity) {
        return ProjectileUtil.getMobArrow(this, pArrowStack, pVelocity, this.getMainHandItem());


    }

    Goal aggroGoal = null;

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            LivingEntity owner = getOwner();
            if (owner != null) {
                double distSq = this.distanceToSqr(owner);
                // 주인과 너무 멀어지면 (32블록 이상) 강제 순간이동
                if (distSq > 1024.0D) {
                    this.teleportTo(owner.getX(), owner.getY(), owner.getZ());
                    this.setTarget(null); // 순간이동 시 타겟 초기화
                }
            }

            if (this.tickCount % 20 == 0) {
                var owner2 = getOwner();
                if (owner2 instanceof Player p) {
                    if (Load.player(p).config.isConfigEnabled(PlayerConfigData.Config.AGGRESSIVE_SUMMONS)) {
                        if (aggroGoal == null) {
                            aggroGoal = new NearestAttackableTargetGoal<>(this, Monster.class, false);
                            this.targetSelector.addGoal(2, aggroGoal);
                        }
                    }
                }
            }

            if (this.tickCount % 10 == 0 && !(focusEntity instanceof LivingEntity focus && focus.isAlive() && isInAggroRadius(focus))) {
                LivingEntity nearest = null;
                double nearestDistance = Double.MAX_VALUE;
                double aggroRadius = Load.Unit(this).summonedPetData.aggro_radius;

                for (LivingEntity target : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(aggroRadius),
                        target -> target != this && this.canAttack(target) && this.getSensing().hasLineOfSight(target))) {
                    double distance = this.distanceToSqr(target);
                    if (distance < nearestDistance) {
                        nearest = target;
                        nearestDistance = distance;
                    }
                }

                if (nearest != null && nearest != this.getTarget()) {
                    this.setTarget(nearest);
                }
            }
        }
    }

    @Override
    public void performRangedAttack(LivingEntity pTarget, float pDistanceFactor) {
        autoAimingRangedAttack(pTarget);
    }


    /**
     * Launches a Wither skull toward (par2, par4, par6)
     */
    private void autoAimingRangedAttack(LivingEntity target) {

        SoundUtils.playSound(this, SoundEvents.ARROW_SHOOT, 1, 0.2F);


        AutoAimingProj en = SlashEntities.AUTO_AIMING_SKELETON_SKULL.get().create(level());

        en.setOwner(this);

        en.setPosRaw(getX(), getEyeY(), getZ());

        en.setDeltaMovement(ProjectileCastHelper.positionToVelocity(new MyPosition(getEyePosition()), new MyPosition(target.getEyePosition())));

        en.target = target;
        en.speed = 2;

        this.level().addFreshEntity(en);
    }

    public boolean usesMelee() {
        return true;
    }

    public boolean usesRanged() {
        return false;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }

    @Override
    protected void registerGoals() {


        this.goalSelector.addGoal(2, new FollowOwnerGoal(this, 1.2D, 12.0F, 2.0F)); // HIGH PRIORITY Follow

        if (usesMelee()) {
            this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2D, true));
        }
        if (usesRanged()) {
            this.goalSelector.addGoal(1, new RangedBowAttackGoal<>(this, 1.0D, 20, 15F));
        }

        this.goalSelector.addGoal(6, new RandomSwimmingGoal(this, 1, 1));
        this.goalSelector.addGoal(8, new FollowOwnerGoal(this, 1.0D, 6.0F, 2.0F));
        this.goalSelector.addGoal(9, new RandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new OwnerTargetedByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, true, false, x -> canAttack(x)));
    }

    public static class OwnerTargetedByTargetGoal extends TargetGoal {
        private final SummonEntity summon;
        private LivingEntity lastTarget;

        public OwnerTargetedByTargetGoal(SummonEntity summon) {
            super(summon, false);
            this.summon = summon;
            this.setFlags(EnumSet.of(Goal.Flag.TARGET));
        }

        @Override
        public boolean canUse() {
            LivingEntity owner = this.summon.getOwner();
            if (owner == null) return false;

            double radius = Load.Unit(this.summon).summonedPetData.aggro_radius + 5; // Extra search range
            List<Mob> mobs = this.summon.level().getEntitiesOfClass(Mob.class, owner.getBoundingBox().inflate(radius),
                    mob -> mob.getTarget() == owner && this.summon.canAttack(mob));

            if (mobs.isEmpty()) return false;

            // 주인에게 가장 가까운 적을 우선 선택
            mobs.sort((o1, o2) -> Double.compare(o1.distanceToSqr(owner), o2.distanceToSqr(owner)));

            for (Mob mob : mobs) {
                if (this.summon.isTargetReachable(mob)) {
                    this.lastTarget = mob;
                    return true;
                }
            }
            
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity target = this.summon.getTarget();
            if (target != null && target.isAlive() && this.summon.canAttack(target)) {
                return true;
            }
            return false;
        }

        @Override
        public void start() {
            this.mob.setTarget(this.lastTarget);
            super.start();
        }
    }

    public Entity focusEntity = null;

    private final Map<Integer, Boolean> reachabilityCache = new HashMap<>();
    private int lastCacheClear = -1;

    private boolean isTargetReachable(LivingEntity target) {
        if (usesRanged()) return true;
        if (target.distanceToSqr(this) < 12.25D) return true; // Melee range check (3.5 blocks)

        if (this.tickCount != lastCacheClear) {
            reachabilityCache.clear();
            lastCacheClear = this.tickCount;
        }

        return reachabilityCache.computeIfAbsent(target.getId(), id -> {
            Path path = this.getNavigation().createPath(target, 0);
            return path != null && path.canReach();
        });
    }

    @Override
    public boolean canAttack(LivingEntity pTarget) {
        LivingEntity owner = getOwner();

        if (owner == null) {
            return false;
        }
        if (!pTarget.isAlive()) {
            return false;
        }

        // 주인과 너무 멀리 떨어진 타겟은 무시 (순간이동 로직과 연동)
        if (pTarget.distanceToSqr(owner) > 1024.0D) {
            return false;
        }

        if (!AllyOrEnemy.summonShouldAttack.is(owner, pTarget)) {
            return false;
        }

        if (!isTargetReachable(pTarget)) {
            return false;
        }

        // aggro the focus target, otherwise find something else
        if (focusEntity != null && focusEntity.isAlive() && isInAggroRadius((LivingEntity) focusEntity)) {
            return focusEntity == pTarget;
        } else {
            return isInAggroRadius(pTarget);
        }

    }

    // todo test this
    private boolean isInAggroRadius(LivingEntity target) {
        var aggroRadius = Load.Unit(this).summonedPetData.aggro_radius;
        LivingEntity owner = getOwner();

        if (owner != null) {
            return target.distanceTo(owner) <= aggroRadius;
        }

        int distance = (int) target.distanceTo(this);

        return aggroRadius >= distance;

    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    protected boolean canRide(Entity pVehicle) {
        return false;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
        return null;
    }
}
