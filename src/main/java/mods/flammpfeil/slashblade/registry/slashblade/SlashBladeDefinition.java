package mods.flammpfeil.slashblade.registry.slashblade;

import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import mods.flammpfeil.slashblade.SlashBlade;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class SlashBladeDefinition {
    // TODO : 记得处理相关耦合代码
    public static final Codec<SlashBladeDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RenderDefinition.CODEC.fieldOf("model")
                    .forGetter(SlashBladeDefinition::getRenderDefinition),
            PropertiesDefinition.CODEC.fieldOf("properties")
                    .forGetter(SlashBladeDefinition::getStateDefinition),
            EnchantmentInstance.CODEC.listOf().optionalFieldOf("enchantments", Lists.newArrayList())
                    .forGetter(SlashBladeDefinition::getEnchantments))
            .apply(instance, SlashBladeDefinition::new));
    
    public static final ResourceKey<Registry<SlashBladeDefinition>> REGISTRY_KEY = ResourceKey
            .createRegistryKey(SlashBlade.prefix("named_blades"));
    
    private final RenderDefinition renderDefinition;
    private final PropertiesDefinition stateDefinition;
    private final List<EnchantmentInstance> enchantments;
    
    public SlashBladeDefinition(
            RenderDefinition renderDefinition, 
            PropertiesDefinition stateDefinition, 
            List<EnchantmentInstance> enchantments
            ) {
        this.renderDefinition = renderDefinition;
        this.stateDefinition = stateDefinition;
        this.enchantments = enchantments;
    }
    
    public RenderDefinition getRenderDefinition() {
        return renderDefinition;
    }

    public PropertiesDefinition getStateDefinition() {
        return stateDefinition;
    }

    public List<EnchantmentInstance> getEnchantments() {
        return enchantments;
    }
    
    public static class EnchantmentInstance{
        public static final Codec<EnchantmentInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("id")
                        .forGetter(EnchantmentInstance::getEnchantmentID),
                Codec.INT.optionalFieldOf("lvl", 1)
                        .forGetter(EnchantmentInstance::getEnchantmentLevel))
                .apply(instance, EnchantmentInstance::new));
        
        private final ResourceLocation id;
        private final int lvl;
        
        public EnchantmentInstance(ResourceLocation enchantment, int level) {
            this.id = enchantment;
            this.lvl = level;
        }

        public ResourceLocation getEnchantmentID() {
            return id;
        }

        public int getEnchantmentLevel() {
            return lvl;
        }
    }
    
}
