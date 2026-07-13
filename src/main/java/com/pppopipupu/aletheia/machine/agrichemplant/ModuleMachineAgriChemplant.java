package com.pppopipupu.aletheia.machine.agrichemplant;

import net.minecraft.item.ItemStack;

import com.hbm.inventory.recipes.loader.GenericRecipes;
import com.hbm.module.machine.ModuleMachineChemplant;

import api.hbm.energymk2.IEnergyHandlerMK2;

public class ModuleMachineAgriChemplant extends ModuleMachineChemplant {

    public ModuleMachineAgriChemplant(int index, IEnergyHandlerMK2 battery, ItemStack[] slots) {
        super(index, battery, slots);
    }

    @Override
    public GenericRecipes getRecipeSet() {
        return AgriChemicalPlantRecipes.INSTANCE;
    }
}
