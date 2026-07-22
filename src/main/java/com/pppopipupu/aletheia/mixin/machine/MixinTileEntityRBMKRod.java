package com.pppopipupu.aletheia.mixin.machine;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.hbm.items.machine.ItemRBMKRod;
import com.hbm.tileentity.machine.rbmk.TileEntityRBMKRod;
import com.pppopipupu.aletheia.AletheiaQGPMeltdownHandler;
import com.pppopipupu.aletheia.item.AletheiaItems;
import com.pppopipupu.aletheia.item.ItemRBMKFuelQGP;

@Mixin(value = TileEntityRBMKRod.class, remap = false)
public class MixinTileEntityRBMKRod {

    @Inject(method = "onMelt", at = @At("HEAD"))
    private void aletheia$onQgpMelt(int reduce, CallbackInfo ci) {
        TileEntityRBMKRod rod = (TileEntityRBMKRod) (Object) this;
        if (rod.slots != null && rod.slots[0] != null && rod.slots[0].getItem() instanceof ItemRBMKRod) {
            if (rod.slots[0].getItem() == AletheiaItems.rbmk_fuel_qgp
                || rod.slots[0].getItem() instanceof ItemRBMKFuelQGP) {
                AletheiaQGPMeltdownHandler.qgpMeltdown = true;
            }
        }
    }
}
