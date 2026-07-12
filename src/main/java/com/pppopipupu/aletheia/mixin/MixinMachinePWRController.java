package com.pppopipupu.aletheia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import com.hbm.blocks.machine.MachinePWRController;

@Mixin(value = MachinePWRController.class, remap = false)
public class MixinMachinePWRController {

    @ModifyConstant(method = "floodFill", constant = @Constant(intValue = 4096))
    private int aletheia$increasePWRMaxSize(int original) {
        return 8192;
    }
}
