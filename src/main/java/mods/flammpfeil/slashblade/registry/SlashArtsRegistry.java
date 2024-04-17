package mods.flammpfeil.slashblade.registry;

import java.util.function.Supplier;

import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.specialattack.SlashArts;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

public class SlashArtsRegistry {
    public static final DeferredRegister<SlashArts> SLASH_ARTS = DeferredRegister.create(SlashArts.REGISTRY_KEY, SlashBlade.MODID);

    public static final Supplier<IForgeRegistry<SlashArts>> REGISTRY = SLASH_ARTS.makeRegistry(RegistryBuilder::new);
    public static final RegistryObject<SlashArts> NONE = SLASH_ARTS.register("none",
            ()->new SlashArts((e)->ComboStateRegistry.NONE.getId())
            );
    public static final RegistryObject<SlashArts> JUDGEMENT_CUT = SLASH_ARTS.register("judgement_cut", 
            ()->new SlashArts((e)-> e.onGround() ? ComboStateRegistry.JUDGEMENT_CUT.getId() 
                    : ComboStateRegistry.JUDGEMENT_CUT_SLASH_AIR.getId())
            .setComboStateJust((e)->ComboStateRegistry.JUDGEMENT_CUT_SLASH_JUST.getId())
            .setComboStateBroken((e)->ComboStateRegistry.VOID_SLASH.getId())
    );
}
