package mods.flammpfeil.slashblade.entity;

import mods.flammpfeil.slashblade.SlashBlade;
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

public class EntitySpiralSwords extends EntityAbstractSummonedSword {
    private static final EntityDataAccessor<Boolean> IT_FIRED = SynchedEntityData.defineId(EntitySpiralSwords.class, EntityDataSerializers.BOOLEAN);

    public EntitySpiralSwords(EntityType<? extends Projectile> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
        this.setPierce((byte) 5);
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

    public static EntitySpiralSwords createInstance(PlayMessages.SpawnEntity packet, Level worldIn) {
        return new EntitySpiralSwords(SlashBlade.RegistryEvents.SpiralSwords, worldIn);
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
        if (itFired()) {
            faceEntityStandby();
            Entity target = getVehicle();
            this.stopRiding();

            this.tickCount = 0;
            Vec3 dir = target != null
                    ? this.position().subtract(target.position()).multiply(1, 0, 1).normalize()
                    : this.getViewVector(1.0f);

            this.shoot(dir.x, dir.y, dir.z, 3.0f, 1.0f);
            return;
        }

        this.setDeltaMovement(Vec3.ZERO);
        if (canUpdate()) {
            this.baseTick();
        }

        faceEntityStandby();

        if (this.tickCount > 200) {
            burst();
        }

        if (!level().isClientSide()) {
            hitCheck();
        }
    }

    private void hitCheck() {
        if (this.tickCount % 5 != 0) return;

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
        Entity vehicle = this.getVehicle();
        if (vehicle == null) return;

        long cycle = 30;
        long tickOffset = this.level().isClientSide() ? 1 : 0;
        int ticks = (int) ((this.level().getGameTime() + tickOffset) % cycle);

        double rotParTick = 360.0 / cycle;
        double offset = getDelay();
        double degYaw = (ticks * rotParTick + offset) % 360.0;
        double yaw = Math.toRadians(degYaw);

        Vec3 dir = new Vec3(0, 0, 1).yRot((float) -yaw).normalize().scale(2);
        Vec3 targetPos = vehicle.position().add(0, vehicle.getEyeHeight() / 2.0, 0);

        this.setPos(targetPos.add(dir));
        this.setRot((float) -degYaw, 0);
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        burst();
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity targetEntity = result.getEntity();
        if (targetEntity instanceof LivingEntity) {
            KnockBacks.cancel.action.accept((LivingEntity) targetEntity);
        }
        super.onHitEntity(result);
    }
}