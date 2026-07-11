package com.pppopipupu.aletheia.mixin;

import com.hbm.entity.logic.EntityExplosionChunkloading;
import com.pppopipupu.aletheia.explosion.ExplosionFilter;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityExplosionChunkloading.class, remap = false)
public abstract class MixinEntityExplosionChunkloading extends Entity {

    public MixinEntityExplosionChunkloading(World world) {
        super(world);
    }

    @Inject(method = "onUpdate", at = @At("HEAD"), cancellable = true)
    private void aletheia$onUpdateHead(CallbackInfo ci) {
        if (ExplosionFilter.shouldBlock(this.worldObj, this.posX, this.posY, this.posZ)) {
            this.setDead();
            ci.cancel();
        }
    }
}
