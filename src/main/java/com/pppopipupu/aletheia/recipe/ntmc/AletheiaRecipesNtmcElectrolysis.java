package com.pppopipupu.aletheia.recipe.ntmc;

import net.minecraft.item.ItemStack;

import com.hbm.inventory.FluidStack;
import com.hbm.inventory.RecipesCommon.ComparableStack;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.recipes.CentrifugeRecipes;
import com.hbm.inventory.recipes.ElectrolyserFluidRecipes;
import com.hbm.inventory.recipes.ElectrolyserFluidRecipes.ElectrolysisRecipe;
import com.hbm.items.ModItems;
import com.pppopipupu.aletheia.item.AletheiaItems;

public class AletheiaRecipesNtmcElectrolysis {

    public static void register() {

        ElectrolyserFluidRecipes.recipes.put(
            Fluids.BRINE,
            new ElectrolysisRecipe(
                400,
                new FluidStack(Fluids.HYDROGEN, 200),
                new FluidStack(Fluids.OXYGEN, 200),
                40,
                new ItemStack(AletheiaItems.powder_sodium, 2)));

        CentrifugeRecipes.recipes.put(
            new ComparableStack(ModItems.mineral_fragment, 1, 4),
            new ItemStack[] { new ItemStack(ModItems.powder_zirconium, 4),
                new ItemStack(AletheiaItems.powder_neodymium, 2), new ItemStack(ModItems.powder_niobium, 1),
                new ItemStack(ModItems.powder_cobalt, 1) });

        CentrifugeRecipes.recipes.put(
            new ComparableStack(ModItems.nickel_salts),
            new ItemStack[] { new ItemStack(ModItems.powder_iron, 2), new ItemStack(ModItems.powder_nickel, 2),
                new ItemStack(AletheiaItems.powder_sodium, 1), new ItemStack(ModItems.sulfur, 1) });
    }
}
