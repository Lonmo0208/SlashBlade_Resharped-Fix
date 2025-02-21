package mods.flammpfeil.slashblade.entity.ai;

import mods.flammpfeil.slashblade.capability.mobeffect.CapabilityMobEffect;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class StunGoal extends Goal {
    private final PathfinderMob entity;

    public StunGoal(PathfinderMob entity) {
        this.entity = entity;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK, Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        return this.entity.getCapability(CapabilityMobEffect.MOB_EFFECT)
                .map(state -> state.isStun(this.entity.level().getGameTime()))
                .orElse(false);
    }

    @Override
    public void stop() {
        this.entity.getCapability(CapabilityMobEffect.MOB_EFFECT).ifPresent(state -> state.clearStunTimeOut());
    }
}