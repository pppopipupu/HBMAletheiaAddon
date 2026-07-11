package com.pppopipupu.aletheia.inventory;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

import com.hbm.inventory.container.ContainerBase;

public class ContainerAMSEmitter extends ContainerBase {

    public ContainerAMSEmitter(InventoryPlayer invPlayer, IInventory inv) {
        super(invPlayer, inv);
        this.addSlotToContainer(new Slot(inv, 0, 44, 17));
        this.addSlotToContainer(new Slot(inv, 1, 44, 53));
        this.addSlotToContainer(new Slot(inv, 2, 80, 53));
        this.addSlotToContainer(new Slot(inv, 3, 116, 53));

        playerInv(invPlayer, 8, 84);
    }
}
