package mods.flammpfeil.slashblade.entity;

import com.google.common.collect.Lists;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.ability.StunManager;
import mods.flammpfeil.slashblade.capability.concentrationrank.IConcentrationRank;
import mods.flammpfeil.slashblade.util.AttackManager;
import mods.flammpfeil.slashblade.util.EnumSetConverter;
import mods.flammpfeil.slashblade.util.KnockBacks;
import mods.flammpfeil.slashblade.util.NBTHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;

import javax.annotation.Nullable;
import java.util.List;

public class EntityDrive extends EntityAbstractSummonedSword {
    private static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(EntityDrive.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> FLAGS = SynchedEntityData.defineId(EntityDrive.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> RANK = SynchedEntityData.defineId(EntityDrive.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> ROTATION_OFFSET = SynchedEntityData.defineId(EntityDrive.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> ROTATION_ROLL = SynchedEntityData.defineId(EntityDrive.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> BASESIZE = SynchedEntityData.defineId(EntityDrive.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> SPEED = SynchedEntityData.defineId(EntityDrive.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> LIFETIME = SynchedEntityData.defineId(EntityDrive.class, EntityDataSerializers.FLOAT);

    private KnockBacks action = KnockBacks.cancel;
    private double damage = 7.0D;
    private final List<Entity> alreadyHits = Lists.newArrayList();

    public EntityDrive(EntityType<? extends Projectile> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
        this.setNoGravity(true);
    }

    public static EntityDrive createInstance(PlayMessages.SpawnEntity packet, Level worldIn) {
        return new EntityDrive(SlashBlade.RegistryEvents.Drive, worldIn);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(COLOR, 0x3333FF);
        this.entityData.define(FLAGS, 0);
        this.entityData.define(RANK, 0.0f);
        this.entityData.define(LIFETIME, 10.0f);
        this.entityData.define(ROTATION_OFFSET, 0.0f);
        this.entityData.define(ROTATION_ROLL, 0.0f);
        this.entityData.define(BASESIZE, 1.0f);
        this.entityData.define(SPEED, 0.5f);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        NBTHelper.getNBTCoupler(compound)
                .put("RotationOffset", this.getRotationOffset())
                .put("RotationRoll", this.getRotationRoll())
                .put("BaseSize", this.getBaseSize())
                .put("Speed", this.getSpeed())
                .put("Color", this.getColor())
                .put("Rank", this.getRank())
                .put("damage", this.damage)
                .put("crit", this.getIsCritical())
                .put("clip", this.isNoClip())
                .put("Lifetime", this.getLifetime())
                .put("Knockback", this.getKnockBack().ordinal());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        NBTHelper.getNBTCoupler(compound)
                .get("RotationOffset", this::setRotationOffset)
                .get("RotationRoll", this::setRotationRoll)
                .get("BaseSize", this::setBaseSize)
                .get("Speed", this::setSpeed)
                .get("Color", this::setColor)
                .get("Rank", this::setRank)
                .get("damage", ((Double v) -> this.damage = v), this.damage)
                .get("crit", this::setIsCritical)
                .get("clip", this::setNoClip)
                .get("Lifetime", this::setLifetime)
                .get("Knockback", this::setKnockBackOrdinal);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
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
    public void tick() {
        super.tick();

        if (this.getShooter() != null && this.tickCount % 2 == 0) {
            boolean forceHit = true;
            List<Entity> hits;

            if (getShooter() instanceof LivingEntity shooter) {
                float ratio = (float) damage * (getIsCritical() ? 1.1f : 1.0f);
                hits = AttackManager.areaAttack(shooter, this.action.action, ratio, forceHit, false, true, alreadyHits);
            } else {
                hits = AttackManager.areaAttack(this, this.action.action, 4.0, forceHit, false, alreadyHits);
            }
            alreadyHits.addAll(hits);
        }

        tryDespawn();
    }

    protected void tryDespawn() {
        if (!this.level().isClientSide() && getLifetime() < this.tickCount) {
            this.remove(RemovalReason.DISCARDED);
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity targetEntity = result.getEntity();
        int damageValue = Mth.ceil(this.getDamage());

        if (this.getIsCritical()) {
            damageValue += this.random.nextInt(damageValue / 2 + 2);
        }

        Entity shooter = this.getShooter();
        DamageSource damageSource = shooter == null
                ? this.damageSources().indirectMagic(this, this)
                : this.damageSources().indirectMagic(this, shooter);

        if (shooter instanceof LivingEntity livingShooter) {
            Entity hitEntity = targetEntity instanceof PartEntity ? ((PartEntity<?>) targetEntity).getParent() : targetEntity;
            livingShooter.setLastHurtMob(hitEntity);
        }

        int fireTime = targetEntity.getRemainingFireTicks();
        if (this.isOnFire() && !(targetEntity instanceof EnderMan)) {
            targetEntity.setSecondsOnFire(5);
        }

        targetEntity.invulnerableTime = 0;
        float finalDamage = (float) damageValue;
        if (this.getOwner() instanceof LivingEntity living) {
            finalDamage *= living.getAttributeValue(Attributes.ATTACK_DAMAGE);
        }

        if (targetEntity.hurt(damageSource, finalDamage)) {
            Entity hitEntity = targetEntity instanceof PartEntity ? ((PartEntity<?>) targetEntity).getParent() : targetEntity;

            if (hitEntity instanceof LivingEntity targetLivingEntity) {
                StunManager.setStun(targetLivingEntity);
                if (!this.level().isClientSide() && shooter instanceof LivingEntity) {
                    EnchantmentHelper.doPostHurtEffects(targetLivingEntity, shooter);
                    EnchantmentHelper.doPostDamageEffects((LivingEntity) shooter, targetLivingEntity);
                }

                affectEntity(targetLivingEntity, getPotionEffects(), 1.0f);

                if (shooter != null && targetLivingEntity != shooter && targetLivingEntity instanceof Player
                        && shooter instanceof ServerPlayer) {
                    ((ServerPlayer) shooter).playNotifySound(this.getHitEntityPlayerSound(), SoundSource.PLAYERS, 0.18F, 0.45F);
                }
            }

            this.playSound(this.getHitEntitySound(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
        } else {
            targetEntity.setRemainingFireTicks(fireTime);
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        this.setRemoved(RemovalReason.DISCARDED);
    }

    @Nullable
    public EntityHitResult getRayTrace(Vec3 start, Vec3 end) {
        return ProjectileUtil.getEntityHitResult(this.level(), this, start, end,
                this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0D),
                entity -> !entity.isSpectator() && entity.isAlive() && entity.isPickable() && entity != this.getShooter());
    }

    // Getters and Setters
    public int getColor() { return this.getEntityData().get(COLOR); }
    public void setColor(int value) { this.getEntityData().set(COLOR, value); }
    public float getRank() { return this.getEntityData().get(RANK); }
    public void setRank(float value) { this.getEntityData().set(RANK, value); }
    public IConcentrationRank.ConcentrationRanks getRankCode() { return IConcentrationRank.ConcentrationRanks.getRankFromLevel(getRank()); }
    public float getRotationOffset() { return this.getEntityData().get(ROTATION_OFFSET); }
    public void setRotationOffset(float value) { this.getEntityData().set(ROTATION_OFFSET, value); }
    public float getRotationRoll() { return this.getEntityData().get(ROTATION_ROLL); }
    public void setRotationRoll(float value) { this.getEntityData().set(ROTATION_ROLL, value); }
    public float getBaseSize() { return this.getEntityData().get(BASESIZE); }
    public void setBaseSize(float value) { this.getEntityData().set(BASESIZE, value); }
    public float getSpeed() { return this.getEntityData().get(SPEED); }
    public void setSpeed(float value) { this.getEntityData().set(SPEED, value); }
    public float getLifetime() { return this.getEntityData().get(LIFETIME); }
    public void setLifetime(float value) { this.getEntityData().set(LIFETIME, value); }
    public KnockBacks getKnockBack() { return action; }
    public void setKnockBack(KnockBacks action) { this.action = action; }
    public void setKnockBackOrdinal(int ordinal) { this.action = (0 <= ordinal && ordinal < KnockBacks.values().length) ? KnockBacks.values()[ordinal] : KnockBacks.cancel; }
    public List<Entity> getAlreadyHits() { return alreadyHits; }
    public void setDamage(double damageIn) { this.damage = damageIn; }
    @Override
    public double getDamage() { return this.damage; }
    @Nullable
    @Override
    public Entity getShooter() { return this.getOwner(); }
    @Override
    public void setShooter(Entity shooter) { setOwner(shooter); }
    public List<MobEffectInstance> getPotionEffects() {
        List<MobEffectInstance> effects = PotionUtils.getAllEffects(this.getPersistentData());
        if (effects.isEmpty()) effects.add(new MobEffectInstance(MobEffects.POISON, 1, 1));
        return effects;
    }
}