package mods.flammpfeil.slashblade.entity;

import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.ability.StunManager;
import mods.flammpfeil.slashblade.capability.inputstate.InputStateCapabilityProvider;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.util.InputCommand;
import mods.flammpfeil.slashblade.util.KnockBacks;
import mods.flammpfeil.slashblade.util.RayTraceHelper;
import mods.flammpfeil.slashblade.util.TargetSelector;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PlayMessages;

import javax.annotation.Nullable;
import java.util.Optional;

public class EntityBlisteringSwords extends EntityAbstractSummonedSword {
    private static final EntityDataAccessor<Boolean> IT_FIRED = SynchedEntityData.defineId(EntityBlisteringSwords.class,
            EntityDataSerializers.BOOLEAN);

    private boolean itFiredCache;
    private long fireTime = -1;
    private final Vec3 tempVec = new Vec3(0, 0, 0);

    public EntityBlisteringSwords(EntityType<? extends Projectile> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
        this.setPierce((byte) 5);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IT_FIRED, false);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (IT_FIRED.equals(key)) {
            this.itFiredCache = this.entityData.get(IT_FIRED);
        }
    }

    public void doFire() {
        this.entityData.set(IT_FIRED, true);
        this.itFiredCache = true;
    }

    public boolean itFired() {
        return this.itFiredCache;
    }

    public static EntityBlisteringSwords createInstance(PlayMessages.SpawnEntity packet, Level worldIn) {
        return new EntityBlisteringSwords(SlashBlade.RegistryEvents.BlisteringSwords, worldIn);
    }

    @Override
    public void tick() {
        if (!itFired()) {
            if (getVehicle() == null) {
                startRiding(this.getOwner(), true);
            }
        }
        super.tick();
    }

    @Override
    public void rideTick() {
        Entity vehicle = this.getVehicle();
        if (vehicle == null) return;

        // 提前检查射击状态
        if (itFired() && fireTime <= tickCount) {
            // 移除 faceEntityStandby() 调用，直接进行射击逻辑
            Vec3 dir = this.getLookAngle();

            if (!(vehicle instanceof LivingEntity)) {
                this.shoot(dir.x, dir.y, dir.z, 5.0f, 1.0f); // 提高初始速度
                return;
            }

            LivingEntity sender = (LivingEntity) vehicle;
            this.stopRiding();
            this.tickCount = 0;

            Entity foundTarget = findTarget(sender);
            Vec3 targetPos = calculateTargetPosition(sender, foundTarget);
            Vec3 pos = this.getPosition(0.0f);
            dir = targetPos.subtract(pos).normalize();

            // 根据目标距离动态调整速度
            float speed = 5.0f; // 基础速度
            if (foundTarget != null) {
                double distance = pos.distanceTo(targetPos);
                speed = (float) Math.min(8.0f, 5.0f + distance * 0.2f); // 距离越远速度越快
            }

            this.shoot(dir.x, dir.y, dir.z, speed, 1.0f);
            if (sender instanceof ServerPlayer serverPlayer) {
                serverPlayer.playNotifySound(SoundEvents.ENDER_DRAGON_FLAP, SoundSource.PLAYERS, 1.0F, 1.0F);
            }
            return;
        }

        // 未发射时的逻辑
        this.setDeltaMovement(Vec3.ZERO);
        if (canUpdate()) {
            this.baseTick();
        }

        // 只在未发射时调整位置
        if (!itFired()) {
            faceEntityStandby();
        }

        // 发射检测
        if (!itFired() && vehicle instanceof LivingEntity owner) {
            owner.getCapability(InputStateCapabilityProvider.INPUT_STATE).ifPresent(s -> {
                if (!s.getCommands().contains(InputCommand.M_DOWN)) {
                    fireTime = tickCount + getDelay();
                    doFire();
                }
            });
        }
    }

    @Nullable
    private Entity findTarget(LivingEntity sender) {
        Entity lockTarget = null;
        if (sender.getMainHandItem().getItem() instanceof ItemSlashBlade) {
            lockTarget = sender.getMainHandItem().getCapability(ItemSlashBlade.BLADESTATE)
                    .filter(state -> state.getTargetEntity(sender.level()) != null)
                    .map(state -> state.getTargetEntity(sender.level()))
                    .orElse(null);
        }

        if (lockTarget != null) {
            return lockTarget;
        }

        Optional<HitResult> rayResultOpt = RayTraceHelper.rayTrace(
                sender.level(),
                sender,
                sender.getEyePosition(1.0f),
                sender.getLookAngle(),
                12,
                12,
                e -> true
        );

        if (rayResultOpt.isPresent() && rayResultOpt.get() instanceof EntityHitResult entityHit) {
            Entity target = entityHit.getEntity();
            if (validateTarget(sender, target)) {
                return target;
            }
        }
        return null;
    }

    private boolean validateTarget(LivingEntity sender, Entity target) {
        if (target instanceof LivingEntity living) {
            if (!TargetSelector.test.test(sender, living)) {
                return false;
            }
        }
        if (target instanceof IShootable shootable) {
            if (shootable.getShooter() == sender) {
                return false;
            }
        }
        return true;
    }

    private Vec3 calculateTargetPosition(LivingEntity sender, @Nullable Entity target) {
        if (target != null) {
            return new Vec3(target.getX(), target.getY() + target.getEyeHeight() * 0.5, target.getZ());
        }

        Vec3 start = sender.getEyePosition(1.0f);
        Vec3 end = start.add(sender.getLookAngle().scale(40));
        HitResult result = sender.level().clip(new ClipContext(
                start,
                end,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                sender
        ));
        return result.getLocation();
    }

    private void faceEntityStandby() {
        int spawnNum = getDelay();
        boolean isRight = spawnNum % 2 == 0;
        int level = spawnNum / 2;

        Entity vehicle = this.getVehicle();
        if (vehicle == null) {
            doFire();
            return;
        }

        Vec3 pos = new Vec3(vehicle.getX(), vehicle.getY() + vehicle.getEyeHeight() * 0.8, vehicle.getZ());

        double xOffset = (1 - 0.1 * level) * (isRight ? 1 : -1);
        double yOffset = 0.25 * level;
        double zOffset = -0.1 * level;

        Vec3 offset = new Vec3(xOffset, yOffset, zOffset);
        float yawRad = (float)Math.toRadians(-vehicle.getYRot());
        float pitchRad = (float)Math.toRadians(-vehicle.getXRot());

        offset = offset.xRot(pitchRad).yRot(yawRad);
        pos = pos.add(offset);

        this.xRotO = this.getXRot();
        this.yRotO = this.getYRot();
        setPos(pos);
        setRot(-vehicle.getYRot(), -vehicle.getXRot());
    }

    @Override
    protected void onHitBlock(BlockHitResult blockraytraceresult) {
        burst();
    }

    @Override
    protected void onHitEntity(EntityHitResult p_213868_1_) {
        Entity targetEntity = p_213868_1_.getEntity();
        if (targetEntity instanceof LivingEntity living) {
            KnockBacks.cancel.action.accept(living);
            StunManager.setStun(living);
        }
        super.onHitEntity(p_213868_1_);
    }
}