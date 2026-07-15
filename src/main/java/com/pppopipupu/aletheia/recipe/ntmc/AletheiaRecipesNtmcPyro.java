package com.pppopipupu.aletheia.recipe.ntmc;

import java.util.Iterator;

import com.hbm.inventory.FluidStack;
import com.hbm.inventory.RecipesCommon.ComparableStack;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.recipes.PyroOvenRecipes;
import com.hbm.inventory.recipes.PyroOvenRecipes.PyroOvenRecipe;
import com.hbm.items.special.ItemBedrockOreNew;
import com.hbm.items.special.ItemBedrockOreNew.BedrockOreGrade;
import com.hbm.items.special.ItemBedrockOreNew.CelestialBedrockOre;
import com.hbm.items.special.ItemBedrockOreNew.CelestialBedrockOreType;

public class AletheiaRecipesNtmcPyro {

    public static void register() {
        Iterator<PyroOvenRecipes.PyroOvenRecipe> it = PyroOvenRecipes.recipes.iterator();
        while (it.hasNext()) {
            PyroOvenRecipe r = it.next();
            if (r.inputItem instanceof ComparableStack
                && ((ComparableStack) r.inputItem).item.getClass() == ItemBedrockOreNew.class) {
                it.remove();
            }
        }
        for (CelestialBedrockOreType type : CelestialBedrockOre.getAllTypes()) {
            PyroOvenRecipes.recipes.add(
                new PyroOvenRecipe(10).in(new ComparableStack(ItemBedrockOreNew.make(BedrockOreGrade.BASE, type)))
                    .out(new FluidStack(Fluids.VITRIOL, 50))
                    .out(ItemBedrockOreNew.make(BedrockOreGrade.BASE_ROASTED, type)));
            PyroOvenRecipes.recipes.add(
                new PyroOvenRecipe(10).in(new ComparableStack(ItemBedrockOreNew.make(BedrockOreGrade.PRIMARY, type)))
                    .out(new FluidStack(Fluids.VITRIOL, 50))
                    .out(ItemBedrockOreNew.make(BedrockOreGrade.PRIMARY_ROASTED, type)));
            PyroOvenRecipes.recipes.add(
                new PyroOvenRecipe(10)
                    .in(new ComparableStack(ItemBedrockOreNew.make(BedrockOreGrade.SULFURIC_BYPRODUCT, type, 2)))
                    .out(new FluidStack(Fluids.VITRIOL, 100))
                    .out(ItemBedrockOreNew.make(BedrockOreGrade.SULFURIC_ROASTED, type, 2)));
            PyroOvenRecipes.recipes.add(
                new PyroOvenRecipe(10)
                    .in(new ComparableStack(ItemBedrockOreNew.make(BedrockOreGrade.SOLVENT_BYPRODUCT, type, 2)))
                    .out(new FluidStack(Fluids.VITRIOL, 100))
                    .out(ItemBedrockOreNew.make(BedrockOreGrade.SOLVENT_ROASTED, type, 2)));
            PyroOvenRecipes.recipes.add(
                new PyroOvenRecipe(10)
                    .in(new ComparableStack(ItemBedrockOreNew.make(BedrockOreGrade.RAD_BYPRODUCT, type, 2)))
                    .out(new FluidStack(Fluids.VITRIOL, 100))
                    .out(ItemBedrockOreNew.make(BedrockOreGrade.RAD_ROASTED, type, 2)));
        }
    }
}
