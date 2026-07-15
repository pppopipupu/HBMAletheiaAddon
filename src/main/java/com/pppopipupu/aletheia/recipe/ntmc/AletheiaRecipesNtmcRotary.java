package com.pppopipupu.aletheia.recipe.ntmc;

import static com.hbm.inventory.OreDictManager.*;

import com.hbm.inventory.RecipesCommon.ComparableStack;
import com.hbm.inventory.RecipesCommon.OreDictStack;
import com.hbm.inventory.material.MaterialShapes;
import com.hbm.inventory.material.Mats;
import com.hbm.inventory.material.Mats.MaterialStack;
import com.hbm.inventory.recipes.RotaryFurnaceRecipes;
import com.hbm.inventory.recipes.RotaryFurnaceRecipes.RotaryFurnaceRecipe;
import com.hbm.items.ModItems;

public class AletheiaRecipesNtmcRotary {

    public static void register() {
        RotaryFurnaceRecipes.recipes.add(
            new RotaryFurnaceRecipe(
                new MaterialStack(Mats.MAT_MINGRADE, MaterialShapes.INGOT.q(2)),
                100,
                50,
                new OreDictStack(CU.ingot()),
                new OreDictStack(REDSTONE.dust())));

        RotaryFurnaceRecipes.recipes.add(
            new RotaryFurnaceRecipe(
                new MaterialStack(Mats.MAT_DURA, MaterialShapes.INGOT.q(9)),
                200,
                50,
                new OreDictStack(STEEL.ingot(), 5),
                new OreDictStack(W.ingot(), 3),
                new OreDictStack(CO.ingot())));
        RotaryFurnaceRecipes.recipes.add(
            new RotaryFurnaceRecipe(
                new MaterialStack(Mats.MAT_FERRO, MaterialShapes.INGOT.q(3)),
                250,
                50,
                new OreDictStack(STEEL.ingot(), 2),
                new OreDictStack(U238.ingot(), 1)));
        RotaryFurnaceRecipes.recipes.add(
            new RotaryFurnaceRecipe(
                new MaterialStack(Mats.MAT_BBRONZE, MaterialShapes.INGOT.q(9)),
                400,
                100,
                new OreDictStack(CU.ingot(), 8),
                new OreDictStack(BI.ingot(), 1),
                new ComparableStack(ModItems.powder_flux, 3)));
        RotaryFurnaceRecipes.recipes.add(
            new RotaryFurnaceRecipe(
                new MaterialStack(Mats.MAT_ABRONZE, MaterialShapes.INGOT.q(9)),
                400,
                100,
                new OreDictStack(CU.ingot(), 8),
                new OreDictStack(AS.ingot(), 1),
                new ComparableStack(ModItems.powder_flux, 3)));
        RotaryFurnaceRecipes.recipes.add(
            new RotaryFurnaceRecipe(
                new MaterialStack(Mats.MAT_CMB, MaterialShapes.INGOT.q(3)),
                300,
                100,
                new OreDictStack(MAGTUNG.ingot(), 2),
                new OreDictStack(MUD.ingot())));
        RotaryFurnaceRecipes.recipes.add(
            new RotaryFurnaceRecipe(
                new MaterialStack(Mats.MAT_MAGTUNG, MaterialShapes.INGOT.q(1)),
                200,
                50,
                new OreDictStack(W.ingot()),
                new OreDictStack(SA326.nugget())));
    }
}
