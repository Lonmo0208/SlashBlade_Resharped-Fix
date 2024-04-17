package mods.flammpfeil.slashblade.capability.inputstate;

import net.minecraftforge.common.capabilities.*;

public class CapabilityInputState {

    public static final Capability<IInputState> INPUT_STATE = CapabilityManager.get(new CapabilityToken<>(){});

    public static void register(RegisterCapabilitiesEvent event)
    {
        event.register(IInputState.class);
    }
}
