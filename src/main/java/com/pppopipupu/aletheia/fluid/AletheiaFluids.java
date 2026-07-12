package com.pppopipupu.aletheia.fluid;

import net.minecraft.util.ResourceLocation;

import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.Fluids.CD_Canister;
import com.hbm.inventory.fluid.trait.FT_Coolable;
import com.hbm.inventory.fluid.trait.FT_Coolable.CoolingType;
import com.hbm.inventory.fluid.trait.FT_Heatable;
import com.hbm.inventory.fluid.trait.FT_Heatable.HeatingType;
import com.hbm.inventory.fluid.trait.FT_VentRadiation;
import com.hbm.render.util.EnumSymbol;
import com.hbm.util.CompatFluidRegistry;

import api.hbm.fluidmk2.IFluidRegisterListener;

public class AletheiaFluids implements IFluidRegisterListener {

    public static FluidType fluid_qgp;
    public static FluidType fluid_liquid_nitrogen;
    public static FluidType fluid_thermal_colloid;
    public static FluidType fluid_modified_cold_gel;
    public static FluidType fluid_hot_modified_cold_gel;

    public static void init() {
        fluid_qgp = CompatFluidRegistry
            .registerFluid(
                "QGP",
                1000,
                0xFF5500,
                4,
                0,
                4,
                EnumSymbol.RADIATION,
                new ResourceLocation("aletheia:textures/fluid/qgp.png"))
            .setTemp(10000000)
            .addContainers(new CD_Canister(0xFF5500))
            .addTraits(Fluids.LIQUID, Fluids.PLASMA, Fluids.EXPLOSIVE, Fluids.LEADCON, new FT_VentRadiation(0.5F));
        fluid_qgp.addTraits(
            new FT_Coolable(Fluids.NONE, 1, 0, 12500000).setEff(CoolingType.TURBINE, 3.0D)
                .setEff(CoolingType.HEATEXCHANGER, 3.0D));
    }

    @Override
    public void onFluidsLoad() {
        fluid_qgp = CompatFluidRegistry
            .registerFluid(
                "QGP",
                1000,
                0xFF5500,
                4,
                0,
                4,
                EnumSymbol.RADIATION,
                new ResourceLocation("aletheia:textures/fluid/qgp.png"))
            .setTemp(10000000)
            .addContainers(new CD_Canister(0xFF5500))
            .addTraits(Fluids.LIQUID, Fluids.PLASMA, Fluids.EXPLOSIVE, Fluids.LEADCON, new FT_VentRadiation(0.5F));
        fluid_qgp.addTraits(
            new FT_Coolable(Fluids.NONE, 1, 0, 12500000).setEff(CoolingType.TURBINE, 3.0D)
                .setEff(CoolingType.HEATEXCHANGER, 3.0D));

        fluid_liquid_nitrogen = CompatFluidRegistry
            .registerFluid(
                "liquid_nitrogen",
                1001,
                0x80c8ff,
                0,
                0,
                0,
                EnumSymbol.CROYGENIC,
                new ResourceLocation("aletheia:textures/fluid/liquid_nitrogen.png"))
            .setTemp(-200)
            .addTraits(Fluids.LIQUID);
        fluid_liquid_nitrogen.addTraits(
            new FT_Heatable().setEff(HeatingType.HEATEXCHANGER, 1.0D)
                .addStep(100, 1, Fluids.NONE, 0));

        fluid_thermal_colloid = CompatFluidRegistry
            .registerFluid(
                "thermal_colloid",
                1002,
                0x60c060,
                0,
                0,
                0,
                EnumSymbol.NONE,
                new ResourceLocation("aletheia:textures/fluid/thermal_colloid.png"))
            .addTraits(Fluids.LIQUID, Fluids.VISCOUS);
        fluid_thermal_colloid
            .addTraits(new FT_Coolable(Fluids.NONE, 1, 0, 300).setEff(CoolingType.HEATEXCHANGER, 1.0D));

        fluid_modified_cold_gel = CompatFluidRegistry
            .registerFluid(
                "modified_cold_gel",
                1003,
                0x4080ff,
                0,
                0,
                0,
                EnumSymbol.CROYGENIC,
                new ResourceLocation("aletheia:textures/fluid/modified_cold_gel.png"))
            .setTemp(-150)
            .addTraits(Fluids.LIQUID, Fluids.VISCOUS);
        fluid_modified_cold_gel
            .addTraits(new FT_Coolable(Fluids.NONE, 1, 0, 6400).setEff(CoolingType.HEATEXCHANGER, 1.0D));

        fluid_hot_modified_cold_gel = CompatFluidRegistry
            .registerFluid(
                "hot_modified_cold_gel",
                1004,
                0xff8040,
                0,
                0,
                0,
                EnumSymbol.NONE,
                new ResourceLocation("aletheia:textures/fluid/hot_modified_cold_gel.png"))
            .setTemp(2000)
            .addTraits(Fluids.LIQUID);
        fluid_hot_modified_cold_gel.addTraits(new FT_VentRadiation(0.5F));
    }
}
