package mods.flammpfeil.slashblade.entity;

import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.ability.StunManager;
import mods.flammpfeil.slashblade.util.KnockBacks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PlayMessages;

public class EntityHeavyRainSwords extends EntityAbstractSummonedSword {
    private static final EntityDataAccessor<Boolean> IT_FIRED = SynchedEntityData.defineId(EntityHeavyRainSwords.class, EntityDataSerializers.BOOLEAN);

    private static final MobEffectInstance SLOW_EFFECT = new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 10);
    private static final int ON_GROUND_LIFE_TIME = 20;
    private int ticksInGround = 0;
    private long fireTime = -1;

    public EntityHeavyRainSwords(EntityType<? extends Projectile> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
        this.setPierce((byte) 5);

        CompoundTag compoundtag = this.getPersistentData();
        ListTag listtag = compoundtag.getList("CustomPotionEffects", 9);
        listtag.add(SLOW_EFFECT.save(new CompoundTag()));
        this.getPersistentData().put("CustomPotionEffects", listtag);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IT_FIRED, false);
    }

    public void doFire() {
        this.entityData.set(IT_FIRED, true);
    }

    public boolean itFired() {
        return this.entityData.get(IT_FIRED);
    }

    public static EntityHeavyRainSwords createInstance(PlayMessages.SpawnEntity packet, Level worldIn) {
        return new EntityHeavyRainSwords(SlashBlade.RegistryEvents.HeavyRainSwords, worldIn);
    }

    @Override
    public void tick() {
        if (!itFired() && level().isClientSide() && getVehicle() == null) {
            startRiding(this.getOwner(), true);
        }
        super.tick();
    }

    @Override
    public void rideTick() {
        if (itFired() && fireTime <= tickCount) {
            faceEntityStandby();
            this.stopRiding();

            Vec3 dir = new Vec3(0, -1, 0);
            this.shoot(dir.x, dir.y, dir.z, 4.0f, 2.0f);

            this.tickCount = 0;
            return;
        }

        this.setDeltaMovement(Vec3.ZERO);
        if (canUpdate()) {
            this.baseTick();
        }

        faceEntityStandby();

        if (!itFired()) {
            int basedelay = 10;
            fireTime = tickCount + basedelay + getDelay();
            doFire();
        }

        tryDespawn();
    }

    private void faceEntityStandby() {
        setPos(this.position());
        setRot(this.getYRot(), -90);
    }

    public void setSpread(Vec3 basePos) {
        double areaSize = 2.5;
        double offsetX = (this.random.nextDouble() * 2.0 - 1.0) * areaSize;
        double offsetZ = (this.random.nextDouble() * 2.0 - 1.0) * areaSize;

        setPos(basePos.x + offsetX, basePos.y, basePos.z + offsetZ);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity targetEntity = result.getEntity();
        if (targetEntity instanceof LivingEntity) {
            KnockBacks.cancel.action.accept((LivingEntity) targetEntity);
            StunManager.setStun((LivingEntity) targetEntity);
        }
        super.onHitEntity(result);
    }

    protected void tryDespawn() {
        if (++this.ticksInGround >= ON_GROUND_LIFE_TIME) {
            this.burst();
        }
    }
}