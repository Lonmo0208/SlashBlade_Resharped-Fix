package mods.flammpfeil.slashblade.event.bladestand;

import mods.flammpfeil.slashblade.event.SlashBladeEvent;
import mods.flammpfeil.slashblade.init.SBItems;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.registry.SlashArtsRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@EventBusSubscriber()
public class BlandStandEventHandler {
	@SubscribeEvent
	public static void eventChangeSE(SlashBladeEvent.BladeStandAttackEvent event) {
		if(!(event.getDamageSource().getEntity() instanceof ServerPlayer))
			return;
		Player player = (Player) event.getDamageSource().getEntity();
		ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);


	}

	@SubscribeEvent
	public static void eventChangeSA(SlashBladeEvent.BladeStandAttackEvent event) {
		if(!(event.getDamageSource().getEntity() instanceof ServerPlayer))
			return;
		Player player = (Player) event.getDamageSource().getEntity();
		ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
		CompoundTag tag = stack.getTag();

		if (!stack.is(SBItems.proudsoul_sphere) || tag == null || !tag.contains("SpecialAttackType"))
			return;

		ResourceLocation SAKey = new ResourceLocation(tag.getString("SpecialAttackType"));
		if (!SlashArtsRegistry.REGISTRY.get().containsKey(SAKey))
			return;

		ItemStack blade = event.getBlade();

		blade.getCapability(ItemSlashBlade.BLADESTATE).ifPresent(state -> {
			if (!SAKey.equals(state.getSlashArtsKey())) {
				state.setSlashArtsKey(SAKey);

				RandomSource random = player.getRandom();
				player.level().playSound(event.getBladeStand(), event.getBladeStand().getPos(),
						SoundEvents.WITHER_SPAWN, SoundSource.BLOCKS, 1f, 1f);
				for(int i = 0; i < 32; ++i) {
					double xDist = (random.nextFloat() * 2.0F - 1.0F);
					double yDist = (random.nextFloat() * 2.0F - 1.0F);
					double zDist = (random.nextFloat() * 2.0F - 1.0F);
					if (!(xDist * xDist + yDist * yDist + zDist * zDist > 1.0D)) {
						double x = event.getBladeStand().getX(xDist / 4.0D);
						double y = event.getBladeStand().getY(0.5D + yDist / 4.0D);
						double z = event.getBladeStand().getZ(zDist / 4.0D);
						((ServerLevel)player.level()).sendParticles(ParticleTypes.PORTAL, x, y, z,0, xDist, yDist + 0.2D, zDist,1);
					}
				}

				if (!player.isCreative()){
					stack.shrink(1);
				}
			}
		});
		event.setCanceled(true);//防止掉落拔刀

	}

	@SubscribeEvent
	public static void eventCopySE(SlashBladeEvent.BladeStandAttackEvent event) {
		if(!(event.getDamageSource().getEntity() instanceof ServerPlayer))
			return;
		Player player = (Player) event.getDamageSource().getEntity();
		ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);


	}

	@SubscribeEvent
	public static void eventCopySA(SlashBladeEvent.BladeStandAttackEvent event) {
		if(!(event.getDamageSource().getEntity() instanceof ServerPlayer))
			return;
		Player player = (Player) event.getDamageSource().getEntity();
		ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);


	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void eventProudSoulEnchantment(SlashBladeEvent.BladeStandAttackEvent event) {
		if(!(event.getDamageSource().getEntity() instanceof ServerPlayer))
			return;
		Player player = (Player) event.getDamageSource().getEntity();
		ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
		if(!stack.isEnchanted())
			return ;

		float successProbability;
		if (stack.is(SBItems.proudsoul_tiny)){
			successProbability = 0.25f;
		}else if (stack.is(SBItems.proudsoul)){
			successProbability = 0.5f;
		}else if (stack.is(SBItems.proudsoul_ingot)){
			successProbability = 0.75f;
		} else if (stack.is(SBItems.proudsoul_sphere) || stack.is(SBItems.proudsoul_crystal)
				|| stack.is(SBItems.proudsoul_trapezohedron)) {
			successProbability = 1f;
		} else {
			return;
		}

		ItemStack blade = event.getBlade();
		RandomSource random = player.getRandom();
		Map<Enchantment, Integer> currentBladeEnchantments = EnchantmentHelper.getEnchantments(blade);
		AtomicBoolean canEnchanted = new AtomicBoolean(false);
		stack.getAllEnchantments().forEach((enchantment,level)->{
			if(!blade.canApplyAtEnchantingTable(enchantment))
				return;
			if (currentBladeEnchantments.containsKey(enchantment)){
				if (currentBladeEnchantments.get(enchantment) >= enchantment.getMaxLevel())//已达该附魔的最大等级则不添加
					return;
				currentBladeEnchantments.put(enchantment, currentBladeEnchantments.get(enchantment) + level);
				canEnchanted.set(true);
			}else {
				currentBladeEnchantments.put(enchantment, level);
				canEnchanted.set(true);
			}
		});

		if (canEnchanted.get()){
			if (random.nextFloat() <= successProbability){
				EnchantmentHelper.setEnchantments(currentBladeEnchantments, blade);

				player.level().playSound(event.getBladeStand(), event.getBladeStand().getPos(),
						SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1f, random.nextFloat() * 0.1F + 0.9F);
				for(int i = 0; i < 32; ++i) {
					double d0 = (random.nextFloat() * 2.0F - 1.0F);
					double d1 = (random.nextFloat() * 2.0F - 1.0F);
					double d2 = (random.nextFloat() * 2.0F - 1.0F);
					if (!(d0 * d0 + d1 * d1 + d2 * d2 > 1.0D)) {
						double d3 = event.getBladeStand().getX(d0 / 4.0D);
						double d4 = event.getBladeStand().getY(0.5D + d1 / 4.0D);
						double d5 = event.getBladeStand().getZ(d2 / 4.0D);
						((ServerLevel)player.level()).sendParticles(ParticleTypes.ENCHANTED_HIT, d3, d4, d5,0, d0, d1 + 0.2D, d2,1);
					}
				}
			}

			if (!player.isCreative()){
				stack.shrink(1);
			}
		}
		event.setCanceled(true);//防止掉落拔刀
	}
}
