package com.pppopipupu.aletheia;

import java.util.List;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import com.pppopipupu.aletheia.entity.EntityQGPSingularity;
import com.pppopipupu.aletheia.explosion.ExplosionQGP;

public class AletheiaQGPMeltdownHandler {

    public static boolean qgpMeltdown = false;

    public static void spawnSingularity(World world, double x, double y, double z) {
        if (world == null || world.isRemote) return;

        AxisAlignedBB box = AxisAlignedBB
            .getBoundingBox(x - 20.0D, y - 20.0D, z - 20.0D, x + 20.0D, y + 20.0D, z + 20.0D);
        List list = world.getEntitiesWithinAABB(EntityQGPSingularity.class, box);
        if (list == null || list.isEmpty()) {
            EntityQGPSingularity singularity = new EntityQGPSingularity(world);
            singularity.setPosition(x, y, z);
            world.spawnEntityInWorld(singularity);
        }
    }

    public static void executeSingularityExplosion(World world, double x, double y, double z, int state) {
        ExplosionQGP explosion = new ExplosionQGP(world, x, y, z, state);
        explosion.doExplosion();
    }
}
