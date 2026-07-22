package com.pppopipupu.aletheia.entity;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import com.hbm.items.ModItems;
import com.hbm.lib.ModDamageSource;
import com.hbm.packet.PacketDispatcher;
import com.pppopipupu.aletheia.AletheiaQGPMeltdownHandler;
import com.pppopipupu.aletheia.item.AletheiaItems;
import com.pppopipupu.aletheia.packet.QGPDistortionPacket;
import com.pppopipupu.aletheia.stats.AletheiaAchievements;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityQGPSingularity extends Entity {

    public static final int STATE_NORMAL = 0;
    public static final int STATE_QUENCHED = 1;
    public static final int STATE_SUPERCRITICAL = 2;

    public int state = STATE_NORMAL;
    public int age = 0;

    public EntityQGPSingularity(World world) {
        super(world);
        this.setSize(8.0F, 8.0F);
        this.isImmuneToFire = true;
        this.ignoreFrustumCheck = true;
    }

    @Override
    protected void entityInit() {
        this.dataWatcher.addObject(16, (byte) 0);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        this.motionX = 0;
        this.motionY = 0;
        this.motionZ = 0;

        age++;

        if (!worldObj.isRemote) {
            this.dataWatcher.updateObject(16, (byte) this.state);

            if (age % 5 == 0) {
                PacketDispatcher.wrapper.sendToAllAround(
                    new QGPDistortionPacket(25),
                    new TargetPoint(worldObj.provider.dimensionId, posX, posY, posZ, 200.0D));
            }

            if (age % 10 == 0) {
                performQGPTeslaDischarge();
            }

            handleAttractionAndInteractivity();

            if (age >= 600) {
                detonate();
                this.setDead();
            }
        } else {
            this.state = this.dataWatcher.getWatchableObjectByte(16);
        }
    }

    private void performQGPTeslaDischarge() {
        double range = (state == STATE_SUPERCRITICAL) ? 45.0D : 30.0D;
        AxisAlignedBB box = AxisAlignedBB
            .getBoundingBox(posX - range, posY - range, posZ - range, posX + range, posY + range, posZ + range);

        List<EntityLivingBase> livingList = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, box);
        float damage = (state == STATE_SUPERCRITICAL) ? 45.0F : 22.0F;

        if (!livingList.isEmpty()) {
            for (EntityLivingBase living : livingList) {
                living.attackEntityFrom(ModDamageSource.electricity, damage);
            }
            worldObj.playSoundEffect(posX, posY, posZ, "hbm:weapon.tesla", 20.0F, 0.9F + rand.nextFloat() * 0.3F);
        } else {
            worldObj.playSoundEffect(posX, posY, posZ, "hbm:weapon.tesla", 15.0F, 0.8F + rand.nextFloat() * 0.4F);
        }
    }

    private void handleAttractionAndInteractivity() {
        double radius = (state == STATE_SUPERCRITICAL) ? 65.0D : 45.0D;
        AxisAlignedBB box = AxisAlignedBB
            .getBoundingBox(posX - radius, posY - radius, posZ - radius, posX + radius, posY + radius, posZ + radius);

        List<Entity> entities = worldObj.getEntitiesWithinAABB(Entity.class, box);
        for (Entity e : entities) {
            if (e == this) continue;

            double dx = posX - e.posX;
            double dy = posY - e.posY;
            double dz = posZ - e.posZ;
            double distSq = dx * dx + dy * dy + dz * dz;

            if (distSq > 0.01D && distSq < radius * radius) {
                double dist = Math.sqrt(distSq);
                double force = (state == STATE_SUPERCRITICAL ? 0.75D : 0.40D) / Math.max(1.0D, dist);
                e.motionX += (dx / dist) * force;
                e.motionY += (dy / dist) * force;
                e.motionZ += (dz / dist) * force;

                if (dist < 4.0D && e instanceof EntityItem) {
                    EntityItem itemEntity = (EntityItem) e;
                    ItemStack stack = itemEntity.getEntityItem();
                    if (stack != null) {
                        checkItemInteractivity(stack);
                        itemEntity.setDead();
                    }
                }
            }
        }
    }

    private void checkItemInteractivity(ItemStack stack) {
        Item item = stack.getItem();
        if (state == STATE_NORMAL) {
            if (item == AletheiaItems.bucket_liquid_nitrogen || item == AletheiaItems.qgp_cladding
                || item == Item.getItemFromBlock(Blocks.ice)
                || item == ModItems.ingot_schrabidium) {
                this.state = STATE_QUENCHED;
            } else if (item == ModItems.pellet_antimatter || item == ModItems.particle_digamma
                || item == ModItems.rbmk_pellet_balefire) {
                    this.state = STATE_SUPERCRITICAL;
                }
        }
    }

    private void detonate() {
        AletheiaQGPMeltdownHandler.executeSingularityExplosion(worldObj, posX, posY, posZ, state);

        List<EntityPlayer> players = worldObj.getEntitiesWithinAABB(
            EntityPlayer.class,
            AxisAlignedBB.getBoundingBox(posX - 150, posY - 150, posZ - 150, posX + 150, posY + 150, posZ + 150));

        for (EntityPlayer player : players) {
            if (state == STATE_QUENCHED) {
                player.triggerAchievement(AletheiaAchievements.achievementQgpQuench);
            } else if (state == STATE_SUPERCRITICAL) {
                player.triggerAchievement(AletheiaAchievements.achievementQgpBlackhole);
            } else {
                player.triggerAchievement(AletheiaAchievements.achievementQgpMeltdown);
            }
        }

        spawnRewards();
    }

    private void spawnRewards() {
        if (state == STATE_QUENCHED) {
            worldObj.spawnEntityInWorld(
                new EntityItem(worldObj, posX, posY, posZ, new ItemStack(AletheiaItems.qgp_singularity_core, 1)));
            worldObj.spawnEntityInWorld(
                new EntityItem(worldObj, posX, posY, posZ, new ItemStack(AletheiaItems.solidified_quark, 3)));
        } else if (state == STATE_SUPERCRITICAL) {
            worldObj.spawnEntityInWorld(
                new EntityItem(worldObj, posX, posY, posZ, new ItemStack(AletheiaItems.quark_micro_singularity, 1)));
        } else {
            worldObj.spawnEntityInWorld(
                new EntityItem(worldObj, posX, posY, posZ, new ItemStack(AletheiaItems.qgp_fuel_slag, 3)));
            worldObj.spawnEntityInWorld(
                new EntityItem(worldObj, posX, posY, posZ, new ItemStack(AletheiaItems.solidified_quark, 2)));
        }
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound nbt) {
        this.age = nbt.getInteger("Age");
        this.state = nbt.getInteger("State");
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound nbt) {
        nbt.setInteger("Age", this.age);
        nbt.setInteger("State", this.state);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean isInRangeToRenderDist(double distance) {
        return distance < 160000.0D;
    }
}
