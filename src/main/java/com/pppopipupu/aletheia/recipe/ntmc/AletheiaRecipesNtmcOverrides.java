package com.pppopipupu.aletheia.recipe.ntmc;

import static com.hbm.inventory.OreDictManager.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.hbm.blocks.ModBlocks;
import com.hbm.inventory.RecipesCommon.AStack;
import com.hbm.inventory.RecipesCommon.ComparableStack;
import com.hbm.inventory.RecipesCommon.OreDictStack;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.recipes.CrystallizerRecipes;
import com.hbm.inventory.recipes.anvil.AnvilRecipes;
import com.hbm.inventory.recipes.anvil.AnvilRecipes.AnvilConstructionRecipe;
import com.hbm.inventory.recipes.anvil.AnvilRecipes.AnvilOutput;
import com.hbm.items.ItemEnums.EnumCokeType;
import com.hbm.items.ModItems;
import com.hbm.items.food.ItemFlask.EnumInfusion;
import com.hbm.items.machine.ItemCircuit.EnumCircuitType;
import com.hbm.util.Tuple.Pair;
import com.pppopipupu.aletheia.Aletheia;

public class AletheiaRecipesNtmcOverrides {

    public static void register() {
        overrideConstructionTiers();
        overrideSmithingTiers();
        overrideCrystallizerProd();
    }

    private static OreDictStack ods(String ore) {
        return new OreDictStack(ore);
    }

    private static OreDictStack ods(String ore, int n) {
        return new OreDictStack(ore, n);
    }

    private static ComparableStack cs(Item item) {
        return new ComparableStack(item);
    }

    private static ComparableStack cs(Item item, int meta) {
        return new ComparableStack(item, meta);
    }

    private static ComparableStack cs(Block block, int meta) {
        return new ComparableStack(block, meta);
    }

    private static ComparableStack cs(Item item, int meta, int stack) {
        return new ComparableStack(item, meta, stack);
    }

    private static String aKey(AStack a) {
        if (a instanceof OreDictStack o) return "O:" + o.name;
        if (a instanceof ComparableStack c) {
            return "C:" + Item.itemRegistry.getNameForObject(c.item) + ":" + c.meta;
        }
        return "?";
    }

    private static String outKey(ItemStack s) {
        return "I:" + Item.itemRegistry.getNameForObject(s.getItem()) + ":" + s.getItemDamage();
    }

    private static String joinSig(List<String> in, List<String> out) {
        List<String> i = new ArrayList<>(in);
        List<String> o = new ArrayList<>(out);
        Collections.sort(i);
        Collections.sort(o);
        return String.join("+", i) + ">" + String.join("+", o);
    }

    private static void putTier(Map<String, Integer> map, int tier, AStack[] in, ItemStack... out) {
        List<String> ik = new ArrayList<>();
        for (AStack a : in) ik.add(aKey(a));
        List<String> ok = new ArrayList<>();
        for (ItemStack s : out) ok.add(outKey(s));
        map.put(joinSig(ik, ok), tier);
    }

    private static void putSmithing(Map<String, Integer> map, int tier, AStack left, AStack right, ItemStack out) {
        List<String> ik = new ArrayList<>();
        ik.add(aKey(left));
        ik.add(aKey(right));
        List<String> ok = new ArrayList<>();
        ok.add(outKey(out));
        map.put(joinSig(ik, ok), tier);
    }

    private static void putCryst(Map<String, Float> map, float prod, AStack in, FluidType fluid, ItemStack out) {
        String sig = aKey(in) + "@" + fluid.getName() + ">" + outKey(out);
        map.put(sig, prod);
    }

    private static Map<String, Integer> constructionTierMap;
    private static Map<String, Integer> smithingTierMap;
    private static Map<String, Float> crystallizerProdMap;

    private static void overrideConstructionTiers() {
        try {
            if (constructionTierMap == null) constructionTierMap = buildConstructionTierMap();
            for (AnvilConstructionRecipe r : AnvilRecipes.constructionRecipes) {
                String sig = joinSig(collectIn(r.input), collectOut(r.output));
                Integer tier = constructionTierMap.get(sig);
                if (tier != null) r.setTier(tier);
            }
        } catch (Exception e) {
            Aletheia.LOG.error("Failed to override NTMC construction anvil tiers", e);
        }
    }

    private static List<String> collectIn(List<AStack> in) {
        List<String> ik = new ArrayList<>();
        for (AStack a : in) ik.add(aKey(a));
        return ik;
    }

    private static List<String> collectOut(List<AnvilOutput> out) {
        List<String> ok = new ArrayList<>();
        for (AnvilOutput o : out) ok.add(outKey(o.stack));
        return ok;
    }

    private static void overrideSmithingTiers() {
        try {
            if (smithingTierMap == null) smithingTierMap = buildSmithingTierMap();
            Class<?> smithClass = com.hbm.inventory.recipes.anvil.AnvilSmithingRecipe.class;
            Field tierField = smithClass.getDeclaredField("tier");
            tierField.setAccessible(true);
            Field leftField = smithClass.getDeclaredField("left");
            leftField.setAccessible(true);
            Field rightField = smithClass.getDeclaredField("right");
            rightField.setAccessible(true);
            Field outField = smithClass.getDeclaredField("output");
            outField.setAccessible(true);
            for (com.hbm.inventory.recipes.anvil.AnvilSmithingRecipe r : AnvilRecipes.smithingRecipes) {
                List<String> ik = new ArrayList<>();
                ik.add(aKey((AStack) leftField.get(r)));
                ik.add(aKey((AStack) rightField.get(r)));
                List<String> ok = new ArrayList<>();
                ok.add(outKey((ItemStack) outField.get(r)));
                String sig = joinSig(ik, ok);
                Integer tier = smithingTierMap.get(sig);
                if (tier != null) tierField.set(r, tier);
            }
        } catch (Exception e) {
            Aletheia.LOG.error("Failed to override NTMC smithing anvil tiers", e);
        }
    }

