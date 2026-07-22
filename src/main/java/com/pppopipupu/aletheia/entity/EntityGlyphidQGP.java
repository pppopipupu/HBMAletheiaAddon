package com.pppopipupu.aletheia.entity;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import com.hbm.entity.mob.glyphid.EntityGlyphidBehemoth;
import com.hbm.extprop.HbmLivingProps;
import com.hbm.handler.radiation.ChunkRadiationManager;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.items.ModItems;
import com.hbm.items.machine.ItemFluidIcon;
import com.hbm.lib.ModDamageSource;
import com.hbm.main.ResourceManager;
import com.pppopipupu.aletheia.item.AletheiaItems;

public class EntityGlyphidQGP extends EntityGlyphidBehemoth {

    public EntityGlyphidQGP(World world) {
        super(world);
        this.setSize(4.2F, 2.7F);
        this.isImmuneToFire = true;
        this.setHealth(6000.0F);
    }

    @Override
    public ResourceLocation getSkin() {
        return ResourceManager.glyphid_behemoth_tex;
    }

    @Override
    public double getScale() {
        return 2.8D;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth)
            .setBaseValue(6000.0D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed)
            .setBaseValue(0.32D);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage)
            .setBaseValue(45.0D);
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (source == ModDamageSource.radiation || (source != null && source.getDamageType() != null
            && source.getDamageType()
                .toLowerCase()
                .contains("rad"))) {
            amount *= 0.00001F;
            if (amount < 0.0001F) {
                amount = 0.0001F;
            }
        }
        return super.attackEntityFrom(source, amount);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (!this.worldObj.isRemote) {
            float currentRad = HbmLivingProps.getRadiation(this);
            if (currentRad > 0.0F) {
                HbmLivingProps.setRadiation(this, currentRad * 0.00001F);
            }
            float currentRadEnv = HbmLivingProps.getRadEnv(this);
            if (currentRadEnv > 0.0F) {
                HbmLivingProps.setRadEnv(this, currentRadEnv * 0.00001F);
            }

            ChunkRadiationManager.proxy
                .incrementRad(this.worldObj, (int) this.posX, (int) this.posY, (int) this.posZ, 50.0F);

            if (this.ticksExisted % 20 == 0 && this.getHealth() > 0) {
                for (Object obj : this.worldObj
                    .getEntitiesWithinAABB(EntityPlayer.class, this.boundingBox.expand(12.0D, 6.0D, 12.0D))) {
                    if (obj instanceof EntityPlayer) {
                        EntityPlayer player = (EntityPlayer) obj;
                        player.setFire(5);
                        player.addPotionEffect(new PotionEffect(Potion.wither.id, 5 * 20, 2));
                    }
                }
            }
        }
    }

    @Override
    protected void dropFewItems(boolean byPlayer, int looting) {
        this.entityDropItem(new ItemStack(AletheiaItems.qgp_chitin, 1 + this.rand.nextInt(3 + looting)), 1.0F);
        if (this.rand.nextInt(100) < 15 + looting * 5) {
            this.entityDropItem(new ItemStack(AletheiaItems.qgp_apple), 1.0F);
        }
        this.entityDropItem(new ItemStack(ModItems.ingot_bakelite, 1 + this.rand.nextInt(3 + looting)), 1.0F);
        this.entityDropItem(new ItemStack(ModItems.ingot_pc, 1 + this.rand.nextInt(2 + looting)), 1.0F);
        if (this.rand.nextInt(100) < 25 + looting * 10) {
            this.entityDropItem(ItemFluidIcon.make(Fluids.SCHRABIDIC, 1000), 1.0F);
        }
        super.dropFewItems(byPlayer, looting);
    }

    @Override
    public boolean interact(EntityPlayer player) {
        ItemStack held = player.getHeldItem();
        if (held != null && held.getItem() == AletheiaItems.qgp_apple) {
            if (!player.capabilities.isCreativeMode) {
                held.stackSize--;
            }
            if (!this.worldObj.isRemote) {
                this.heal(300.0F);
                this.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 60 * 20, 3));
            }
            return true;
        }
        return super.interact(player);
    }
}
