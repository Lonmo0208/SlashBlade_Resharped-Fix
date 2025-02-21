package mods.flammpfeil.slashblade.entity;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public abstract class Projectile extends net.minecraft.world.entity.projectile.Projectile {
    private static final EntityDataAccessor<Integer> OWNERID = SynchedEntityData.defineId(Projectile.class, EntityDataSerializers.INT);

    protected Projectile(EntityType<? extends net.minecraft.world.entity.projectile.Projectile> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(OWNERID, -1);
    }

    @Nullable
    @Override
    public Entity getOwner() {
        int id = this.entityData.get(OWNERID);
        if (id >= 0) {
            Entity owner = this.level().getEntity(id);
            if (owner != null && super.getOwner() != owner) {
                this.setOwner(owner);
            }
            return owner;
        }
        return null;
    }

    @Override
    public void setOwner(@Nullable Entity owner) {
        if (owner != null) {
            this.entityData.set(OWNERID, owner.getId());
        } else {
            this.entityData.set(OWNERID, -1);
        }
        super.setOwner(owner);
    }
}