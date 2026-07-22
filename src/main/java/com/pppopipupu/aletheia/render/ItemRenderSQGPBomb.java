package com.pppopipupu.aletheia.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import com.hbm.render.shader.Shader;
import com.pppopipupu.aletheia.Aletheia;

public class ItemRenderSQGPBomb implements IItemRenderer {

    private static Shader shader = null;

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return type != ItemRenderType.FIRST_PERSON_MAP;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return type == ItemRenderType.ENTITY;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        Minecraft mc = Minecraft.getMinecraft();
        GL11.glPushMatrix();

        IIcon iicon = Blocks.tnt.getIcon(2, 0);

        if (iicon != null) {
            mc.getTextureManager()
                .bindTexture(TextureMap.locationBlocksTexture);

            if (shader == null) {
                shader = new Shader(
                    new ResourceLocation(Aletheia.MODID, "shaders/qgp.vert"),
                    new ResourceLocation(Aletheia.MODID, "shaders/sqgp_bomb.frag"));
            }

            shader.use();
            shader.setUniform1f("time", (System.currentTimeMillis() % 100000) / 1000.0F);

            if (type == ItemRenderType.INVENTORY) {
                RenderItem.getInstance()
                    .renderIcon(0, 0, iicon, 16, 16);
            } else {
                Tessellator tessellator = Tessellator.instance;
                GL11.glPushMatrix();
                if (type == ItemRenderType.ENTITY) {
                    GL11.glTranslated(-0.5D, -0.5D, 0.0D);
                }
                ItemRenderer.renderItemIn2D(
                    tessellator,
                    iicon.getMaxU(),
                    iicon.getMinV(),
                    iicon.getMinU(),
                    iicon.getMaxV(),
                    iicon.getIconWidth(),
                    iicon.getIconHeight(),
                    0.0625F);
                GL11.glPopMatrix();
            }

            shader.stop();
        }

        GL11.glPopMatrix();
    }
}
