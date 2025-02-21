package mods.flammpfeil.slashblade.entity;

import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.util.*;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.network.NetworkHooks;

public class EntityJudgementCut extends Projectile implements IShootable {
    private static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(EntityJudgementCut.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> FLAGS = SynchedEntityData.defineId(EntityJudgementCut.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> RANK = SynchedEntityData.defineId(EntityJudgementCut.class, EntityDataSerializers.FLOAT);

    private int lifetime = 10;
    private int seed = -1; // 确保 seed 字段是 private 的
    private double damage = 1.0D;
    private boolean cycleHit = false; // 确保 cycleHit 字段是 private 的
    private final SoundEvent livingEntitySound = SoundEvents.WITHER_HURT;

    public EntityJudgementCut(EntityType<? extends Projectile> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
        this.setNoGravity(true);
        this.seed = this.random.nextInt(360); // 初始化 seed
    }

    // 添加 getSeed() 方法
    public int getSeed() {
        return this.seed;
    }

    // 添加 doCycleHit() 方法
    public boolean doCycleHit() {
        return this.cycleHit;
    }

    // 添加 setCycleHit() 方法
    public void setCycleHit(boolean cycleHit) {
        this.cycleHit = cycleHit;
    }

    public static EntityJudgementCut createInstance(PlayMessages.SpawnEntity packet, Level worldIn) {
        return new EntityJudgementCut(SlashBlade.RegistryEvents.JudgementCut, worldIn);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(COLOR, 0x3333FF);
        this.entityData.define(FLAGS, 0);
        this.entityData.define(RANK, 0.0f);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        NBTHelper.getNBTCoupler(compound)
                .put("Color", this.getColor())
                .put("Rank", this.getRank())
                .put("damage", this.damage)
                .put("crit", this.getIsCritical())
                .put("clip", this.isNoClip())
                .put("Lifetime", this.getLifetime());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        NBTHelper.getNBTCoupler(compound)
                .get("Color", this::setColor)
                .get("Rank", this::setRank)
                .get("damage", ((Double v) -> this.damage = v), this.damage)
                .get("crit", this::setIsCritical)
                .get("clip", this::setNoClip)
                .get("Lifetime", this::setLifetime);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
        this.setDeltaMovement(0, 0, 0);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean shouldRenderAtSqrDistance(double distance) {
        double d0 = this.getBoundingBox().getSize() * 10.0D;
        if (Double.isNaN(d0)) {
            d0 = 1.0D;
        }
        d0 = d0 * 64.0D * getViewScale();
        return distance < d0 * d0;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void lerpTo(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {
        this.setPos(x, y, z);
        this.setRot(yaw, pitch);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void lerpMotion(double x, double y, double z) {
        this.setDeltaMovement(0, 0, 0);
    }

    enum FlagsState {
        Critical, NoClip
    }

    private final EnumSet<FlagsState> flags = EnumSet.noneOf(FlagsState.class);
    private int intFlags = 0;

    private void refreshFlags() {
        if (this.level().isClientSide()) {
            int newValue = this.entityData.get(FLAGS).intValue();
            if (intFlags != newValue) {
                intFlags = newValue;
                flags.clear();
                flags.addAll(EnumSetConverter.convertToEnumSet(FlagsState.class, intFlags));
            }
        } else {
            int newValue = EnumSetConverter.convertToInt(this.flags);
            if (this.intFlags != newValue) {
                this.entityData.set(FLAGS, newValue);
                this.intFlags = newValue;
            }
        }
    }

    public void setIsCritical(boolean value) {
        if (value) {
            flags.add(FlagsState.Critical);
        } else {
            flags.remove(FlagsState.Critical);
        }
        refreshFlags();
    }

    public boolean getIsCritical() {
        refreshFlags();
        return flags.contains(FlagsState.Critical);
    }

    public void setNoClip(boolean value) {
        this.noPhysics = value;
        if (value) {
            flags.add(FlagsState.NoClip);
        } else {
            flags.remove(FlagsState.NoClip);
        }
        refreshFlags();
    }

    public boolean isNoClip() {
        if (!this.level().isClientSide()) {
            return this.noPhysics;
        } else {
            refreshFlags();
            return flags.contains(FlagsState.NoClip);
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (tickCount < 8 && tickCount % 2 == 0) {
            this.playSound(livingEntitySound, 0.2F, 0.5F + 0.25f * this.random.nextFloat());
        }

        if (this.getShooter() != null) {
            if (this.tickCount % 2 == 0) {
                KnockBacks knockBackType = getIsCritical() ? KnockBacks.toss : KnockBacks.cancel;
                AttackManager.areaAttack(this, knockBackType.action, 4.0, this.doCycleHit(), false);
            }

            if (getIsCritical() && tickCount > 0 && tickCount <= 3) {
                EntitySlashEffect jc = new EntitySlashEffect(SlashBlade.RegistryEvents.SlashEffect, this.level());
                jc.absMoveTo(this.getX(), this.getY(), this.getZ(), (360.0f / 3) * tickCount + this.seed, 0);
                jc.setRotationRoll(30);
                jc.setOwner(this.getShooter());
                jc.setMute(false);
                jc.setIsCritical(true);
                jc.setDamage(0.1F);
                jc.setColor(this.getColor());
                jc.setBaseSize(0.5f);
                jc.setKnockBack(KnockBacks.cancel);
                jc.setIndirect(true);
                jc.setRank(this.getRank());
                this.level().addFreshEntity(jc);
            }
        }

        tryDespawn();
    }

    protected void tryDespawn() {
        if (!this.level().isClientSide() && getLifetime() < this.tickCount) {
            this.burst();
        }
    }

    public int getColor() {
        return this.getEntityData().get(COLOR);
    }

    public void setColor(int value) {
        this.getEntityData().set(COLOR, value);
    }

    public float getRank() {
        return this.getEntityData().get(RANK);
    }

    public void setRank(float value) {
        this.getEntityData().set(RANK, value);
    }

    public int getLifetime() {
        return Math.min(this.lifetime, 1000);
    }

    public void setLifetime(int value) {
        this.lifetime = value;
    }

    @Nullable
    @Override
    public Entity getShooter() {
        return this.getOwner();
    }

    @Override
    public void setShooter(Entity shooter) {
        setOwner(shooter);
    }

    public List<MobEffectInstance> getPotionEffects() {
        List<MobEffectInstance> effects = PotionUtils.getAllEffects(this.getPersistentData());
        if (effects.isEmpty()) {
            effects.add(new MobEffectInstance(MobEffects.POISON, 1, 1));
        }
        return effects;
    }

    public void burst() {
        if (!this.level().isClientSide() && this.level() instanceof ServerLevel) {
            ((ServerLevel) this.level()).sendParticles(ParticleTypes.CRIT, this.getX(), this.getY(), this.getZ(), 16, 0.5, 0.5, 0.5, 0.25f);
            this.burst(getPotionEffects(), null);
        }
        super.remove(RemovalReason.DISCARDED);
    }

    public void burst(List<MobEffectInstance> effects, @Nullable Entity focusEntity) {
        List<Entity> list = TargetSelector.getTargettableEntitiesWithinAABB(this.level(), 2, this);
        list.stream()
                .filter(e -> e instanceof LivingEntity)
                .map(e -> (LivingEntity) e)
                .forEach(e -> {
                    double distanceSq = this.distanceToSqr(e);
                    if (distanceSq < 9.0D) {
                        double factor = 1.0D - Math.sqrt(distanceSq) / 4.0D;
                        if (e == focusEntity) {
                            factor = 1.0D;
                        }
                        affectEntity(e, effects, factor);
                    }
                });
    }

    public void affectEntity(LivingEntity focusEntity, List<MobEffectInstance> effects, double factor) {
        for (MobEffectInstance effectinstance : effects) {
            MobEffect effect = effectinstance.getEffect();
            if (effect.isInstantenous()) {
                effect.applyInstantenousEffect(this, this.getShooter(), focusEntity, effectinstance.getAmplifier(), factor);
            } else {
                int duration = (int) (factor * (double) effectinstance.getDuration() + 0.5D);
                if (duration > 0) {
                    focusEntity.addEffect(new MobEffectInstance(effect, duration, effectinstance.getAmplifier(), effectinstance.isAmbient(), effectinstance.isVisible()));
                }
            }
        }
    }

    public void setDamage(double damageIn) {
        this.damage = damageIn;
    }

    @Override
    public double getDamage() {
        return this.damage;
    }

    @Nullable
    public EntityHitResult getRayTrace(Vec3 start, Vec3 end) {
        return ProjectileUtil.getEntityHitResult(this.level(), this, start, end,
                this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0D),
                entity -> !entity.isSpectator() && entity.isAlive() && entity.isPickable() && entity != this.getShooter());
    }
}