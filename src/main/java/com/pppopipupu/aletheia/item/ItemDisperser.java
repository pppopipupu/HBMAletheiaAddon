package com.pppopipupu.aletheia.item;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.items.machine.ItemFluidTank;
import com.pppopipupu.aletheia.entity.EntityDisperserCanisterAletheia;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemDisperser extends ItemFluidTank {

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!player.capabilities.isCreativeMode) {
            --stack.stackSize;
        }

        world.playSoundAtEntity(player, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

        if (!world.isRemote) {
            EntityDisperserCanisterAletheia canister = new EntityDisperserCanisterAletheia(world, player);
            canister.setType(Item.getIdFromItem(this));
            canister.setFluid(stack.getItemDamage());
            world.spawnEntityInWorld(canister);
        }
        return stack;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tabs, List list) {
        FluidType[] order = Fluids.getInNiceOrder();

        for (int i = 1; i < order.length; ++i) {
            FluidType type = order[i];
            int id = type.getID();
            if (type.isDispersable() && this == AletheiaItems.disperser_canister) {
                list.add(new ItemStack(item, 1, id));
            } else
                if ((type == Fluids.PHEROMONE || type == Fluids.SULFURIC_ACID) && this == AletheiaItems.glyphid_gland) {
                    list.add(new ItemStack(item, 1, id));
                }
        }
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        String s = ("" + StatCollector.translateToLocal(this.getUnlocalizedName() + ".name")).trim();
        String s1 = ("" + StatCollector.translateToLocal(
            Fluids.fromID(stack.getItemDamage())
                .getConditionalName())).trim();

        s = this == AletheiaItems.glyphid_gland ? s1 + " " + s : s + " " + s1;
        return s;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        super.registerIcons(register);
        this.overlayIcon = this == AletheiaItems.disperser_canister
            ? register.registerIcon("hbm:disperser_canister_overlay")
            : register.registerIcon("hbm:fluid_identifier_overlay");
    }
}
