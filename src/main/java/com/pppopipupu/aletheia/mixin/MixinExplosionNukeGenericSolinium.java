package com.pppopipupu.aletheia.mixin;

import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.hbm.blocks.ModBlocks;
import com.hbm.explosion.ExplosionNukeGeneric;

@Mixin(value = ExplosionNukeGeneric.class, remap = false)
public class MixinExplosionNukeGenericSolinium {

    @Inject(method = "solinium", at = @At("TAIL"))
    private static void aletheia$soliniumExtra(World world, int x, int y, int z, CallbackInfo ci) {
        if (!world.isRemote) {
            if (world.getBlock(x, y, z) == ModBlocks.fallout || world.getBlock(x, y, z) == ModBlocks.balefire
                || world.getBlock(x, y, z) == ModBlocks.fire_digamma) {
                world.setBlockToAir(x, y, z);
            }
        }
    }
}
