package com.pppopipupu.aletheia.recipe;

import static com.hbm.inventory.OreDictManager.*;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;

import com.hbm.blocks.ModBlocks;
import com.hbm.inventory.FluidStack;
import com.hbm.inventory.OreDictManager.DictFrame;
import com.hbm.inventory.RecipesCommon.ComparableStack;
import com.hbm.inventory.RecipesCommon.OreDictStack;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.material.MaterialShapes;
import com.hbm.inventory.material.Mats;
import com.hbm.inventory.material.Mats.MaterialStack;
import com.hbm.inventory.recipes.AssemblyMachineRecipes;
import com.hbm.inventory.recipes.CentrifugeRecipes;
import com.hbm.inventory.recipes.ChemicalPlantRecipes;
import com.hbm.inventory.recipes.ElectrolyserMetalRecipes;
import com.hbm.inventory.recipes.ElectrolyserMetalRecipes.ElectrolysisMetalRecipe;
import com.hbm.inventory.recipes.loader.GenericRecipe;
import com.hbm.inventory.recipes.loader.GenericRecipes;
import com.hbm.items.ItemEnums.EnumAshType;
import com.hbm.items.ItemEnums.EnumChunkType;
import com.hbm.items.ModItems;
import com.hbm.items.machine.ItemBatteryPack.EnumBatteryPack;
import com.hbm.items.machine.ItemCircuit.EnumCircuitType;
import com.pppopipupu.aletheia.block.AletheiaBlocks;
import com.pppopipupu.aletheia.fluid.AletheiaFluids;
import com.pppopipupu.aletheia.item.AletheiaItems;

import cpw.mods.fml.common.registry.GameRegistry;

public class AletheiaRecipes {

