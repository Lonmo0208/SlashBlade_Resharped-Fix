package mods.flammpfeil.slashblade.registry.specialeffects;

import mods.flammpfeil.slashblade.SlashBlade;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class SpecialEffect {
    public static final ResourceKey<Registry<SpecialEffect>> REGISTRY_KEY = ResourceKey
            .createRegistryKey(SlashBlade.prefix("special_effect"));
    
    private final int requestLevel;
    private final boolean isCopiable;
    private final boolean isRemovable;
    
    public SpecialEffect(int requestLevel) {
		this(requestLevel, false, false);
	}
    
    public SpecialEffect(int requestLevel, boolean isCopiable, boolean isRemovable) {
		this.requestLevel = requestLevel;
		this.isCopiable = isCopiable;
		this.isRemovable = isRemovable;
	}

	public int getRequestLevel() {
		return requestLevel;
	}

	public boolean isCopiable() {
		return isCopiable;
	}

	public boolean isRemovable() {
		return isRemovable;
	}

}
