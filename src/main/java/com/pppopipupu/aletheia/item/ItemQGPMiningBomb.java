package com.pppopipupu.aletheia.item;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import com.hbm.blocks.ModBlocks;
import com.hbm.items.weapon.ItemGenericGrenade;
import com.hbm.packet.PacketDispatcher;
import com.hbm.tileentity.machine.storage.TileEntityCrateDesh;
import com.pppopipupu.aletheia.packet.QGPDistortionPacket;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemQGPMiningBomb extends ItemGenericGrenade {

    private final boolean isSuper;

    public ItemQGPMiningBomb(int fuse) {
        this(fuse, false);
    }

    public ItemQGPMiningBomb(int fuse, boolean isSuper) {
        super(fuse);
        this.isSuper = isSuper;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int meta) {
        return Blocks.tnt.getIcon(2, 0);
    }

    @Override
    public void explode(Entity grenade, EntityLivingBase thrower, World world, double x, double y, double z) {
        if (world.isRemote) {
            return;
        }

        int blockX = (int) Math.floor(x);
        int blockY = (int) Math.floor(y);
        int blockZ = (int) Math.floor(z);
        int centerChunkX = blockX >> 4;
        int centerChunkZ = blockZ >> 4;

        int minChunkX = centerChunkX;
        int maxChunkX = isSuper ? centerChunkX + 1 : centerChunkX;
        int minChunkZ = centerChunkZ;
        int maxChunkZ = isSuper ? centerChunkZ + 1 : centerChunkZ;

        int minX = minChunkX * 16;
        int maxX = maxChunkX * 16 + 15;
        int minZ = minChunkZ * 16;
        int maxZ = maxChunkZ * 16 + 15;

        int oreMultiplier = isSuper ? 5 : 2;
        int radonGasCount = isSuper ? 20 : 5;
        int distortionDuration = isSuper ? 120 : 60;

        List<ItemStack> collectedItems = new ArrayList<ItemStack>();

        for (int ix = minX; ix <= maxX; ix++) {
            for (int iz = minZ; iz <= maxZ; iz++) {
                for (int iy = 0; iy < 256; iy++) {
                    Block block = world.getBlock(ix, iy, iz);
                    if (block == null || block == Blocks.air) {
                        continue;
                    }
                    int meta = world.getBlockMetadata(ix, iy, iz);

                    if (block == Blocks.bedrock) {
                        if (isSuper) {
                            collectedItems.add(new ItemStack(Item.getItemFromBlock(Blocks.bedrock), 1, meta));
                        }
                        continue;
                    }

                    Item item = Item.getItemFromBlock(block);
                    if (item != null) {
                        ItemStack checkStack = new ItemStack(item, 1, meta);
                        int[] ids = OreDictionary.getOreIDs(checkStack);
                        boolean isOre = false;
                        for (int id : ids) {
                            String name = OreDictionary.getOreName(id);
                            if (name != null && name.toLowerCase()
                                .contains("ore")) {
                                isOre = true;
                                break;
                            }
                        }
                        if (isOre) {
                            collectedItems.add(new ItemStack(item, oreMultiplier, meta));
                        }
                    }
                }
            }
        }

        List<ItemStack> mergedItems = new ArrayList<ItemStack>();
        for (ItemStack stack : collectedItems) {
            boolean found = false;
            for (ItemStack merged : mergedItems) {
                if (merged.getItem() == stack.getItem() && merged.getItemDamage() == stack.getItemDamage()) {
                    merged.stackSize += stack.stackSize;
                    found = true;
                    break;
                }
            }
            if (!found) {
                mergedItems.add(stack);
            }
        }

        List<ItemStack> finalSlots = new ArrayList<ItemStack>();
        for (ItemStack stack : mergedItems) {
            int total = stack.stackSize;
            while (total > 0) {
                int count = Math.min(total, stack.getMaxStackSize());
                ItemStack split = new ItemStack(stack.getItem(), count, stack.getItemDamage());
                finalSlots.add(split);
                total -= count;
            }
        }

        for (int ix = minX; ix <= maxX; ix++) {
            for (int iz = minZ; iz <= maxZ; iz++) {
                for (int iy = 0; iy < 256; iy++) {
                    Block b = world.getBlock(ix, iy, iz);
                    if (b != Blocks.air) {
                        world.setBlock(ix, iy, iz, Blocks.air, 0, 2);
                    }
                }
            }
        }

        int chunkSpan = (maxChunkX - minChunkX + 1) * 16;
        for (int i = 0; i < radonGasCount; i++) {
            int rx = minX + world.rand.nextInt(chunkSpan);
            int rz = minZ + world.rand.nextInt(chunkSpan);
            int ry = 1 + world.rand.nextInt(250);
            world.setBlock(rx, ry, rz, ModBlocks.gas_radon, 0, 3);
        }

        int itemsPerCrate = 104;
        int totalCrates = (finalSlots.size() + itemsPerCrate - 1) / itemsPerCrate;

        int targetY = Math.max(1, Math.min(254, blockY));
        if (totalCrates == 0) {
            world.setBlock(blockX, targetY, blockZ, ModBlocks.crate_desh, 0, 3);
        } else {
            int gridWidth = (int) Math.ceil(Math.sqrt(totalCrates));
            for (int crateIdx = 0; crateIdx < totalCrates; crateIdx++) {
                int offsetX = (crateIdx % gridWidth) - (gridWidth / 2);
                int offsetZ = (crateIdx / gridWidth) - (gridWidth / 2);
                int targetX = blockX + offsetX;
                int targetZ = blockZ + offsetZ;
                world.setBlock(targetX, targetY, targetZ, ModBlocks.crate_desh, 0, 3);
                TileEntityCrateDesh tile = (TileEntityCrateDesh) world.getTileEntity(targetX, targetY, targetZ);
                if (tile != null) {
                    int startIdx = crateIdx * itemsPerCrate;
                    int endIdx = Math.min(startIdx + itemsPerCrate, finalSlots.size());
                    for (int i = startIdx; i < endIdx; i++) {
                        ItemStack stack = finalSlots.get(i);
                        int slotIdx = i - startIdx;
                        tile.setInventorySlotContents(slotIdx, stack);
                    }
                }
            }
        }

        for (Object pObj : world.playerEntities) {
            if (pObj instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) pObj;
                int pChunkX = ((int) Math.floor(player.posX)) >> 4;
                int pChunkZ = ((int) Math.floor(player.posZ)) >> 4;
                int rangeCheck = isSuper ? 6 : 4;
                if (Math.abs(pChunkX - centerChunkX) <= rangeCheck && Math.abs(pChunkZ - centerChunkZ) <= rangeCheck) {
                    if (player instanceof EntityPlayerMP) {
                        PacketDispatcher.wrapper
                            .sendTo(new QGPDistortionPacket(distortionDuration), (EntityPlayerMP) player);
                    }
                }
            }
        }

        float soundVolume = isSuper ? 6.0F : 4.0F;
        float soundPitch = isSuper ? 0.5F : 0.7F;
        world.playSoundEffect(
            x,
            y,
            z,
            "random.explode",
            soundVolume,
            (1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F) * soundPitch);
    }
}
