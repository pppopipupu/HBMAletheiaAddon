package com.pppopipupu.aletheia.machine.agrichemplant;

import com.hbm.handler.nei.NEIGenericRecipeHandler;
import com.pppopipupu.aletheia.block.AletheiaBlocks;

public class AgriChemicalPlantRecipeHandler extends NEIGenericRecipeHandler {

    public AgriChemicalPlantRecipeHandler() {
        super("Agricultural Chemical Plant", AgriChemicalPlantRecipes.INSTANCE, AletheiaBlocks.machine_agri_chem_plant);
    }

    @Override
    public String getRecipeID() {
        return "aletheiaAgriChemPlant";
    }
}
