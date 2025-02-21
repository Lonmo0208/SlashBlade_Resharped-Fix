package mods.flammpfeil.slashblade.entity;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

public interface IShootable {

    void shoot(double x, double y, double z, float velocity, float inaccuracy);

    @Nullable
    EntityHitResult getRayTrace(Vec3 start, Vec3 end);

    List<MobEffectInstance> getPotionEffects();

    void setNoClip(boolean value);

    Entity getShooter();

    void setShooter(Entity shooter);

    double getDamage();
}
