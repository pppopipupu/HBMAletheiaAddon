package com.pppopipupu.aletheia.item;

import java.util.List;
import java.util.Locale;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;

import com.hbm.inventory.RecipesCommon.ComparableStack;
import com.hbm.items.machine.ItemZirnoxRod;
import com.hbm.main.MainRegistry;
import com.hbm.tileentity.machine.TileEntityReactorZirnox;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemZirnoxRodAletheia extends Item {

    public static Item rod_zirnox_digamma_depleted;
    public static Item rod_zirnox_qgp_depleted;

    public enum EnumZirnoxAletheiaType {

        DIGAMMA(250_000, 440, "Digamma"),
        QGP(160_000, 960, "QGP");

        public final int maxLife;
        public final int heat;
        public final String label;

        private EnumZirnoxAletheiaType(int life, int heat, String label) {
            this.maxLife = life;
            this.heat = heat;
            this.label = label;
        }
    }

    private final EnumZirnoxAletheiaType type;

    public ItemZirnoxRodAletheia(EnumZirnoxAletheiaType type) {
        super();
        this.type = type;
        this.setMaxStackSize(1);
        this.setTextureName(
            "aletheia:rod_zirnox_" + type.name()
                .toLowerCase(Locale.US));
        this.setUnlocalizedName(
            "rod_zirnox_" + type.name()
                .toLowerCase(Locale.US));
        this.setCreativeTab(MainRegistry.controlTab);
    }

    public EnumZirnoxAletheiaType getType() {
        return type;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean bool) {
        list.add(EnumChatFormatting.YELLOW + "ZIRNOX " + type.label + " Fuel");
        list.add(EnumChatFormatting.GRAY + "Heat: " + type.heat + " / Lifetime: " + type.maxLife + " ticks");
    }

    public static void registerFuelMap() {
        TileEntityReactorZirnox.fuelMap.put(
            new ComparableStack(AletheiaItems.rod_zirnox_digamma, 1, 0),
            new ItemStack(rod_zirnox_digamma_depleted));
        TileEntityReactorZirnox.fuelMap
            .put(new ComparableStack(AletheiaItems.rod_zirnox_qgp, 1, 0), new ItemStack(rod_zirnox_qgp_depleted));
    }

    public static boolean isAletheiaRod(ItemStack stack) {
        return stack != null && stack.getItem() instanceof ItemZirnoxRodAletheia;
    }

    public static EnumZirnoxAletheiaType grabType(ItemStack stack) {
        if (isAletheiaRod(stack)) {
            return ((ItemZirnoxRodAletheia) stack.getItem()).type;
        }
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg) {
        this.itemIcon = reg.registerIcon(this.getIconString());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int meta) {
        return this.itemIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        list.add(new ItemStack(item, 1, 0));
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return this.getUnlocalizedName();
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return getDurabilityForDisplay(stack) > 0D;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        return (double) ItemZirnoxRod.getLifeTime(stack) / (double) type.maxLife;
    }
}
