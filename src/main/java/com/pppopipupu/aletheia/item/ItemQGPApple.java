package com.pppopipupu.aletheia.item;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

import com.hbm.main.MainRegistry;
import com.hbm.potion.HbmPotion;
import com.hbm.util.i18n.I18nUtil;

public class ItemQGPApple extends ItemFood {

    public ItemQGPApple() {
        super(20, 2.0F, false);
        this.setAlwaysEdible();
        this.setUnlocalizedName("qgp_apple");
        this.setTextureName("hbm:apple_schrabidium");
        this.setCreativeTab(MainRegistry.consumableTab);
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.epic;
    }

    @Override
    protected void onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            List<PotionEffect> effectsToRemove = new ArrayList<PotionEffect>();
            for (Object obj : player.getActivePotionEffects()) {
                if (obj instanceof PotionEffect) {
                    PotionEffect effect = (PotionEffect) obj;
                    Potion p = Potion.potionTypes[effect.getPotionID()];
                    if (p != null && p.isBadEffect()) {
                        effectsToRemove.add(effect);
                    }
                }
            }
            for (PotionEffect effect : effectsToRemove) {
                player.removePotionEffect(effect.getPotionID());
            }

            player.addPotionEffect(new PotionEffect(Potion.regeneration.id, 2147483647, 5));
            player.addPotionEffect(new PotionEffect(Potion.field_76444_x.id, 2147483647, 29));
            player.addPotionEffect(new PotionEffect(Potion.field_76434_w.id, 2147483647, 49));
            player.addPotionEffect(new PotionEffect(Potion.resistance.id, 2147483647, 5));
            player.addPotionEffect(new PotionEffect(Potion.damageBoost.id, 2147483647, 14));
            player.addPotionEffect(new PotionEffect(Potion.digSpeed.id, 2147483647, 9));
            player.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 2147483647, 4));
            player.addPotionEffect(new PotionEffect(Potion.jump.id, 2147483647, 4));
            player.addPotionEffect(new PotionEffect(Potion.fireResistance.id, 2147483647, 0));
            player.addPotionEffect(new PotionEffect(Potion.waterBreathing.id, 2147483647, 0));
            player.addPotionEffect(new PotionEffect(Potion.nightVision.id, 2147483647, 0));
            player.addPotionEffect(new PotionEffect(Potion.field_76443_y.id, 2147483647, 99));

            player.removePotionEffect(HbmPotion.radiation.id);
        }
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean bool) {
        list.add(I18nUtil.resolveKey("desc.item.qgp_apple"));
    }
}
