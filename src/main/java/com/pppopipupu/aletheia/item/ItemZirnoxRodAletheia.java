package com.pppopipupu.aletheia.item;

import java.util.List;
import java.util.Locale;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import com.hbm.inventory.RecipesCommon.ComparableStack;
import com.hbm.main.MainRegistry;
import com.hbm.tileentity.machine.TileEntityReactorZirnox;

public class ItemZirnoxRodAletheia extends Item {

    public static Item rod_zirnox_digamma_depleted;
    public static Item rod_zirnox_qgp_depleted;

    public enum EnumZirnoxAletheiaType {

        DIGAMMA(250_000, 220, "Digamma"),
        QGP(160_000, 320, "QGP");

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
}
