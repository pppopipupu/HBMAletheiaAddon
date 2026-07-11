package com.pppopipupu.aletheia.mixin;

import com.hbm.inventory.fluid.FluidType;
import com.hbm.lib.RefStrings;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = FluidType.class, remap = false)
public abstract class MixinFluidType {

    @Shadow public String stringId;

    @Inject(method = "getColor", at = @At("HEAD"), cancellable = true)
    private void aletheia$getColor(CallbackInfoReturnable<Integer> cir) {
        if ("QGP".equals(this.stringId)) {
            float speed = 1.5F;
            float iTime = (System.currentTimeMillis() % 100000) / 1000.0F;
            int r = (int)((0.5F + 0.5F * Math.sin(iTime * speed)) * 255);
            int g = (int)((0.5F + 0.5F * Math.sin(-iTime * speed * 0.8F + 2.0F)) * 255);
            int b = (int)((0.5F + 0.5F * Math.sin(iTime * speed * 1.2F + 4.0F)) * 255);
            cir.setReturnValue((r << 16) | (g << 8) | b);
        }
    }

    @Inject(method = "getTint", at = @At("HEAD"), cancellable = true)
    private void aletheia$getTint(CallbackInfoReturnable<Integer> cir) {
        if ("QGP".equals(this.stringId)) {
            float speed = 1.5F;
            float iTime = (System.currentTimeMillis() % 100000) / 1000.0F;
            int r = (int)((0.5F + 0.5F * Math.sin(iTime * speed)) * 255);
            int g = (int)((0.5F + 0.5F * Math.sin(-iTime * speed * 0.8F + 2.0F)) * 255);
            int b = (int)((0.5F + 0.5F * Math.sin(iTime * speed * 1.2F + 4.0F)) * 255);
            cir.setReturnValue((r << 16) | (g << 8) | b);
        }
    }

    @Inject(method = "getTexture", at = @At("HEAD"), cancellable = true)
    private void aletheia$getTexture(CallbackInfoReturnable<ResourceLocation> cir) {
        if ("QGP".equals(this.stringId)) {
            cir.setReturnValue(new ResourceLocation(RefStrings.MODID + ":textures/gui/fluids/water.png"));
        }
    }
}
