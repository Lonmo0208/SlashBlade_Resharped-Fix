package mods.flammpfeil.slashblade.event;

import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.entity.BladeItemEntity;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class EntityDropEvent {
    @SubscribeEvent
    public static void name(LivingDropsEvent event) {
        if (event.getSource().getEntity()instanceof LivingEntity living) {
            ItemStack yamatoBroken = SlashBlade.getSlashBladeDefinitionRegistry(living.level())
                    .get(SlashBlade.prefix("yamato")).getBlade();
            yamatoBroken.getCapability(ItemSlashBlade.BLADESTATE).ifPresent(state->{
                state.setBroken(true);
                state.setSealed(true);
                yamatoBroken.setDamageValue(yamatoBroken.getMaxDamage() - 1);
            });
            dropBlade(event.getEntity(), EntityType.ENDER_DRAGON, yamatoBroken, 1F, 0D, 70D, 0D);
            if (living.getMainHandItem().getItem() instanceof ItemSlashBlade) {
                float looting = living.getMainHandItem().getEnchantmentLevel(Enchantments.MOB_LOOTING) * 0.15F;
                dropBlade(event.getEntity(), EntityType.WITHER, SlashBlade.prefix("sange"),
                        Math.min(1F, 0.3F + looting));
            }
        }
    }

    public static void dropBlade(LivingEntity entity, EntityType<?> type, ResourceLocation bladeName, float percent) {
        var result = SlashBlade.getSlashBladeDefinitionRegistry(entity.level()).get(bladeName).getBlade();
        dropBlade(entity, type, result, percent);
    }

    public static void dropBlade(LivingEntity entity, EntityType<?> type, ResourceLocation bladeName, float percent,
            double x, double y, double z) {
        var result = SlashBlade.getSlashBladeDefinitionRegistry(entity.level()).get(bladeName).getBlade();
        dropBlade(entity, type, result, percent, x, y, z);
    }

    public static void dropBlade(LivingEntity entity, EntityType<?> type, ItemStack blade, float percent) {
        dropBlade(entity, type, blade, percent, entity.getX(), entity.getY(), entity.getZ());
    }

    public static void dropBlade(LivingEntity entity, EntityType<?> type, ItemStack blade, float percent, double x,
            double y, double z) {
        if (entity.getType().equals(type)) {
            var rand = entity.level().getRandom();

            if (rand.nextFloat() > percent)
                return;
            ItemEntity itementity = new ItemEntity(entity.level(), x, y, z, blade);
            BladeItemEntity e = new BladeItemEntity(SlashBlade.RegistryEvents.BladeItem, entity.level());

            e.restoreFrom(itementity);
            e.init();
            e.push(0, 0.4, 0);

            e.setPickUpDelay(20 * 2);
            e.setGlowingTag(true);

            e.setAirSupply(-1);

            e.setThrower(entity.getUUID());

            entity.level().addFreshEntity(e);
        }
    }
}
