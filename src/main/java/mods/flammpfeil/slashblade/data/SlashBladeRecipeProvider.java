package mods.flammpfeil.slashblade.data;

import java.util.function.Consumer;

import mods.flammpfeil.slashblade.data.builtin.SlashBladeBuiltInRegistry;
import mods.flammpfeil.slashblade.data.tag.SlashBladeItemTags;
import mods.flammpfeil.slashblade.init.SBItemRegistry;
import mods.flammpfeil.slashblade.item.SwordType;
import mods.flammpfeil.slashblade.recipe.RequestDefinition;
import mods.flammpfeil.slashblade.recipe.SlashBladeIngredient;
import mods.flammpfeil.slashblade.recipe.SlashBladeShapedRecipeBuilder;
import mods.flammpfeil.slashblade.registry.slashblade.EnchantmentDefinition;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.minecraftforge.registries.ForgeRegistries;

public class SlashBladeRecipeProvider extends RecipeProvider implements IConditionBuilder {

    public SlashBladeRecipeProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, SBItemRegistry.slashblade_wood)
        .pattern("  L")
        .pattern(" L ")
        .pattern("B  ")
        .define('B', Items.WOODEN_SWORD)
        .define('L', ItemTags.LOGS)
        .unlockedBy(getHasName(Items.WOODEN_SWORD), has(Items.WOODEN_SWORD))
        .save(consumer);
        SlashBladeShapedRecipeBuilder.shaped(SBItemRegistry.slashblade_bamboo)
        .pattern("  L")
        .pattern(" L ")
        .pattern("B  ")
        .define('B', SBItemRegistry.slashblade_wood)
        .define('L', SlashBladeItemTags.BAMBOO)
        .unlockedBy(getHasName(SBItemRegistry.slashblade_wood), has(SBItemRegistry.slashblade_wood))
        .save(consumer);
        SlashBladeShapedRecipeBuilder.shaped(SBItemRegistry.slashblade_silverbamboo)
        .pattern(" EI")
        .pattern("SBD")
        .pattern("PS ")
        .define('B', SBItemRegistry.slashblade_bamboo)
        .define('I', Tags.Items.INGOTS_IRON)
        .define('S', Tags.Items.STRING)
        .define('P', Items.PAPER)
        .define('E', Items.EGG)
        .define('D', Tags.Items.DYES_BLACK)
        .unlockedBy(getHasName(SBItemRegistry.slashblade_bamboo), has(SBItemRegistry.slashblade_bamboo))
        .save(consumer);
        SlashBladeShapedRecipeBuilder.shaped(SBItemRegistry.slashblade_white)
        .pattern("  L")
        .pattern(" L ")
        .pattern("BG ")
        .define('B', SBItemRegistry.slashblade_wood)
        .define('L', SBItemRegistry.proudsoul_ingot)
        .define('G', Tags.Items.INGOTS_GOLD)
        .unlockedBy(getHasName(SBItemRegistry.slashblade_wood), has(SBItemRegistry.slashblade_wood))
        .save(consumer);
        
        SlashBladeShapedRecipeBuilder.shaped(SBItemRegistry.slashblade)
        .pattern(" EI")
        .pattern("PBD")
        .pattern("SI ")
        .define('B', SlashBladeIngredient.of(SBItemRegistry.slashblade_white, 
                RequestDefinition.Builder.newInstance().addSwordType(SwordType.BROKEN).build()))
        .define('I', Tags.Items.INGOTS_GOLD)
        .define('S', Tags.Items.STRING)
        .define('P', Tags.Items.DYES_BLUE)
        .define('E', Tags.Items.RODS_BLAZE)
        .define('D', Tags.Items.STORAGE_BLOCKS_COAL)
        .unlockedBy(getHasName(SBItemRegistry.slashblade_white), has(SBItemRegistry.slashblade_white))
        .save(consumer);
        
        SlashBladeShapedRecipeBuilder.shaped(SlashBladeBuiltInRegistry.MURAMASA.location())
        .pattern("SSS")
        .pattern("SBS")
        .pattern("SSS")
        .define('B', SlashBladeIngredient.of(RequestDefinition.Builder.newInstance().refineCount(20).build()))
        .define('S', Ingredient.of(SBItemRegistry.proudsoul_sphere))
        .unlockedBy(getHasName(SBItemRegistry.slashblade), has(SBItemRegistry.slashblade))
        .save(consumer);
        
        SlashBladeShapedRecipeBuilder.shaped(SlashBladeBuiltInRegistry.TUKUMO.location())
        .pattern("ESD")
        .pattern("RBL")
        .pattern("ISG")
        .define('D', Tags.Items.STORAGE_BLOCKS_DIAMOND)
        .define('L', Tags.Items.STORAGE_BLOCKS_LAPIS)
        .define('G', Tags.Items.STORAGE_BLOCKS_GOLD)
        .define('I', Tags.Items.STORAGE_BLOCKS_IRON)
        .define('R', Tags.Items.STORAGE_BLOCKS_REDSTONE)
        .define('E', Tags.Items.STORAGE_BLOCKS_EMERALD)
        
        .define('B', SlashBladeIngredient.of(RequestDefinition.Builder.newInstance().addEnchantment(new EnchantmentDefinition(getEnchantmentID(Enchantments.FIRE_ASPECT), 1)).build()))
        .define('S', Ingredient.of(SBItemRegistry.proudsoul_sphere))
        .unlockedBy(getHasName(SBItemRegistry.slashblade), has(SBItemRegistry.slashblade))
        .save(consumer);
    }
    private static ResourceLocation getEnchantmentID(Enchantment enchantment) {
        return ForgeRegistries.ENCHANTMENTS.getKey(enchantment);
    }
}
