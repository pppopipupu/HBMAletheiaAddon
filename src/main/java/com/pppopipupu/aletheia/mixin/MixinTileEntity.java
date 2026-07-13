package com.pppopipupu.aletheia.mixin;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.hbm.entity.mob.glyphid.EntityGlyphid;
import com.hbm.entity.mob.glyphid.EntityGlyphidBrawler;
import com.hbm.entity.mob.glyphid.EntityGlyphidScout;
import com.hbm.items.ModItems;
import com.pppopipupu.aletheia.item.AletheiaItems;
import com.pppopipupu.aletheia.stats.AletheiaAchievements;

@Mixin(value = TileEntity.class)
public class MixinTileEntity {

    @Shadow
    protected World worldObj;
    @Shadow
    public int xCoord;
    @Shadow
    public int yCoord;
    @Shadow
    public int zCoord;

    private static long getSpoilDuration(Item item) {
        if (item == ModItems.egg_glyphid) return 72000L;
        if (item == AletheiaItems.alien_jelly) return 144000L;
        return 72000L;
    }

    private static boolean isSpoilable(Item item) {
        return item == ModItems.egg_glyphid || item == AletheiaItems.alien_jelly;
    }

    private static EntityGlyphid createRandomGlyphid(World world) {
        double r = world.rand.nextDouble();
        if (r < 0.40) {
            return new EntityGlyphid(world);
        } else if (r < 0.65) {
            return new EntityGlyphidScout(world);
        } else if (r < 0.85) {
            return new EntityGlyphidBrawler(world);
        } else {
            return new EntityGlyphid(world);
        }
    }

    private static void spawnSpoilEffects(World world, double x, double y, double z) {
        world.playSoundEffect(x, y, z, "mob.slime.big", 1.0F, 1.0F);
        for (int i = 0; i < 8; i++) {
            world.spawnParticle(
                "slime",
                x + world.rand.nextGaussian() * 0.5,
                y + world.rand.nextGaussian() * 0.5,
                z + world.rand.nextGaussian() * 0.5,
                0,
                0,
                0);
        }
    }

    private static void initSpoilageTime(ItemStack stack, World world) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) {
            nbt = new NBTTagCompound();
            stack.setTagCompound(nbt);
        }
        if (!nbt.hasKey("spoilTime")) {
            long duration = getSpoilDuration(stack.getItem());
            nbt.setLong("spoilTime", world.getTotalWorldTime() + duration);
            nbt.setLong("spoilDuration", duration);
        }
    }

    @Inject(method = "updateEntity", at = @At("RETURN"))
    private void aletheia$updateEntity(CallbackInfo ci) {
        if (this.worldObj == null) return;
        if (this.worldObj.isRemote) return;
        if (this.worldObj.getTotalWorldTime() % 20 != 0) return;
        if (!(this instanceof IInventory)) return;

        IInventory inv = (IInventory) this;
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (stack == null) continue;
            if (!isSpoilable(stack.getItem())) continue;

            initSpoilageTime(stack, this.worldObj);

            NBTTagCompound nbt = stack.getTagCompound();
            long spoilTime = nbt.getLong("spoilTime");
            if (this.worldObj.getTotalWorldTime() < spoilTime) continue;

            double x = this.xCoord + 0.5;
            double y = this.yCoord + 1.1;
            double z = this.zCoord + 0.5;

            if (stack.getItem() == ModItems.egg_glyphid) {
                inv.decrStackSize(i, 1);
                EntityGlyphid glyphid = createRandomGlyphid(this.worldObj);
                glyphid.setPosition(x, y, z);
                this.worldObj.spawnEntityInWorld(glyphid);
                spawnSpoilEffects(this.worldObj, x, y, z);

                EntityPlayer nearestPlayer = this.worldObj.getClosestPlayer(x, y, z, 16.0);
                if (nearestPlayer != null) {
                    nearestPlayer.triggerAchievement(AletheiaAchievements.achievementGlyphidHatchUnexpected);
                }
            } else if (stack.getItem() == AletheiaItems.alien_jelly) {
                inv.setInventorySlotContents(i, new ItemStack(ModItems.biomass, stack.stackSize));
                spawnSpoilEffects(this.worldObj, x, y, z);
            }

            inv.markDirty();
        }
    }
}
