package mods.flammpfeil.slashblade.event;

import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.entity.BladeStandEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

public abstract class SlashBladeEvent extends Event {
	private final ItemStack blade;
	private final ISlashBladeState state;
	public SlashBladeEvent(ItemStack blade, ISlashBladeState state) {
		this.blade = blade;
		this.state = state;
	}
	
	public ItemStack getBlade() {
		return blade;
	}

	public ISlashBladeState getSlashBladeState() {
		return state;
	}
	
	@Cancelable
	public static class BladeStandAttackEvent extends SlashBladeEvent {
		private final BladeStandEntity bladeStand;
		private final DamageSource damageSource;
		public BladeStandAttackEvent(ItemStack blade, ISlashBladeState state, BladeStandEntity bladeStand, DamageSource damageSource) {
			super(blade, state);
			this.bladeStand = bladeStand;
			this.damageSource = damageSource;
		}
		
		public BladeStandEntity getBladeStand() {
			return bladeStand;
		}
		
		public DamageSource getDamageSource() {
			return damageSource;
		}
		
	}
	
	@Cancelable
	public static class HitEvent extends SlashBladeEvent {
		private final LivingEntity target;
		private final LivingEntity user;
		public HitEvent(ItemStack blade, ISlashBladeState state, LivingEntity target, LivingEntity user) {
			super(blade, state);
			this.target = target;
			this.user = user;
		}
		
		public LivingEntity getUser() {
			return user;
		}
		
		public LivingEntity getTarget() {
			return target;
		}
		
	}
	
	@Cancelable
	public static class UpdateEvent extends SlashBladeEvent {
		private final Level level;
		private final Entity entity;
		private final int itemSlot;
		private final boolean isSelected;
		
		public UpdateEvent(ItemStack blade, ISlashBladeState state, 
				Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
			super(blade, state);
			this.level = worldIn;
			this.entity = entityIn;
			this.itemSlot = itemSlot;
			this.isSelected = isSelected;
		}
		
		public Level getLevel() {
			return level;
		}
		
		public Entity getEntity() {
			return entity;
		}
		
		public int getItemSlot() {
			return itemSlot;
		}
		
		public boolean isSelected() {
			return isSelected;
		}

	}
}
