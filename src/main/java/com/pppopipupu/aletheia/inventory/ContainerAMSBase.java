package com.pppopipupu.aletheia.inventory;

import com.hbm.inventory.container.ContainerBase;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

public class ContainerAMSBase extends ContainerBase {
	public ContainerAMSBase(InventoryPlayer invPlayer, IInventory inv) {
		super(invPlayer, inv);
		this.addSlotToContainer(new Slot(inv, 0, 8, 18));
		this.addSlotToContainer(new Slot(inv, 1, 8, 54));
		this.addSlotToContainer(new Slot(inv, 2, 152, 18));
		this.addSlotToContainer(new Slot(inv, 3, 152, 54));
		this.addSlotToContainer(new Slot(inv, 4, 8, 72));
		this.addSlotToContainer(new Slot(inv, 5, 8, 108));
		this.addSlotToContainer(new Slot(inv, 6, 152, 72));
		this.addSlotToContainer(new Slot(inv, 7, 152, 108));
		this.addSlotToContainer(new Slot(inv, 8, 80, 45));
		this.addSlotToContainer(new Slot(inv, 9, 62, 63));
		this.addSlotToContainer(new Slot(inv, 10, 98, 63));
		this.addSlotToContainer(new Slot(inv, 11, 80, 81));
		this.addSlotToContainer(new Slot(inv, 12, 80, 63));
		this.addSlotToContainer(new Slot(inv, 13, 62, 108));
		this.addSlotToContainer(new Slot(inv, 14, 62 + 18, 108));
		this.addSlotToContainer(new Slot(inv, 15, 62 + 36, 108));
		
		playerInv(invPlayer, 8, 140);
	}
}
