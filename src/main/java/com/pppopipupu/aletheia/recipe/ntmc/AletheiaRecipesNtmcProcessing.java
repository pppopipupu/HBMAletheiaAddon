package com.pppopipupu.aletheia.recipe.ntmc;

import java.lang.reflect.Field;
import java.util.HashMap;

import net.minecraft.item.ItemStack;

import com.hbm.inventory.OreDictManager.DictFrame;
import com.hbm.inventory.RecipesCommon.ComparableStack;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.recipes.SILEXRecipes;
import com.hbm.inventory.recipes.ShredderRecipes;
import com.hbm.inventory.recipes.anvil.AnvilRecipes;
import com.hbm.items.ItemEnums.EnumChunkType;
import com.hbm.items.ModItems;
import com.hbm.items.machine.ItemFELCrystal.EnumWavelengths;
import com.pppopipupu.aletheia.Aletheia;
import com.pppopipupu.aletheia.item.AletheiaItems;

public class AletheiaRecipesNtmcProcessing {

    public static void register() {
        registerSILEX();
        registerShredder();
        registerAnvil();
    }

    private static void registerSILEX() {
        try {
            Field field = SILEXRecipes.class.getDeclaredField("recipes");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            HashMap<Object, SILEXRecipes.SILEXRecipe> map = (HashMap<Object, SILEXRecipes.SILEXRecipe>) field.get(null);

            map.put(
                new ComparableStack(ModItems.fluid_icon, 1, Fluids.REDMUD.getID()),
                new SILEXRecipes.SILEXRecipe(900, 150, EnumWavelengths.VISIBLE)
                    .addOut(new ItemStack(ModItems.powder_aluminium), 10)
                    .addOut(new ItemStack(AletheiaItems.powder_neodymium_tiny, 3), 5)
                    .addOut(new ItemStack(ModItems.powder_boron_tiny, 3), 5)
                    .addOut(new ItemStack(ModItems.nugget_zirconium), 5)
                    .addOut(new ItemStack(ModItems.powder_iron), 20)
                    .addOut(new ItemStack(ModItems.powder_titanium), 15));

            for (int i = 0; i < 5; i++) {
                map.put(
                    new ComparableStack(AletheiaItems.rbmk_pellet_rs1, 1, i),
                    new SILEXRecipes.SILEXRecipe(600, 100, 1).addOut(new ItemStack(ModItems.nugget_ra226), 23 - 5 * i)
                        .addOut(new ItemStack(ModItems.nugget_beryllium), 23 - 5 * i)
                        .addOut(new ItemStack(ModItems.nugget_polonium), 2 + 5 * i)
                        .addOut(new ItemStack(ModItems.powder_coal_tiny), 2 + 5 * i)
                        .addOut(new ItemStack(ModItems.nugget_pu239), 45 - 10 * i)
                        .addOut(new ItemStack(ModItems.nugget_pu240), 5 + 10 * i));

                map.put(
                    new ComparableStack(AletheiaItems.rbmk_pellet_rs2, 1, i),
                    new SILEXRecipes.SILEXRecipe(600, 100, 2)
                        .addOut(new ItemStack(ModItems.nugget_polonium), 23 - 5 * i)
                        .addOut(new ItemStack(ModItems.nugget_beryllium), 23 - 5 * i)
                        .addOut(new ItemStack(ModItems.nugget_lead), 2 + 5 * i)
                        .addOut(new ItemStack(ModItems.powder_coal_tiny), 2 + 5 * i)
                        .addOut(new ItemStack(ModItems.nugget_pu241), 45 - 10 * i)
                        .addOut(new ItemStack(ModItems.nugget_am_mix), 5 + 10 * i));
            }
        } catch (Exception e) {
            Aletheia.LOG.error("Failed to register NTMC SILEX recipes", e);
        }
    }

    private static void registerShredder() {
        ShredderRecipes.setRecipe(
            new ComparableStack(AletheiaItems.fragment_neodymium),
            new ItemStack(AletheiaItems.powder_neodymium_tiny, 1));
    }

    private static void registerAnvil() {
        AnvilRecipes.constructionRecipes.add(
            new AnvilRecipes.AnvilConstructionRecipe(
                new ComparableStack(DictFrame.fromOne(ModItems.chunk_ore, EnumChunkType.RARE)),
                new AnvilRecipes.AnvilOutput[] { new AnvilRecipes.AnvilOutput(new ItemStack(ModItems.fragment_boron)),
                    new AnvilRecipes.AnvilOutput(new ItemStack(ModItems.fragment_boron), 0.5F),
                    new AnvilRecipes.AnvilOutput(new ItemStack(ModItems.fragment_lanthanium), 0.1F),
                    new AnvilRecipes.AnvilOutput(new ItemStack(ModItems.fragment_cobalt)),
                    new AnvilRecipes.AnvilOutput(new ItemStack(ModItems.fragment_cobalt), 0.5F),
                    new AnvilRecipes.AnvilOutput(new ItemStack(ModItems.fragment_cerium), 0.1F),
                    new AnvilRecipes.AnvilOutput(new ItemStack(AletheiaItems.fragment_neodymium), 0.5F),
                    new AnvilRecipes.AnvilOutput(new ItemStack(ModItems.fragment_niobium), 0.5F) }).setTier(2));
    }
}
