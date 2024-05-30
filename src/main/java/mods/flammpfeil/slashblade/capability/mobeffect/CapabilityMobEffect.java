package mods.flammpfeil.slashblade.capability.mobeffect;

import net.minecraftforge.common.capabilities.*;

public class CapabilityMobEffect {

    public static final Capability<IMobEffectState> MOB_EFFECT = CapabilityManager.get(new CapabilityToken<>() {
    });

    public static void register(RegisterCapabilitiesEvent event) {
        event.register(IMobEffectState.class);
    }
}
