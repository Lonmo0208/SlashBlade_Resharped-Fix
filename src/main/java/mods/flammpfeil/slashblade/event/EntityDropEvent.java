package mods.flammpfeil.slashblade.event;

import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.entity.BladeItemEntity;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class EntityDropEvent {
    @SubscribeEvent
    public static void name(LivingDropsEvent event) {
        if(event.getSource().getEntity() instanceof LivingEntity living) {
            dropBlade(event.getEntity(), EntityType.ENDER_DRAGON, SlashBlade.prefix("yamato"), 0D, 70D, 0D);
            if(living.getMainHandItem().getItem() instanceof ItemSlashBlade) {
                dropBlade(event.getEntity(), EntityType.WITHER, SlashBlade.prefix("sange"));
                
            }
        }        
    }
    
    private static void dropBlade(LivingEntity entity, EntityType<?> type, ResourceLocation bladeName) {
        var result = SlashBlade.getSlashBladeDefinitionRegistry(entity.level()).get(bladeName).getBlade();
        dropBlade(entity, type, result);
    }
    
    private static void dropBlade(LivingEntity entity, EntityType<?> type, ResourceLocation bladeName, double x, double y, double z) {
        var result = SlashBlade.getSlashBladeDefinitionRegistry(entity.level()).get(bladeName).getBlade();
        dropBlade(entity, type, result, x, y, z);
    }
    
    private static void dropBlade(LivingEntity entity, EntityType<?> type, ItemStack blade) {
        dropBlade(entity, type, blade, entity.getX(), entity.getY(), entity.getZ());
    }
    
    private static void dropBlade(LivingEntity entity, EntityType<?> type, ItemStack blade, double x, double y, double z) {
        if(entity.getType().equals(type)) {
            ItemEntity itementity = new ItemEntity(entity.level(), x, y, z, blade);
            BladeItemEntity e = new BladeItemEntity(SlashBlade.RegistryEvents.BladeItem, entity.level());

            e.restoreFrom(itementity);
            e.init();
            e.push(0,0.4,0);
            
            e.setPickUpDelay(20*2);
            e.setGlowingTag(true);

            e.setAirSupply(-1);

            e.setThrower(entity.getUUID());
            
            entity.level().addFreshEntity(e);
        }
    }
}
