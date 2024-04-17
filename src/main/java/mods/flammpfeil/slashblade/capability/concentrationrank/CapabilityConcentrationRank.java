package mods.flammpfeil.slashblade.capability.concentrationrank;

import net.minecraftforge.common.capabilities.*;

public class CapabilityConcentrationRank {

    public static final Capability<IConcentrationRank> RANK_POINT = CapabilityManager.get(new CapabilityToken<>(){});

    public static void register(RegisterCapabilitiesEvent event)
    {
        event.register(IConcentrationRank.class);
    }
}
