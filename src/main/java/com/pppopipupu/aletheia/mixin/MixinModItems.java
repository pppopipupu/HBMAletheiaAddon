package com.pppopipupu.aletheia.mixin;

import net.minecraft.item.Item;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.hbm.items.ModItems;

@Mixin(value = ModItems.class, remap = false)
public class MixinModItems {

    @Inject(method = "mainRegistry", at = @At("RETURN"))
    private static void aletheia$makeSa326ToolsUnbreakable(CallbackInfo ci) {
        setUnbreakable(ModItems.schrabidium_sword);
        setUnbreakable(ModItems.schrabidium_pickaxe);
        setUnbreakable(ModItems.schrabidium_axe);
        setUnbreakable(ModItems.schrabidium_shovel);
        setUnbreakable(ModItems.schrabidium_hoe);
    }

    private static void setUnbreakable(Item item) {
        if (item != null) {
            item.setMaxDamage(0);
        }
    }
}
