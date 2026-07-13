package com.pppopipupu.aletheia.recipe.ntmc;

import static com.hbm.inventory.OreDictManager.*;

import com.hbm.inventory.RecipesCommon.ComparableStack;
import com.hbm.inventory.RecipesCommon.OreDictStack;
import com.hbm.inventory.recipes.CyclotronRecipes;
import com.hbm.items.ModItems;
import com.hbm.util.Tuple.Pair;

import net.minecraft.item.ItemStack;

import com.pppopipupu.aletheia.item.AletheiaItems;

public class AletheiaRecipesNtmcReactors {

    public static void register() {

        CyclotronRecipes.recipes.put(
            new Pair(new ComparableStack(ModItems.part_lithium), new ComparableStack(AletheiaItems.powder_strontium)),
            new Pair(new ItemStack(ModItems.powder_zirconium), 50));
        CyclotronRecipes.recipes.put(
            new Pair(new ComparableStack(ModItems.part_beryllium), new ComparableStack(AletheiaItems.powder_strontium)),
            new Pair(new ItemStack(ModItems.powder_niobium), 25));
        CyclotronRecipes.recipes.put(
            new Pair(new ComparableStack(ModItems.part_beryllium), new ComparableStack(ModItems.powder_cerium)),
            new Pair(new ItemStack(AletheiaItems.powder_neodymium), 25));
        CyclotronRecipes.recipes.put(
            new Pair(new ComparableStack(ModItems.part_carbon), new ComparableStack(AletheiaItems.powder_neodymium)),
            new Pair(new ItemStack(ModItems.powder_gold), 10));
        CyclotronRecipes.recipes.put(
            new Pair(new ComparableStack(ModItems.part_copper), new OreDictStack("dustStrontium")),
            new Pair(new ItemStack(AletheiaItems.powder_strontium), 15));
        CyclotronRecipes.recipes.put(
            new Pair(new ComparableStack(ModItems.part_copper), new ComparableStack(AletheiaItems.powder_strontium)),
            new Pair(new ItemStack(AletheiaItems.powder_neodymium), 15));
    }
}