    public static void registerForgeRecipes() {
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                new ItemStack(ModItems.disperser_canister_empty, 4),
                new Object[] { " P ", "PGP", " P ", 'P', "platePlastic", 'G', ModBlocks.glass_boron }));
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                new ItemStack(ModItems.glyphid_gland_empty, 4),
                new Object[] { " P ", "PEP", " P ", 'P', "platePlastic", 'E', Items.emerald }));

        GameRegistry.addRecipe(
            new ItemStack(AletheiaItems.qgp_mining_bomb, 1),
            new Object[] { "TTT", "TQT", "TTT", 'T', Blocks.tnt, 'Q', AletheiaItems.bucket_qgp });
        GameRegistry.addRecipe(
            new ItemStack(AletheiaItems.gun_pppop, 1),
            new Object[] { " U ", " U ", "D D", 'U', ModBlocks.block_u235, 'D', ModBlocks.block_desh });
        GameRegistry.addRecipe(
            new ItemStack(AletheiaItems.ams_muzzle, 1),
            new Object[] { "PPP", " P ", " P ", 'P', ModItems.plate_schrabidium });
        GameRegistry.addRecipe(
            new ItemStack(AletheiaItems.ams_focus_blank, 1),
            new Object[] { " P ", "PGP", " P ", 'P', ModItems.plate_dineutronium, 'G', Blocks.obsidian });
        GameRegistry.addShapelessRecipe(
            new ItemStack(AletheiaItems.ams_focus_limiter, 1),
            new Object[] { AletheiaItems.ams_focus_blank, ModItems.rune_isa, Blocks.diamond_block });
        GameRegistry.addShapelessRecipe(
            new ItemStack(AletheiaItems.ams_focus_booster, 1),
            new Object[] { AletheiaItems.ams_focus_blank, ModItems.rune_hagalaz, Blocks.emerald_block });

        GameRegistry.addShapelessRecipe(
            new ItemStack(Items.gold_nugget, 1),
            new Object[] { Items.golden_apple, Items.golden_apple, Items.golden_apple, Items.golden_apple,
                Items.golden_apple, Items.golden_apple, Items.golden_apple, Items.golden_apple, Items.egg });

        GameRegistry.addSmelting(AletheiaItems.powder_sodium, new ItemStack(AletheiaItems.ingot_sodium), 0.0F);
        GameRegistry.addSmelting(AletheiaItems.powder_strontium, new ItemStack(AletheiaItems.ingot_strontium), 0.0F);
        GameRegistry.addSmelting(AletheiaItems.powder_neodymium, new ItemStack(AletheiaItems.ingot_neodymium), 0.0F);

        GameRegistry.addShapedRecipe(
            new ItemStack(AletheiaBlocks.block_sodium),
            "###",
            "###",
            "###",
            '#',
            AletheiaItems.ingot_sodium);
        GameRegistry.addShapelessRecipe(new ItemStack(AletheiaItems.ingot_sodium, 9), AletheiaBlocks.block_sodium);
        GameRegistry.addShapedRecipe(
            new ItemStack(AletheiaBlocks.block_strontium),
            "###",
            "###",
            "###",
            '#',
            AletheiaItems.ingot_strontium);
        GameRegistry
            .addShapelessRecipe(new ItemStack(AletheiaItems.ingot_strontium, 9), AletheiaBlocks.block_strontium);
        GameRegistry.addShapedRecipe(
            new ItemStack(AletheiaBlocks.block_neodymium),
            "###",
            "###",
            "###",
            '#',
            AletheiaItems.ingot_neodymium);
        GameRegistry
            .addShapelessRecipe(new ItemStack(AletheiaItems.ingot_neodymium, 9), AletheiaBlocks.block_neodymium);
    }

    public static void registerHBMRecipes() {
        AssemblyMachineRecipes.INSTANCE.register(
            new GenericRecipe("ass.ams_base").setup(1200, 5000)
                .outputItems(new ItemStack(AletheiaBlocks.ams_base, 1))
                .inputItems(
                    new OreDictStack(OSMIRIDIUM.plateWelded(), 8),
                    new OreDictStack(DNT.wireDense(), 8),
                    new ComparableStack(ModItems.coil_gold_torus, 4),
                    new ComparableStack(ModItems.circuit, 4, EnumCircuitType.CONTROLLER_QUANTUM),
                    new ComparableStack(ModItems.singularity_spark, 1)));
        AssemblyMachineRecipes.INSTANCE.register(
            new GenericRecipe("ass.ams_emitter").setup(1000, 5000)
                .outputItems(new ItemStack(AletheiaBlocks.ams_emitter, 1))
                .inputItems(
                    new OreDictStack(OSMIRIDIUM.plateWelded(), 4),
                    new OreDictStack(STAR.wireDense(), 4),
                    new ComparableStack(ModItems.coil_gold_torus, 2),
                    new ComparableStack(ModItems.circuit, 2, EnumCircuitType.CONTROLLER_QUANTUM),
                    new ComparableStack(ModItems.singularity_spark, 1)));
        AssemblyMachineRecipes.INSTANCE.register(
            new GenericRecipe("ass.ams_limiter").setup(800, 5000)
                .outputItems(new ItemStack(AletheiaBlocks.ams_limiter, 1))
                .inputItems(
                    new OreDictStack(OSMIRIDIUM.plateWelded(), 4),
                    new OreDictStack(SBD.wireDense(), 4),
                    new ComparableStack(ModItems.coil_copper_torus, 2),
                    new ComparableStack(ModItems.circuit, 2, EnumCircuitType.CONTROLLER_QUANTUM),
                    new ComparableStack(ModItems.singularity_spark, 1)));
        AssemblyMachineRecipes.INSTANCE.register(
            new GenericRecipe("ass.schrabtransmutator").setup(500, 100)
                .outputItems(new ItemStack(AletheiaBlocks.machine_schrabidium_transmutator, 1))
                .inputItems(
                    new OreDictStack(MAGTUNG.ingot(), 1),
                    new OreDictStack(TI.ingot(), 24),
                    new OreDictStack(STEEL.plate(), 18),
                    new OreDictStack(STEEL.plateWelded(), 12),
                    new ComparableStack(ModItems.plate_desh, 6),
                    new OreDictStack(RUBBER.ingot(), 8),
                    new ComparableStack(ModItems.battery_pack, 1, EnumBatteryPack.BATTERY_LEAD.ordinal()),
                    new ComparableStack(ModItems.circuit, 2, EnumCircuitType.ADVANCED.ordinal())));

        ChemicalPlantRecipes.INSTANCE.register(
            new GenericRecipe("chem.ams_base").setup(200, 5000)
                .inputItems(
                    new ComparableStack(AletheiaItems.gun_pppop, 1),
                    new ComparableStack(AletheiaItems.gun_pppop, 1),
                    new ComparableStack(ModItems.glyphid_meat, 16),
                    new ComparableStack(ModItems.singularity_spark, 1))
                .inputFluids(new FluidStack(Fluids.PEROXIDE, 4000), new FluidStack(Fluids.ESTRADIOL, 4000))
                .outputItems(new ItemStack(AletheiaBlocks.ams_base, 1)));
        ChemicalPlantRecipes.INSTANCE.register(
            new GenericRecipe("chem.ams_emitter").setup(150, 4000)
                .inputItems(
                    new ComparableStack(AletheiaItems.gun_pppop, 1),
                    new ComparableStack(ModItems.glyphid_meat, 8))
                .inputFluids(new FluidStack(Fluids.PEROXIDE, 2000), new FluidStack(Fluids.ESTRADIOL, 2000))
                .outputItems(new ItemStack(AletheiaBlocks.ams_emitter, 1)));
        ChemicalPlantRecipes.INSTANCE.register(
            new GenericRecipe("chem.ams_limiter").setup(120, 3000)
                .inputItems(
                    new ComparableStack(AletheiaItems.gun_pppop, 1),
                    new ComparableStack(ModItems.glyphid_meat, 4))
                .inputFluids(new FluidStack(Fluids.PEROXIDE, 2000), new FluidStack(Fluids.ESTRADIOL, 2000))
                .outputItems(new ItemStack(AletheiaBlocks.ams_limiter, 1)));
        ChemicalPlantRecipes.INSTANCE.register(
            new GenericRecipe("chem.alien_jelly").setup(100, 100)
                .inputFluids(new FluidStack(Fluids.SALIENT, 200), new FluidStack(Fluids.SULFURIC_ACID, 10))
                .outputItems(new ItemStack(AletheiaItems.alien_jelly, 5)));
        ChemicalPlantRecipes.INSTANCE.register(
            new GenericRecipe("chem.glyphid_spawner").setup(200, 500)
                .inputItems(new ComparableStack(ModItems.egg_glyphid, 1))
                .inputFluids(new FluidStack(Fluids.ACID, 1000))
                .outputItems(new ItemStack(ModBlocks.glyphid_spawner, 1)));
        ChemicalPlantRecipes.INSTANCE.register(
            new GenericRecipe("chem.upgrade_ultimate").setup(400, 1000)
                .inputItems(
                    new ComparableStack(ModItems.upgrade_overdrive_3, 1),
                    new ComparableStack(ModItems.upgrade_speed_3, 1),
                    new ComparableStack(ModItems.upgrade_effect_3, 1))
                .inputFluids(new FluidStack(AletheiaFluids.fluid_qgp, 10000))
                .outputItems(new ItemStack(AletheiaItems.upgrade_ultimate, 1)));

        ChemicalPlantRecipes.INSTANCE.register(
            new GenericRecipe("chem.oxygen").setupNamed(20, 400)
                .setIcon(ModItems.gas_full, Fluids.OXYGEN.getID())
                .inputFluids(new FluidStack(Fluids.AIR, 8_000))
                .outputFluids(new FluidStack(Fluids.OXYGEN, 500)));
        ChemicalPlantRecipes.INSTANCE.register(
            new GenericRecipe("chem.ln2").setupNamed(400, 2_000)
                .outputFluids(new FluidStack(Fluids.NITROGEN, 5_000)));
        ChemicalPlantRecipes.INSTANCE.register(
            new GenericRecipe("chem.cryolite").setupNamed(50, 100)
                .inputItems(new OreDictStack(NA.dust()), new OreDictStack(AL.dust()), new OreDictStack(F.dust(), 2))
                .outputItems(DictFrame.fromOne(ModItems.chunk_ore, EnumChunkType.CRYOLITE, 1)));
        ChemicalPlantRecipes.INSTANCE.register(
            new GenericRecipe("chem.electrolysis_h2o").setupNamed(160, 1_000)
                .inputFluids(new FluidStack(Fluids.WATER, 1_000))
                .outputFluids(new FluidStack(Fluids.HYDROGEN, 1_600), new FluidStack(Fluids.OXYGEN, 800)));
        ChemicalPlantRecipes.INSTANCE.register(
            new GenericRecipe("chem.electrolysis_d2o").setupNamed(160, 1_000)
                .inputFluids(new FluidStack(Fluids.HEAVYWATER, 1_000))
                .outputFluids(new FluidStack(Fluids.DEUTERIUM, 1_600), new FluidStack(Fluids.OXYGEN, 800)));
        ChemicalPlantRecipes.INSTANCE.register(
            new GenericRecipe("chem.powder_magic").setup(80, 500)
                .inputItems(new ComparableStack(ModItems.powder_lapis))
                .inputFluids(new FluidStack(Fluids.XPJUICE, 25))
                .outputItems(new ItemStack(ModItems.powder_magic)));
        ChemicalPlantRecipes.INSTANCE.register(
            new GenericRecipe("chem.btx2soot").setupNamed(400, 5_000)
                .inputFluids(new FluidStack(Fluids.XYLENE, 1_000), new FluidStack(Fluids.OXYGEN, 1_000))
                .outputItems(DictFrame.fromOne(ModItems.powder_ash, EnumAshType.SOOT, 1)));
        ChemicalPlantRecipes.INSTANCE.register(
            new GenericRecipe("chem.tungsten_carbide").setup(240, 10_000)
                .inputItems(new OreDictStack(W.dust()))
                .inputFluids(new FluidStack(Fluids.SYNGAS, 2_000))
                .outputItems(new ItemStack(ModItems.ingot_tungsten_carbide))
                .outputFluids(new FluidStack(Fluids.SPENTSTEAM, 1_000)));

        GenericRecipes chem = ChemicalPlantRecipes.INSTANCE;
        GenericRecipe oldMeat = (GenericRecipe) chem.recipeNameMap.get("chem.meatprocessing");
        if (oldMeat != null) {
            chem.recipeOrderedList.remove(oldMeat);
            chem.recipeNameMap.remove("chem.meatprocessing");
        }
        ChemicalPlantRecipes.INSTANCE.register(
            new GenericRecipe("chem.meatprocessing").setupNamed(200, 200)
                .setIcon(ModItems.glyphid_meat)
                .inputItems(new OreDictStack(KEY_GLYPHID_MEAT, 1))
                .inputFluids(new FluidStack(Fluids.WATER, 1000))
                .outputItems(
                    new ItemStack(ModItems.sulfur, 12),
                    new ItemStack(ModItems.niter, 9),
                    new ItemStack(Items.coal, 6))
                .outputFluids(new FluidStack(Fluids.SALIENT, 250)));

        CentrifugeRecipes.recipes.put(
            new ComparableStack(ModItems.crystal_copper),
            new ItemStack[] { new ItemStack(ModItems.powder_copper, 4), new ItemStack(ModItems.sulfur, 1),
                new ItemStack(ModItems.powder_cobalt_tiny, 1), new ItemStack(ModItems.powder_gold, 3) });

        CentrifugeRecipes.recipes.put(
            new ComparableStack(Items.blaze_rod),
            new ItemStack[] { new ItemStack(ModItems.sulfur, 3), new ItemStack(ModItems.niter, 2),
                new ItemStack(ModItems.powder_coal_tiny, 1), new ItemStack(ModItems.sulfur, 1) });

        ElectrolyserMetalRecipes.recipes.put(
            new ComparableStack(ModItems.crystal_copper),
            new ElectrolysisMetalRecipe(
                new MaterialStack(Mats.MAT_COPPER, MaterialShapes.INGOT.q(6)),
                new MaterialStack(Mats.MAT_LEAD, MaterialShapes.NUGGET.q(4)),
                new ItemStack(ModItems.powder_lithium_tiny, 3),
                new ItemStack(ModItems.sulfur, 2),
                new ItemStack(ModItems.powder_gold, 3)));
    }
}
