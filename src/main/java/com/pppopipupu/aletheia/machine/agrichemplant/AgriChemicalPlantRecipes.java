package com.pppopipupu.aletheia.machine.agrichemplant;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import com.hbm.inventory.FluidStack;
import com.hbm.inventory.RecipesCommon.ComparableStack;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.recipes.loader.GenericRecipe;
import com.hbm.inventory.recipes.loader.GenericRecipes;
import com.hbm.items.ModItems;
import com.pppopipupu.aletheia.item.AletheiaItems;

public class AgriChemicalPlantRecipes extends GenericRecipes<GenericRecipe> {

    public static final AgriChemicalPlantRecipes INSTANCE = new AgriChemicalPlantRecipes();

    @Override
    public int inputItemLimit() {
        return 3;
    }

    @Override
    public int inputFluidLimit() {
        return 3;
    }

    @Override
    public int outputItemLimit() {
        return 3;
    }

    @Override
    public int outputFluidLimit() {
        return 3;
    }

    @Override
    public String getFileName() {
        return "aletheiaAgriChemPlant.json";
    }

    @Override
    public GenericRecipe instantiateRecipe(String name) {
        return new GenericRecipe(name);
    }

    @Override
    public void registerDefaults() {

        /// BIO CRYSTAL ///
        this.register(
            new GenericRecipe("agri.bio_crystal").setupNamed(200, 100)
                .setIcon(AletheiaItems.bio_crystal)
                .inputItems(
                    new ComparableStack(AletheiaItems.alien_jelly, 1),
                    new ComparableStack(ModItems.egg_glyphid, 1))
                .inputFluids(new FluidStack(Fluids.WATER, 2_000))
                .outputItems(new ItemStack(AletheiaItems.bio_crystal, 4)));

        this.register(
            new GenericRecipe("agri.bio_crystal_egg").setupNamed(200, 100)
                .setIcon(AletheiaItems.bio_crystal)
                .inputItems(
                    new ComparableStack(ModItems.egg_glyphid, 1),
                    new ComparableStack(AletheiaItems.bio_crystal, 1))
                .inputFluids(new FluidStack(Fluids.WATER, 2_000))
                .outputItems(new ItemStack(AletheiaItems.bio_crystal, 8)));

        /// EGG CULTIVATION ///
        this.register(
            new GenericRecipe("agri.egg_culture").setupNamed(400, 100)
                .setIcon(com.hbm.items.ModItems.egg_glyphid)
                .inputItems(
                    new ComparableStack(ModItems.egg_glyphid, 1),
                    new ComparableStack(AletheiaItems.bio_crystal, 1))
                .inputFluids(new FluidStack(Fluids.WATER, 2_000))
                .outputItems(new ItemStack(ModItems.egg_glyphid, 4)));

        /// CHEAP ALIEN JELLY ///
        this.register(
            new GenericRecipe("agri.jelly_cheap").setupNamed(200, 100)
                .setIcon(AletheiaItems.alien_jelly)
                .inputFluids(new FluidStack(Fluids.SULFURIC_ACID, 300))
                .inputItems(new ComparableStack(Blocks.grass, 1))
                .outputItems(new ItemStack(AletheiaItems.alien_jelly, 8)));

        /// DIRT TO GRASS ///
        this.register(
            new GenericRecipe("agri.dirt_grass").setupNamed(200, 100)
                .setIcon(Blocks.grass)
                .inputItems(new ComparableStack(Blocks.dirt, 1))
                .inputFluids(new FluidStack(Fluids.WATER, 1_000))
                .outputItems(new ItemStack(Blocks.grass, 4)));

        /// AGRICULTURAL SCIENCE BOTTLE (BULK) ///
        this.register(
            new GenericRecipe("agri.bottle").setupNamed(200, 100)
                .setIcon(AletheiaItems.agricultural_science)
                .inputItems(
                    new ComparableStack(AletheiaItems.bio_crystal, 1),
                    new ComparableStack(Items.glass_bottle, 1),
                    new ComparableStack(ModItems.egg_glyphid, 1))
                .inputFluids(new FluidStack(Fluids.WATER, 1_000))
                .outputItems(new ItemStack(AletheiaItems.agricultural_science, 4)));
    }
}
