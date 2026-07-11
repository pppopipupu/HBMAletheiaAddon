package com.pppopipupu.aletheia;

import com.pppopipupu.aletheia.tileentity.TileEntityAMSBase;
import com.pppopipupu.aletheia.tileentity.TileEntityAMSEmitter;
import com.pppopipupu.aletheia.tileentity.TileEntityAMSLimiter;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.registry.GameRegistry;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
        Config.synchronizeConfiguration(event.getSuggestedConfigurationFile());

        GameRegistry.registerTileEntity(TileEntityAMSBase.class, "aletheia_ams_base");
        GameRegistry.registerTileEntity(TileEntityAMSEmitter.class, "aletheia_ams_emitter");
        GameRegistry.registerTileEntity(TileEntityAMSLimiter.class, "aletheia_ams_limiter");

        Aletheia.LOG.info(Config.greeting);
        Aletheia.LOG.info("I am Aletheia at version " + Tags.VERSION);
    }

    public void init(FMLInitializationEvent event) {}

    public void postInit(FMLPostInitializationEvent event) {}

    public void serverStarting(FMLServerStartingEvent event) {}
}
