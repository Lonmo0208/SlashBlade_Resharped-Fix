package mods.flammpfeil.slashblade.item;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;

import java.util.EnumSet;

import com.mojang.serialization.Codec;

import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;

public enum SwordType{
    NONE,
    EDGEFRAGMENT,
    BROKEN,
    PERFECT,
    ENCHANTED,
    BEWITCHED,
    SOULEATER,
    FIERCEREDGE,
    NOSCABBARD,
    SEALED,
    CURSED,
    ;
    
    public static final Codec<SwordType> CODEC = Codec.STRING.xmap(
            string -> SwordType.valueOf(string.toUpperCase()), instance -> instance.name().toLowerCase());
    
    public static EnumSet<SwordType> from(ItemStack itemStackIn){
        EnumSet<SwordType> types = EnumSet.noneOf(SwordType.class);

        LazyOptional<ISlashBladeState> state = itemStackIn.getCapability(ItemSlashBlade.BLADESTATE);

        if(state.isPresent()){
            itemStackIn.getCapability(ItemSlashBlade.BLADESTATE).ifPresent(s->{
                if(s.isBroken())
                    types.add(BROKEN);

                if(s.isSealed())
                    types.add(CURSED);

                if(!s.isSealed() && itemStackIn.isEnchanted() && (itemStackIn.hasCustomHoverName() || s.isDefaultBewitched()))
                    types.add(BEWITCHED);
            });
        }else{
            types.add(NOSCABBARD);
            types.add(EDGEFRAGMENT);
        }


        if(itemStackIn.isEnchanted())
            types.add(ENCHANTED);

        return types;
    }
}
