package com.pppopipupu.aletheia.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.hbm.handler.ability.IToolAreaAbility;
import com.hbm.handler.ability.IToolHarvestAbility;
import com.hbm.items.tool.ItemToolAbilityPower;
import com.hbm.main.MainRegistry;
import com.hbm.util.i18n.I18nUtil;

public class ItemQGPDrill extends ItemToolAbilityPower {

    public ItemQGPDrill() {
        super(100.0F, -0.05, MainRegistry.tMatElec, EnumToolType.MINER, 10000000000L, 10000000, 1000);
        this.addAbility(IToolAreaAbility.HAMMER, 5);
        this.addAbility(IToolAreaAbility.HAMMER_FLAT, 5);
        this.addAbility(IToolAreaAbility.RECURSION, 8);
        this.addAbility(IToolHarvestAbility.SILK, 0);
        this.addAbility(IToolHarvestAbility.LUCK, 9);
        this.addAbility(IToolHarvestAbility.SMELTER, 0);
        this.addAbility(IToolHarvestAbility.SHREDDER, 0);
        this.addAbility(IToolHarvestAbility.CENTRIFUGE, 0);
        this.addAbility(IToolHarvestAbility.CRYSTALLIZER, 0);
        this.addAbility(IToolHarvestAbility.MERCURY, 0);
        this.setDepthRockBreaker();
        this.setUnlocalizedName("qgp_drill");
        this.setTextureName("aletheia:drax_mk3");
        this.setCreativeTab(MainRegistry.controlTab);
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        Block block = world.getBlock(x, y, z);
        if (block == Blocks.bedrock) {
            if (canOperate(stack)) {
                if (!world.isRemote) {
                    world.func_147480_a(x, y, z, true);
                    this.dischargeBattery(stack, 10000);
                }
                return true;
            }
        }
        return super.onItemUse(stack, player, world, x, y, z, side, hitX, hitY, hitZ);
    }

    @Override
    public boolean onBlockStartBreak(ItemStack stack, int x, int y, int z, EntityPlayer player) {
        World world = player.worldObj;
        Block block = world.getBlock(x, y, z);
        if (block == Blocks.bedrock) {
            if (canOperate(stack)) {
                if (!world.isRemote) {
                    world.setBlockToAir(x, y, z);
                    ItemStack drop = new ItemStack(Blocks.bedrock);
                    EntityItem entityItem = new EntityItem(world, x + 0.5, y + 0.5, z + 0.5, drop);
                    world.spawnEntityInWorld(entityItem);
                    this.dischargeBattery(stack, 1000);
                }
                return true;
            }
        }
        return super.onBlockStartBreak(stack, x, y, z, player);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean bool) {
        list.add(I18nUtil.resolveKey("desc.item.qgp_drill"));
        super.addInformation(stack, player, list, bool);
    }
}
