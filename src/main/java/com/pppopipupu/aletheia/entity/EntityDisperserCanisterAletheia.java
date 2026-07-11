package com.pppopipupu.aletheia.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

import com.hbm.entity.effect.EntityCloudFleija;
import com.hbm.entity.effect.EntityMist;
import com.hbm.entity.grenade.EntityDisperserCanister;
import com.hbm.entity.logic.EntityNukeExplosionMK3;

public class EntityDisperserCanisterAletheia extends EntityDisperserCanister {

    public EntityDisperserCanisterAletheia(World world) {
        super(world);
    }

    public EntityDisperserCanisterAletheia(World world, EntityLivingBase living) {
        super(world, living);
    }

    public EntityDisperserCanisterAletheia(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    public void explode() {
        if (!worldObj.isRemote) {
            EntityMist mist = new EntityMist(worldObj);
            mist.setType(getFluid());
            mist.setPosition(posX, posY, posZ);
            mist.setArea(10, 5);
            mist.setDuration(80);
            worldObj.spawnEntityInWorld(mist);

            if (getFluid().getName()
                .equals("QGP")) {
                EntityNukeExplosionMK3 ex = EntityNukeExplosionMK3.statFacFleija(worldObj, posX, posY, posZ, 20);
                if (!ex.isDead) {
                    worldObj.playSoundEffect(
                        posX,
                        posY,
                        posZ,
                        "random.explode",
                        100.0F,
                        worldObj.rand.nextFloat() * 0.1F + 0.9F);
                    worldObj.spawnEntityInWorld(ex);
                    EntityCloudFleija cloud = new EntityCloudFleija(worldObj, 20);
                    cloud.setPosition(posX, posY, posZ);
                    worldObj.spawnEntityInWorld(cloud);
                }
            }

            this.setDead();
        }
    }
}
