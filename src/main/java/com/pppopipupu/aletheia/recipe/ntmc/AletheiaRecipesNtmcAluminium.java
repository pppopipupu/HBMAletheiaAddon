package com.pppopipupu.aletheia.recipe.ntmc;

import static com.hbm.inventory.OreDictManager.*;

import net.minecraft.item.ItemStack;

import com.hbm.blocks.BlockEnums.EnumStoneType;
import com.hbm.blocks.ModBlocks;
import com.hbm.inventory.FluidStack;
import com.hbm.inventory.OreDictManager.DictFrame;
import com.hbm.inventory.RecipesCommon.ComparableStack;
import com.hbm.inventory.RecipesCommon.OreDictStack;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.material.MaterialShapes;
import com.hbm.inventory.material.Mats;
import com.hbm.inventory.material.Mats.MaterialStack;
import com.hbm.inventory.recipes.ChemicalPlantRecipes;
import com.hbm.inventory.recipes.ElectrolyserFluidRecipes;
import com.hbm.inventory.recipes.ElectrolyserFluidRecipes.ElectrolysisRecipe;
import com.hbm.inventory.recipes.ElectrolyserMetalRecipes;
import com.hbm.inventory.recipes.ElectrolyserMetalRecipes.ElectrolysisMetalRecipe;
import com.hbm.inventory.recipes.MixerRecipes;
import com.hbm.inventory.recipes.loader.GenericRecipe;
import com.hbm.inventory.recipes.loader.GenericRecipes;
import com.hbm.items.ItemEnums.EnumAshType;
import com.hbm.items.ItemEnums.EnumChunkType;
import com.hbm.items.ModItems;

public class AletheiaRecipesNtmcAluminium {

    private static final class Mixer extends MixerRecipes.MixerRecipe {

        private Mixer(int output, int processTime) {
            super(output, processTime);
        }
    }

    private static MixerRecipes.MixerRecipe mixer(int output, int processTime) {
        return new Mixer(output, processTime);
    }

    public static void register() {

        GenericRecipes<GenericRecipe> chem = ChemicalPlantRecipes.INSTANCE;
        GenericRecipe oldBauxite = chem.recipeNameMap.get("chem.fp_bauxite_solution");
        if (oldBauxite != null) {
            chem.recipeOrderedList.remove(oldBauxite);
            chem.recipeNameMap.remove("chem.fp_bauxite_solution");
        }
        ChemicalPlantRecipes.INSTANCE.register(
            new GenericRecipe("chem.fp_bauxite_solution").setupNamed(50, 1_000)
                .inputFluids(new FluidStack(Fluids.BAUXITE_SOLUTION, 1_000))
                .outputFluids(new FluidStack(Fluids.REDMUD, 500), new FluidStack(Fluids.SODIUM_ALUMINATE, 500)));

        MixerRecipes.MixerRecipe mBauxite = mixer(1_500, 80);
        mBauxite.input1 = new FluidStack(Fluids.LYE, 100);
        mBauxite.input2 = new FluidStack(Fluids.WATER, 1_000);
        mBauxite.solidInput = new ComparableStack(ModBlocks.stone_resource, 1, EnumStoneType.BAUXITE.ordinal());
        MixerRecipes.register(Fluids.BAUXITE_SOLUTION, mBauxite);

        MixerRecipes.MixerRecipe mAluminaA = mixer(1000, 40);
        mAluminaA.input1 = new FluidStack(Fluids.SODIUM_ALUMINATE, 750);
        mAluminaA.solidInput = new OreDictStack(F.dust(), 3);
        MixerRecipes.MixerRecipe mAluminaB = mixer(1500, 40);
        mAluminaB.input1 = new FluidStack(Fluids.SODIUM_ALUMINATE, 750);
        mAluminaB.input2 = new FluidStack(Fluids.LYE, 500);
        mAluminaB.solidInput = new ComparableStack(DictFrame.fromOne(ModItems.chunk_ore, EnumChunkType.CRYOLITE));
        MixerRecipes.register(Fluids.ALUMINA, mAluminaA, mAluminaB);

        MixerRecipes.MixerRecipe mLyeWood = mixer(50, 100);
        mLyeWood.input1 = new FluidStack(Fluids.WATER, 500);
        mLyeWood.solidInput = new ComparableStack(ModItems.powder_ash, 2, EnumAshType.WOOD);
        MixerRecipes.MixerRecipe mLyeNa = mixer(500, 50);
        mLyeNa.input1 = new FluidStack(Fluids.WATER, 1000);
        mLyeNa.solidInput = new OreDictStack(NA.dust());
        MixerRecipes.MixerRecipe mLyeLi = mixer(500, 50);
        mLyeLi.input1 = new FluidStack(Fluids.WATER, 1000);
        mLyeLi.solidInput = new OreDictStack(LI.dust());
        MixerRecipes.register(Fluids.LYE, mLyeWood, mLyeNa, mLyeLi);

        ElectrolyserMetalRecipes.recipes.put(
            new ComparableStack(ModItems.crystal_aluminium),
            new ElectrolysisMetalRecipe(
                new MaterialStack(Mats.MAT_ALUMINIUM, MaterialShapes.INGOT.q(2)),
                new MaterialStack(Mats.MAT_IRON, MaterialShapes.INGOT.q(2)),
                new ItemStack(ModItems.chunk_ore, 4, EnumChunkType.CRYOLITE.ordinal()),
                new ItemStack(ModItems.powder_lithium_tiny, 3)));

        ElectrolyserFluidRecipes.recipes.put(
            Fluids.ALUMINA,
            new ElectrolysisRecipe(
                1_000,
                new FluidStack(Fluids.SODIUM, 200),
                new FluidStack(Fluids.NONE, 0),
                40,
                new ItemStack(ModItems.powder_aluminium, 7),
                new ItemStack(ModItems.fluorite, 2)));
    }
}
