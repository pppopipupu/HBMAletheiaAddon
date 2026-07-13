package com.pppopipupu.aletheia;

import net.minecraft.item.ItemStack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hbm.inventory.FluidContainer;
import com.hbm.inventory.FluidContainerRegistry;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.packet.PacketDispatcher;
import com.hbm.util.CompatExternal;
import com.pppopipupu.aletheia.block.AletheiaBlocks;
import com.pppopipupu.aletheia.entity.EntityDisperserCanisterAletheia;
import com.pppopipupu.aletheia.fluid.AletheiaFluids;
import com.pppopipupu.aletheia.item.AletheiaItems;
import com.pppopipupu.aletheia.packet.AlienJellyEffectPacket;
import com.pppopipupu.aletheia.packet.QGPDistortionPacket;
import com.pppopipupu.aletheia.recipe.AletheiaRecipes;
import com.pppopipupu.aletheia.stats.AletheiaAchievements;
import com.pppopipupu.aletheia.weapon.AletheiaBullets;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.relauncher.Side;

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

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        CompatExternal.registerFluidRegisterListener(new AletheiaFluids());
        AletheiaFluids.init();
        AletheiaBullets.init();
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

        AletheiaAchievements.init();
        AletheiaRecipes.registerForgeRecipes();

        PacketDispatcher.wrapper
            .registerMessage(AlienJellyEffectPacket.Handler.class, AlienJellyEffectPacket.class, 200, Side.CLIENT);
        PacketDispatcher.wrapper
            .registerMessage(QGPDistortionPacket.Handler.class, QGPDistortionPacket.class, 201, Side.CLIENT);

        registerFluidContainers();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
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
    }
}
