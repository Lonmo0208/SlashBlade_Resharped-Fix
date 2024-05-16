package mods.flammpfeil.slashblade;

import mods.flammpfeil.slashblade.client.renderer.model.BladeModelManager;
import mods.flammpfeil.slashblade.init.SBItemRegistry;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.registry.slashblade.SlashBladeDefinition;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class SlashBladeCreativeGroup {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister
            .create(Registries.CREATIVE_MODE_TAB, SlashBlade.MODID);
    
    private static final CreativeModeTab SLASHBLADE = CreativeModeTab.builder()
            .title(Component.translatable(SlashBlade.MODID))
            .icon(()->{
                ItemStack stack = new ItemStack(SBItemRegistry.slashblade);
                stack.getCapability(ItemSlashBlade.BLADESTATE).ifPresent(s->{
                    s.setModel(new ResourceLocation(SlashBlade.MODID,"model/named/yamato.obj"));
                    s.setTexture(new ResourceLocation(SlashBlade.MODID,"model/named/yamato.png"));
                });
                return stack;
            })
            .displayItems((features, output) -> {
                

                output.accept(SBItemRegistry.proudsoul);
                output.accept(SBItemRegistry.proudsoul_tiny);
                output.accept(SBItemRegistry.proudsoul_ingot);
                output.accept(SBItemRegistry.proudsoul_sphere);
                output.accept(SBItemRegistry.proudsoul_crystal);
                output.accept(SBItemRegistry.proudsoul_trapezohedron);
                output.accept(SBItemRegistry.proudsoul_activated);
                output.accept(SBItemRegistry.proudsoul_awakened);
                
                output.accept(SBItemRegistry.bladestand_1);
                output.accept(SBItemRegistry.bladestand_1w);
                output.accept(SBItemRegistry.bladestand_2);
                output.accept(SBItemRegistry.bladestand_2w);
                output.accept(SBItemRegistry.bladestand_s);
                output.accept(SBItemRegistry.bladestand_v);
                
                output.accept(SBItemRegistry.slashblade_wood);
                output.accept(SBItemRegistry.slashblade_bamboo);
                output.accept(SBItemRegistry.slashblade_silverbamboo);
                output.accept(SBItemRegistry.slashblade_white);
                output.accept(SBItemRegistry.slashblade);

                fillBlades(output);
                })
            .build();
    
    public static final RegistryObject<CreativeModeTab> SLASHBLADE_GROUP = CREATIVE_MODE_TABS.register("slashblade", ()->SLASHBLADE);
    
    
    
    private static void fillBlades(CreativeModeTab.Output output) {
        if (Minecraft.getInstance().getConnection() != null) {
            BladeModelManager.getClientSlashBladeRegistry()
            .entrySet().stream().sorted(SlashBladeDefinition.COMPARATOR)
            .forEach(entry ->{
                output.accept(entry.getValue().getBlade());
            });
        }
    }
}
