package com.pppopipupu.aletheia.recipe.ntmc;

import net.minecraft.item.ItemStack;

import com.hbm.inventory.RecipesCommon.ComparableStack;
import com.hbm.inventory.recipes.OutgasserRecipes;
import com.hbm.inventory.recipes.OutgasserRecipes.OutgasserRecipe;
import com.hbm.items.ModItems;

public class AletheiaRecipesNtmcOutgasser {

    public static void register() {
        OutgasserRecipes.recipes.put(
            new ComparableStack(ModItems.book_of_),
            new OutgasserRecipe(new ItemStack(ModItems.book_lemegeton), null));
    }
}
