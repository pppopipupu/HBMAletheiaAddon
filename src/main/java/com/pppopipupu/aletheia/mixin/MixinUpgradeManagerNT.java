package com.pppopipupu.aletheia.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.hbm.inventory.UpgradeManagerNT;
import com.pppopipupu.aletheia.interfaces.IUpgradeManagerAccess;
import com.pppopipupu.aletheia.item.AletheiaItems;

@Mixin(value = UpgradeManagerNT.class, remap = false)
public class MixinUpgradeManagerNT implements IUpgradeManagerAccess {

    public int ultimateCount;
    public int aletheia$productionMult = 1;
    public int aletheia$speedMult = 1;
    public double aletheia$powerMult = 1.0D;

    @Override
    public int aletheia$getUltimateCount() {
        return this.ultimateCount;
    }

    @Override
    public int aletheia$getProductionMult() {
        return this.aletheia$productionMult;
    }

    @Override
    public int aletheia$getSpeedMult() {
        return this.aletheia$speedMult;
    }

    @Override
    public double aletheia$getPowerMult() {
        return this.aletheia$powerMult;
    }

    @Inject(method = "checkSlotsInternal", at = @At("RETURN"))
    private void injectCheckSlots(TileEntity te, ItemStack[] slots, int start, int end, CallbackInfo ci) {
        this.ultimateCount = 0;
        int production = 0;
        int speed = 0;
        if (slots == null) {
            this.aletheia$productionMult = 1;
            this.aletheia$speedMult = 1;
            this.aletheia$powerMult = 1.0D;
            return;
        }

        for (int i = start; i <= end; i++) {
            if (i >= 0 && i < slots.length && slots[i] != null) {
                int weight = this.aletheia$contribute(slots[i]);
                if (weight > 0) {
                    this.ultimateCount += weight;
                    production += weight;
                    speed += weight;
                }
            }
        }

        this.aletheia$productionMult = 1 << production;
        this.aletheia$speedMult = 1 + speed * 4;
        this.aletheia$powerMult = Math.pow(0.5D, production);
    }

    private int aletheia$contribute(ItemStack stack) {
        if (stack.getItem() == AletheiaItems.upgrade_ultimate) {
            return 1;
        }
        return 0;
    }
}
