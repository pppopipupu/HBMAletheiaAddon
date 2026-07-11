package com.pppopipupu.aletheia;

import java.lang.reflect.Method;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.EnumHelper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hbm.inventory.FluidContainer;
import com.hbm.inventory.FluidContainerRegistry;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.Fluids.CD_Canister;
import com.hbm.inventory.fluid.trait.FT_Combustible;
import com.hbm.inventory.fluid.trait.FT_Combustible.FuelGrade;
import com.hbm.inventory.fluid.trait.FT_Coolable;
import com.hbm.inventory.fluid.trait.FT_Coolable.CoolingType;
import com.hbm.inventory.fluid.trait.FT_VentRadiation;
import com.hbm.render.util.EnumSymbol;
import com.pppopipupu.aletheia.block.AletheiaBlocks;
import com.pppopipupu.aletheia.entity.EntityDisperserCanisterAletheia;
import com.pppopipupu.aletheia.item.AletheiaItems;
import com.pppopipupu.aletheia.recipe.AletheiaRecipes;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLMissingMappingsEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(
    modid = Aletheia.MODID,
    version = Tags.VERSION,
    name = "Aletheia",
    acceptedMinecraftVersions = "[1.7.10]",
    dependencies = "required-after:hbm")
public class Aletheia {

    public static final String MODID = "aletheia";
    public static final Logger LOG = LogManager.getLogger(MODID);

    @Mod.Instance(Aletheia.MODID)
    public static Aletheia instance;

    @SidedProxy(clientSide = "com.pppopipupu.aletheia.ClientProxy", serverSide = "com.pppopipupu.aletheia.CommonProxy")
    public static CommonProxy proxy;

    public static FluidType fluid_qgp;

    public static com.hbm.items.weapon.sedna.BulletConfig energy_pppop;
    public static com.hbm.items.weapon.sedna.BulletConfig energy_pppop_steel;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        try {
            EnumHelper.addEnum(FuelGrade.class, "QGP", new Class[] { String.class }, new Object[] { "Quark-Gluon" });
            LOG.info("Successfully added FuelGrade QGP via EnumHelper!");
        } catch (Exception e) {
            LOG.error("Failed to add FuelGrade QGP!", e);
        }

        fluid_qgp = new FluidType("QGP", 0xFF5500, 4, 0, 4, EnumSymbol.RADIATION).setTemp(10000000)
            .addContainers(new CD_Canister(0xFF5500))
            .addTraits(Fluids.LIQUID, Fluids.PLASMA, Fluids.EXPLOSIVE, Fluids.LEADCON, new FT_VentRadiation(0.5F));

        fluid_qgp.addTraits(
            new FT_Coolable(Fluids.NONE, 1, 0, 12500000).setEff(CoolingType.TURBINE, 3.0D)
                .setEff(CoolingType.HEATEXCHANGER, 3.0D));

        try {
            Method mReg = Fluids.class.getDeclaredMethod("registerSelf", FluidType.class);
            mReg.setAccessible(true);
            mReg.invoke(null, fluid_qgp);
            Fluids.metaOrder.add(fluid_qgp);

            Method mCalculated = Fluids.class.getDeclaredMethod(
                "registerCalculatedFuel",
                FluidType.class,
                long.class,
                double.class,
                FuelGrade.class);
            mCalculated.setAccessible(true);

            long balefireVal = 0L;
            FT_Combustible balefireComb = Fluids.BALEFIRE.getTrait(FT_Combustible.class);
            if (balefireComb != null) {
                balefireVal = balefireComb.getCombustionEnergy();
            }
            FuelGrade fuelGradeVal;
            try {
                fuelGradeVal = FuelGrade.valueOf("QGP");
            } catch (IllegalArgumentException exVal) {
                fuelGradeVal = FuelGrade.HIGH;
            }
            mCalculated.invoke(null, fluid_qgp, balefireVal * 500L, 3.0, fuelGradeVal);
        } catch (Exception e) {
            LOG.error("Failed to register QGP fluid traits!", e);
        }

        AletheiaBlocks.init();
        AletheiaItems.init();

        EntityRegistry.registerModEntity(
            EntityDisperserCanisterAletheia.class,
            "entity_disperser_canister",
            1001,
            Aletheia.instance,
            80,
            3,
            true);

        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);

        FluidType[] fluidsList = Fluids.getAll();
        for (int i = 1; i < fluidsList.length; i++) {
            FluidType type = fluidsList[i];
            if (type.hasNoContainer()) continue;
            if (type.isDispersable()) {
                FluidContainerRegistry.registerContainer(
                    new FluidContainer(
                        new ItemStack(AletheiaItems.disperser_canister, 1, i),
                        new ItemStack(AletheiaItems.disperser_canister_empty),
                        Fluids.fromID(i),
                        2000));
                FluidContainerRegistry.registerContainer(
                    new FluidContainer(
                        new ItemStack(AletheiaItems.glyphid_gland, 1, i),
                        new ItemStack(AletheiaItems.glyphid_gland_empty),
                        Fluids.fromID(i),
                        4000));
            }
        }

        AletheiaRecipes.init();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        proxy.serverStarting(event);
    }

    @Mod.EventHandler
    public void onMissingMappings(FMLMissingMappingsEvent event) {
        for (FMLMissingMappingsEvent.MissingMapping mapping : event.get()) {
            if (mapping.type == GameRegistry.Type.ITEM) {
                if ("hbm:bucket_qgp".equals(mapping.name)) {
                    mapping.remap(AletheiaItems.bucket_qgp);
                } else if ("hbm:qgp_mining_bomb".equals(mapping.name)) {
                    mapping.remap(AletheiaItems.qgp_mining_bomb);
                } else if ("hbm:upgrade_ultimate".equals(mapping.name)) {
                    mapping.remap(AletheiaItems.upgrade_ultimate);
                } else if ("hbm:gun_pppop".equals(mapping.name)) {
                    mapping.remap(AletheiaItems.gun_pppop);
                } else if ("hbm:ams_muzzle".equals(mapping.name)) {
                    mapping.remap(AletheiaItems.ams_muzzle);
                } else if ("hbm:ams_focus_limiter".equals(mapping.name)) {
                    mapping.remap(AletheiaItems.ams_focus_limiter);
                } else if ("hbm:ams_focus_booster".equals(mapping.name)) {
                    mapping.remap(AletheiaItems.ams_focus_booster);
                }
            } else if (mapping.type == GameRegistry.Type.BLOCK) {
                if ("hbm:qgp_block".equals(mapping.name)) {
                    mapping.remap(AletheiaBlocks.qgp_block);
                } else if ("hbm:ams_base".equals(mapping.name)) {
                    mapping.remap(AletheiaBlocks.ams_base);
                } else if ("hbm:ams_emitter".equals(mapping.name)) {
                    mapping.remap(AletheiaBlocks.ams_emitter);
                } else if ("hbm:ams_limiter".equals(mapping.name)) {
                    mapping.remap(AletheiaBlocks.ams_limiter);
                }
            }
        }
    }
}
