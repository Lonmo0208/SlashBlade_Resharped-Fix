package mods.flammpfeil.slashblade.recipe;

import java.util.stream.Stream;

import com.google.gson.JsonElement;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.AbstractIngredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;

public class SlashBladeIngredient extends AbstractIngredient {
// TODO: 完全没开始写
    protected SlashBladeIngredient(Stream<? extends Value> p_43907_) {
        super(p_43907_);
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public IIngredientSerializer<? extends Ingredient> getSerializer() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public JsonElement toJson() {
        // TODO Auto-generated method stub
        return null;
    }

}
