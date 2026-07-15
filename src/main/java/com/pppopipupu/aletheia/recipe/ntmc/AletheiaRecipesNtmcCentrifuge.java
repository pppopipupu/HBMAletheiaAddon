package com.pppopipupu.aletheia.recipe.ntmc;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import com.hbm.inventory.FluidStack;
import com.hbm.inventory.RecipesCommon.ComparableStack;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.recipes.CentrifugeRecipes;
import com.hbm.inventory.recipes.GasCentrifugeRecipes;
import com.hbm.inventory.recipes.GasCentrifugeRecipes.PseudoFluidType;
import com.hbm.items.ModItems;

public class AletheiaRecipesNtmcCentrifuge {

    public static void register() {
        CentrifugeRecipes.recipes.put(
            new ComparableStack(Items.blaze_rod),
            new ItemStack[] { new ItemStack(Items.blaze_powder, 2), new ItemStack(Items.blaze_powder, 2),
                new ItemStack(ModItems.powder_fire, 1), new ItemStack(ModItems.powder_fire, 1) });
        CentrifugeRecipes.recipes.put(
            new ComparableStack(ModItems.coal_infernal),
            new ItemStack[] { new ItemStack(ModItems.powder_coal, 1), new ItemStack(ModItems.powder_coal, 1),
                new ItemStack(ModItems.powder_coal, 1), new ItemStack(Items.blaze_powder, 1) });
        CentrifugeRecipes.recipes.put(
            new ComparableStack(ModItems.crystal_schrabidium),
            new ItemStack[] { new ItemStack(ModItems.powder_schrabidium, 2),
                new ItemStack(ModItems.powder_schrabidium, 2), new ItemStack(ModItems.powder_neptunium, 1),
                new ItemStack(ModItems.powder_lithium_tiny, 1) });

        GasCentrifugeRecipes.fluidConversions.put(Fluids.WATZ, PseudoFluidType.MUD);
        GasCentrifugeRecipes.getGasCentrifugeRecipes()
            .put(
                new FluidStack(Fluids.WATZ, 1000),
                new Object[] {
                    new ItemStack[] { new ItemStack(ModItems.powder_iron, 4), new ItemStack(ModItems.powder_lead, 4),
                        new ItemStack(ModItems.nuclear_waste_tiny, 1), new ItemStack(ModItems.dust, 8) },
                    false, 2 });
    }
}
