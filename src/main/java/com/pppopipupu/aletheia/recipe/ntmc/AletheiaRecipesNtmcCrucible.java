package com.pppopipupu.aletheia.recipe.ntmc;

import net.minecraft.item.ItemStack;

import com.hbm.inventory.material.MaterialShapes;
import com.hbm.inventory.material.Mats;
import com.hbm.inventory.material.Mats.MaterialStack;
import com.hbm.inventory.recipes.CrucibleRecipe;
import com.hbm.inventory.recipes.CrucibleRecipes;
import com.hbm.items.ModItems;
import com.hbm.util.Compat;
import com.pppopipupu.aletheia.recipe.AletheiaRecipes;

public class AletheiaRecipesNtmcCrucible {

    public static void register() {
        int q = MaterialShapes.QUANTUM.q(1);
        int n = MaterialShapes.NUGGET.q(1);
        int i = MaterialShapes.INGOT.q(1);

        AletheiaRecipes.registerOverride(
            CrucibleRecipes.INSTANCE,
            "crucible.steel",
            new CrucibleRecipe("crucible.steel").setup(20, new ItemStack(ModItems.ingot_steel))
                .inputs(new MaterialStack(Mats.MAT_IRON, n), new MaterialStack(Mats.MAT_CARBON, n / 2))
                .outputs(new MaterialStack(Mats.MAT_STEEL, n)));

        if (Compat.isModLoaded(Compat.MOD_GT6)) {
            AletheiaRecipes.registerOverride(
                CrucibleRecipes.INSTANCE,
                "crucible.steelWrought",
                new CrucibleRecipe("crucible.steelWrought").setup(20, new ItemStack(ModItems.ingot_steel))
                    .inputs(
                        new MaterialStack(Mats.MAT_WROUGHTIRON, n * 2),
                        new MaterialStack(Mats.MAT_CARBON, n * 3),
                        new MaterialStack(Mats.MAT_FLUX, n))
                    .outputs(new MaterialStack(Mats.MAT_STEEL, n * 2)));
            AletheiaRecipes.registerOverride(
                CrucibleRecipes.INSTANCE,
                "crucible.steelPig",
                new CrucibleRecipe("crucible.steelPig").setup(20, new ItemStack(ModItems.ingot_steel))
                    .inputs(
                        new MaterialStack(Mats.MAT_PIGIRON, n * 2),
                        new MaterialStack(Mats.MAT_CARBON, n * 3),
                        new MaterialStack(Mats.MAT_FLUX, n))
                    .outputs(new MaterialStack(Mats.MAT_STEEL, n * 2)));
            AletheiaRecipes.registerOverride(
                CrucibleRecipes.INSTANCE,
                "crucible.steelMeteoric",
                new CrucibleRecipe("crucible.steelMeteoric").setup(20, new ItemStack(ModItems.ingot_steel))
                    .inputs(
                        new MaterialStack(Mats.MAT_METEORICIRON, n * 2),
                        new MaterialStack(Mats.MAT_CARBON, n * 3),
                        new MaterialStack(Mats.MAT_FLUX, n))
                    .outputs(new MaterialStack(Mats.MAT_STEEL, n * 2)));
        }
    }
}
