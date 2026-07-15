package com.pppopipupu.aletheia.recipe.ntmc;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.hbm.inventory.RecipesCommon.OreDictStack;
import com.hbm.inventory.recipes.ArcWelderRecipes;
import com.hbm.util.Compat;

public class AletheiaRecipesNtmcAE2 {

    public static void register() {
        Item certus = Compat.tryLoadItem("appliedenergistics2", "item.ItemMultiMaterial");
        if (certus != null) ArcWelderRecipes.recipes.add(
            new ArcWelderRecipes.ArcWelderRecipe(
                new ItemStack(certus, 1, 1),
                10,
                1_000L,
                new OreDictStack("crystalCertusQuartz")));
    }
}