    private static void overrideCrystallizerProd() {
        try {
            if (crystallizerProdMap == null) crystallizerProdMap = buildCrystallizerProdMap();
            Field f = CrystallizerRecipes.class.getDeclaredField("recipes");
            f.setAccessible(true);
            @SuppressWarnings("unchecked")
            HashMap<Pair<AStack, FluidType>, CrystallizerRecipes.CrystallizerRecipe> map = (HashMap<Pair<AStack, FluidType>, CrystallizerRecipes.CrystallizerRecipe>) f
                .get(null);
            for (Map.Entry<Pair<AStack, FluidType>, CrystallizerRecipes.CrystallizerRecipe> e : map.entrySet()) {
                AStack in = e.getKey()
                    .getKey();
                FluidType fluid = e.getKey()
                    .getValue();
                CrystallizerRecipes.CrystallizerRecipe recipe = e.getValue();
                String sig = aKey(in) + "@" + fluid.getName() + ">" + outKey(recipe.output);
                Float prod = crystallizerProdMap.get(sig);
                if (prod != null) recipe.prod(prod);
            }
        } catch (Exception e) {
            Aletheia.LOG.error("Failed to override NTMC crystallizer productivity", e);
        }
    }

    private static Map<String, Integer> buildConstructionTierMap() {
        Map<String, Integer> m = new HashMap<>();

        putTier(m, 3, new AStack[] { ods(IRON.ingot()) }, new ItemStack(ModItems.plate_iron));
        putTier(m, 3, new AStack[] { ods(GOLD.ingot()) }, new ItemStack(ModItems.plate_gold));
        putTier(m, 3, new AStack[] { ods(TI.ingot()) }, new ItemStack(ModItems.plate_titanium));
        putTier(m, 3, new AStack[] { ods(NI.ingot()) }, new ItemStack(ModItems.plate_nickel));
        putTier(m, 3, new AStack[] { ods(AL.ingot()) }, new ItemStack(ModItems.plate_aluminium));
        putTier(m, 3, new AStack[] { ods(STEEL.ingot()) }, new ItemStack(ModItems.plate_steel));
        putTier(m, 3, new AStack[] { ods(STAINLESS.ingot()) }, new ItemStack(ModItems.plate_stainless));
        putTier(m, 3, new AStack[] { ods(PB.ingot()) }, new ItemStack(ModItems.plate_lead));
        putTier(m, 3, new AStack[] { ods(CU.ingot()) }, new ItemStack(ModItems.plate_copper));
        putTier(m, 3, new AStack[] { ods(GUNMETAL.ingot()) }, new ItemStack(ModItems.plate_gunmetal));
        putTier(m, 3, new AStack[] { ods(WEAPONSTEEL.ingot()) }, new ItemStack(ModItems.plate_weaponsteel));
        putTier(m, 3, new AStack[] { ods(BIGMT.ingot()) }, new ItemStack(ModItems.plate_saturnite));
        putTier(m, 3, new AStack[] { ods(DURA.ingot()) }, new ItemStack(ModItems.plate_dura_steel));
        putTier(m, 3, new AStack[] { ods(SA326.ingot()) }, new ItemStack(ModItems.plate_schrabidium));
        putTier(m, 3, new AStack[] { ods(CMB.ingot()) }, new ItemStack(ModItems.plate_combine_steel));

        putTier(m, 3, new AStack[] { ods(COAL.dust()) }, new ItemStack(Items.coal));
        putTier(
            m,
            3,
            new AStack[] { ods(COALCOKE.dust()) },
            new ItemStack(ModItems.coke, 1, EnumCokeType.COAL.ordinal()));
        putTier(
            m,
            3,
            new AStack[] { ods(LIGCOKE.dust()) },
            new ItemStack(ModItems.coke, 1, EnumCokeType.LIGNITE.ordinal()));
        putTier(
            m,
            3,
            new AStack[] { ods(PETCOKE.dust()) },
            new ItemStack(ModItems.coke, 1, EnumCokeType.PETROLEUM.ordinal()));
        putTier(m, 3, new AStack[] { ods(NETHERQUARTZ.dust()) }, new ItemStack(Items.quartz));
        putTier(m, 3, new AStack[] { ods(LAPIS.dust()) }, new ItemStack(Items.dye, 1, 4));
        putTier(m, 3, new AStack[] { ods(DIAMOND.dust()) }, new ItemStack(Items.diamond));
        putTier(m, 3, new AStack[] { ods(EMERALD.dust()) }, new ItemStack(Items.emerald));

        putTier(
            m,
            2,
            new AStack[] { ods(IRON.ingot(), 8), ods(CU.plate(), 4), cs(ModItems.motor, 0),
                cs(ModItems.circuit, 4, EnumCircuitType.VACUUM_TUBE.ordinal()) },
            new ItemStack(ModBlocks.machine_assembly_machine));
        putTier(
            m,
            3,
            new AStack[] { ods(KEY_COBBLESTONE, 8), ods(KEY_PLANKS, 16), ods(CU.plate(), 8), ods(PB.pipe(), 2) },
            new ItemStack(ModBlocks.pump_steam));
        putTier(
            m,
            3,
            new AStack[] { cs(Blocks.stonebrick, 8), ods(STEEL.plate(), 16), ods(PB.pipe(), 4), cs(ModItems.motor, 0),
                cs(ModItems.circuit, 4, EnumCircuitType.VACUUM_TUBE.ordinal()) },
            new ItemStack(ModBlocks.pump_electric));
        putTier(
            m,
            2,
            new AStack[] { cs(Blocks.furnace, 0), ods(STEEL.plate(), 8), ods(CU.ingot(), 8) },
            new ItemStack(ModBlocks.heater_firebox));
        putTier(
            m,
            2,
            new AStack[] { cs(ModItems.ingot_firebrick, 16), ods(STEEL.plate(), 4), ods(CU.ingot(), 8) },
            new ItemStack(ModBlocks.heater_oven));
        putTier(
            m,
            2,
            new AStack[] { ods(KEY_STONE, 8), ods(STEEL.plate(), 2), ods(IRON.ingot(), 4) },
            new ItemStack(ModBlocks.machine_ashpit));
        putTier(
            m,
            2,
            new AStack[] { ods(ANY_PLASTIC.ingot(), 4), ods(CU.ingot(), 8), ods(STEEL.plate(), 8),
                cs(ModItems.coil_tungsten, 8), cs(ModItems.circuit, 1, EnumCircuitType.BASIC.ordinal()) },
            new ItemStack(ModBlocks.heater_electric));
        putTier(
            m,
            3,
            new AStack[] { ods(RUBBER.ingot(), 4), ods(CU.ingot(), 16), ods(STEEL.plate(), 16), ods(STEEL.pipe(), 3) },
            new ItemStack(ModBlocks.heater_heatex));
        putTier(
            m,
            2,
            new AStack[] { cs(Blocks.stonebrick, 16), ods(IRON.ingot(), 4), ods(STEEL.plate(), 16), ods(CU.ingot(), 8),
                cs(ModBlocks.steel_grate, 16) },
            new ItemStack(ModBlocks.furnace_steel));
        putTier(
            m,
            2,
            new AStack[] { cs(Blocks.stonebrick, 8), ods(KEY_LOG, 16), ods(CU.plateCast(), 2), ods(KEY_BRICK, 16) },
            new ItemStack(ModBlocks.furnace_combination));
        putTier(
            m,
            2,
            new AStack[] { cs(Blocks.stonebrick, 8), cs(ModItems.ingot_firebrick, 16), ods(IRON.ingot(), 4),
                ods(CU.plate(), 8) },
            new ItemStack(ModBlocks.machine_rotary_furnace));
        putTier(
            m,
            2,
            new AStack[] { ods(KEY_PLANKS, 16), ods(STEEL.plate(), 6), ods(CU.ingot(), 8), cs(ModItems.coil_copper, 4),
                cs(ModItems.gear_large, 0) },
            new ItemStack(ModBlocks.machine_stirling));
        putTier(
            m,
            2,
            new AStack[] { ods(STEEL.plate(), 16), ods(BE.ingot(), 6), ods(CU.ingot(), 8), cs(ModItems.coil_gold, 16),
                cs(ModItems.gear_large, 1) },
            new ItemStack(ModBlocks.machine_stirling_steel));
        putTier(
            m,
            2,
            new AStack[] { cs(ModBlocks.reinforced_stone, 16), ods(STEEL.plate(), 12), ods(STEEL.shell(), 2),
                cs(ModItems.coil_copper, 4), cs(ModItems.gear_large, 0) },
            new ItemStack(ModBlocks.machine_steam_engine));
        putTier(
            m,
            2,
            new AStack[] { ods(KEY_PLANKS, 16), ods(STEEL.plate(), 6), ods(CU.ingot(), 8), ods(IRON.ingot(), 4),
                cs(ModItems.sawblade) },
            new ItemStack(ModBlocks.machine_sawmill));
        putTier(
            m,
            2,
            new AStack[] { cs(ModItems.ingot_firebrick, 20), ods(CU.ingot(), 8), ods(STEEL.plate(), 8) },
            new ItemStack(ModBlocks.machine_crucible));
        putTier(
            m,
            2,
            new AStack[] { ods(STEEL.ingot(), 4), ods(CU.plate(), 16), cs(ModItems.plate_polymer, 8) },
            new ItemStack(ModBlocks.machine_boiler));
        putTier(
            m,
            2,
            new AStack[] { ods(STEEL.plateCast(), 2), cs(ModItems.coil_copper, 4), ods(W.bolt(), 4),
                cs(ModItems.circuit, 2, EnumCircuitType.VACUUM_TUBE.ordinal()) },
            new ItemStack(ModBlocks.machine_soldering_station));
        putTier(
            m,
            2,
            new AStack[] { ods(STEEL.plateCast(), 4), ods(W.ingot(), 8), cs(ModBlocks.machine_transformer, 0),
                cs(ModItems.arc_electrode, 2) },
            new ItemStack(ModBlocks.machine_arc_welder));
        putTier(
            m,
            3,
            new AStack[] { ods(STEEL.plateCast(), 8), ods(CU.ingot(), 8), ods(ANY_PLASTIC.ingot(), 4) },
            new ItemStack(ModBlocks.machine_industrial_boiler));
        putTier(
            m,
            2,
            new AStack[] { ods(STEEL.plate(), 4), ods(IRON.ingot(), 12), ods(CU.ingot(), 2),
                cs(ModItems.circuit, 2, EnumCircuitType.VACUUM_TUBE.ordinal()), cs(ModItems.sawblade) },
            new ItemStack(ModBlocks.machine_autosaw));
        putTier(
            m,
            2,
            new AStack[] { ods(STEEL.plate(), 8), ods(IRON.ingot(), 12), ods(CU.ingot(), 2),
                cs(ModItems.circuit, 1, EnumCircuitType.VACUUM_TUBE.ordinal()) },
            new ItemStack(ModBlocks.machine_thresher));
        putTier(
            m,
            3,
            new AStack[] { cs(ModBlocks.brick_concrete, 64), cs(Blocks.iron_bars, 128),
                cs(ModBlocks.machine_condenser, 4) },
            new ItemStack(ModBlocks.machine_tower_small));
        putTier(
            m,
            4,
            new AStack[] { cs(ModBlocks.concrete_smooth, 128), cs(ModBlocks.steel_scaffold, 32),
                cs(ModBlocks.machine_condenser, 16), ods(STEEL.pipe(), 8) },
            new ItemStack(ModBlocks.machine_tower_large));
        putTier(
            m,
            2,
            new AStack[] { cs(Items.bone, 16), cs(Items.leather, 4), cs(Items.feather, 24) },
            new ItemStack(ModItems.wings_limp));
        putTier(
            m,
            2,
            new AStack[] { cs(ModItems.sulfur, 12), ods(STEEL.shell(), 4), ods(CU.plateCast(), 6),
                cs(ModItems.circuit, 2, EnumCircuitType.BASIC.ordinal()) },
            new ItemStack(ModBlocks.machine_deuterium_extractor));
        putTier(
            m,
            2,
            new AStack[] { ods(STEEL.shell(), 4), ods(STAINLESS.plate(), 4), cs(ModBlocks.concrete_smooth, 4),
                cs(ModItems.turbine_titanium, 1) },
            new ItemStack(ModBlocks.machine_atmo_vent));
        putTier(
            m,
            2,
            new AStack[] { ods(STEEL.pipe(), 6), ods(STAINLESS.plateCast(), 8), ods(ANY_PLASTIC.ingot(), 4),
                cs(ModItems.turbine_titanium, 1) },
            new ItemStack(ModBlocks.machine_atmo_emitter));
        putTier(
            m,
            2,
            new AStack[] { ods(ANY_CONCRETE.any(), 2), cs(ModBlocks.steel_scaffold, 8), cs(ModItems.plate_polymer, 8),
                cs(ModItems.coil_copper, 4) },
            new ItemStack(ModBlocks.red_pylon_large));
        putTier(
            m,
            2,
            new AStack[] { ods(ANY_CONCRETE.any(), 8), ods(STEEL.ingot(), 8), cs(ModItems.plate_polymer, 12),
                cs(ModItems.coil_copper, 8) },
            new ItemStack(ModBlocks.substation, 2));
        putTier(
            m,
            2,
            new AStack[] { ods(STEEL.plate(), 4), cs(Blocks.brick_block, 16), cs(ModBlocks.steel_grate, 2) },
            new ItemStack(ModBlocks.chimney_brick));
        putTier(
            m,
            3,
            new AStack[] { ods(STEEL.plate(), 16), ods(ANY_CONCRETE.any(), 64), cs(ModBlocks.steel_grate, 4),
                cs(ModItems.filter_coal, 4) },
            new ItemStack(ModBlocks.chimney_industrial));
        putTier(
            m,
            3,
            new AStack[] { cs(ModItems.tank_steel, 1), ods(PB.plate(), 2), cs(ModItems.nuclear_waste, 10) },
            new ItemStack(ModBlocks.yellow_barrel));
        putTier(
            m,
            3,
            new AStack[] { cs(ModItems.tank_steel, 1), ods(PB.plate(), 2), cs(ModItems.nuclear_waste_vitrified, 10) },
            new ItemStack(ModBlocks.vitrified_barrel));
        putTier(
            m,
            3,
            new AStack[] { cs(ModItems.man_core, 1), ods(BE.ingot(), 4), cs(ModItems.screwdriver, 1) },
            new ItemStack(ModItems.demon_core_open));
        putTier(
            m,
            3,
            new AStack[] { ods(DESH.ingot(), 4), ods(ANY_PLASTIC.dust(), 2), ods(DURA.ingot(), 1) },
            new ItemStack(ModItems.plate_desh, 4));
        putTier(
            m,
            4,
            new AStack[] { cs(ModItems.nugget_bismuth, 2), ods(U238.billet(), 2), ods(NB.dust(), 1) },
            new ItemStack(ModItems.plate_bismuth, 1));
        putTier(
            m,
            2,
            new AStack[] { ods(TI.plate(), 2), ods(STEEL.ingot(), 1), ods(STEEL.bolt(), 4) },
            new ItemStack(ModItems.plate_armor_titanium));
        putTier(
            m,
            3,
            new AStack[] { ods(IRON.plate(), 6), ods(NB.ingot(), 1), cs(ModItems.plate_armor_titanium, 1) },
            new ItemStack(ModItems.plate_armor_ajr, 2));
        putTier(
            m,
            4,
            new AStack[] { ods(DURA.plate(), 4), cs(ModItems.plate_armor_titanium, 1), ods(W.wireFine(), 8) },
            new ItemStack(ModItems.plate_armor_hev));
        putTier(
            m,
            4,
            new AStack[] { ods(WEAPONSTEEL.plate(), 4), ods(STAR.ingot(), 1), ods(MAGTUNG.wireFine(), 8) },
            new ItemStack(ModItems.plate_armor_lunar));
        putTier(
            m,
            2,
            new AStack[] { cs(ModBlocks.glass_quartz, 3), cs(ModItems.pill_herbal, 2), cs(ModItems.powder_magic, 2) },
            new ItemStack(ModItems.flask_infusion, 1, EnumInfusion.NITAN.ordinal()));
        putTier(
            m,
            5,
            new AStack[] { cs(ModItems.missile_doomsday_rusted, 1), ods(ANY_HARDPLASTIC.ingot(), 8),
                ods(AL.plateWelded(), 2), ods(PU239.billet(), 3) },
            new ItemStack(ModItems.missile_doomsday));
        putTier(m, 4, new AStack[] { cs(ModItems.ingot_u233, 1) }, new ItemStack(ModItems.plate_fuel_u233));
        putTier(m, 4, new AStack[] { cs(ModItems.ingot_u235, 1) }, new ItemStack(ModItems.plate_fuel_u235));
        putTier(m, 4, new AStack[] { cs(ModItems.ingot_mox_fuel, 1) }, new ItemStack(ModItems.plate_fuel_mox));
        putTier(m, 4, new AStack[] { cs(ModItems.ingot_pu239, 1) }, new ItemStack(ModItems.plate_fuel_pu239));
        putTier(m, 4, new AStack[] { cs(ModItems.ingot_schrabidium, 1) }, new ItemStack(ModItems.plate_fuel_sa326));
        putTier(m, 4, new AStack[] { cs(ModItems.billet_ra226be, 1) }, new ItemStack(ModItems.plate_fuel_ra226be));
        putTier(m, 4, new AStack[] { cs(ModItems.billet_pu238be, 1) }, new ItemStack(ModItems.plate_fuel_pu238be));

        putTier(m, 1, new AStack[] { cs(ModBlocks.deco_aluminium, 1) }, new ItemStack(ModBlocks.deco_aluminium, 1));
        putTier(m, 1, new AStack[] { cs(ModBlocks.deco_beryllium, 1) }, new ItemStack(ModBlocks.deco_beryllium, 1));
        putTier(m, 1, new AStack[] { cs(ModBlocks.deco_lead, 1) }, new ItemStack(ModBlocks.deco_lead, 1));
        putTier(m, 1, new AStack[] { cs(ModBlocks.deco_red_copper, 1) }, new ItemStack(ModBlocks.deco_red_copper, 1));
        putTier(m, 1, new AStack[] { cs(ModBlocks.deco_steel, 1) }, new ItemStack(ModBlocks.deco_steel, 1));
        putTier(m, 1, new AStack[] { cs(ModBlocks.deco_titanium, 1) }, new ItemStack(ModBlocks.deco_titanium, 1));
        putTier(m, 1, new AStack[] { cs(ModBlocks.deco_tungsten, 1) }, new ItemStack(ModBlocks.deco_tungsten, 1));
        putTier(m, 1, new AStack[] { cs(ModBlocks.deco_stainless, 1) }, new ItemStack(ModBlocks.deco_stainless, 1));

        putTier(m, 1, new AStack[] { cs(ModItems.coil_copper, 2) }, new ItemStack(ModItems.coil_copper_torus));
        putTier(m, 1, new AStack[] { cs(ModItems.coil_gold, 2) }, new ItemStack(ModItems.coil_gold_torus));
        putTier(
            m,
            1,
            new AStack[] { ods(IRON.plate(), 2), cs(ModItems.coil_copper, 0), cs(ModItems.coil_copper_torus, 0) },
            new ItemStack(ModItems.motor, 2));
        putTier(
            m,
            3,
            new AStack[] { cs(ModItems.motor, 0), ods(ANY_PLASTIC.ingot(), 2), ods(DESH.ingot(), 2),
                ods(GOLD.wireDense(), 0) },
            new ItemStack(ModItems.motor_desh, 1));

        putTier(
            m,
            2,
            new AStack[] { cs(Blocks.stonebrick, 16), cs(ModItems.ingot_firebrick, 16), ods(CU.ingot(), 8),
                ods(CU.plate(), 8) },
            new ItemStack(ModBlocks.machine_annihilator));

        putTier(m, 1, new AStack[] { cs(ModItems.stamp_stone_flat, 1) }, new ItemStack(ModItems.stamp_stone_plate));
        putTier(m, 1, new AStack[] { cs(ModItems.stamp_stone_flat, 1) }, new ItemStack(ModItems.stamp_stone_wire));
        putTier(m, 1, new AStack[] { cs(ModItems.stamp_stone_flat, 1) }, new ItemStack(ModItems.stamp_stone_circuit));
        putTier(m, 1, new AStack[] { cs(ModItems.stamp_iron_flat, 1) }, new ItemStack(ModItems.stamp_iron_plate));
        putTier(m, 1, new AStack[] { cs(ModItems.stamp_iron_flat, 1) }, new ItemStack(ModItems.stamp_iron_wire));
        putTier(m, 1, new AStack[] { cs(ModItems.stamp_iron_flat, 1) }, new ItemStack(ModItems.stamp_iron_circuit));
        putTier(m, 2, new AStack[] { cs(ModItems.stamp_steel_flat, 1) }, new ItemStack(ModItems.stamp_steel_plate));
        putTier(m, 2, new AStack[] { cs(ModItems.stamp_steel_flat, 1) }, new ItemStack(ModItems.stamp_steel_wire));
        putTier(m, 2, new AStack[] { cs(ModItems.stamp_steel_flat, 1) }, new ItemStack(ModItems.stamp_steel_circuit));
        putTier(
            m,
            2,
            new AStack[] { cs(ModItems.stamp_titanium_flat, 1) },
            new ItemStack(ModItems.stamp_titanium_plate));
        putTier(
            m,
            2,
            new AStack[] { cs(ModItems.stamp_titanium_flat, 1) },
            new ItemStack(ModItems.stamp_titanium_wire));
        putTier(
            m,
            2,
            new AStack[] { cs(ModItems.stamp_titanium_flat, 1) },
            new ItemStack(ModItems.stamp_titanium_circuit));
        putTier(
            m,
            2,
            new AStack[] { cs(ModItems.stamp_obsidian_flat, 1) },
            new ItemStack(ModItems.stamp_obsidian_plate));
        putTier(
            m,
            2,
            new AStack[] { cs(ModItems.stamp_obsidian_flat, 1) },
            new ItemStack(ModItems.stamp_obsidian_wire));
        putTier(
            m,
            2,
            new AStack[] { cs(ModItems.stamp_obsidian_flat, 1) },
            new ItemStack(ModItems.stamp_obsidian_circuit));
        putTier(m, 3, new AStack[] { cs(ModItems.stamp_desh_flat, 1) }, new ItemStack(ModItems.stamp_desh_plate));
        putTier(m, 3, new AStack[] { cs(ModItems.stamp_desh_flat, 1) }, new ItemStack(ModItems.stamp_desh_wire));
        putTier(m, 3, new AStack[] { cs(ModItems.stamp_desh_flat, 1) }, new ItemStack(ModItems.stamp_desh_circuit));

        putTier(
            m,
            2,
            new AStack[] { cs(ModItems.stamp_iron_flat, 0), ods(GUNMETAL.ingot(), 2) },
            new ItemStack(ModItems.stamp_9));
        putTier(
            m,
            2,
            new AStack[] { cs(ModItems.stamp_iron_flat, 0), ods(GUNMETAL.ingot(), 2) },
            new ItemStack(ModItems.stamp_50));
        putTier(
            m,
            4,
            new AStack[] { cs(ModItems.stamp_desh_flat, 0), ods(WEAPONSTEEL.ingot(), 4) },
            new ItemStack(ModItems.stamp_desh_9));
        putTier(
            m,
            4,
            new AStack[] { cs(ModItems.stamp_desh_flat, 0), ods(WEAPONSTEEL.ingot(), 4) },
            new ItemStack(ModItems.stamp_desh_50));
        putTier(
            m,
            1,
            new AStack[] { cs(ModItems.mold_base, 0), ods(IRON.ingot(), 2) },
            new ItemStack(ModItems.mold, 1, 16));
        putTier(
            m,
            1,
            new AStack[] { cs(ModItems.mold_base, 0), ods(IRON.ingot(), 2) },
            new ItemStack(ModItems.mold, 1, 17));
        putTier(
            m,
            2,
            new AStack[] { cs(ModItems.mold_base, 0), ods(STEEL.ingot(), 4) },
            new ItemStack(ModItems.mold, 1, 22));
        putTier(
            m,
            2,
            new AStack[] { cs(ModItems.mold_base, 0), ods(STEEL.ingot(), 4) },
            new ItemStack(ModItems.mold, 1, 23));
        putTier(
            m,
            2,
            new AStack[] { cs(ModItems.mold_base, 0), ods(STEEL.ingot(), 4) },
            new ItemStack(ModItems.mold, 1, 24));
        putTier(
            m,
            2,
            new AStack[] { cs(ModItems.mold_base, 0), ods(STEEL.ingot(), 4) },
            new ItemStack(ModItems.mold, 1, 25));
        putTier(
            m,
            2,
            new AStack[] { cs(ModItems.mold_base, 0), ods(STEEL.ingot(), 4) },
            new ItemStack(ModItems.mold, 1, 26));
        putTier(
            m,
            2,
            new AStack[] { cs(ModItems.mold_base, 0), ods(STEEL.ingot(), 4) },
            new ItemStack(ModItems.mold, 1, 27));
        putTier(
            m,
            2,
            new AStack[] { cs(ModItems.mold_base, 0), ods(STEEL.ingot(), 4) },
            new ItemStack(ModItems.mold, 1, 28));

        putTier(m, 1, new AStack[] { cs(ModBlocks.deco_titanium, 0) }, new ItemStack(ModItems.ingot_titanium, 1));
        putTier(m, 1, new AStack[] { cs(ModBlocks.deco_red_copper, 0) }, new ItemStack(ModItems.ingot_red_copper, 1));
        putTier(m, 1, new AStack[] { cs(ModBlocks.deco_tungsten, 0) }, new ItemStack(ModItems.ingot_tungsten, 1));
        putTier(m, 1, new AStack[] { cs(ModBlocks.deco_aluminium, 0) }, new ItemStack(ModItems.ingot_aluminium, 1));
        putTier(m, 1, new AStack[] { cs(ModBlocks.deco_steel, 0) }, new ItemStack(ModItems.ingot_steel, 1));
        putTier(m, 1, new AStack[] { cs(ModBlocks.deco_rusty_steel, 0) }, new ItemStack(ModItems.ingot_steel, 1));
        putTier(m, 1, new AStack[] { cs(ModBlocks.deco_lead, 0) }, new ItemStack(ModItems.ingot_lead, 1));
        putTier(m, 1, new AStack[] { cs(ModBlocks.deco_beryllium, 0) }, new ItemStack(ModItems.ingot_beryllium, 1));
        putTier(m, 1, new AStack[] { cs(ModBlocks.deco_stainless, 0) }, new ItemStack(ModItems.ingot_stainless, 1));

        return m;
    }

