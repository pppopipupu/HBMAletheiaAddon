package com.pppopipupu.aletheia.recipe.ntmc;

import net.minecraft.item.ItemStack;

import com.hbm.blocks.ModBlocks;
import com.hbm.inventory.RecipesCommon.ComparableStack;
import com.hbm.inventory.material.Mats;
import com.hbm.inventory.recipes.anvil.AnvilRecipes;
import com.hbm.inventory.recipes.anvil.AnvilRecipes.AnvilConstructionRecipe;
import com.hbm.inventory.recipes.anvil.AnvilRecipes.AnvilOutput;
import com.hbm.inventory.recipes.anvil.AnvilSmithingRecipe;
import com.hbm.items.ModItems;

public class AletheiaRecipesNtmcAnvil {

    public static void register() {
        AnvilRecipes.constructionRecipes.add(
            new AnvilConstructionRecipe(
                new ComparableStack(ModBlocks.rbmk_rod_reasim),
                new AnvilOutput[] { new AnvilOutput(new ItemStack(ModBlocks.rbmk_blank, 1)),
                    new AnvilOutput(new ItemStack(ModItems.ingot_zirconium, 6)),
                    new AnvilOutput(new ItemStack(ModItems.shell, 2, Mats.MAT_STEEL.id)) }).setTier(4));

        AnvilRecipes.smithingRecipes.add(
            new AnvilSmithingRecipe(
                7,
                new ItemStack(ModItems.stamp_book, 1, 1),
                new ComparableStack(ModItems.stamp_book, 1, 0),
                new ComparableStack(ModItems.gem_alexandrite)));
        AnvilRecipes.smithingRecipes.add(
            new AnvilSmithingRecipe(
                7,
                new ItemStack(ModItems.stamp_book, 1, 2),
                new ComparableStack(ModItems.stamp_book, 1, 1),
                new ComparableStack(ModItems.gem_alexandrite)));
        AnvilRecipes.smithingRecipes.add(
            new AnvilSmithingRecipe(
                7,
                new ItemStack(ModItems.stamp_book, 1, 3),
                new ComparableStack(ModItems.stamp_book, 1, 2),
                new ComparableStack(ModItems.gem_alexandrite)));
        AnvilRecipes.smithingRecipes.add(
            new AnvilSmithingRecipe(
                7,
                new ItemStack(ModItems.stamp_book, 1, 4),
                new ComparableStack(ModItems.stamp_book, 1, 3),
                new ComparableStack(ModItems.gem_alexandrite)));
        AnvilRecipes.smithingRecipes.add(
            new AnvilSmithingRecipe(
                7,
                new ItemStack(ModItems.stamp_book, 1, 5),
                new ComparableStack(ModItems.stamp_book, 1, 4),
                new ComparableStack(ModItems.gem_alexandrite)));
        AnvilRecipes.smithingRecipes.add(
            new AnvilSmithingRecipe(
                7,
                new ItemStack(ModItems.stamp_book, 1, 6),
                new ComparableStack(ModItems.stamp_book, 1, 5),
                new ComparableStack(ModItems.gem_alexandrite)));
        AnvilRecipes.smithingRecipes.add(
            new AnvilSmithingRecipe(
                7,
                new ItemStack(ModItems.stamp_book, 1, 7),
                new ComparableStack(ModItems.stamp_book, 1, 6),
                new ComparableStack(ModItems.gem_alexandrite)));
        AnvilRecipes.smithingRecipes.add(
            new AnvilSmithingRecipe(
                7,
                new ItemStack(ModItems.stamp_book, 1, 0),
                new ComparableStack(ModItems.stamp_book, 1, 7),
                new ComparableStack(ModItems.gem_alexandrite)));
    }
}
