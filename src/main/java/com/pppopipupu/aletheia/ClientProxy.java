package com.pppopipupu.aletheia;

import com.hbm.render.entity.projectile.RenderGenericGrenade;
import com.pppopipupu.aletheia.entity.EntityDisperserCanisterAletheia;
import com.pppopipupu.aletheia.item.AletheiaItems;
import com.pppopipupu.aletheia.render.ItemRenderPPPOP;
import com.pppopipupu.aletheia.render.ItemRenderQGPBucket;
import com.pppopipupu.aletheia.render.ItemRenderQGPDisperser;
import com.pppopipupu.aletheia.render.ItemRenderQGPMiningBomb;
import com.pppopipupu.aletheia.render.ItemRenderUltimateUpgrade;
import com.pppopipupu.aletheia.render.RenderAMSBase;
import com.pppopipupu.aletheia.render.RenderAMSEmitter;
import com.pppopipupu.aletheia.render.RenderAMSLimiter;
import com.pppopipupu.aletheia.render.RenderQGPBlock;
import com.pppopipupu.aletheia.tileentity.TileEntityAMSBase;
import com.pppopipupu.aletheia.tileentity.TileEntityAMSEmitter;
import com.pppopipupu.aletheia.tileentity.TileEntityAMSLimiter;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy {

    public static int qgpDistortionTicks = 0;

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);

        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityAMSBase.class, new RenderAMSBase());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityAMSEmitter.class, new RenderAMSEmitter());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityAMSLimiter.class, new RenderAMSLimiter());

        MinecraftForgeClient.registerItemRenderer(AletheiaItems.gun_pppop, new ItemRenderPPPOP());
        MinecraftForgeClient.registerItemRenderer(AletheiaItems.upgrade_ultimate, new ItemRenderUltimateUpgrade());
        MinecraftForgeClient.registerItemRenderer(AletheiaItems.bucket_qgp, new ItemRenderQGPBucket());
        MinecraftForgeClient.registerItemRenderer(AletheiaItems.qgp_mining_bomb, new ItemRenderQGPMiningBomb());
        MinecraftForgeClient.registerItemRenderer(AletheiaItems.disperser_canister, new ItemRenderQGPDisperser());
        MinecraftForgeClient.registerItemRenderer(AletheiaItems.glyphid_gland, new ItemRenderQGPDisperser());

        RenderingRegistry.registerEntityRenderingHandler(EntityDisperserCanisterAletheia.class, new RenderGenericGrenade());

        RenderQGPBlock.renderId = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(new RenderQGPBlock());

        MinecraftForgeForgeEventRegister();
    }

    private void MinecraftForgeForgeEventRegister() {
        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
        FMLCommonHandler.instance().bus().register(new ClientEventHandler());
    }
}
