package com.pppopipupu.aletheia.mixin;

import com.hbm.inventory.UpgradeManagerNT;
import com.pppopipupu.aletheia.interfaces.IUpgradeManagerAccess;
import com.pppopipupu.aletheia.item.AletheiaItems;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = UpgradeManagerNT.class, remap = false)
public class MixinUpgradeManagerNT implements IUpgradeManagerAccess {

    public boolean hasUltimate;
    public int ultimateCount;

    @Override
    public int aletheia$getUltimateCount() {
        return this.ultimateCount;
    }

    @Override
    public boolean aletheia$hasUltimate() {
        return this.hasUltimate;
    }

    @Inject(method = "checkSlotsInternal", at = @At("RETURN"))
    private void injectCheckSlots(TileEntity te, ItemStack[] slots, int start, int end, CallbackInfo ci) {
        this.hasUltimate = false;
        this.ultimateCount = 0;
        if (slots == null) return;
        
        for (int i = start; i <= end; i++) {
            if (i >= 0 && i < slots.length && slots[i] != null) {
                if (slots[i].getItem() == AletheiaItems.upgrade_ultimate) {
                    this.hasUltimate = true;
                    this.ultimateCount++;
                }
            }
        }
    }
}
