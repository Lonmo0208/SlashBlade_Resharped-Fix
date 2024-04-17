package mods.flammpfeil.slashblade.specialattack;

import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.registry.ComboStateRegistry;
import mods.flammpfeil.slashblade.registry.SlashArtsRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Function;

public class SlashArts {
    public static final ResourceKey<Registry<SlashArts>> REGISTRY_KEY = ResourceKey
            .createRegistryKey(SlashBlade.prefix("slash_arts"));
    public static ResourceLocation getRegistryKey(SlashArts state) {
        return SlashArtsRegistry.REGISTRY.get().getKey(state);
    }
    
    static public final int ChargeTicks = 9;
    static public final int ChargeJustTicks = 3;
    static public final int ChargeJustTicksMax = 5;

    static public int getJustReceptionSpan(LivingEntity user){
        return Math.min(ChargeJustTicksMax , ChargeJustTicks + EnchantmentHelper.getEnchantmentLevel(Enchantments.SOUL_SPEED,user));
    }

    public enum ArtsType{
        Fail,
        Success,
        Jackpot,
        Broken
    }
    
    private Function<LivingEntity, ResourceLocation> comboState;
    private Function<LivingEntity, ResourceLocation> comboStateJust;
    private Function<LivingEntity, ResourceLocation> comboStateBroken;

    public ResourceLocation doArts(ArtsType type, LivingEntity user) {
        switch (type){
            case Jackpot:
                return getComboStateJust(user);
            case Success:
                return getComboState(user);
            case Broken:
                return getComboStateBroken(user);
            default:
                break;
        }
        return ComboStateRegistry.NONE.getId();
    }

    public SlashArts(Function<LivingEntity, ResourceLocation> state) {
        this.comboState = state;
        this.comboStateJust = state;
        this.comboStateBroken = state;
    }

    public ResourceLocation getComboState(LivingEntity user) {
        return this.comboState.apply(user);
    }

    public ResourceLocation getComboStateJust(LivingEntity user) {
        return this.comboStateJust.apply(user);
    }
    public SlashArts setComboStateJust(Function<LivingEntity, ResourceLocation> state){
        this.comboStateJust = state;
        return this;
    }

    public ResourceLocation getComboStateBroken(LivingEntity user) {
        return this.comboStateBroken.apply(user);
    }
    public SlashArts setComboStateBroken(Function<LivingEntity, ResourceLocation> state){
        this.comboStateBroken = state;
        return this;
    }
}
