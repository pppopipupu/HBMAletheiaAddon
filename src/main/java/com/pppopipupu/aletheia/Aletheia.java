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
import com.pppopipupu.aletheia.recipe.AletheiaRecipes;
import com.pppopipupu.aletheia.stats.AletheiaAchievements;
import com.pppopipupu.aletheia.weapon.AletheiaBullets;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLMissingMappingsEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
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
        CompatExternal.registerRecipeRegisterListener(new AletheiaRecipes());

        PacketDispatcher.wrapper
            .registerMessage(AlienJellyEffectPacket.Handler.class, AlienJellyEffectPacket.class, 200, Side.CLIENT);

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
                } else if ("hbm:machine_schrabidium_transmutator".equals(mapping.name)) {
                    mapping.remap(AletheiaBlocks.machine_schrabidium_transmutator);
                }
            }
        }
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
