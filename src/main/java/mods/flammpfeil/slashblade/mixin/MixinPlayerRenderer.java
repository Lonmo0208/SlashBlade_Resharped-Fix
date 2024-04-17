package mods.flammpfeil.slashblade.mixin;

import mods.flammpfeil.slashblade.client.renderer.layers.LayerMainBlade;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public class MixinPlayerRenderer {


    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Inject(at = @At("TAIL")
            , method="<init>(Lnet/minecraft/client/renderer/entity/EntityRendererProvider$Context;Z)V")
    public void extraLayers(EntityRendererProvider.Context p_174557_, boolean p_174558_, CallbackInfo callback) {
        ((PlayerRenderer)(Object)this).addLayer(new LayerMainBlade(((PlayerRenderer)(Object)this)));
    }

    PlayerRenderer self(){
        return ((PlayerRenderer)(Object)this);
    }
}
