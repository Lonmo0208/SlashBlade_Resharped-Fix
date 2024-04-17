package mods.flammpfeil.slashblade.item;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;

import java.util.EnumSet;

import mods.flammpfeil.slashblade.registry.slashblade.ISlashBladeState;

public enum SwordType{
    None,
    EdgeFragment,
    Broken,
    Perfect,
    Enchanted,
    Bewitched,
    SoulEeater,
    FiercerEdge,
    Sealed,
    Cursed,
    ;

    public static EnumSet<SwordType> from(ItemStack itemStackIn){
        EnumSet<SwordType> types = EnumSet.noneOf(SwordType.class);

        LazyOptional<ISlashBladeState> state = itemStackIn.getCapability(ItemSlashBlade.BLADESTATE);

        if(state.isPresent()){
            itemStackIn.getCapability(ItemSlashBlade.BLADESTATE).ifPresent(s->{
                if(s.isBroken())
                    types.add(Broken);

                if(s.isSealed())
                    types.add(Cursed);

                if(!s.isSealed() && itemStackIn.isEnchanted() && (itemStackIn.hasCustomHoverName() || s.isDefaultBewitched()))
                    types.add(Bewitched);
            });
        }else{
            types.add(EdgeFragment);
        }


        if(itemStackIn.isEnchanted())
            types.add(Enchanted);

        return types;
    }
}
