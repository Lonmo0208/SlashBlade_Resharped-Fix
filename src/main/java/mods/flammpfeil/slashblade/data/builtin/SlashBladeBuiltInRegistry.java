package mods.flammpfeil.slashblade.data.builtin;

import java.util.List;

import org.apache.commons.compress.utils.Lists;

import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.item.SwordType;
import mods.flammpfeil.slashblade.registry.slashblade.EnchantmentDefinition;
import mods.flammpfeil.slashblade.registry.slashblade.PropertiesDefinition;
import mods.flammpfeil.slashblade.registry.slashblade.RenderDefinition;
import mods.flammpfeil.slashblade.registry.slashblade.SlashBladeDefinition;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.registries.ForgeRegistries;

public class SlashBladeBuiltInRegistry {
    public static final ResourceKey<SlashBladeDefinition> YAMATO = register("yamato");
    public static final ResourceKey<SlashBladeDefinition> TUKUMO = register("yuzukitukumo");
    public static final ResourceKey<SlashBladeDefinition> MURAMASA = register("muramasa");
    public static final ResourceKey<SlashBladeDefinition> SANGE = register("sange");
    public static void registerAll(BootstapContext<SlashBladeDefinition> bootstrap) {
        bootstrap.register(YAMATO, new SlashBladeDefinition(SlashBlade.prefix("yamato"), 
                RenderDefinition.Builder.newInstance()
                .textureName(SlashBlade.prefix("model/named/yamato.png"))
                .modelName(SlashBlade.prefix("model/named/yamato.obj"))
                .build(), 
                PropertiesDefinition.Builder.newInstance()
                .baseAttackModifier(12.0F)
                .defaultSwordType(List.of(SwordType.BEWITCHED))
                .build(), 
                List.of(new EnchantmentDefinition(getEnchantmentID(Enchantments.SOUL_SPEED), 1))
                ));
        
        bootstrap.register(MURAMASA, new SlashBladeDefinition(SlashBlade.prefix("muramasa"), 
                RenderDefinition.Builder.newInstance()
                .textureName(SlashBlade.prefix("model/named/muramasa/muramasa.png"))
                .modelName(SlashBlade.prefix("model/named/muramasa/muramasa.obj"))
                .build(), 
                PropertiesDefinition.Builder.newInstance()
                .baseAttackModifier(10.0F)
                .maxDamage(50)
                .build(), 
                Lists.newArrayList()
                ));
        
        bootstrap.register(SANGE, new SlashBladeDefinition(SlashBlade.prefix("sange"), 
                RenderDefinition.Builder.newInstance()
                .textureName(SlashBlade.prefix("model/named/sange/sange.png"))
                .modelName(SlashBlade.prefix("model/named/sange/sange.obj"))
                .build(), 
                PropertiesDefinition.Builder.newInstance()
                .baseAttackModifier(10.0F)
                .maxDamage(70)
                .defaultSwordType(List.of(SwordType.BEWITCHED))
                .build(), 
                Lists.newArrayList()
                ));
        
        bootstrap.register(TUKUMO, new SlashBladeDefinition(SlashBlade.prefix("yuzukitukumo"), 
                RenderDefinition.Builder.newInstance()
                .textureName(SlashBlade.prefix("model/named/a_tukumo.png"))
                .modelName(SlashBlade.prefix("model/named/agito.obj"))
                .build(), 
                PropertiesDefinition.Builder.newInstance()
                .baseAttackModifier(10.0F)
                .defaultSwordType(List.of(SwordType.BEWITCHED))
                .build(), 
                List.of(
                        new EnchantmentDefinition(getEnchantmentID(Enchantments.FIRE_ASPECT), 1),
                        new EnchantmentDefinition(getEnchantmentID(Enchantments.SHARPNESS), 4),
                        new EnchantmentDefinition(getEnchantmentID(Enchantments.UNBREAKING), 3)
                        )
                ));
    }
    
    private static ResourceLocation getEnchantmentID(Enchantment enchantment) {
        return ForgeRegistries.ENCHANTMENTS.getKey(enchantment);
    }
    
    private static ResourceKey<SlashBladeDefinition> register(String id) {
        ResourceKey<SlashBladeDefinition> loc = ResourceKey.create(SlashBladeDefinition.REGISTRY_KEY, SlashBlade.prefix(id));
        return loc;
    }
}