    private static Map<String, Integer> buildSmithingTierMap() {
        Map<String, Integer> m = new HashMap<>();

        putSmithing(m, 1, cs(ModBlocks.anvil_iron, 0), ods(STEEL.ingot(), 10), new ItemStack(ModBlocks.anvil_steel, 1));
        putSmithing(m, 1, cs(ModBlocks.anvil_iron, 0), ods(DESH.ingot(), 10), new ItemStack(ModBlocks.anvil_desh, 1));
        putSmithing(
            m,
            1,
            cs(ModBlocks.anvil_iron, 0),
            ods(BIGMT.ingot(), 10),
            new ItemStack(ModBlocks.anvil_saturnite, 1));
        putSmithing(
            m,
            1,
            cs(ModBlocks.anvil_iron, 0),
            cs(ModItems.ingot_ferrouranium, 10),
            new ItemStack(ModBlocks.anvil_ferrouranium, 1));
        putSmithing(
            m,
            1,
            cs(ModBlocks.anvil_iron, 0),
            ods(BBRONZE.ingot(), 10),
            new ItemStack(ModBlocks.anvil_bismuth_bronze, 1));
        putSmithing(
            m,
            1,
            cs(ModBlocks.anvil_iron, 0),
            ods(ABRONZE.ingot(), 10),
            new ItemStack(ModBlocks.anvil_arsenic_bronze, 1));
        putSmithing(
            m,
            1,
            cs(ModBlocks.anvil_iron, 0),
            ods(SBD.ingot(), 10),
            new ItemStack(ModBlocks.anvil_schrabidate, 1));
        putSmithing(m, 1, cs(ModBlocks.anvil_iron, 0), ods(DNT.ingot(), 10), new ItemStack(ModBlocks.anvil_dnt, 1));
        putSmithing(
            m,
            1,
            cs(ModBlocks.anvil_iron, 0),
            ods(OSMIRIDIUM.ingot(), 10),
            new ItemStack(ModBlocks.anvil_osmiridium, 1));
        putSmithing(m, 1, cs(ModBlocks.anvil_lead, 0), ods(STEEL.ingot(), 10), new ItemStack(ModBlocks.anvil_steel, 1));
        putSmithing(m, 1, cs(ModBlocks.anvil_lead, 0), ods(DESH.ingot(), 10), new ItemStack(ModBlocks.anvil_desh, 1));
        putSmithing(
            m,
            1,
            cs(ModBlocks.anvil_lead, 0),
            ods(BIGMT.ingot(), 10),
            new ItemStack(ModBlocks.anvil_saturnite, 1));
        putSmithing(
            m,
            1,
            cs(ModBlocks.anvil_lead, 0),
            cs(ModItems.ingot_ferrouranium, 10),
            new ItemStack(ModBlocks.anvil_ferrouranium, 1));
        putSmithing(
            m,
            1,
            cs(ModBlocks.anvil_lead, 0),
            ods(BBRONZE.ingot(), 10),
            new ItemStack(ModBlocks.anvil_bismuth_bronze, 1));
        putSmithing(
            m,
            1,
            cs(ModBlocks.anvil_lead, 0),
            ods(ABRONZE.ingot(), 10),
            new ItemStack(ModBlocks.anvil_arsenic_bronze, 1));
        putSmithing(
            m,
            1,
            cs(ModBlocks.anvil_lead, 0),
            ods(SBD.ingot(), 10),
            new ItemStack(ModBlocks.anvil_schrabidate, 1));
        putSmithing(m, 1, cs(ModBlocks.anvil_lead, 0), ods(DNT.ingot(), 10), new ItemStack(ModBlocks.anvil_dnt, 1));
        putSmithing(
            m,
            1,
            cs(ModBlocks.anvil_lead, 0),
            ods(OSMIRIDIUM.ingot(), 10),
            new ItemStack(ModBlocks.anvil_osmiridium, 1));

        putSmithing(
            m,
            3,
            cs(ModItems.ingot_steel_dusted, 1),
            cs(ModItems.ingot_steel_dusted, 1),
            new ItemStack(ModItems.ingot_steel_dusted, 1, 1));
        putSmithing(
            m,
            3,
            cs(ModItems.ingot_chainsteel, 0),
            cs(ModItems.ingot_steel_dusted, 9),
            new ItemStack(ModItems.ingot_chainsteel, 1));
        putSmithing(
            m,
            3,
            cs(ModItems.ingot_meteorite, 0),
            cs(ModItems.ingot_meteorite, 0),
            new ItemStack(ModItems.ingot_meteorite_forged, 1));
        putSmithing(
            m,
            3,
            cs(ModItems.ingot_meteorite_forged, 0),
            cs(ModItems.ingot_meteorite_forged, 0),
            new ItemStack(ModItems.blade_meteorite, 1));
        putSmithing(
            m,
            3,
            cs(ModItems.meteorite_sword_seared, 0),
            cs(ModItems.ingot_meteorite_forged, 0),
            new ItemStack(ModItems.meteorite_sword_reforged, 1));

        putSmithing(m, 1, ods(CU.ingot(), 0), ods(ZI.ingot(), 0), new ItemStack(ModItems.ingot_gunmetal, 1));
        putSmithing(
            m,
            4,
            cs(ModItems.gem_alexandrite, 0),
            cs(ModItems.bottle_nuka, 0),
            new ItemStack(ModItems.flask_infusion, 1, EnumInfusion.SHIELD.ordinal()));

        return m;
    }

