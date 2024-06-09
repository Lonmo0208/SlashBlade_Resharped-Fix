package mods.flammpfeil.slashblade.capability.slashblade;

import net.minecraft.core.Direction;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Furia on 2017/01/10.
 */
public class NamedBladeStateCapabilityProvider implements ICapabilityProvider, INBTSerializable<Tag> {

    public static final Capability<ISlashBladeState> CAP = CapabilityManager.get(new CapabilityToken<>() {
    });

    protected LazyOptional<ISlashBladeState> state = LazyOptional.of(SlashBladeState::new);

    public NamedBladeStateCapabilityProvider() {
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return CAP.orEmpty(cap, state);
    }

    @Override
    public Tag serializeNBT() {
        return this.state.orElseGet(SlashBladeState::new).serializeNBT();
    }

    @Override
    public void deserializeNBT(Tag inTag) {
        state.ifPresent(state -> state.deserializeNBT(inTag));
    }
}
