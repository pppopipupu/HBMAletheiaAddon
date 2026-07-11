package com.pppopipupu.aletheia.inventory;

import com.hbm.inventory.container.ContainerBase;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

public class ContainerAMSLimiter extends ContainerBase {
	public ContainerAMSLimiter(InventoryPlayer invPlayer, IInventory inv) {
		super(invPlayer, inv);
		this.addSlotToContainer(new Slot(inv, 0, 44, 17));
		this.addSlotToContainer(new Slot(inv, 1, 44, 53));
		this.addSlotToContainer(new Slot(inv, 2, 80, 53));
		this.addSlotToContainer(new Slot(inv, 3, 116, 53));
		
		playerInv(invPlayer, 8, 84);
	}
}
