package com.pppopipupu.aletheia.recipe.ntmc;

import static com.hbm.inventory.OreDictManager.*;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import com.hbm.blocks.ModBlocks;
import com.hbm.inventory.OreDictManager.DictFrame;
import com.hbm.items.ItemEnums.EnumChunkType;
import com.hbm.items.ModItems;
import com.hbm.items.machine.ItemCircuit.EnumCircuitType;
import com.pppopipupu.aletheia.Aletheia;
import com.pppopipupu.aletheia.item.AletheiaItems;
import com.pppopipupu.aletheia.item.ItemZirnoxRodAletheia;

import cpw.mods.fml.common.registry.GameRegistry;

public class AletheiaRecipesNtmcWorkbench {

    public static void register() {
        removeRecipesByOutput(ModItems.upgrade_template);
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                new ItemStack(ModItems.upgrade_template, 2),
                "WIW",
                "PCP",
                "WIW",
                'W',
                CU.wireFine(),
                'I',
                ANY_PLASTIC.ingot(),
                'C',
                DictFrame.fromOne(ModItems.circuit, EnumCircuitType.BASIC),
                'P',
                ModItems.plate_polymer));

        GameRegistry.addRecipe(
            new ShapedOreRecipe(new ItemStack(ModItems.nothing, 8, 0), "B B", " B ", "B B", 'B', "dyeBlack"));

        GameRegistry.addRecipe(
            new ShapelessOreRecipe(
                new ItemStack(Items.gold_nugget, 1, 0),
                ModItems.spawn_duck,
                ModItems.spawn_duck,
                ModItems.spawn_duck,
                ModItems.spawn_duck,
                ModItems.spawn_duck,
                ModItems.spawn_duck,
                ModItems.spawn_duck,
                ModItems.spawn_duck));

        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ModBlocks.pwr_block, 8), ModBlocks.pwr_casing));

        GameRegistry.addRecipe(
            new ShapelessOreRecipe(
                DictFrame.fromOne(ModItems.chunk_ore, EnumChunkType.CRYOLITE),
                NA.dust(),
                AL.dust(),
                F.dust(),
                F.dust(),
                KEY_TOOL_CHEMISTRYSET));

        GameRegistry.addRecipe(
            new ShapelessOreRecipe(
                new ItemStack(AletheiaItems.rod_zirnox_qgp),
                ModItems.rod_zirnox_empty,
                AletheiaItems.billet_qgp,
                AletheiaItems.billet_qgp));
        GameRegistry.addRecipe(
            new ShapelessOreRecipe(
                new ItemStack(AletheiaItems.rod_zirnox_digamma),
                ModItems.rod_zirnox_empty,
                ModItems.particle_digamma,
                ModItems.particle_digamma));
        GameRegistry.addRecipe(
            new ShapelessOreRecipe(
                new ItemStack(AletheiaItems.rbmk_fuel_qgp),
                ModItems.rbmk_fuel_empty,
                AletheiaItems.billet_qgp,
                AletheiaItems.billet_qgp,
                AletheiaItems.billet_qgp,
                AletheiaItems.billet_qgp,
                AletheiaItems.billet_qgp,
                AletheiaItems.billet_qgp,
                AletheiaItems.billet_qgp,
                AletheiaItems.billet_qgp));

        GameRegistry.addRecipe(
            new ShapelessOreRecipe(
                new ItemStack(AletheiaItems.waste_digamma, 2, 1),
                ItemZirnoxRodAletheia.rod_zirnox_digamma_depleted));
        GameRegistry.addRecipe(
            new ShapelessOreRecipe(
                new ItemStack(AletheiaItems.waste_qgp, 2, 1),
                ItemZirnoxRodAletheia.rod_zirnox_qgp_depleted));
    }

    private static void removeRecipesByOutput(Item output) {
        List<IRecipe> recipes = net.minecraft.item.crafting.CraftingManager.getInstance()
            .getRecipeList();
        List<IRecipe> toRemove = new ArrayList<>();
        for (IRecipe recipe : recipes) {
            ItemStack stack = recipe.getRecipeOutput();
            if (stack != null && stack.getItem() == output) {
                toRemove.add(recipe);
            }
        }
        if (!toRemove.isEmpty()) {
            recipes.removeAll(toRemove);
            Aletheia.LOG.info(
                "AletheiaRecipesNtmcWorkbench: removed " + toRemove.size()
                    + " existing recipe(s) for "
                    + output.getUnlocalizedName());
        }
    }
}
