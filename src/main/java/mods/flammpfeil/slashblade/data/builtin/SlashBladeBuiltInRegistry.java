package mods.flammpfeil.slashblade.data.builtin;

import java.util.List;

import org.apache.commons.compress.utils.Lists;

import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.item.SwordType;
import mods.flammpfeil.slashblade.registry.SlashArtsRegistry;
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
    public static final ResourceKey<SlashBladeDefinition> RUBY = register("ruby");
    public static final ResourceKey<SlashBladeDefinition> SANGE = register("sange");
    
    public static final ResourceKey<SlashBladeDefinition> FOX_BLACK = register("fox_black");
    public static final ResourceKey<SlashBladeDefinition> FOX_WHITE = register("fox_white");

    public static void registerAll(BootstapContext<SlashBladeDefinition> bootstrap) {

        bootstrap.register(RUBY,
                new SlashBladeDefinition(SlashBlade.prefix("ruby"),
                        RenderDefinition.Builder.newInstance().textureName(SlashBlade.prefix("model/ruby.png")).build(),
                        PropertiesDefinition.Builder.newInstance().baseAttackModifier(5.0F).maxDamage(45).build(),
                        Lists.newArrayList()));

        bootstrap.register(FOX_BLACK, new SlashBladeDefinition(SlashBlade.prefix("fox_black"),
                RenderDefinition.Builder.newInstance()
                    .textureName(SlashBlade.prefix("model/named/sange/black.png"))
                    .modelName(SlashBlade.prefix("model/named/sange/sange.obj")).build(),
                PropertiesDefinition.Builder.newInstance()
                    .baseAttackModifier(5.0F)
                    .maxDamage(70)
                    .slashArtsType(SlashArtsRegistry.VOID_SLASH.getId())
                    .defaultSwordType(List.of(SwordType.BEWITCHED)).build(),
                List.of(
                        new EnchantmentDefinition(getEnchantmentID(Enchantments.SMITE), 4),
                        new EnchantmentDefinition(getEnchantmentID(Enchantments.KNOCKBACK), 2),
                        new EnchantmentDefinition(getEnchantmentID(Enchantments.FIRE_ASPECT), 2)
                        )));
        
        bootstrap.register(FOX_WHITE, new SlashBladeDefinition(SlashBlade.prefix("fox_white"),
                RenderDefinition.Builder.newInstance()
                    .textureName(SlashBlade.prefix("model/named/sange/white.png"))
                    .modelName(SlashBlade.prefix("model/named/sange/sange.obj")).build(),
                PropertiesDefinition.Builder.newInstance()
                    .baseAttackModifier(5.0F)
                    .maxDamage(70)
                    .defaultSwordType(List.of(SwordType.BEWITCHED)).build(),
                List.of(
                        new EnchantmentDefinition(getEnchantmentID(Enchantments.KNOCKBACK), 2),
                        new EnchantmentDefinition(getEnchantmentID(Enchantments.BANE_OF_ARTHROPODS), 2),
                        new EnchantmentDefinition(getEnchantmentID(Enchantments.UNBREAKING), 3),
                        new EnchantmentDefinition(getEnchantmentID(Enchantments.FIRE_ASPECT), 2),
                        new EnchantmentDefinition(getEnchantmentID(Enchantments.MOB_LOOTING), 3)
                        )));

        bootstrap.register(YAMATO,
                new SlashBladeDefinition(SlashBlade.prefix("yamato"),
                        RenderDefinition.Builder.newInstance().textureName(SlashBlade.prefix("model/named/yamato.png"))
                                .modelName(SlashBlade.prefix("model/named/yamato.obj")).build(),
                        PropertiesDefinition.Builder.newInstance().baseAttackModifier(8.0F)
                                .defaultSwordType(List.of(SwordType.BEWITCHED)).build(),
                        List.of(new EnchantmentDefinition(getEnchantmentID(Enchantments.SOUL_SPEED), 2),
                                new EnchantmentDefinition(getEnchantmentID(Enchantments.POWER_ARROWS), 5),
                                new EnchantmentDefinition(getEnchantmentID(Enchantments.FALL_PROTECTION), 4))));

        bootstrap.register(TUKUMO, new SlashBladeDefinition(SlashBlade.prefix("yuzukitukumo"),
                RenderDefinition.Builder.newInstance().textureName(SlashBlade.prefix("model/named/a_tukumo.png"))
                        .modelName(SlashBlade.prefix("model/named/agito.obj")).build(),
                PropertiesDefinition.Builder.newInstance().baseAttackModifier(6.0F)
                        .slashArtsType(SlashArtsRegistry.SAKURA_END.getId())
                        .defaultSwordType(List.of(SwordType.BEWITCHED)).build(),
                List.of(new EnchantmentDefinition(getEnchantmentID(Enchantments.FIRE_ASPECT), 1),
                        new EnchantmentDefinition(getEnchantmentID(Enchantments.SHARPNESS), 4),
                        new EnchantmentDefinition(getEnchantmentID(Enchantments.UNBREAKING), 3))));

        bootstrap.register(MURAMASA,
                new SlashBladeDefinition(SlashBlade.prefix("muramasa"),
                        RenderDefinition.Builder
                                .newInstance().textureName(SlashBlade.prefix("model/named/muramasa/muramasa.png"))
                                .modelName(SlashBlade.prefix("model/named/muramasa/muramasa.obj")).build(),
                        PropertiesDefinition.Builder.newInstance().baseAttackModifier(8.0F).maxDamage(50).build(),
                        Lists.newArrayList()));

        bootstrap.register(SANGE, new SlashBladeDefinition(SlashBlade.prefix("sange"),
                RenderDefinition.Builder.newInstance().textureName(SlashBlade.prefix("model/named/sange/sange.png"))
                        .modelName(SlashBlade.prefix("model/named/sange/sange.obj")).build(),
                PropertiesDefinition.Builder.newInstance().baseAttackModifier(6.0F).maxDamage(70)
                        .slashArtsType(SlashArtsRegistry.SAKURA_END.getId())
                        .defaultSwordType(List.of(SwordType.BEWITCHED)).build(),
                Lists.newArrayList()));

    }

    private static ResourceLocation getEnchantmentID(Enchantment enchantment) {
        return ForgeRegistries.ENCHANTMENTS.getKey(enchantment);
    }

    private static ResourceKey<SlashBladeDefinition> register(String id) {
        ResourceKey<SlashBladeDefinition> loc = ResourceKey.create(SlashBladeDefinition.REGISTRY_KEY,
                SlashBlade.prefix(id));
        return loc;
    }
}
