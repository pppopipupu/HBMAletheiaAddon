package com.pppopipupu.aletheia.recipe.ntmc;

import net.minecraft.init.Items;

import com.hbm.inventory.OreDictManager.DictFrame;
import com.hbm.inventory.RecipesCommon.ComparableStack;
import com.hbm.inventory.recipes.PressRecipes;
import com.hbm.items.ItemEnums.EnumBriquetteType;
import com.hbm.items.ModItems;
import com.hbm.items.machine.ItemStamp.StampType;

public class AletheiaRecipesNtmcPress {

    public static void register() {
        PressRecipes.makeRecipe(
            StampType.FLAT,
            new ComparableStack(DictFrame.fromOne(ModItems.briquette, EnumBriquetteType.COAL)),
            Items.coal);
        PressRecipes.makeRecipe(
            StampType.FLAT,
            new ComparableStack(DictFrame.fromOne(ModItems.briquette, EnumBriquetteType.LIGNITE)),
            ModItems.lignite);
    }
}
