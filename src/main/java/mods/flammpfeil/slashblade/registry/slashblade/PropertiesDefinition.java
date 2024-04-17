package mods.flammpfeil.slashblade.registry.slashblade;

import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import mods.flammpfeil.slashblade.registry.ComboStateRegistry;
import mods.flammpfeil.slashblade.registry.SlashArtsRegistry;
import net.minecraft.resources.ResourceLocation;

public class PropertiesDefinition {
    public static final Codec<PropertiesDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.optionalFieldOf("root_combo", ComboStateRegistry.STANDBY.getId())
                    .forGetter(PropertiesDefinition::getComboRoot),
            ResourceLocation.CODEC.optionalFieldOf("slash_art", SlashArtsRegistry.JUDGEMENT_CUT.getId())
                    .forGetter(PropertiesDefinition::getSpecialAttackType),
            Codec.FLOAT.optionalFieldOf("attack_base", 4.0F).forGetter(PropertiesDefinition::getBaseAttackModifier),
            Codec.FLOAT.optionalFieldOf("attack_amplifier", 1.0F).forGetter(PropertiesDefinition::getAttackAmplifier),
            Codec.INT.optionalFieldOf("max_damage", 40).forGetter(PropertiesDefinition::getMaxDamage),
            DefaultSwordType.CODEC.listOf().optionalFieldOf("carry_type", Lists.newArrayList())
                    .forGetter(PropertiesDefinition::getDefaultType))
            .apply(instance, PropertiesDefinition::new));

    private final ResourceLocation comboRoot;
    private final ResourceLocation specialAttackType;
    private final float baseAttackModifier;
    private final float attackAmplifier;
    private final int maxDamage;
    private final List<DefaultSwordType> defaultType;

    private PropertiesDefinition(ResourceLocation comboRoot, ResourceLocation specialAttackType, float baseAttackModifier,
            float AttackAmplifier, int damage, List<DefaultSwordType> defaultType) {
        this.comboRoot = comboRoot;
        this.specialAttackType = specialAttackType;
        this.baseAttackModifier = baseAttackModifier;
        this.attackAmplifier = AttackAmplifier;
        this.maxDamage = damage;
        this.defaultType = defaultType;
    }

    public ResourceLocation getComboRoot() {
        return comboRoot;
    }

    public ResourceLocation getSpecialAttackType() {
        return specialAttackType;
    }

    public float getBaseAttackModifier() {
        return baseAttackModifier;
    }

    public float getAttackAmplifier() {
        return attackAmplifier;
    }

    public int getMaxDamage() {
        return maxDamage;
    }

    public List<DefaultSwordType> getDefaultType() {
        return defaultType;
    }
    
    public static class Builder {
        
        private ResourceLocation comboRoot;
        private ResourceLocation specialAttackType;
        private float baseAttackModifier;
        private float attackAmplifier;
        private int maxDamage;
        private List<DefaultSwordType> defaultType;
        
        public Builder() {
            this.comboRoot = ComboStateRegistry.STANDBY.getId();
            this.specialAttackType = SlashArtsRegistry.JUDGEMENT_CUT.getId();
            this.baseAttackModifier = 4.0F;
            this.attackAmplifier = 1.0F;
            this.maxDamage = 40;
            this.defaultType = Lists.newArrayList();
        }

        public Builder rootComboState(ResourceLocation comboRoot) {
            this.comboRoot = comboRoot;
            return this;
        }

        public Builder slashArtsType(ResourceLocation specialAttackType) {
            this.specialAttackType = specialAttackType;
            return this;
        }

        public Builder baseAttackModifier(float baseAttackModifier) {
            this.baseAttackModifier = baseAttackModifier;
            return this;
        }

        public Builder attackAmplifier(float attackAmplifier) {
            this.attackAmplifier = attackAmplifier;
            return this;
        }

        public Builder maxDamage(int maxDamage) {
            this.maxDamage = maxDamage;
            return this;
        }

        public Builder defaultSwordType(List<DefaultSwordType> defaultType) {
            this.defaultType = defaultType;
            return this;
        }

        public PropertiesDefinition build() {
            return new PropertiesDefinition(comboRoot, specialAttackType, baseAttackModifier, attackAmplifier, maxDamage, defaultType);
        }
    }

    public static enum DefaultSwordType {
        BEWITCHED, BROKEN, SEALED;

        public static final Codec<DefaultSwordType> CODEC = Codec.STRING.xmap(
                string -> DefaultSwordType.valueOf(string.toUpperCase()), instance -> instance.name().toLowerCase());
    }

}
