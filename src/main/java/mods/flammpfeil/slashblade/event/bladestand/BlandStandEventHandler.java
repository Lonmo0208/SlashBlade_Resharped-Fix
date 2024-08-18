package mods.flammpfeil.slashblade.event.bladestand;

import java.util.Map;

import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.data.builtin.SlashBladeBuiltInRegistry;
import mods.flammpfeil.slashblade.event.SlashBladeEvent;
import mods.flammpfeil.slashblade.init.SBItems;
import mods.flammpfeil.slashblade.recipe.RequestDefinition;
import mods.flammpfeil.slashblade.recipe.SlashBladeIngredient;
import mods.flammpfeil.slashblade.registry.SlashArtsRegistry;
import mods.flammpfeil.slashblade.registry.SpecialEffectsRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class BlandStandEventHandler {
	@SubscribeEvent
	public static void eventKoseki(SlashBladeEvent.BladeStandAttackEvent event) {
		var slashBladeDefinitionRegistry = SlashBlade.getSlashBladeDefinitionRegistry(event.getBladeStand().level());
		if(!slashBladeDefinitionRegistry.containsKey(SlashBladeBuiltInRegistry.KOSEKI.location()))
			return;
		if (!(event.getDamageSource().getEntity() instanceof WitherBoss))
			return;
		if(!event.getDamageSource().is(DamageTypeTags.IS_EXPLOSION))
			return;
		var in = SlashBladeIngredient.of(RequestDefinition.Builder.newInstance().build());
		if(!in.test(event.getBlade()))
			return;
		event.getBladeStand().setItem(slashBladeDefinitionRegistry.get(SlashBladeBuiltInRegistry.KOSEKI).getBlade());
		event.setCanceled(true);
	}

	@SubscribeEvent
	public static void eventChangeSE(SlashBladeEvent.BladeStandAttackEvent event) {
		if (!(event.getDamageSource().getEntity() instanceof Player))
			return;
		Player player = (Player) event.getDamageSource().getEntity();
		ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
		ItemStack blade = event.getBlade();
		if (blade.isEmpty())
			return;
		if (!stack.is(SBItems.proudsoul_crystal))
			return;
		var world = player.level();
		var state = event.getSlashBladeState();
		var bladeStand = event.getBladeStand();
		if (stack.getTag() == null)
			return;

		CompoundTag tag = stack.getTag();
		if (tag.contains("SpecialEffectType")) {
			ResourceLocation SEKey = new ResourceLocation(tag.getString("SpecialEffectType"));
			if (!(SpecialEffectsRegistry.REGISTRY.get().containsKey(SEKey)))
				return;
			if (state.hasSpecialEffect(SEKey))
				return;
			state.addSpecialEffect(SEKey);
			world.playSound(bladeStand, bladeStand.getPos(), SoundEvents.WITHER_SPAWN, SoundSource.BLOCKS, 1f, 1f);
			if (world.isClientSide())
				Minecraft.getInstance().particleEngine.createTrackingEmitter(bladeStand, ParticleTypes.PORTAL);
			if (!player.isCreative())
				stack.shrink(1);
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void eventChangeSA(SlashBladeEvent.BladeStandAttackEvent event) {
		if (!(event.getDamageSource().getEntity() instanceof Player))
			return;
		Player player = (Player) event.getDamageSource().getEntity();
		ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
		ItemStack blade = event.getBlade();
		if (blade.isEmpty())
			return;
		if (!stack.is(SBItems.proudsoul_sphere))
			return;
		var world = player.level();
		var state = event.getSlashBladeState();
		var bladeStand = event.getBladeStand();
		if (stack.getTag() == null)
			return;

		CompoundTag tag = stack.getTag();
		if (!tag.contains("SpecialAttackType")) return;
		
		ResourceLocation SAKey = new ResourceLocation(tag.getString("SpecialAttackType"));
		if (!(SlashArtsRegistry.REGISTRY.get().containsKey(SAKey)))
			return;

		ResourceLocation currentSA = state.getSlashArtsKey();
		if (!SAKey.equals(currentSA)) {
			state.setSlashArtsKey(SAKey);
			world.playSound(bladeStand, bladeStand.getPos(), SoundEvents.WITHER_SPAWN, SoundSource.BLOCKS, 1f, 1f);
			if (world.isClientSide())
				Minecraft.getInstance().particleEngine.createTrackingEmitter(bladeStand, ParticleTypes.PORTAL);
			if (!player.isCreative())
				stack.shrink(1);
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void eventCopySE(SlashBladeEvent.BladeStandAttackEvent event) {
		if (!(event.getDamageSource().getEntity() instanceof Player))
			return;
		Player player = (Player) event.getDamageSource().getEntity();
		ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
		ItemStack blade = event.getBlade();
		if (blade.isEmpty())
			return;
		if (!stack.is(SBItems.proudsoul_crystal))
			return;
		var world = player.level();
		var state = event.getSlashBladeState();
		var bladeStand = event.getBladeStand();
		var specialEffects = state.getSpecialEffects();

		for (var se : specialEffects) {
			if (!SpecialEffectsRegistry.REGISTRY.get().containsKey(se))
				continue;
			if (!SpecialEffectsRegistry.REGISTRY.get().getValue(se).isCopiable())
				continue;
			ItemStack orb = new ItemStack(SBItems.proudsoul_crystal);
			CompoundTag tag = new CompoundTag();
			tag.putString("SpecialEffectType", se.toString());
			orb.setTag(tag);
			if (!player.isCreative())
				stack.shrink(1);
			world.playSound(bladeStand, bladeStand.getPos(), SoundEvents.WITHER_SPAWN, SoundSource.BLOCKS, 1f, 1f);
			if (world.isClientSide())
				Minecraft.getInstance().particleEngine.createTrackingEmitter(bladeStand, ParticleTypes.PORTAL);
			player.drop(orb, true);
			if(SpecialEffectsRegistry.REGISTRY.get().getValue(se).isRemovable())
				state.removeSpecialEffect(se);
			event.setCanceled(true);
			return;
		}
	}

	@SubscribeEvent
	public static void eventCopySA(SlashBladeEvent.BladeStandAttackEvent event) {
		if (!(event.getDamageSource().getEntity() instanceof Player))
			return;
		Player player = (Player) event.getDamageSource().getEntity();
		ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
		ItemStack blade = event.getBlade();
		if (blade.isEmpty())
			return;
		if (!stack.is(SBItems.proudsoul_ingot) || !stack.isEnchanted())
			return;
		var world = player.level();
		var state = event.getSlashBladeState();
		var bladeStand = event.getBladeStand();

		ResourceLocation SA = state.getSlashArtsKey();
		if (SA != null && !SA.equals(SlashArtsRegistry.NONE.getId())) {
			ItemStack orb = new ItemStack(SBItems.proudsoul_sphere);
			CompoundTag tag = new CompoundTag();
			tag.putString("SpecialAttackType", state.getSlashArtsKey().toString());
			orb.setTag(tag);
			if (!player.isCreative())
				stack.shrink(1);
			world.playSound(bladeStand, bladeStand.getPos(), SoundEvents.WITHER_SPAWN, SoundSource.BLOCKS, 1f, 1f);
			if (world.isClientSide())
				Minecraft.getInstance().particleEngine.createTrackingEmitter(bladeStand, ParticleTypes.PORTAL);
			player.drop(orb, true);
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void eventProudSoulEnchantment(SlashBladeEvent.BladeStandAttackEvent event) {
		if (!(event.getDamageSource().getEntity() instanceof Player))
			return;
		Player player = (Player) event.getDamageSource().getEntity();
		ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
		ItemStack blade = event.getBlade();

		if (blade.isEmpty())
			return;

		if (!stack.isEnchanted())
			return;
		
		var world = player.level();
		var random = world.getRandom();
		var bladeStand = event.getBladeStand();
		Map<Enchantment, Integer> currentBladeEnchantments = blade.getAllEnchantments();
		stack.getAllEnchantments().forEach((enchantment, level) -> {
			if(event.isCanceled()) 
				return;
			if (!blade.canApplyAtEnchantingTable(enchantment))
				return;

			var probability = 1.0F;
			if (stack.is(SBItems.proudsoul_tiny))
				probability = 0.25F;
			if (stack.is(SBItems.proudsoul))
				probability = 0.5F;
			if (stack.is(SBItems.proudsoul_ingot))
				probability = 0.75F;
			if (random.nextFloat() <= probability) {
				int enchantLevel = EnchantmentHelper.getTagEnchantmentLevel(enchantment, blade) + 1;
				currentBladeEnchantments.put(enchantment, enchantLevel);
				EnchantmentHelper.setEnchantments(currentBladeEnchantments, blade);
				world.playSound(bladeStand, bladeStand.getPos(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS,
						1.0F, random.nextFloat() * 0.1F + 0.9F);
				if (world.isClientSide())
					Minecraft.getInstance().particleEngine.createTrackingEmitter(bladeStand,
							ParticleTypes.ENCHANTED_HIT);
			}
			if (!player.isCreative())
				stack.shrink(1);
			event.setCanceled(true);
		});

	}
}
