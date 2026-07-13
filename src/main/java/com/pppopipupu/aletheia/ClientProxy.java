package com.pppopipupu.aletheia;

import java.util.Iterator;
import java.util.Map;

import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;

import com.google.common.collect.Maps;
import com.hbm.items.ModItems;
import com.hbm.items.weapon.sedna.ItemGunBaseNT;
import com.hbm.items.weapon.sedna.factory.LegoClient;
import com.pppopipupu.aletheia.block.AletheiaBlocks;
import com.pppopipupu.aletheia.item.AletheiaItems;
import com.pppopipupu.aletheia.machine.agrichemplant.TileEntityMachineAgriChemicalPlant;
import com.pppopipupu.aletheia.render.ItemRenderPPPOP;
import com.pppopipupu.aletheia.render.ItemRenderQGPBucket;
import com.pppopipupu.aletheia.render.ItemRenderQGPDisperser;
import com.pppopipupu.aletheia.render.ItemRenderQGPMiningBomb;
import com.pppopipupu.aletheia.render.ItemRenderUltimateUpgrade;
import com.pppopipupu.aletheia.render.RenderAMSBase;
import com.pppopipupu.aletheia.render.RenderAMSEmitter;
import com.pppopipupu.aletheia.render.RenderAMSLimiter;
import com.pppopipupu.aletheia.render.RenderAgriChemicalPlant;
import com.pppopipupu.aletheia.render.RenderPPPOPProjectile;
import com.pppopipupu.aletheia.render.RenderQGPBlock;
import com.pppopipupu.aletheia.tileentity.TileEntityAMSBase;
import com.pppopipupu.aletheia.tileentity.TileEntityAMSEmitter;
import com.pppopipupu.aletheia.tileentity.TileEntityAMSLimiter;
import com.pppopipupu.aletheia.weapon.AletheiaBullets;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {

    public static int qgpDistortionTicks = 0;
    public static final Map<Integer, Integer> alienJellyRayTicksByEntity = Maps.newHashMap();

    public static void addAlienJellyRay(int entityId) {
        alienJellyRayTicksByEntity.put(entityId, 100);
    }

    public static void tickAlienJellyRays() {
        Iterator<Map.Entry<Integer, Integer>> it = alienJellyRayTicksByEntity.entrySet()
            .iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, Integer> entry = it.next();
            int remaining = entry.getValue() - 1;
            if (remaining <= 0) {
                it.remove();
            } else {
                entry.setValue(remaining);
            }
        }
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);

        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityAMSBase.class, new RenderAMSBase());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityAMSEmitter.class, new RenderAMSEmitter());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityAMSLimiter.class, new RenderAMSLimiter());
        ClientRegistry
            .bindTileEntitySpecialRenderer(TileEntityMachineAgriChemicalPlant.class, new RenderAgriChemicalPlant());

        MinecraftForgeClient.registerItemRenderer(AletheiaItems.gun_pppop, new ItemRenderPPPOP());
        MinecraftForgeClient.registerItemRenderer(AletheiaItems.upgrade_ultimate, new ItemRenderUltimateUpgrade());
        MinecraftForgeClient.registerItemRenderer(AletheiaItems.bucket_qgp, new ItemRenderQGPBucket());
        MinecraftForgeClient.registerItemRenderer(AletheiaItems.qgp_mining_bomb, new ItemRenderQGPMiningBomb());
        MinecraftForgeClient.registerItemRenderer(ModItems.disperser_canister, new ItemRenderQGPDisperser());
        MinecraftForgeClient.registerItemRenderer(ModItems.glyphid_gland, new ItemRenderQGPDisperser());
        MinecraftForgeClient.registerItemRenderer(
            Item.getItemFromBlock(AletheiaBlocks.machine_agri_chem_plant),
            new RenderAgriChemicalPlant().getRenderer());

        RenderQGPBlock.renderId = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(new RenderQGPBlock());

        AletheiaBullets.energy_pppop.setRenderer(RenderPPPOPProjectile.RENDERER);
        AletheiaBullets.energy_pppop_steel.setRenderer(RenderPPPOPProjectile.RENDERER);

        ((ItemGunBaseNT) AletheiaItems.gun_pppop).getConfig(null, 0)
            .hud(LegoClient.HUD_COMPONENT_DURABILITY, LegoClient.HUD_COMPONENT_AMMO);

        MinecraftForgeForgeEventRegister();
    }

    private void MinecraftForgeForgeEventRegister() {
        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
        FMLCommonHandler.instance()
            .bus()
            .register(new ClientEventHandler());
    }
}
