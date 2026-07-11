package com.pppopipupu.aletheia.mixin;

import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.hbm.explosion.ExplosionNukeSmall;
import com.pppopipupu.aletheia.explosion.ExplosionFilter;

@Mixin(value = ExplosionNukeSmall.class, remap = false)
public class MixinExplosionNukeSmall {

    @Inject(method = "explode", at = @At("HEAD"), cancellable = true)
    private static void aletheia$explodeHead(World world, double posX, double posY, double posZ,
        ExplosionNukeSmall.MukeParams params, CallbackInfo ci) {
        if (ExplosionFilter.shouldBlock(world, posX, posY, posZ)) {
            ci.cancel();
        }
    }
}
