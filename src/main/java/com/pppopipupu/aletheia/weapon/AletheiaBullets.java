package com.pppopipupu.aletheia.weapon;

import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

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
        BulletConfig.configs = new BulletConfigList(BulletConfig.configs);

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
                    explodeZOMG(bullet.worldObj, x, y, z, 5);
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
                    mop.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(bullet, bullet.getThrower()), dmg);
                }
            })
            .setDamage(0F);

        energy_pppop.id = 9001;
        energy_pppop_steel.id = 9002;
    }

    public static void explodeZOMG(World world, int x, int y, int z, int bombStartStrength) {
        int r = bombStartStrength;
        int r2 = r * r;
        int r22 = r2 / 2;
        for (int xx = -r; xx < r; xx++) {
            int X = xx + x;
            int XX = xx * xx;
            for (int yy = -r; yy < r; yy++) {
                int Y = yy + y;
                int YY = XX + yy * yy;
                for (int zz = -r; zz < r; zz++) {
                    int Z = zz + z;
                    int ZZ = YY + zz * zz;
                    if (ZZ < r22) {
                        if (!(world.getBlock(X, Y, Z) == Blocks.bedrock && Y <= 0)) {
                            world.setBlock(X, Y, Z, Blocks.air);
                        }
                    }
                }
            }
        }
    }
}
