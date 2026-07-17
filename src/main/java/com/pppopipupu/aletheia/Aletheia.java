package com.pppopipupu.aletheia;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hbm.hazard.HazardData;
import com.hbm.hazard.HazardRegistry;
import com.hbm.hazard.HazardSystem;
import com.hbm.inventory.FluidContainer;
import com.hbm.inventory.FluidContainerRegistry;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.items.ModItems;
import com.hbm.packet.PacketDispatcher;
import com.hbm.util.CompatExternal;
import com.pppopipupu.aletheia.block.AletheiaBlocks;
import com.pppopipupu.aletheia.fluid.AletheiaFluids;
import com.pppopipupu.aletheia.item.AletheiaItems;
import com.pppopipupu.aletheia.item.ItemZirnoxRodAletheia;
import com.pppopipupu.aletheia.machine.agrichemplant.AgriChemicalPlantRecipeHandler;
import com.pppopipupu.aletheia.packet.AlienJellyBeamPacket;
import com.pppopipupu.aletheia.packet.QGPDistortionPacket;
import com.pppopipupu.aletheia.recipe.AletheiaRecipes;
import com.pppopipupu.aletheia.recipe.ntmc.AletheiaRecipesNtmcOverrides;
import com.pppopipupu.aletheia.stats.AletheiaAchievements;
import com.pppopipupu.aletheia.weapon.AletheiaBullets;

import codechicken.nei.api.API;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.relauncher.Side;

@Mod(
    modid = Aletheia.MODID,
    version = Tags.VERSION,
    name = "Aletheia",
    acceptedMinecraftVersions = "[1.7.10]",
    dependencies = "required-after:decaylib;required-after:hbm")
public class Aletheia {

    public static final String MODID = "aletheia";
    public static final Logger LOG = LogManager.getLogger(MODID);

    @Mod.Instance(Aletheia.MODID)
    public static Aletheia instance;

    @SidedProxy(clientSide = "com.pppopipupu.aletheia.ClientProxy", serverSide = "com.pppopipupu.aletheia.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        CompatExternal.registerFluidRegisterListener(new AletheiaFluids());
        AletheiaFluids.init();
        AletheiaBullets.init();
        AletheiaBlocks.init();
        AletheiaItems.init();

        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);

        AletheiaAchievements.init();
        AletheiaDecayRegistry.register();

        MinecraftForge.EVENT_BUS.register(new AletheiaCommonEventHandler());
        MinecraftForge.EVENT_BUS.register(new AletheiaDecayEventHandler());
        FMLCommonHandler.instance()
            .bus()
            .register(new AletheiaCommonEventHandler());

        PacketDispatcher.wrapper
            .registerMessage(AlienJellyBeamPacket.Handler.class, AlienJellyBeamPacket.class, 200, Side.CLIENT);
        PacketDispatcher.wrapper
            .registerMessage(QGPDistortionPacket.Handler.class, QGPDistortionPacket.class, 201, Side.CLIENT);

        registerFluidContainers();

        if (Loader.isModLoaded("NotEnoughItems")) {
            try {
                API.registerRecipeHandler(new AgriChemicalPlantRecipeHandler());
            } catch (Throwable t) {
                LOG.warn("Failed to register Agri Chemical Plant NEI handler", t);
            }
        }
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);

        HazardSystem.register(AletheiaItems.powder_sodium, new HazardData().addEntry(HazardRegistry.HYDROACTIVE, 2.0F));
        HazardSystem.register(AletheiaItems.ingot_sodium, new HazardData().addEntry(HazardRegistry.HYDROACTIVE, 3.0F));
        HazardSystem.register(AletheiaBlocks.block_sodium, new HazardData().addEntry(HazardRegistry.HYDROACTIVE, 9.0F));

        HazardSystem.register(
            AletheiaItems.rbmk_pellet_qgp_depleted,
            new HazardData().addEntry(HazardRegistry.RADIATION, 50000.0F)
                .addEntry(HazardRegistry.DIGAMMA, 1000.0F));
        HazardSystem.register(
            AletheiaItems.rbmk_fuel_qgp,
            new HazardData().addEntry(HazardRegistry.RADIATION, 5000.0F)
                .addEntry(HazardRegistry.DIGAMMA, 100.0F));
        HazardSystem.register(
            ItemZirnoxRodAletheia.rod_zirnox_digamma_depleted,
            new HazardData().addEntry(HazardRegistry.RADIATION, 800.0F)
                .addEntry(HazardRegistry.DIGAMMA, 25.0F));
        HazardSystem.register(
            ItemZirnoxRodAletheia.rod_zirnox_qgp_depleted,
            new HazardData().addEntry(HazardRegistry.RADIATION, 60000.0F)
                .addEntry(HazardRegistry.DIGAMMA, 1500.0F));
        HazardSystem
            .register(AletheiaItems.rod_zirnox_digamma, new HazardData().addEntry(HazardRegistry.RADIATION, 250.0F));
        HazardSystem.register(
            AletheiaItems.rod_zirnox_qgp,
            new HazardData().addEntry(HazardRegistry.RADIATION, 6000.0F)
                .addEntry(HazardRegistry.DIGAMMA, 150.0F));

        HazardSystem.register(
            AletheiaItems.billet_qgp,
            new HazardData().addEntry(HazardRegistry.RADIATION, 15000.0F)
                .addEntry(HazardRegistry.DIGAMMA, 375.0F));
        HazardSystem.register(
            AletheiaItems.waste_digamma,
            new HazardData().addEntry(HazardRegistry.RADIATION, 400.0F)
                .addEntry(HazardRegistry.DIGAMMA, 12.0F));
        HazardSystem.register(
            AletheiaItems.waste_qgp,
            new HazardData().addEntry(HazardRegistry.RADIATION, 30000.0F)
                .addEntry(HazardRegistry.DIGAMMA, 750.0F));

        AletheiaRecipes.registerForgeRecipes();
        AletheiaRecipesNtmcOverrides.register();
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        proxy.serverStarting(event);
    }

    private static void registerFluidContainers() {
        FluidType[] fluidsList = Fluids.getAll();
        for (int i = 1; i < fluidsList.length; i++) {
            FluidType type = fluidsList[i];
            if (type.hasNoContainer()) continue;
            if (type.isDispersable()) {
                FluidContainerRegistry.registerContainer(
                    new FluidContainer(
                        new ItemStack(ModItems.disperser_canister, 1, i),
                        new ItemStack(ModItems.disperser_canister_empty),
                        Fluids.fromID(i),
                        2000));
                FluidContainerRegistry.registerContainer(
                    new FluidContainer(
                        new ItemStack(ModItems.glyphid_gland, 1, i),
                        new ItemStack(ModItems.glyphid_gland_empty),
                        Fluids.fromID(i),
                        4000));
            }
        }
    }

}
