package mods.flammpfeil.slashblade.registry;

import java.util.function.Supplier;

import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.registry.specialeffects.SpecialEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

public class SpecialEffectsRegistry {
    public static final DeferredRegister<SpecialEffect> SPECIAL_EFFECT = DeferredRegister.create(SpecialEffect.REGISTRY_KEY,
            SlashBlade.MODID);

    public static final Supplier<IForgeRegistry<SpecialEffect>> REGISTRY = SPECIAL_EFFECT.makeRegistry(RegistryBuilder::new);
    
}
