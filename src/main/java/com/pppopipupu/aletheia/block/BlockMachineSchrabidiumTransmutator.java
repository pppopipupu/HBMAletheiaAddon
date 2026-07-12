package com.pppopipupu.aletheia.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import com.hbm.main.MainRegistry;
import com.pppopipupu.aletheia.tileentity.TileEntityMachineSchrabidiumTransmutator;

import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockMachineSchrabidiumTransmutator extends BlockContainer {

    private final Random rand = new Random();
    private static boolean keepInventory;

    @SideOnly(Side.CLIENT)
    private IIcon iconTop;
    @SideOnly(Side.CLIENT)
    private IIcon iconBottom;

    public BlockMachineSchrabidiumTransmutator() {
        super(Material.iron);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        this.iconTop = register.registerIcon("aletheia:transmutator_top");
        this.iconBottom = register.registerIcon("aletheia:transmutator_bottom");
        this.blockIcon = register.registerIcon("aletheia:transmutator_side");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int metadata) {
        return side == 1 ? this.iconTop : (side == 0 ? this.iconBottom : this.blockIcon);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityMachineSchrabidiumTransmutator();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        } else if (!player.isSneaking()) {
            TileEntityMachineSchrabidiumTransmutator te = (TileEntityMachineSchrabidiumTransmutator) world
                .getTileEntity(x, y, z);
            if (te != null) {
                FMLNetworkHandler.openGui(player, MainRegistry.instance, 0, world, x, y, z);
            }
            return true;
        }
        return false;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        if (!keepInventory) {
            TileEntityMachineSchrabidiumTransmutator te = (TileEntityMachineSchrabidiumTransmutator) world
                .getTileEntity(x, y, z);
            if (te != null) {
                for (int i = 0; i < te.getSizeInventory(); i++) {
                    ItemStack stack = te.getStackInSlot(i);
                    if (stack != null) {
                        float f = this.rand.nextFloat() * 0.8F + 0.1F;
                        float f1 = this.rand.nextFloat() * 0.8F + 0.1F;
                        float f2 = this.rand.nextFloat() * 0.8F + 0.1F;
                        while (stack.stackSize > 0) {
                            int j = this.rand.nextInt(21) + 10;
                            if (j > stack.stackSize) j = stack.stackSize;
                            stack.stackSize -= j;
                            EntityItem entity = new EntityItem(
                                world,
                                x + f,
                                y + f1,
                                z + f2,
                                new ItemStack(stack.getItem(), j, stack.getItemDamage()));
                            if (stack.hasTagCompound()) {
                                entity.getEntityItem()
                                    .setTagCompound(stack.getTagCompound());
                            }
                            float f3 = 0.05F;
                            entity.motionX = (float) this.rand.nextGaussian() * f3;
                            entity.motionY = (float) this.rand.nextGaussian() * f3 + 0.2F;
                            entity.motionZ = (float) this.rand.nextGaussian() * f3;
                            world.spawnEntityInWorld(entity);
                        }
                    }
                }
                world.func_147453_f(x, y, z, block);
            }
        }
        super.breakBlock(world, x, y, z, block, meta);
    }
}
