package com.pppopipupu.aletheia;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.Achievement;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;

import com.hbm.items.ModItems;
import com.pppopipupu.aletheia.block.AletheiaBlocks;
import com.pppopipupu.aletheia.stats.AletheiaAchievements;

public class AletheiaCommonEventHandler {

    @SubscribeEvent
    public void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        ItemStack crafted = event.crafting;
        if (crafted == null || !crafted.isItemEqual(new ItemStack(AletheiaBlocks.ams_base))) {
            return;
        }

        EntityPlayer player = event.player;
        if (player == null || player.worldObj.isRemote) {
            return;
        }

        Achievement achievement = AletheiaAchievements.achievementAmsBase;

        boolean alreadyUnlocked = false;
        if (player instanceof EntityPlayerMP) {
            alreadyUnlocked = ((EntityPlayerMP) player).func_147099_x()
                .hasAchievementUnlocked(achievement);
        }

        player.triggerAchievement(achievement);

        if (!alreadyUnlocked) {
            player.addPotionEffect(new PotionEffect(Potion.fireResistance.id, 100, 0));
            player.worldObj
                .spawnEntityInWorld(new EntityLightningBolt(player.worldObj, player.posX, player.posY, player.posZ));
            player.worldObj.spawnEntityInWorld(
                new EntityLightningBolt(player.worldObj, player.posX + 1, player.posY, player.posZ + 1));
            player.worldObj.spawnEntityInWorld(
                new EntityLightningBolt(player.worldObj, player.posX - 1, player.posY, player.posZ - 1));

            List<Entity> list = player.worldObj
                .getEntitiesWithinAABBExcludingEntity(player, player.boundingBox.expand(8.0D, 8.0D, 8.0D));
            for (Entity ent : list) {
                if (ent instanceof EntityLivingBase) {
                    double dx = player.posX - ent.posX;
                    double dy = player.posY - ent.posY;
                    double dz = player.posZ - ent.posZ;
                    ent.motionX += dx * 0.35D;
                    ent.motionY += dy * 0.35D + 0.2D;
                    ent.motionZ += dz * 0.35D;
                }
            }

            if (!player.inventory.addItemStackToInventory(new ItemStack(ModItems.ams_lens))) {
                player.dropPlayerItemWithRandomChoice(new ItemStack(ModItems.ams_lens), false);
            }
        }
    }
}
