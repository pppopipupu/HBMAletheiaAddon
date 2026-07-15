package com.pppopipupu.aletheia.recipe.ntmc;

import static com.hbm.inventory.OreDictManager.*;

import java.util.Iterator;

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
            new ItemStack(ModItems.particle_muon),
            null);
        addParticleRecipe(
            new ComparableStack(ModItems.particle_hydrogen),
            new ComparableStack(ModItems.particle_lead),
            6_500,
            new ItemStack(ModItems.particle_higgs),
            null);
        addParticleRecipe(
            new ComparableStack(ModItems.particle_muon),
            new ComparableStack(ModItems.particle_higgs),
            5_000,
            new ItemStack(ModItems.particle_tachyon),
            null);

        Iterator<ExposureChamberRecipes.ExposureChamberRecipe> it = ExposureChamberRecipes.recipes.iterator();
        while (it.hasNext()) {
            ExposureChamberRecipes.ExposureChamberRecipe r = it.next();
            if (r.particle.equals(new ComparableStack(ModItems.particle_higgs))
                && r.ingredient.equals(new OreDictStack(U.ingot()))) {
                it.remove();
            }
        }
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

    private static void addParticleRecipe(AStack in1, AStack in2, int momentum, ItemStack out1, ItemStack out2) {
        Iterator<ParticleAcceleratorRecipes.ParticleAcceleratorRecipe> it = ParticleAcceleratorRecipes.recipes
            .iterator();
        while (it.hasNext()) {
            ParticleAcceleratorRecipes.ParticleAcceleratorRecipe r = it.next();
            if ((r.input1.equals(in1) && r.input2.equals(in2)) || (r.input1.equals(in2) && r.input2.equals(in1))) {
                it.remove();
            }
        }
        ParticleAcceleratorRecipes.recipes
            .add(new ParticleAcceleratorRecipes.ParticleAcceleratorRecipe(in1, in2, momentum, out1, out2));
    }
}
