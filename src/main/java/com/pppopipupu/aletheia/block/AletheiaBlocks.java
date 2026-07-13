package com.pppopipupu.aletheia.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import com.hbm.blocks.generic.BlockBeaconable;
import com.hbm.blocks.machine.BlockHadronCoil;
import com.hbm.main.MainRegistry;
import com.pppopipupu.aletheia.machine.agrichemplant.BlockMachineAgriChemicalPlant;

import cpw.mods.fml.common.registry.GameRegistry;

public class AletheiaBlocks {

    public static Fluid qgp_fluid;
    public static Block qgp_block;
    public static Block ams_base;
    public static Block ams_emitter;
    public static Block ams_limiter;

    public static Block machine_agri_chem_plant;
    public static Block machine_schrabidium_transmutator;
    public static Block block_sodium;
    public static Block block_strontium;
    public static Block block_neodymium;
    public static Block hadron_coil_neodymium;

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
            .setBlockTextureName("aletheia:ams_base");
        GameRegistry.registerBlock(ams_base, "ams_base");

        ams_emitter = new BlockAMSEmitter().setBlockName("ams_emitter")
            .setHardness(5.0F)
            .setResistance(10.0F)
            .setBlockTextureName("aletheia:ams_emitter");
        GameRegistry.registerBlock(ams_emitter, "ams_emitter");

        ams_limiter = new BlockAMSLimiter().setBlockName("ams_limiter")
            .setHardness(5.0F)
            .setResistance(10.0F)
            .setBlockTextureName("aletheia:ams_limiter");
        GameRegistry.registerBlock(ams_limiter, "ams_limiter");

        machine_schrabidium_transmutator = new BlockMachineSchrabidiumTransmutator()
            .setBlockName("machine_schrabidium_transmutator")
            .setHardness(5.0F)
            .setResistance(100.0F)
            .setCreativeTab(MainRegistry.machineTab);
        GameRegistry.registerBlock(machine_schrabidium_transmutator, "machine_schrabidium_transmutator");

        block_sodium = new BlockBeaconable(Material.rock).setBlockName("block_sodium")
            .setCreativeTab(MainRegistry.blockTab)
            .setHardness(1.0F)
            .setResistance(1.0F)
            .setBlockTextureName("aletheia:block_sodium");
        GameRegistry.registerBlock(block_sodium, "block_sodium");

        block_strontium = new BlockBeaconable(Material.iron).setBlockName("block_strontium")
            .setCreativeTab(MainRegistry.blockTab)
            .setHardness(4.0F)
            .setResistance(8.0F)
            .setBlockTextureName("aletheia:block_strontium");
        GameRegistry.registerBlock(block_strontium, "block_strontium");

        block_neodymium = new BlockBeaconable(Material.iron).setBlockName("block_neodymium")
            .setCreativeTab(MainRegistry.blockTab)
            .setHardness(5.0F)
            .setResistance(10.0F)
            .setBlockTextureName("aletheia:block_neodymium");
        GameRegistry.registerBlock(block_neodymium, "block_neodymium");

        hadron_coil_neodymium = new BlockHadronCoil(Material.iron, 50).setStepSound(Block.soundTypeMetal)
            .setBlockName("hadron_coil_neodymium")
            .setHardness(5.0F)
            .setResistance(10.0F)
            .setCreativeTab(MainRegistry.machineTab)
            .setBlockTextureName("aletheia:hadron_coil_neodymium");
        GameRegistry.registerBlock(hadron_coil_neodymium, "hadron_coil_neodymium");

        machine_agri_chem_plant = new BlockMachineAgriChemicalPlant(Material.iron)
            .setBlockName("machine_agri_chem_plant")
            .setHardness(5.0F)
            .setResistance(30.0F)
            .setCreativeTab(MainRegistry.machineTab)
            .setBlockTextureName("aletheia:ams_base");
        GameRegistry.registerBlock(machine_agri_chem_plant, "machine_agri_chem_plant");
    }
}
