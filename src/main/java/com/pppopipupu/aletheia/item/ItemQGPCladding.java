package com.pppopipupu.aletheia.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import com.hbm.items.armor.ItemModCladding;
import com.hbm.main.MainRegistry;
import com.hbm.util.i18n.I18nUtil;

public class ItemQGPCladding extends ItemModCladding {

    public ItemQGPCladding() {
        super(1.0F);
        this.setUnlocalizedName("qgp_cladding");
        this.setTextureName("hbm:cladding_ghiorsium");
        this.setCreativeTab(MainRegistry.controlTab);
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.epic;
    }

    @Override
    public void addDesc(List list, ItemStack stack, ItemStack armor) {
        list.add(EnumChatFormatting.YELLOW + "+1.0 " + I18nUtil.resolveKey("trait.radResistance", ""));
        list.add(EnumChatFormatting.LIGHT_PURPLE + "[" + I18nUtil.resolveKey("trait.digamma") + "]");
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean bool) {
        list.add(I18nUtil.resolveKey("desc.item.qgp_cladding"));
        super.addInformation(stack, player, list, bool);
    }
}
