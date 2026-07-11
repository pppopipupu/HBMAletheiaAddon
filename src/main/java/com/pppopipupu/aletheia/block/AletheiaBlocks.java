package com.pppopipupu.aletheia.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import cpw.mods.fml.common.registry.GameRegistry;

public class AletheiaBlocks {

    public static Fluid qgp_fluid;
    public static Block qgp_block;
    public static Block ams_base;
    public static Block ams_emitter;
    public static Block ams_limiter;

    public static void init() {
        qgp_fluid = new QGPFluid().setDensity(10000)
            .setViscosity(200)
            .setLuminosity(15)
            .setTemperature(10000)
            .setUnlocalizedName("qgp_fluid");
        FluidRegistry.registerFluid(qgp_fluid);

        qgp_block = new QGPBlock(qgp_fluid, Material.lava).setBlockName("qgp_block")
            .setResistance(10F);
        GameRegistry.registerBlock(qgp_block, "qgp_block");

        ams_base = new BlockAMSBase().setBlockName("ams_base")
            .setHardness(5.0F)
            .setResistance(10.0F)
            .setBlockTextureName("hbm:block_steel");
        GameRegistry.registerBlock(ams_base, "ams_base");

        ams_emitter = new BlockAMSEmitter().setBlockName("ams_emitter")
            .setHardness(5.0F)
            .setResistance(10.0F)
            .setBlockTextureName("hbm:block_steel");
        GameRegistry.registerBlock(ams_emitter, "ams_emitter");

        ams_limiter = new BlockAMSLimiter().setBlockName("ams_limiter")
            .setHardness(5.0F)
            .setResistance(10.0F)
            .setBlockTextureName("hbm:block_steel");
        GameRegistry.registerBlock(ams_limiter, "ams_limiter");
    }
}
