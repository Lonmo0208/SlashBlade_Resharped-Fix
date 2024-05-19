package mods.flammpfeil.slashblade.client;

import org.apache.logging.log4j.util.LoaderUtil;
import org.jetbrains.annotations.Nullable;

import mods.flammpfeil.slashblade.SlashBladeKeyMappings;
import mods.flammpfeil.slashblade.client.renderer.LockonCircleRender;
import mods.flammpfeil.slashblade.client.renderer.gui.RankRenderer;
import mods.flammpfeil.slashblade.client.renderer.model.BladeModel;
import mods.flammpfeil.slashblade.client.renderer.model.BladeModelManager;
import mods.flammpfeil.slashblade.client.renderer.model.BladeMotionManager;
import mods.flammpfeil.slashblade.event.BladeMaterialTooltips;
import mods.flammpfeil.slashblade.event.BladeMotionEventBroadcaster;
import mods.flammpfeil.slashblade.event.client.AdvancementsRecipeRenderer;
import mods.flammpfeil.slashblade.event.client.SneakingMotionCanceller;
import mods.flammpfeil.slashblade.event.client.UserPoseOverrider;
import mods.flammpfeil.slashblade.init.SBItemRegistry;
import mods.flammpfeil.slashblade.optional.playerAnim.PlayerAnimationOverrider;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;


@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
@OnlyIn(Dist.CLIENT)
public class ClientHandler {

    @SubscribeEvent
    public static void doClientStuff(final FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(BladeModelManager.getInstance());
        MinecraftForge.EVENT_BUS.register(BladeMotionManager.getInstance());
/*
        Minecraft.getInstance().getEntityRenderDispatcher().getSkinMap().values().stream()
                .filter((er)-> er instanceof LivingEntityRenderer)
                .forEach((lr)-> ((LivingEntityRenderer)lr).addLayer(new LayerMainBlade((LivingEntityRenderer)lr)));
*/
        SneakingMotionCanceller.getInstance().register();


        if(LoaderUtil.isClassAvailable("dev.kosmx.playerAnim.api.layered.AnimationStack")){
            PlayerAnimationOverrider.getInstance().register();
        }else{
            UserPoseOverrider.getInstance().register();
        }
        LockonCircleRender.getInstance().register();
        BladeMaterialTooltips.getInstance().register();
        AdvancementsRecipeRenderer.getInstance().register();
        BladeMotionEventBroadcaster.getInstance().register();


        RankRenderer.getInstance().register();

        ItemProperties.register(SBItemRegistry.slashblade, new ResourceLocation("slashblade:user"), new ClampedItemPropertyFunction() {
            @Override
            public float unclampedCall(ItemStack p_174564_, @Nullable ClientLevel p_174565_, @Nullable LivingEntity p_174566_, int p_174567_) {
                BladeModel.user = p_174566_;
                return 0;
            }
        });
        
        ItemProperties.register(SBItemRegistry.slashblade_bamboo, new ResourceLocation("slashblade:user"), new ClampedItemPropertyFunction() {
            @Override
            public float unclampedCall(ItemStack p_174564_, @Nullable ClientLevel p_174565_, @Nullable LivingEntity p_174566_, int p_174567_) {
                BladeModel.user = p_174566_;
                return 0;
            }
        });
        
        ItemProperties.register(SBItemRegistry.slashblade_silverbamboo, new ResourceLocation("slashblade:user"), new ClampedItemPropertyFunction() {
            @Override
            public float unclampedCall(ItemStack p_174564_, @Nullable ClientLevel p_174565_, @Nullable LivingEntity p_174566_, int p_174567_) {
                BladeModel.user = p_174566_;
                return 0;
            }
        });
        
        ItemProperties.register(SBItemRegistry.slashblade_white, new ResourceLocation("slashblade:user"), new ClampedItemPropertyFunction() {
            @Override
            public float unclampedCall(ItemStack p_174564_, @Nullable ClientLevel p_174565_, @Nullable LivingEntity p_174566_, int p_174567_) {
                BladeModel.user = p_174566_;
                return 0;
            }
        });
        
        ItemProperties.register(SBItemRegistry.slashblade_wood, new ResourceLocation("slashblade:user"), new ClampedItemPropertyFunction() {
            @Override
            public float unclampedCall(ItemStack p_174564_, @Nullable ClientLevel p_174565_, @Nullable LivingEntity p_174566_, int p_174567_) {
                BladeModel.user = p_174566_;
                return 0;
            }
        });

        // do something that can only be done on the client

        //OBJLoader.INSTANCE.addDomain("slashblade");

        /*
        RenderingRegistry.registerEntityRenderingHandler(RegistryEvents.SummonedSword, SummonedSwordRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(RegistryEvents.JudgementCut, JudgementCutRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(RegistryEvents.BladeItem, BladeItemEntityRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(RegistryEvents.BladeStand, BladeStandEntityRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(RegistryEvents.SlashEffect, SlashEffectRenderer::new);

        RenderingRegistry.registerEntityRenderingHandler(RegistryEvents.PlacePreview, PlacePreviewEntityRenderer::new);
        */
        //LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().options);
    }
    
    @SubscribeEvent
    public static void registerKeyMapping(RegisterKeyMappingsEvent event) {
        event.register(SlashBladeKeyMappings.KEY_SPECIAL_MOVE);
        event.register(SlashBladeKeyMappings.KEY_SUMMON_BLADE);
    }
    
    @SubscribeEvent
    public static void Baked(final ModelEvent.ModifyBakingResult event){
        {
            bakeBlade(SBItemRegistry.slashblade, event);
            bakeBlade(SBItemRegistry.slashblade_white, event);
            bakeBlade(SBItemRegistry.slashblade_wood, event);
            bakeBlade(SBItemRegistry.slashblade_silverbamboo, event);
            bakeBlade(SBItemRegistry.slashblade_bamboo, event);
        }

    }

    public static void bakeBlade(Item blade, final ModelEvent.ModifyBakingResult event) {
        ModelResourceLocation loc = new ModelResourceLocation(
                ForgeRegistries.ITEMS.getKey(blade), "inventory");
        BladeModel model = new BladeModel(event.getModels().get(loc), event.getModelBakery());
        event.getModels().put(loc, model);
    }
}
