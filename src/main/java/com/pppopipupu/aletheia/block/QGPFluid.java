package com.pppopipupu.aletheia.block;

import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.Fluid;

public class QGPFluid extends Fluid {

    public QGPFluid() {
        super("qgp_fluid");
    }

    @Override
    public IIcon getStillIcon() {
        return QGPBlock.stillIcon;
    }

    @Override
    public IIcon getFlowingIcon() {
        return QGPBlock.flowingIcon;
    }
}
