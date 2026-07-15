package com.pppopipupu.aletheia.recipe.ntmc;

import net.minecraft.item.ItemStack;

import com.hbm.inventory.RecipesCommon.AStack;
import com.hbm.inventory.RecipesCommon.ComparableStack;
import com.hbm.inventory.RecipesCommon.OreDictStack;
import com.hbm.inventory.recipes.CyclotronRecipes;
import com.hbm.items.ModItems;
import com.hbm.util.Tuple.Pair;
import com.pppopipupu.aletheia.item.AletheiaItems;

public class AletheiaRecipesNtmcReactors {

    public static void register() {

        CyclotronRecipes.recipes.put(
            new Pair<ComparableStack, AStack>(
                new ComparableStack(ModItems.part_lithium),
                new ComparableStack(AletheiaItems.powder_strontium)),
            new Pair<ItemStack, Integer>(new ItemStack(ModItems.powder_zirconium), 50));
        CyclotronRecipes.recipes.put(
            new Pair<ComparableStack, AStack>(
                new ComparableStack(ModItems.part_beryllium),
                new ComparableStack(AletheiaItems.powder_strontium)),
            new Pair<ItemStack, Integer>(new ItemStack(ModItems.powder_niobium), 25));
        CyclotronRecipes.recipes.put(
            new Pair<ComparableStack, AStack>(
                new ComparableStack(ModItems.part_beryllium),
                new ComparableStack(ModItems.powder_cerium)),
            new Pair<ItemStack, Integer>(new ItemStack(AletheiaItems.powder_neodymium), 25));
        CyclotronRecipes.recipes.put(
            new Pair<ComparableStack, AStack>(
                new ComparableStack(ModItems.part_carbon),
                new ComparableStack(AletheiaItems.powder_neodymium)),
            new Pair<ItemStack, Integer>(new ItemStack(ModItems.powder_gold), 10));
        CyclotronRecipes.recipes.put(
            new Pair<ComparableStack, AStack>(
                new ComparableStack(ModItems.part_copper),
                new OreDictStack("dustStrontium")),
            new Pair<ItemStack, Integer>(new ItemStack(AletheiaItems.powder_strontium), 15));
        CyclotronRecipes.recipes.put(
            new Pair<ComparableStack, AStack>(
                new ComparableStack(ModItems.part_copper),
                new ComparableStack(AletheiaItems.powder_strontium)),
            new Pair<ItemStack, Integer>(new ItemStack(ModItems.powder_neodymium), 15));

        CyclotronRecipes.recipes.put(
            new Pair<ComparableStack, AStack>(
                new ComparableStack(ModItems.part_plutonium),
                new OreDictStack("dustPhosphorus")),
            new Pair<ItemStack, Integer>(new ItemStack(ModItems.powder_tennessine), 100));
    }
}
