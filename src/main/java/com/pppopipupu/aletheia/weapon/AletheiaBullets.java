package com.pppopipupu.aletheia.weapon;

import com.hbm.entity.effect.EntityCloudFleija;
import com.hbm.entity.logic.EntityNukeExplosionMK3;
import com.hbm.explosion.ExplosionLarge;
import com.hbm.items.ModItems;
import com.hbm.items.weapon.sedna.BulletConfig;
import com.hbm.lib.ModDamageSource;
import com.hbm.util.DamageResistanceHandler.DamageClass;

public class AletheiaBullets {

    public static BulletConfig energy_pppop;
    public static BulletConfig energy_pppop_steel;

    public static void init() {
        energy_pppop = new BulletConfig().setItem(ModItems.ingot_euphemium)
            .setVel(12.0F)
            .setSpread(0.05F)
            .setLife(25)
            .setProjectiles(5)
            .setWear(1.0F)
            .setupDamageClass(DamageClass.SUBATOMIC)
            .setDoesPenetrate(true)
            .setReloadCount(250)
            .setOnImpact((bullet, mop) -> {
                if (!bullet.worldObj.isRemote) {
                    int x = (int) Math.floor(bullet.posX);
                    int y = (int) Math.floor(bullet.posY);
                    int z = (int) Math.floor(bullet.posZ);
                    ExplosionLarge
                        .explode(bullet.worldObj, bullet.posX, bullet.posY, bullet.posZ, 5.0F, true, false, false);
                    bullet.worldObj
                        .playSoundEffect(bullet.posX, bullet.posY, bullet.posZ, "hbm:entity.bombDet", 5.0F, 1.0F);
                    ExplosionLarge.spawnParticles(bullet.worldObj, bullet.posX, bullet.posY, bullet.posZ, 5);
                    EntityNukeExplosionMK3 ex = EntityNukeExplosionMK3
                        .statFacFleija(bullet.worldObj, bullet.posX, bullet.posY, bullet.posZ, 6);
                    if (!ex.isDead) {
                        bullet.worldObj.spawnEntityInWorld(ex);
                        EntityCloudFleija cloud = new EntityCloudFleija(bullet.worldObj, 10);
                        cloud.posX = bullet.posX;
                        cloud.posY = bullet.posY;
                        cloud.posZ = bullet.posZ;
                        bullet.worldObj.spawnEntityInWorld(cloud);
                    }
                    bullet.worldObj.playSoundEffect(
                        bullet.posX,
                        bullet.posY,
                        bullet.posZ,
                        "hbm:entity.oldExplosion",
                        5.0F,
                        0.8F + bullet.worldObj.rand.nextFloat() * 0.2F);
                }
            })
            .setOnEntityHit((bullet, mop) -> {
                if (mop.entityHit != null) {
                    float dmg = 100000F + bullet.worldObj.rand.nextFloat() * 150000F;
                    mop.entityHit
                        .attackEntityFrom(ModDamageSource.causeSubatomicDamage(bullet, bullet.getThrower()), dmg);
                    if (!bullet.worldObj.isRemote) {
                        EntityNukeExplosionMK3 ex = EntityNukeExplosionMK3
                            .statFacFleija(bullet.worldObj, bullet.posX, bullet.posY, bullet.posZ, 6);
                        if (!ex.isDead) {
                            bullet.worldObj.spawnEntityInWorld(ex);
                            EntityCloudFleija cloud = new EntityCloudFleija(bullet.worldObj, 10);
                            cloud.posX = bullet.posX;
                            cloud.posY = bullet.posY;
                            cloud.posZ = bullet.posZ;
                            bullet.worldObj.spawnEntityInWorld(cloud);
                        }
                        bullet.worldObj.playSoundEffect(
                            bullet.posX,
                            bullet.posY,
                            bullet.posZ,
                            "hbm:entity.oldExplosion",
                            5.0F,
                            0.8F + bullet.worldObj.rand.nextFloat() * 0.2F);
                    }
                }
            })
            .setDamage(0F);

        energy_pppop_steel = new BulletConfig().setItem(ModItems.steel_pickaxe)
            .setVel(4.0F)
            .setSpread(0.05F)
            .setLife(25)
            .setProjectiles(5)
            .setWear(1.0F)
            .setupDamageClass(DamageClass.SUBATOMIC)
            .setDoesPenetrate(true)
            .setReloadCount(250)
            .setOnImpact((bullet, mop) -> {
                if (!bullet.worldObj.isRemote) {
                    bullet.worldObj
                        .playSoundEffect(bullet.posX, bullet.posY, bullet.posZ, "random.explode", 1.0F, 1.0F);
                }
            })
            .setOnEntityHit((bullet, mop) -> {
                if (mop.entityHit != null) {
                    float dmg = 35F + bullet.worldObj.rand.nextFloat() * 10F;
                    mop.entityHit.attackEntityFrom(
                        net.minecraft.util.DamageSource.causeThrownDamage(bullet, bullet.getThrower()),
                        dmg);
                }
            })
            .setDamage(0F);
    }
}
