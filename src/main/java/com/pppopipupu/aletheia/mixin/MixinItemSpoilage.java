package com.pppopipupu.aletheia.mixin;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.hbm.entity.mob.glyphid.EntityGlyphid;
import com.hbm.entity.mob.glyphid.EntityGlyphidBrawler;
import com.hbm.entity.mob.glyphid.EntityGlyphidScout;
import com.hbm.items.ModItems;
import com.pppopipupu.aletheia.Aletheia;
import com.pppopipupu.aletheia.item.AletheiaItems;

@Mixin(value = Item.class)
public class MixinItemSpoilage {

    private static long getSpoilDuration(Item item) {
        if (item == ModItems.egg_glyphid) return 72000L;
        if (item == AletheiaItems.alien_jelly) return 18000L;
        return 72000L;
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
            world.spawnParticle("slime", x + world.rand.nextGaussian() * 0.5, y + world.rand.nextGaussian() * 0.5,
                z + world.rand.nextGaussian() * 0.5, 0, 0, 0);
        }
    }

    @Inject(method = "onUpdate", at = @At("HEAD"))
    private void aletheia$onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isSelected, CallbackInfo ci) {
        if (world.isRemote) return;
        if (!isSpoilable(stack.getItem())) return;

        initSpoilageTime(stack, world);

        NBTTagCompound nbt = stack.getTagCompound();
        long spoilTime = nbt.getLong("spoilTime");
        if (world.getTotalWorldTime() < spoilTime) return;

        if (!(entity instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) entity;

        if (stack.getItem() == ModItems.egg_glyphid) {
            player.inventory.decrStackSize(slot, 1);
            EntityGlyphid glyphid = createRandomGlyphid(world);
            glyphid.setPosition(player.posX, player.posY, player.posZ);
            world.spawnEntityInWorld(glyphid);
            spawnSpoilEffects(world, player.posX, player.posY, player.posZ);
            player.triggerAchievement(Aletheia.achievementGlyphidHatch);
        } else if (stack.getItem() == AletheiaItems.alien_jelly) {
            player.inventory.setInventorySlotContents(slot, new ItemStack(ModItems.biomass, stack.stackSize));
            spawnSpoilEffects(world, player.posX, player.posY, player.posZ);
        }
    }

    @Inject(method = "onEntityItemUpdate", at = @At("HEAD"), remap = false)
    private void aletheia$onEntityItemUpdate(EntityItem entityItem, CallbackInfoReturnable<Boolean> cir) {
        World world = entityItem.worldObj;
        if (world.isRemote) return;

        ItemStack stack = entityItem.getEntityItem();
        if (!isSpoilable(stack.getItem())) return;

        initSpoilageTime(stack, world);

        NBTTagCompound nbt = stack.getTagCompound();
        long spoilTime = nbt.getLong("spoilTime");
        if (world.getTotalWorldTime() < spoilTime) return;

        if (stack.getItem() == ModItems.egg_glyphid) {
            stack.splitStack(1);
            EntityGlyphid glyphid = createRandomGlyphid(world);
            glyphid.setPosition(entityItem.posX, entityItem.posY, entityItem.posZ);
            world.spawnEntityInWorld(glyphid);
            spawnSpoilEffects(world, entityItem.posX, entityItem.posY, entityItem.posZ);

            EntityPlayer nearestPlayer = world.getClosestPlayer(entityItem.posX, entityItem.posY, entityItem.posZ, 16.0);
            if (nearestPlayer != null) {
                nearestPlayer.triggerAchievement(Aletheia.achievementGlyphidHatch);
            }

            if (stack.stackSize <= 0) {
                entityItem.setDead();
            }
        } else if (stack.getItem() == AletheiaItems.alien_jelly) {
            entityItem.setEntityItemStack(new ItemStack(ModItems.biomass, stack.stackSize));
            spawnSpoilEffects(world, entityItem.posX, entityItem.posY, entityItem.posZ);
        }
    }

    @Inject(method = "addInformation", at = @At("RETURN"))
    private void aletheia$addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced, CallbackInfo ci) {
        Item item = stack.getItem();
        if (!isSpoilable(item)) return;

        String keyBase = item == ModItems.egg_glyphid ? "egg_glyphid" : "alien_jelly";

        NBTTagCompound nbt = stack.getTagCompound();
        long spoilTime;
        long spoilDuration;

        if (nbt != null && nbt.hasKey("spoilTime")) {
            spoilTime = nbt.getLong("spoilTime");
            spoilDuration = nbt.getLong("spoilDuration");
        } else {
            spoilDuration = getSpoilDuration(item);
            spoilTime = player.worldObj.getTotalWorldTime() + spoilDuration;
        }

        long remaining = spoilTime - player.worldObj.getTotalWorldTime();

        if (remaining > 0) {
            long minutes = remaining / 1200;
            long seconds = (remaining % 1200) / 20;
            double ratio = (double) remaining / spoilDuration;
            String color;
            if (ratio > 0.5) {
                color = "§a";
            } else if (ratio > 0.2) {
                color = "§e";
            } else {
                color = "§c";
            }
            int filled = (int) (ratio * 20);
            StringBuilder bar = new StringBuilder("§7[");
            bar.append(color);
            for (int i = 0; i < filled; i++) bar.append('|');
            bar.append("§7");
            for (int i = filled; i < 20; i++) bar.append('|');
            bar.append("§r]");
            list.add(StatCollector.translateToLocalFormatted("tooltip." + keyBase + ".spoilage", minutes, seconds));
            list.add(bar.toString());
            list.add(StatCollector.translateToLocal("tooltip." + keyBase + ".product"));
        } else {
            list.add(StatCollector.translateToLocal("tooltip." + keyBase + ".spoiled"));
        }
    }
}
