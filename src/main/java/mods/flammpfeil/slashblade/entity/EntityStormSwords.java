package mods.flammpfeil.slashblade.entity;

import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.ability.StunManager;
import mods.flammpfeil.slashblade.util.KnockBacks;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PlayMessages;

public class EntityStormSwords extends EntityAbstractSummonedSword {
    private static final EntityDataAccessor<Boolean> IT_FIRED = SynchedEntityData.defineId(EntityStormSwords.class, EntityDataSerializers.BOOLEAN);

    public EntityStormSwords(EntityType<? extends Projectile> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
        this.setPierce((byte) 1);
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

    public static EntityStormSwords createInstance(PlayMessages.SpawnEntity packet, Level worldIn) {
        return new EntityStormSwords(SlashBlade.RegistryEvents.StormSwords, worldIn);
    }

    @Override
    public void rideTick() {
        if (itFired()) {
            faceEntityStandby();
            Entity target = getVehicle();
            this.stopRiding();

            this.tickCount = 0;
            Vec3 dir = target != null
                    ? target.position().subtract(this.position()).multiply(1, 0, 1).normalize()
                    : this.getViewVector(1.0f);

            this.shoot(dir.x, dir.y, dir.z, 3.0f, 1.0f);
            return;
        }

        this.setDeltaMovement(Vec3.ZERO);
        if (canUpdate()) {
            this.baseTick();
        }

        faceEntityStandby();

        if (this.tickCount >= 20) {
            doFire();
        }

        if (!level().isClientSide()) {
            hitCheck();
        }
    }

    private void hitCheck() {
        Vec3 positionVec = this.position();
        Vec3 dirVec = this.getViewVector(1.0f);
        EntityHitResult raytraceresult = this.getRayTrace(positionVec, dirVec);

        if (raytraceresult != null && raytraceresult.getType() == HitResult.Type.ENTITY) {
            Entity entity = raytraceresult.getEntity();
            Entity shooter = this.getShooter();

            if (entity instanceof Player && shooter instanceof Player
                    && !((Player) shooter).canHarmPlayer((Player) entity)) {
                return;
            }

            if (!net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, raytraceresult)) {
                this.onHit(raytraceresult);
                this.resetAlreadyHits();
                this.hasImpulse = true;
            }
        }
    }

    private void faceEntityStandby() {
        long cycle = 5 + this.tickCount;
        long tickOffset = this.level().isClientSide() ? 1 : 0;
        int ticks = (int) ((this.tickCount + tickOffset) % cycle);

        double rotParTick = 360.0 / cycle;
        double offset = getDelay();
        double degYaw = (ticks * rotParTick + offset) % 360.0;
        double yaw = Math.toRadians(degYaw);

        Vec3 dir = new Vec3(0, 0, 1).yRot((float) -yaw).normalize().scale(4);

        if (this.getVehicle() != null) {
            dir = dir.add(this.getVehicle().position()).add(0, this.getVehicle().getEyeHeight() / 2.0, 0);
        }

        this.setPos(dir);
        this.setRot((float) (-degYaw) - 180, 0);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity targetEntity = result.getEntity();
        if (targetEntity instanceof LivingEntity) {
            KnockBacks.toss.action.accept((LivingEntity) targetEntity);
            StunManager.setStun((LivingEntity) targetEntity);
        }
        super.onHitEntity(result);
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        burst();
    }
}