    private static Map<String, Float> buildCrystallizerProdMap() {
        Map<String, Float> m = new HashMap<>();

        putCryst(m, 0.05F, ods(COAL.ore()), Fluids.PEROXIDE, new ItemStack(ModItems.crystal_coal));
        putCryst(m, 0.05F, ods(IRON.ore()), Fluids.PEROXIDE, new ItemStack(ModItems.crystal_iron));
        putCryst(m, 0.05F, ods(GOLD.ore()), Fluids.PEROXIDE, new ItemStack(ModItems.crystal_gold));
        putCryst(m, 0.05F, ods(REDSTONE.ore()), Fluids.PEROXIDE, new ItemStack(ModItems.crystal_redstone));
        putCryst(m, 0.05F, ods(LAPIS.ore()), Fluids.PEROXIDE, new ItemStack(ModItems.crystal_lapis));
        putCryst(m, 0.05F, ods(DIAMOND.ore()), Fluids.PEROXIDE, new ItemStack(ModItems.crystal_diamond));
        putCryst(m, 0.05F, ods(U.ore()), Fluids.SULFURIC_ACID, new ItemStack(ModItems.crystal_uranium));
        putCryst(m, 0.05F, ods(PU.ore()), Fluids.SULFURIC_ACID, new ItemStack(ModItems.crystal_plutonium));
        putCryst(m, 0.05F, ods(TI.ore()), Fluids.SULFURIC_ACID, new ItemStack(ModItems.crystal_titanium));
        putCryst(m, 0.05F, ods(S.ore()), Fluids.PEROXIDE, new ItemStack(ModItems.crystal_sulfur));
        putCryst(m, 0.05F, ods(KNO.ore()), Fluids.PEROXIDE, new ItemStack(ModItems.crystal_niter));
        putCryst(m, 0.05F, ods(CU.ore()), Fluids.PEROXIDE, new ItemStack(ModItems.crystal_copper));
        putCryst(m, 0.05F, ods(W.ore()), Fluids.SULFURIC_ACID, new ItemStack(ModItems.crystal_tungsten));
        putCryst(m, 0.05F, ods(AL.ore()), Fluids.PEROXIDE, new ItemStack(ModItems.crystal_aluminium));
        putCryst(m, 0.05F, ods(F.ore()), Fluids.PEROXIDE, new ItemStack(ModItems.crystal_fluorite));
        putCryst(m, 0.05F, ods(BE.ore()), Fluids.PEROXIDE, new ItemStack(ModItems.crystal_beryllium));
        putCryst(m, 0.05F, ods(PB.ore()), Fluids.PEROXIDE, new ItemStack(ModItems.crystal_lead));
        putCryst(m, 0.05F, ods(P_RED.ore()), Fluids.PEROXIDE, new ItemStack(ModItems.crystal_phosphorus));
        putCryst(m, 0.05F, ods(SA326.ore()), Fluids.SULFURIC_ACID, new ItemStack(ModItems.crystal_schrabidium));
        putCryst(m, 0.05F, ods(LI.ore()), Fluids.SULFURIC_ACID, new ItemStack(ModItems.crystal_lithium));
        putCryst(m, 0.05F, ods(CO.ore()), Fluids.SULFURIC_ACID, new ItemStack(ModItems.crystal_cobalt));
        putCryst(m, 0.05F, ods(ZI.ore()), Fluids.NITRIC_ACID, new ItemStack(ModItems.crystal_zinc));
        putCryst(m, 0.05F, ods(NB.ore()), Fluids.SULFURIC_ACID, new ItemStack(ModItems.crystal_niobium));
        putCryst(m, 0.05F, ods("oreRareEarth"), Fluids.SULFURIC_ACID, new ItemStack(ModItems.crystal_rare));
        putCryst(m, 0.05F, ods("oreCinnabar"), Fluids.PEROXIDE, new ItemStack(ModItems.crystal_cinnebar));
        putCryst(
            m,
            0.05F,
            cs(ModBlocks.ore_nether_fire, 0),
            Fluids.PEROXIDE,
            new ItemStack(ModItems.crystal_phosphorus));
        putCryst(m, 0.05F, cs(ModBlocks.ore_tikite, 0), Fluids.SULFURIC_ACID, new ItemStack(ModItems.crystal_trixite));
        putCryst(m, 0.05F, cs(ModBlocks.gravel_diamond, 0), Fluids.PEROXIDE, new ItemStack(ModItems.crystal_diamond));
        putCryst(m, 0.05F, cs(ModItems.crystal_mineral, 0), Fluids.PEROXIDE, new ItemStack(ModItems.crystal_diamond));
        putCryst(m, 0.05F, ods(SRN.ingot(), 0), Fluids.PEROXIDE, new ItemStack(ModItems.crystal_schraranium));

        putCryst(m, 0.25F, ods(REDSTONE.block(), 0), Fluids.PEROXIDE, new ItemStack(ModItems.ingot_mercury));
        putCryst(m, 0.25F, ods(CINNABAR.crystal(), 0), Fluids.PEROXIDE, new ItemStack(ModItems.ingot_mercury, 3));
        putCryst(m, 0.25F, ods(BORAX.dust(), 0), Fluids.SULFURIC_ACID, new ItemStack(ModItems.powder_boron_tiny, 3));
        putCryst(m, 0.25F, cs(Items.rotten_flesh, 0), Fluids.PEROXIDE, new ItemStack(Items.leather));
        putCryst(m, 0.25F, cs(ModBlocks.stone_gneiss, 0), Fluids.PEROXIDE, new ItemStack(ModItems.powder_lithium));
        putCryst(m, 0.25F, cs(ModItems.powder_sawdust, 0), Fluids.NITROGLYCERIN, new ItemStack(ModItems.cordite));

        putCryst(
            m,
            0.3F,
            cs(ModItems.powder_meteorite, 0),
            Fluids.PEROXIDE,
            new ItemStack(ModItems.fragment_meteorite));
        putCryst(m, 0.3F, cs(ModItems.scrap_oil, 0), Fluids.RADIOSOLVENT, new ItemStack(ModItems.nugget_arsenic));

        putCryst(m, 0.05F, cs(ModItems.coal_infernal, 0), Fluids.PEROXIDE, new ItemStack(ModItems.solid_fuel));
        putCryst(m, 0.05F, cs(Items.dye, 15), Fluids.SULFURIC_ACID, new ItemStack(Items.slime_ball, 4));
        putCryst(m, 0.05F, cs(Items.bone, 0), Fluids.SULFURIC_ACID, new ItemStack(Items.slime_ball, 16));
        putCryst(m, 0.05F, cs(ModItems.powder_semtex_mix, 0), Fluids.PEROXIDE, new ItemStack(ModItems.ingot_semtex));
        putCryst(m, 0.05F, cs(ModItems.powder_desh_ready, 0), Fluids.PEROXIDE, new ItemStack(ModItems.ingot_desh));
        putCryst(m, 0.05F, ods(CD.dust(), 0), Fluids.FISHOIL, new ItemStack(ModItems.ingot_rubber, 16));
        putCryst(
            m,
            0.05F,
            cs(ModItems.powder_impure_osmiridium, 0),
            Fluids.SCHRABIDIC,
            new ItemStack(ModItems.crystal_osmiridium));

        return m;
    }
}
