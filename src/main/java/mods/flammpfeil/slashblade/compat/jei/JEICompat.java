package mods.flammpfeil.slashblade.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.ISubtypeRegistration;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.init.SBItemRegistry;
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
        registration.registerSubtypeInterpreter(SBItemRegistry.slashblade, (stack, context)->{
            if(!stack.getCapability(ItemSlashBlade.BLADESTATE).isPresent())
                return "";
            var state = stack.getCapability(ItemSlashBlade.BLADESTATE).orElseThrow(NullPointerException::new);
            return state.getTranslationKey();
        });
    }

}
