package com.pppopipupu.aletheia.recipe.ntmc;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.hbm.inventory.FluidStack;
import com.hbm.inventory.RecipesCommon.ComparableStack;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.material.MaterialShapes;
import com.hbm.inventory.material.Mats;
import com.hbm.inventory.material.Mats.MaterialStack;
import com.hbm.inventory.recipes.CrystallizerRecipes;
import com.hbm.items.ModItems;
import com.hbm.items.machine.ItemScraps;
import com.hbm.util.Compat;

public class AletheiaRecipesNtmcCrystallizer {

    public static void register() {
        final int utilityTime = 80;
        final int mixingTime = 20;

        FluidStack sulfur = new FluidStack(Fluids.SULFURIC_ACID, 500);

        CrystallizerRecipes.registerRecipe(
            new ComparableStack(ModItems.powder_desh_ready),
            new CrystallizerRecipes.CrystallizerRecipe(
                ItemScraps.create(new MaterialStack(Mats.MAT_DESH, MaterialShapes.INGOT.q(1))),
                utilityTime).prod(0.05F),
            sulfur);

        Item seed = Compat.tryLoadItem("appliedenergistics2", "item.ItemCrystalSeed");
        Item pure_crystal = Compat.tryLoadItem("appliedenergistics2", "item.ItemMultiMaterial");
        if (seed != null && pure_crystal != null) {
            CrystallizerRecipes.registerRecipe(
                new ComparableStack(seed, 1, 0),
                new CrystallizerRecipes.CrystallizerRecipe(new ItemStack(pure_crystal, 2, 10), mixingTime * 2).setReq(2)
                    .prod(0.05F),
                new FluidStack(Fluids.WATER, 250));
            CrystallizerRecipes.registerRecipe(
                new ComparableStack(seed, 1, 600),
                new CrystallizerRecipes.CrystallizerRecipe(new ItemStack(pure_crystal, 2, 11), mixingTime * 2).setReq(2)
                    .prod(0.05F),
                new FluidStack(Fluids.WATER, 250));
            CrystallizerRecipes.registerRecipe(
                new ComparableStack(seed, 1, 1200),
                new CrystallizerRecipes.CrystallizerRecipe(new ItemStack(pure_crystal, 2, 12), mixingTime * 2).setReq(2)
                    .prod(0.05F),
                new FluidStack(Fluids.WATER, 250));
        }
    }
}
