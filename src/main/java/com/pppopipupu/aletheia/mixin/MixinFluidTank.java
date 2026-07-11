package com.pppopipupu.aletheia.mixin;

import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.tank.FluidTank;
import com.hbm.render.shader.Shader;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = FluidTank.class, remap = false)
public abstract class MixinFluidTank {

    @Shadow public FluidType type;

    @Unique
    private static Shader aletheia$qgpShader = null;

    @Inject(method = "renderTank(IIDIII)V", at = @At("HEAD"))
    private void aletheia$renderTankHead(int x, int y, double z, int width, int height, int orientation, CallbackInfo ci) {
        boolean isQgp = this.type != null && "QGP".equals(this.type.getName());
        if (isQgp) {
            if (aletheia$qgpShader == null) {
                aletheia$qgpShader = new Shader(
                    new ResourceLocation("aletheia", "shaders/qgp.vert"),
                    new ResourceLocation("aletheia", "shaders/qgp.frag")
                );
            }
            aletheia$qgpShader.use();
            aletheia$qgpShader.setUniform1f("iTime", (System.currentTimeMillis() % 100000) / 1000.0F);
        }
    }

    @Inject(method = "renderTank(IIDIII)V", at = @At("RETURN"))
    private void aletheia$renderTankReturn(int x, int y, double z, int width, int height, int orientation, CallbackInfo ci) {
        boolean isQgp = this.type != null && "QGP".equals(this.type.getName());
        if (isQgp && aletheia$qgpShader != null) {
            aletheia$qgpShader.stop();
        }
    }
}
