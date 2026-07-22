package com.pppopipupu.aletheia.explosion;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import com.hbm.blocks.ModBlocks;
import com.hbm.explosion.ExplosionNT;
import com.hbm.explosion.ExplosionNT.ExAttrib;
import com.hbm.explosion.ExplosionNukeGeneric;
import com.hbm.packet.PacketDispatcher;
import com.hbm.packet.toclient.AuxParticlePacketNT;
import com.pppopipupu.aletheia.entity.EntityQGPSingularity;
import com.pppopipupu.aletheia.packet.QGPDistortionPacket;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;

public class ExplosionQGP {

    private final World world;
    private final double posX;
    private final double posY;
    private final double posZ;
    private final int state;
    private final Random rand;

    public ExplosionQGP(World world, double posX, double posY, double posZ, int state) {
        this.world = world;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.state = state;
        this.rand = world.rand;
    }

    public void doExplosion() {
        if (world.isRemote) return;

        executeStage1VacuumImplosion();
        executeStage2BaryonDeconfinementCrust();
        executeStage3ShockwaveAndBedrockDisintegration();
        executeStage4QuarkUpdraftVortex();
        executeStage5FalloutAndDistortion();
    }

    private void executeStage1VacuumImplosion() {
        double pullRadius = (state == EntityQGPSingularity.STATE_SUPERCRITICAL) ? 70.0D : 45.0D;
        AxisAlignedBB box = AxisAlignedBB.getBoundingBox(
            posX - pullRadius,
            posY - pullRadius,
            posZ - pullRadius,
            posX + pullRadius,
            posY + pullRadius,
            posZ + pullRadius);

        List<Entity> list = world.getEntitiesWithinAABB(Entity.class, box);
        for (Entity e : list) {
            double dx = posX - e.posX;
            double dy = posY - e.posY;
            double dz = posZ - e.posZ;
            double distSq = dx * dx + dy * dy + dz * dz;

            if (distSq > 0.1D && distSq < pullRadius * pullRadius) {
                double dist = Math.sqrt(distSq);
                double pull = 2.0D / Math.max(1.0D, dist);
                e.motionX += (dx / dist) * pull;
                e.motionY += (dy / dist) * pull;
                e.motionZ += (dz / dist) * pull;
            }
        }

        world.playSoundEffect(posX, posY, posZ, "hbm:weapon.dFlash", 200000.0F, 0.9F);
    }

    private void executeStage2BaryonDeconfinementCrust() {
        int radius = 45;
        if (state == EntityQGPSingularity.STATE_QUENCHED) {
            radius = 18;
        } else if (state == EntityQGPSingularity.STATE_SUPERCRITICAL) {
            radius = 90;
        }

        int originX = (int) Math.floor(posX);
        int originY = (int) Math.floor(posY);
        int originZ = (int) Math.floor(posZ);

        int samples = (state == EntityQGPSingularity.STATE_SUPERCRITICAL) ? 12000 : 6000;

        for (int i = 0; i < samples; i++) {
            double u = rand.nextDouble();
            double v = rand.nextDouble();
            double theta = u * 2.0D * Math.PI;
            double phi = Math.acos(2.0D * v - 1.0D);
            double r = Math.cbrt(rand.nextDouble()) * radius;

            int bx = originX + (int) Math.floor(r * Math.sin(phi) * Math.cos(theta));
            int by = originY + (int) Math.floor(r * Math.sin(phi) * Math.sin(theta));
            int bz = originZ + (int) Math.floor(r * Math.cos(phi));

            if (by < 0 || by > 255) continue;

            Block current = world.getBlock(bx, by, bz);
            if (current == Blocks.air) continue;

            double distFromCenter = Math.sqrt(
                (bx - originX) * (bx - originX) + (by - originY) * (by - originY) + (bz - originZ) * (bz - originZ));

            if (state == EntityQGPSingularity.STATE_SUPERCRITICAL && current == Blocks.bedrock) {
                if (rand.nextInt(3) == 0) {
                    world.setBlock(bx, by, bz, ModBlocks.ash_digamma);
                } else {
                    world.setBlockToAir(bx, by, bz);
                }
                continue;
            }

            if (distFromCenter < radius * 0.35D) {
                if (rand.nextInt(3) == 0) {
                    world.setBlock(bx, by, bz, ModBlocks.corium_block);
                } else if (rand.nextInt(3) == 0) {
                    world.setBlock(bx, by, bz, ModBlocks.pribris_digamma);
                } else {
                    world.setBlockToAir(bx, by, bz);
                }
            } else if (distFromCenter < radius * 0.75D) {
                if (rand.nextInt(4) == 0) {
                    world.setBlock(bx, by, bz, ModBlocks.fire_digamma);
                } else if (rand.nextInt(3) == 0) {
                    world.setBlock(bx, by, bz, ModBlocks.ash_digamma);
                } else {
                    world.setBlockToAir(bx, by, bz);
                }
            } else {
                if (rand.nextInt(4) == 0) {
                    world.setBlock(bx, by, bz, ModBlocks.ash_digamma);
                }
            }
        }
    }

