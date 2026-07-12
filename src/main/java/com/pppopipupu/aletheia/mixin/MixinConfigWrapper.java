package com.pppopipupu.aletheia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.hbm.config.RunningConfig.ConfigWrapper;

@Mixin(value = ConfigWrapper.class, remap = false)
public class MixinConfigWrapper {

    @Inject(method = "get", at = @At("HEAD"), cancellable = true)
    private void aletheia$forceSecretsVisible(CallbackInfoReturnable<Object> cir) {
        StackTraceElement[] stack = Thread.currentThread()
            .getStackTrace();
        for (StackTraceElement elem : stack) {
            String cn = elem.getClassName();
            if (cn.contains("NEI") || cn.contains("nei")) {
                cir.setReturnValue(Boolean.FALSE);
                return;
            }
        }
    }
}
