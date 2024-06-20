package mods.flammpfeil.slashblade.event.client;

import java.util.Map;

import mods.flammpfeil.slashblade.compat.playerAnim.VmdAnimation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.Event;

@OnlyIn(Dist.CLIENT)
public class RegisterPlayerAnimationEvent extends Event {
	private final Map<ResourceLocation, VmdAnimation> animation;
	
	public RegisterPlayerAnimationEvent(Map<ResourceLocation, VmdAnimation> animation) {
		this.animation = animation;
	}
	
	public Map<ResourceLocation, VmdAnimation> getAnimations() {
		return animation;
	}
	
}
