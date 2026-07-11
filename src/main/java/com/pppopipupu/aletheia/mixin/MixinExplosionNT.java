package com.pppopipupu.aletheia.mixin;

import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.hbm.explosion.ExplosionNT;
import com.pppopipupu.aletheia.explosion.ExplosionFilter;

@Mixin(value = ExplosionNT.class, remap = false)
public class MixinExplosionNT {

    @Shadow
    private World worldObj;

    @Inject(method = "explode", at = @At("HEAD"), cancellable = true)
    private void aletheia$explodeHead(CallbackInfo ci) {
        Explosion exp = (Explosion) (Object) this;
        if (ExplosionFilter.shouldBlock(this.worldObj, exp.explosionX, exp.explosionY, exp.explosionZ)) {
            ci.cancel();
        }
    }
}
