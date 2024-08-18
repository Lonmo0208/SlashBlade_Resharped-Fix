package mods.flammpfeil.slashblade.recipe;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapelessRecipe;

public class ProudsoulShapelessRecipe extends ShapelessRecipe {

	public ProudsoulShapelessRecipe(ResourceLocation p_251840_, String p_249640_, CraftingBookCategory p_249390_,
			ItemStack p_252071_, NonNullList<Ingredient> p_250689_) {
		super(p_251840_, p_249640_, p_249390_, p_252071_, p_250689_);
		// TODO 记得去写耀魂的合成。
	}
	
	@Override
	public ItemStack assemble(CraftingContainer p_44260_, RegistryAccess p_266797_) {
		// TODO Auto-generated method stub
		return super.assemble(p_44260_, p_266797_);
	}
	
	
}
