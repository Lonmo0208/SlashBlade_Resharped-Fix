package mods.flammpfeil.slashblade.ability;

import java.util.EnumSet;
import java.util.Map;

import mods.flammpfeil.slashblade.capability.inputstate.CapabilityInputState;
import mods.flammpfeil.slashblade.event.InputCommandEvent;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.item.SwordType;
import mods.flammpfeil.slashblade.registry.ComboStateRegistry;
import mods.flammpfeil.slashblade.registry.combo.ComboState;
import mods.flammpfeil.slashblade.slasharts.SlashArts;
import mods.flammpfeil.slashblade.util.InputCommand;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.timers.TimerCallback;
import net.minecraft.world.level.timers.TimerQueue;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class SuperSlashArts {
    private static final class SingletonHolder {
        private static final SuperSlashArts instance = new SuperSlashArts();
    }

    public static SuperSlashArts getInstance() {
        return SuperSlashArts.SingletonHolder.instance;
    }

    private SuperSlashArts() {
    }

    public void register() {
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    @SubscribeEvent
    public void onInputChange(InputCommandEvent event) {

        EnumSet<InputCommand> old = event.getOld();
        EnumSet<InputCommand> current = event.getCurrent();
        ServerPlayer sender = event.getEntity();

        InputCommand targetCommnad = InputCommand.SPRINT;

        boolean onDown = !old.contains(targetCommnad) && current.contains(targetCommnad);

        final Long pressTime = event.getState().getLastPressTime(targetCommnad);
        if (onDown) {

            sender.getCapability(CapabilityInputState.INPUT_STATE).ifPresent(input -> {

                // SpiralSwords command
                input.getScheduler().schedule("SuperSA", pressTime + 10, new TimerCallback<LivingEntity>() {

                    @Override
                    public void handle(LivingEntity rawEntity, TimerQueue<LivingEntity> queue, long now) {
                        if (!(rawEntity instanceof ServerPlayer))
                            return;
                        ServerPlayer entity = (ServerPlayer) rawEntity;

                        InputCommand targetCommnad = InputCommand.SPRINT;
                        boolean inputSucceed = entity.getCapability(CapabilityInputState.INPUT_STATE)
                                .filter(input -> input.getCommands().contains(targetCommnad)
                                        && (!InputCommand.anyMatch(input.getCommands(), InputCommand.move)
                                                || !input.getCommands().contains(InputCommand.SNEAK))
                                        && input.getLastPressTime(targetCommnad) == pressTime)
                                .isPresent();
                        if (!inputSucceed)
                            return;

                        ItemStack mainHandItem = entity.getMainHandItem();
						mainHandItem.getCapability(ItemSlashBlade.BLADESTATE).ifPresent((state) -> {
                            if (state.isBroken() || state.getDamage() > 0 || state.isSealed()
                                    || !SwordType.from(mainHandItem).contains(SwordType.BEWITCHED)
                                    || !SwordType.from(mainHandItem).contains(SwordType.FIERCEREDGE))
                                return;
                            
                            if(!entity.onGround())
                            	return;
                            
                            mainHandItem.hurtAndBreak(mainHandItem.getMaxDamage() / 2, entity, ItemSlashBlade.getOnBroken(mainHandItem));
                            
                            Map.Entry<Integer, ResourceLocation> currentloc = state.resolvCurrentComboStateTicks(entity);

                            ComboState currentCS = ComboStateRegistry.REGISTRY.get().getValue(currentloc.getValue());
                            
                            ResourceLocation csloc = state.getSlashArts().doArts(SlashArts.ArtsType.Super, entity);
                            ComboState cs = ComboStateRegistry.REGISTRY.get().getValue(csloc);
                            if (csloc != ComboStateRegistry.NONE.getId() && !currentloc.getValue().equals(csloc)) {
                            	
                                if (currentCS.getPriority() > cs.getPriority()) {
                                	state.updateComboSeq(entity, csloc);
                                }
                            }
                        });
                    }
                    
                });
            });
        }
        
    }
}
