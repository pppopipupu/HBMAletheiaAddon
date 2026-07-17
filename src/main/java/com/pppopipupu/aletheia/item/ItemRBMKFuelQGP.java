package com.pppopipupu.aletheia.item;

import net.minecraft.item.ItemStack;

import com.hbm.items.machine.ItemRBMKPellet;
import com.hbm.items.machine.ItemRBMKRod;

public class ItemRBMKFuelQGP extends ItemRBMKRod {

    private final ItemRBMKPellet depleted;

    public ItemRBMKFuelQGP(ItemRBMKPellet depleted) {
        super(depleted.fullName);
        this.pellet = depleted;
        this.depleted = depleted;
        craftableRods.add(this);
    }

    @Override
    public ItemStack getContainerItem(ItemStack stack) {
        return new ItemStack(this.depleted, 1, 0);
    }

    @Override
    public boolean doesContainerItemLeaveCraftingGrid(ItemStack stack) {
        return true;
    }
}
