package com.pppopipupu.aletheia.recipe.ntmc;

import static com.hbm.inventory.OreDictManager.*;

import com.hbm.config.GeneralConfig;
import com.hbm.inventory.FluidStack;
import com.hbm.inventory.RecipesCommon.OreDictStack;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.recipes.ChemicalPlantRecipes;
import com.hbm.inventory.recipes.MixerRecipes;
import com.hbm.inventory.recipes.loader.GenericRecipe;
import com.hbm.inventory.recipes.loader.GenericRecipes;

public class AletheiaRecipesNtmcChemMix {

    private static final class Mixer extends MixerRecipes.MixerRecipe {

        private Mixer(int output) {
            super(output, 50);
        }
    }

    private static MixerRecipes.MixerRecipe mixer(int output) {
        return new Mixer(output);
    }

    public static void register() {

        GenericRecipes<GenericRecipe> chem = ChemicalPlantRecipes.INSTANCE;
        GenericRecipe oldNitric = chem.recipeNameMap.get("chem.nitricacid");
        if (oldNitric != null) {
            chem.recipeOrderedList.remove(oldNitric);
            chem.recipeNameMap.remove("chem.nitricacid");
        }
        ChemicalPlantRecipes.INSTANCE.register(
            new GenericRecipe("chem.nitricacid").setup(50, 100)
                .inputItems(new OreDictStack(KNO.dust()))
                .inputFluids(new FluidStack(Fluids.SULFURIC_ACID, 2_000))
                .outputFluids(new FluidStack(Fluids.NITRIC_ACID, 2_000)));

        GenericRecipe oldNitricAlt = chem.recipeNameMap.get("chem.nitricacidalt");
        if (oldNitricAlt != null) {
            chem.recipeOrderedList.remove(oldNitricAlt);
            chem.recipeNameMap.remove("chem.nitricacidalt");
        }
        ChemicalPlantRecipes.INSTANCE.register(
            new GenericRecipe("chem.nitricacidalt").setupNamed(50, 1_000)
                .inputFluids(
                    new FluidStack(Fluids.WATER, 500),
                    new FluidStack(Fluids.AMMONIA, 1000, GeneralConfig.enable528PressurizedRecipes ? 1 : 0))
                .outputFluids(new FluidStack(Fluids.NITRIC_ACID, 1_000)));

        MixerRecipes.MixerRecipe mSulfuric = mixer(2000);
        mSulfuric.input1 = new FluidStack(Fluids.PEROXIDE, 800);
        mSulfuric.solidInput = new OreDictStack(S.dust());
        MixerRecipes.register(Fluids.SULFURIC_ACID, mSulfuric);

        MixerRecipes.MixerRecipe mNitricA = mixer(1000);
        mNitricA.input1 = new FluidStack(Fluids.AMMONIA, 1000);
        mNitricA.input2 = new FluidStack(Fluids.WATER, 500);
        MixerRecipes.MixerRecipe mNitricB = mixer(2000);
        mNitricB.input1 = new FluidStack(Fluids.SULFURIC_ACID, 2000);
        mNitricB.solidInput = new OreDictStack(KNO.dust());
        MixerRecipes.register(Fluids.NITRIC_ACID, mNitricA, mNitricB);

        ChemicalPlantRecipes.INSTANCE.register(
            new GenericRecipe("chem.fp_heavyoil").setupNamed(50, 1_000)
                .inputFluids(new FluidStack(Fluids.HEAVYOIL, 1_000))
                .outputFluids(new FluidStack(Fluids.BITUMEN, 300), new FluidStack(Fluids.SMEAR, 700)));

        ChemicalPlantRecipes.INSTANCE.register(
            new GenericRecipe("chem.fp_heavyoil_vacuum").setupNamed(50, 1_000)
                .inputFluids(new FluidStack(Fluids.HEAVYOIL_VACUUM, 1_000))
                .outputFluids(new FluidStack(Fluids.SMEAR, 400), new FluidStack(Fluids.HEATINGOIL_VACUUM, 600)));

        ChemicalPlantRecipes.INSTANCE.register(
            new GenericRecipe("chem.fp_smear").setupNamed(50, 1_000)
                .inputFluids(new FluidStack(Fluids.SMEAR, 1_000))
                .outputFluids(new FluidStack(Fluids.LUBRICANT, 400), new FluidStack(Fluids.HEATINGOIL, 600)));

        ChemicalPlantRecipes.INSTANCE.register(
            new GenericRecipe("chem.fp_naphtha").setupNamed(50, 1_000)
                .inputFluids(new FluidStack(Fluids.NAPHTHA, 1_000))
                .outputFluids(new FluidStack(Fluids.HEATINGOIL, 400), new FluidStack(Fluids.DIESEL, 600)));

        ChemicalPlantRecipes.INSTANCE.register(
            new GenericRecipe("chem.fp_naphtha_ds").setupNamed(50, 1_000)
                .inputFluids(new FluidStack(Fluids.NAPHTHA_DS, 1_000))
                .outputFluids(new FluidStack(Fluids.XYLENE, 600), new FluidStack(Fluids.DIESEL_REFORM, 400)));

        ChemicalPlantRecipes.INSTANCE.register(
            new GenericRecipe("chem.fp_naphtha_crack").setupNamed(50, 1_000)
                .inputFluids(new FluidStack(Fluids.NAPHTHA_CRACK, 1_000))
                .outputFluids(new FluidStack(Fluids.HEATINGOIL, 300), new FluidStack(Fluids.DIESEL_CRACK, 700)));

        ChemicalPlantRecipes.INSTANCE.register(
            new GenericRecipe("chem.fp_naphtha_coker").setupNamed(50, 1_000)
                .inputFluids(new FluidStack(Fluids.NAPHTHA_COKER, 1_000))
                .outputFluids(new FluidStack(Fluids.NAPHTHA_CRACK, 750), new FluidStack(Fluids.LIGHTOIL_CRACK, 250)));

        ChemicalPlantRecipes.INSTANCE.register(
            new GenericRecipe("chem.fp_oil_coker").setupNamed(50, 1_000)
                .inputFluids(new FluidStack(Fluids.OIL_COKER, 1_000))
                .outputFluids(new FluidStack(Fluids.CRACKOIL, 300), new FluidStack(Fluids.HEATINGOIL, 700)));

        ChemicalPlantRecipes.INSTANCE.register(
            new GenericRecipe("chem.fp_gas_coker").setupNamed(50, 1_000)
                .inputFluids(new FluidStack(Fluids.GAS_COKER, 1_000))
                .outputFluids(new FluidStack(Fluids.AROMATICS, 250), new FluidStack(Fluids.CARBONDIOXIDE, 750)));

        ChemicalPlantRecipes.INSTANCE.register(
            new GenericRecipe("chem.fp_lightoil").setupNamed(50, 1_000)
                .inputFluids(new FluidStack(Fluids.LIGHTOIL, 1_000))
                .outputFluids(new FluidStack(Fluids.DIESEL, 400), new FluidStack(Fluids.KEROSENE, 600)));

        ChemicalPlantRecipes.INSTANCE.register(
            new GenericRecipe("chem.fp_lightoil_ds").setupNamed(50, 1_000)
                .inputFluids(new FluidStack(Fluids.LIGHTOIL_DS, 1_000))
                .outputFluids(new FluidStack(Fluids.DIESEL_REFORM, 600), new FluidStack(Fluids.KEROSENE_REFORM, 400)));

        ChemicalPlantRecipes.INSTANCE.register(
            new GenericRecipe("chem.fp_lightoil_crack").setupNamed(50, 1_000)
                .inputFluids(new FluidStack(Fluids.LIGHTOIL_CRACK, 1_000))
                .outputFluids(new FluidStack(Fluids.KEROSENE, 700), new FluidStack(Fluids.PETROLEUM, 300)));

        ChemicalPlantRecipes.INSTANCE.register(
            new GenericRecipe("chem.fp_lightoil_vacuum").setupNamed(50, 1_000)
                .inputFluids(new FluidStack(Fluids.LIGHTOIL_VACUUM, 1_000))
                .outputFluids(new FluidStack(Fluids.KEROSENE, 700), new FluidStack(Fluids.REFORMGAS, 300)));

        ChemicalPlantRecipes.INSTANCE.register(
            new GenericRecipe("chem.fp_coalcreosote").setupNamed(50, 1_000)
                .inputFluids(new FluidStack(Fluids.COALCREOSOTE, 1_000))
                .outputFluids(new FluidStack(Fluids.COALOIL, 100), new FluidStack(Fluids.BITUMEN, 900)));

        ChemicalPlantRecipes.INSTANCE.register(
            new GenericRecipe("chem.fp_coaloil").setupNamed(50, 1_000)
                .inputFluids(new FluidStack(Fluids.COALOIL, 1_000))
                .outputFluids(new FluidStack(Fluids.COALGAS, 300), new FluidStack(Fluids.OIL, 700)));

        ChemicalPlantRecipes.INSTANCE.register(
            new GenericRecipe("chem.fp_reformate").setupNamed(50, 1_000)
                .inputFluids(new FluidStack(Fluids.REFORMATE, 1_000))
                .outputFluids(new FluidStack(Fluids.AROMATICS, 400), new FluidStack(Fluids.XYLENE, 600)));

        ChemicalPlantRecipes.INSTANCE.register(
            new GenericRecipe("chem.fp_egg").setupNamed(50, 1_000)
                .inputFluids(new FluidStack(Fluids.EGG, 1_000))
                .outputFluids(new FluidStack(Fluids.CHOLESTEROL, 500), new FluidStack(Fluids.RADIOSOLVENT, 500)));

        ChemicalPlantRecipes.INSTANCE.register(
            new GenericRecipe("chem.fp_chlorocalcite_mix").setupNamed(50, 1_000)
                .inputFluids(new FluidStack(Fluids.CHLOROCALCITE_MIX, 1_000))
                .outputFluids(new FluidStack(Fluids.CHLOROCALCITE_CLEANED, 500), new FluidStack(Fluids.COLLOID, 500)));

        ChemicalPlantRecipes.INSTANCE.register(
            new GenericRecipe("chem.fp_bauxite_solution").setupNamed(50, 1_000)
                .inputFluids(new FluidStack(Fluids.BAUXITE_SOLUTION, 1_000))
                .outputFluids(new FluidStack(Fluids.REDMUD, 500), new FluidStack(Fluids.SODIUM_ALUMINATE, 500)));
    }
}
