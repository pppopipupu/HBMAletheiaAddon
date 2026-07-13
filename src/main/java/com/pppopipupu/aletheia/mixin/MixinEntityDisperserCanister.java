package com.pppopipupu.aletheia.mixin;

import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.hbm.entity.effect.EntityCloudFleija;
import com.hbm.entity.grenade.EntityDisperserCanister;
import com.hbm.entity.logic.EntityNukeExplosionMK3;

@Mixin(value = EntityDisperserCanister.class, remap = false)
public abstract class MixinEntityDisperserCanister {

    @Inject(method = "explode", at = @At("HEAD"))
    private void aletheia$explodeQGP(CallbackInfo ci) {
        EntityDisperserCanister self = (EntityDisperserCanister) (Object) this;
        World world = self.worldObj;
        if (!world.isRemote && self.getFluid() != null
            && "QGP".equals(
                self.getFluid()
                    .getName())) {
            EntityNukeExplosionMK3 ex = EntityNukeExplosionMK3
                .statFacFleija(world, self.posX, self.posY, self.posZ, 20);
            if (!ex.isDead) {
                world.playSoundEffect(
                    self.posX,
                    self.posY,
                    self.posZ,
                    "random.explode",
                    100.0F,
                    world.rand.nextFloat() * 0.1F + 0.9F);
                world.spawnEntityInWorld(ex);
                EntityCloudFleija cloud = new EntityCloudFleija(world, 20);
                cloud.setPosition(self.posX, self.posY, self.posZ);
                world.spawnEntityInWorld(cloud);
            }
        }
    }
}
