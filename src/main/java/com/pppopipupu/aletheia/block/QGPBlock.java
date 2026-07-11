package com.pppopipupu.aletheia.block;

import java.util.Random;
import com.hbm.lib.ModDamageSource;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import com.hbm.blocks.ModBlocks;
import com.pppopipupu.aletheia.render.RenderQGPBlock;
import com.hbm.explosion.ExplosionNT;
import com.pppopipupu.aletheia.block.AletheiaBlocks;

public class QGPBlock extends BlockFluidClassic {

	@SideOnly(Side.CLIENT)
	public static IIcon stillIcon;
	@SideOnly(Side.CLIENT)
	public static IIcon flowingIcon;
	public Random rand = new Random();

	public QGPBlock(Fluid fluid, Material material) {
		super(fluid, material);
		setCreativeTab(null);
		displacements.put(this, false);
	}

	@Override
	public int getRenderType() {
		return RenderQGPBlock.renderId;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		return (side == 0 || side == 1) ? stillIcon : flowingIcon;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister register) {
		stillIcon = Blocks.lava.getIcon(1, 0);
		flowingIcon = Blocks.lava.getIcon(2, 0);
	}

	@Override
	public int colorMultiplier(IBlockAccess world, int x, int y, int z) {
		float speed = 1.5F;
		float iTime = (System.currentTimeMillis() % 100000) / 1000.0F;
		float coord = (x + y + z) * 0.2F;
		
		int r = (int)((0.5F + 0.5F * MathHelper.sin(coord + iTime * speed)) * 255);
		int g = (int)((0.5F + 0.5F * MathHelper.sin(coord - iTime * speed * 0.8F + 2.0F)) * 255);
		int b = (int)((0.5F + 0.5F * MathHelper.sin(coord + iTime * speed * 1.2F + 4.0F)) * 255);
		
		return (r << 16) | (g << 8) | b;
	}

	@Override
	public int getRenderColor(int meta) {
		float speed = 1.5F;
		float iTime = (System.currentTimeMillis() % 100000) / 1000.0F;
		
		int r = (int)((0.5F + 0.5F * MathHelper.sin(iTime * speed)) * 255);
		int g = (int)((0.5F + 0.5F * MathHelper.sin(-iTime * speed * 0.8F + 2.0F)) * 255);
		int b = (int)((0.5F + 0.5F * MathHelper.sin(iTime * speed * 1.2F + 4.0F)) * 255);
		
		return (r << 16) | (g << 8) | b;
	}

	@Override
	public boolean canDisplace(IBlockAccess world, int x, int y, int z) {
		if (world.getBlock(x, y, z).getMaterial().isLiquid()) {
			return false;
		}
		return super.canDisplace(world, x, y, z);
	}

	@Override
	public boolean displaceIfPossible(World world, int x, int y, int z) {
		if (world.getBlock(x, y, z).getMaterial().isLiquid()) {
			return false;
		}
		return super.displaceIfPossible(world, x, y, z);
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
		if (entity instanceof EntityLivingBase) {
			if (entity.motionY < -0.2)
				entity.motionY *= 0.5;
			entity.attackEntityFrom(ModDamageSource.acid, 20F);
			if (!world.isRemote) {
				int radius = 3;
				for (int dx = -radius; dx <= radius; dx++) {
					for (int dy = -radius; dy <= radius; dy++) {
						for (int dz = -radius; dz <= radius; dz++) {
							if (dx * dx + dy * dy + dz * dz <= radius * radius) {
								if (world.getBlock(x + dx, y + dy, z + dz) == AletheiaBlocks.qgp_block) {
									world.setBlockToAir(x + dx, y + dy, z + dz);
								}
							}
						}
					}
				}
				new ExplosionNT(world, null, entity.posX, entity.posY, entity.posZ, 4.0F).explode();
			}
		}
	}

	@Override
	public float getExplosionResistance(Entity entity, World world, int x, int y, int z, double explosionX, double explosionY, double explosionZ) {
		return 1.0F;
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		super.updateTick(world, x, y, z, rand);
		
		if (!world.isRemote) {
			int parentMeta = world.getBlockMetadata(x, y, z);
			if (parentMeta < 7) {
				spreadAndCorrode(world, x + 1, y, z, parentMeta + 1);
				spreadAndCorrode(world, x - 1, y, z, parentMeta + 1);
				spreadAndCorrode(world, x, y + 1, z, parentMeta + 1);
				spreadAndCorrode(world, x, y - 1, z, parentMeta + 1);
				spreadAndCorrode(world, x, y, z + 1, parentMeta + 1);
				spreadAndCorrode(world, x, y, z - 1, parentMeta + 1);
			}
		}
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		super.onNeighborBlockChange(world, x, y, z, block);
		
		if (!world.isRemote) {
			int parentMeta = world.getBlockMetadata(x, y, z);
			if (parentMeta < 7) {
				spreadAndCorrode(world, x + 1, y, z, parentMeta + 1);
				spreadAndCorrode(world, x - 1, y, z, parentMeta + 1);
				spreadAndCorrode(world, x, y + 1, z, parentMeta + 1);
				spreadAndCorrode(world, x, y - 1, z, parentMeta + 1);
				spreadAndCorrode(world, x, y, z + 1, parentMeta + 1);
				spreadAndCorrode(world, x, y, z - 1, parentMeta + 1);
			}
		}
	}

	private void spreadAndCorrode(World world, int x, int y, int z, int newMeta) {
		Block target = world.getBlock(x, y, z);
		if (target != AletheiaBlocks.qgp_block) {
			if (target != Blocks.air) {
				if (target.getExplosionResistance(null) < 6.0F) {
					world.setBlockToAir(x, y, z);
				} else {
					return;
				}
			}
			world.setBlock(x, y, z, AletheiaBlocks.qgp_block, newMeta, 3);
		}
	}

	@Override
	public int tickRate(World world) {
		return 16;
	}
}