    private void executeStage3ShockwaveAndBedrockDisintegration() {
        float expStrength = 60.0F;
        int radius = 45;

        if (state == EntityQGPSingularity.STATE_QUENCHED) {
            expStrength = 18.0F;
            radius = 18;
        } else if (state == EntityQGPSingularity.STATE_SUPERCRITICAL) {
            expStrength = 160.0F;
            radius = 90;
        }

        new ExplosionNT(world, null, posX, posY, posZ, expStrength).addAttrib(ExAttrib.NOHURT)
            .addAttrib(ExAttrib.NODROP)
            .addAttrib(ExAttrib.DIGAMMA)
            .explode();

        ExplosionNukeGeneric.dealDamage(world, posX, posY, posZ, radius * 1.5D, 4000.0F);

        world.playSoundEffect(posX, posY, posZ, "hbm:block.rbmk_explosion", 300.0F, 0.5F);
    }

    private void executeStage4QuarkUpdraftVortex() {
        if (state == EntityQGPSingularity.STATE_QUENCHED) return;

        double radius = (state == EntityQGPSingularity.STATE_SUPERCRITICAL) ? 70.0D : 40.0D;
        AxisAlignedBB box = AxisAlignedBB.getBoundingBox(
            posX - radius,
            posY - radius,
            posZ - radius,
            posX + radius,
            posY + radius + 30.0D,
            posZ + radius);

        List<Entity> list = world.getEntitiesWithinAABB(Entity.class, box);
        for (Entity e : list) {
            double dx = posX - e.posX;
            double dz = posZ - e.posZ;
            double distSq = dx * dx + dz * dz;

            if (distSq < radius * radius) {
                e.motionY += 1.8D + rand.nextDouble() * 1.5D;
                e.motionX += (rand.nextDouble() - 0.5D) * 2.0D;
                e.motionZ += (rand.nextDouble() - 0.5D) * 2.0D;
            }
        }
    }

    private void executeStage5FalloutAndDistortion() {
        NBTTagCompound data = new NBTTagCompound();
        data.setString("type", "smoke");
        data.setString("mode", "radialDigamma");
        data.setInteger("count", state == EntityQGPSingularity.STATE_SUPERCRITICAL ? 400 : 200);
        data.setDouble("posX", posX);
        data.setDouble("posY", posY);
        data.setDouble("posZ", posZ);
        PacketDispatcher.wrapper.sendToAllAround(
            new AuxParticlePacketNT(data, posX, posY, posZ),
            new TargetPoint(world.provider.dimensionId, posX, posY, posZ, 450.0D));

        PacketDispatcher.wrapper.sendToAllAround(
            new QGPDistortionPacket(90),
            new TargetPoint(world.provider.dimensionId, posX, posY, posZ, 350.0D));
    }
}
