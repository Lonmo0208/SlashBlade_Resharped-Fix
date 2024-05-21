package mods.flammpfeil.slashblade.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.ISubtypeRegistration;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.client.renderer.model.BladeModelManager;
import mods.flammpfeil.slashblade.init.SBItems;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.resources.ResourceLocation;

@JeiPlugin
public class JEICompat implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return SlashBlade.prefix(SlashBlade.MODID);
    }
    
    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.registerSubtypeInterpreter(SBItems.slashblade, (stack, context)->{
            if(!stack.getCapability(ItemSlashBlade.BLADESTATE).isPresent())
                return "";
            
            var state = stack.getCapability(ItemSlashBlade.BLADESTATE).orElseThrow(NullPointerException::new);
            String transKey = state.getTranslationKey();
            if(transKey.isBlank())
                return transKey;
            
            var bladeID = getBladeId(transKey);
            if(BladeModelManager.getClientSlashBladeRegistry().containsKey(bladeID)) {
                var blade = BladeModelManager.getClientSlashBladeRegistry().get(bladeID);
                state.setModel(blade.getRenderDefinition().getModelName());
                state.setTexture(blade.getRenderDefinition().getTextureName());
            };
            return transKey;
        });
    }

    private ResourceLocation getBladeId(String translationKey) {
        return ResourceLocation.tryParse(translationKey.substring(5).replace('.', ':'));
    }
}
