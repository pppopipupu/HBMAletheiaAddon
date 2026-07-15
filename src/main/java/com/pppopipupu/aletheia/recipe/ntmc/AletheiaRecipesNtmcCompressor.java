package com.pppopipupu.aletheia.recipe.ntmc;

import com.hbm.inventory.FluidStack;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.recipes.CompressorRecipes;
import com.hbm.inventory.recipes.CompressorRecipes.CompressorRecipe;
import com.hbm.util.Tuple.Pair;

public class AletheiaRecipesNtmcCompressor {

    public static void register() {
        CompressorRecipes.recipes.put(
            new Pair<FluidType, Integer>(Fluids.BLOOD, 3),
            new CompressorRecipe(1_000, new FluidStack(Fluids.HEAVYOIL, 1000, 0), 80));
    }
}
