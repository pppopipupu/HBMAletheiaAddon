package com.pppopipupu.aletheia.recipe.ntmc;

import static com.hbm.inventory.OreDictManager.*;

import net.minecraft.item.ItemStack;

import com.hbm.inventory.RecipesCommon.AStack;
import com.hbm.inventory.RecipesCommon.ComparableStack;
import com.hbm.inventory.RecipesCommon.OreDictStack;
import com.hbm.inventory.recipes.ExposureChamberRecipes;
import com.hbm.inventory.recipes.ParticleAcceleratorRecipes;
import com.hbm.items.ModItems;

public class AletheiaRecipesNtmcParticle {

    public static void register() {

        addParticleRecipe(
            new ComparableStack(ModItems.particle_hydrogen),
            new ComparableStack(ModItems.particle_amat),
            2_500,
            new ItemStack(ModItems.particle_muon));
        addParticleRecipe(
            new ComparableStack(ModItems.particle_hydrogen),
            new ComparableStack(ModItems.particle_lead),
            6_500,
            new ItemStack(ModItems.particle_higgs));
        addParticleRecipe(
            new ComparableStack(ModItems.particle_muon),
            new ComparableStack(ModItems.particle_higgs),
            5_000,
            new ItemStack(ModItems.particle_tachyon));

        ExposureChamberRecipes.recipes.removeIf(
            r -> r.particle.equals(new ComparableStack(ModItems.particle_higgs))
                && r.ingredient.equals(new OreDictStack(U.ingot())));
        ExposureChamberRecipes.recipes.add(
            new ExposureChamberRecipes.ExposureChamberRecipe(
                new ComparableStack(ModItems.particle_higgs),
                new OreDictStack(U.ingot()),
                new ItemStack(ModItems.ingot_schrabidium)));

        ExposureChamberRecipes.recipes.add(
            new ExposureChamberRecipes.ExposureChamberRecipe(
                new ComparableStack(ModItems.particle_strange),
                new OreDictStack(BI.ingot()),
                new ItemStack(ModItems.ingot_pb209)));
    }

    private static void addParticleRecipe(AStack in1, AStack in2, int momentum, ItemStack out1) {
        ParticleAcceleratorRecipes.recipes
            .removeIf(r -> (r.input1.equals(in1) && r.input2.equals(in2)) || (r.input1.equals(in2) && r.input2.equals(in1)));
        ParticleAcceleratorRecipes.recipes
            .add(new ParticleAcceleratorRecipes.ParticleAcceleratorRecipe(in1, in2, momentum, out1, null));
    }
}
