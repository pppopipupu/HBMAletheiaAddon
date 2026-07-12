package com.pppopipupu.aletheia.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

import com.hbm.packet.PacketDispatcher;
import com.pppopipupu.aletheia.packet.AlienJellyEffectPacket;

public class ItemAlienJelly extends ItemFood {

    public ItemAlienJelly() {
        super(6, 0.5F, false);
        this.setAlwaysEdible();
    }

    @Override
    protected void onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            player.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 2400, 2));
            if (player instanceof EntityPlayerMP) {
                PacketDispatcher.wrapper.sendTo(new AlienJellyEffectPacket(), (EntityPlayerMP) player);
            }
        }
    }
}