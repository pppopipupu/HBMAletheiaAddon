package com.pppopipupu.aletheia.mixin;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.hbm.handler.ArmorModHandler;
import com.hbm.util.ArmorUtil;
import com.pppopipupu.aletheia.item.AletheiaItems;

@Mixin(value = ArmorUtil.class, remap = false)
public abstract class MixinArmorUtil {

    @Inject(method = "checkForDigamma", at = @At("HEAD"), cancellable = true)
    private static void onCheckForDigamma(EntityPlayer player, CallbackInfoReturnable<Boolean> cir) {
        if (player != null && player.inventory != null) {
            for (int i = 0; i < 4; i++) {
                ItemStack armor = player.inventory.armorInventory[i];
                if (armor != null && ArmorModHandler.hasMods(armor)) {
                    ItemStack[] mods = ArmorModHandler.pryMods(armor);
                    ItemStack cladding = mods[ArmorModHandler.cladding];
                    if (cladding != null && cladding.getItem() == AletheiaItems.qgp_cladding) {
                        cir.setReturnValue(true);
                        return;
                    }
                }
            }
        }
    }
}
