package com.pppopipupu.aletheia.recipe.ntmc;

import static com.hbm.inventory.OreDictManager.*;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import com.hbm.blocks.ModBlocks;
import com.hbm.inventory.RecipesCommon.ComparableStack;
import com.hbm.inventory.RecipesCommon.OreDictStack;
import com.hbm.inventory.recipes.AssemblyMachineRecipes;
import com.hbm.inventory.recipes.loader.GenericRecipe;
import com.hbm.items.ModItems;
import com.pppopipupu.aletheia.recipe.AletheiaRecipes;

public class AletheiaRecipesNtmcAssembly {

    @SuppressWarnings("deprecation")
    public static void register() {

        AletheiaRecipes.registerOverride(
            AssemblyMachineRecipes.INSTANCE,
            "ass.rtgfurnace",
            new GenericRecipe("ass.rtgfurnace").setup(200, 100)
                .outputItems(new ItemStack(ModBlocks.machine_rtg_furnace_off, 1))
                .inputItems(
                    new ComparableStack(Blocks.furnace, 1),
                    new ComparableStack(ModItems.rtg_unit, 3),
                    new OreDictStack(PB.plate(), 6),
                    new OreDictStack(WC.plate(), 4),
                    new OreDictStack(CU.plate(), 2)));
    }
}
