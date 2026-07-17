package com.pppopipupu.aletheia.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.hbm.render.shader.Shader;
import com.pppopipupu.aletheia.Aletheia;
import com.pppopipupu.aletheia.tileentity.IAletheiaZirnox;

public class RenderZirnoxAletheia extends TileEntitySpecialRenderer {

    private static Shader digammaShader = null;
    private static Shader qgpShader = null;

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks) {
        if (!(te instanceof IAletheiaZirnox)) {
            return;
        }
        int mode = ((IAletheiaZirnox) te).getRodMode();
        if (mode == 0) {
            return;
        }

        boolean digamma = (mode & 1) != 0;
        boolean qgp = (mode & 2) != 0;

        float time = (System.currentTimeMillis() % 100000) / 1000.0F;
        float pulse = 0.7F + 0.3F * (float) Math.sin(time * 3.0F);

        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5D, y, z + 0.5D);

        boolean hasFbo = OpenGlHelper.isFramebufferEnabled() && Minecraft.getMinecraft()
            .getFramebuffer() != null;
        int screenTex = hasFbo ? Minecraft.getMinecraft()
            .getFramebuffer().framebufferTexture : 0;
        float screenW = hasFbo ? Minecraft.getMinecraft()
            .getFramebuffer().framebufferTextureWidth : Minecraft.getMinecraft().displayWidth;
        float screenH = hasFbo ? Minecraft.getMinecraft()
            .getFramebuffer().framebufferTextureHeight : Minecraft.getMinecraft().displayHeight;

        if (hasFbo) {
            OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit + 1);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, screenTex);
            OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
        }

        if (digamma && digammaShader == null) {
            digammaShader = new Shader(new ResourceLocation(Aletheia.MODID, "shaders/zirnox_digamma_column.frag"));
        }
        if (qgp && qgpShader == null) {
            qgpShader = new Shader(new ResourceLocation(Aletheia.MODID, "shaders/qgp_column.frag"));
        }

        if (digamma) {
            drawColumn(digammaShader, 0.0D, 5.0D, 0.55F, 0.35F, 0.95F, pulse, time, hasFbo, screenW, screenH);
        }
        if (qgp) {
            drawColumn(qgpShader, 0.0D, 5.0D, 0.0F, 0.9F, 1.0F, pulse, time, hasFbo, screenW, screenH);
        }

        if (hasFbo) {
            OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit + 1);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
        }

        GL11.glPopMatrix();
    }

    private void drawColumn(Shader shader, double yMin, double yMax, float r, float g, float b, float pulse, float time,
        boolean hasFbo, float screenW, float screenH) {
        shader.use();
        shader.setUniform1f("iTime", time);
        shader.setUniform1i("u_screenTexture", 1);
        shader.setUniform1f("u_screenWidth", screenW);
        shader.setUniform1f("u_screenHeight", screenH);
        shader.setUniform1i("u_hasFbo", hasFbo ? 1 : 0);

        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDepthMask(false);

        float radius = 1.4F;
        int layers = 8;
        Tessellator tess = Tessellator.instance;
        for (int l = 0; l < layers; l++) {
            double yy = yMin + (yMax - yMin) * (l / (double) layers);
            double yy2 = yMin + (yMax - yMin) * ((l + 1) / (double) layers);
            float a = pulse * (1.0F - l / (float) layers) * 0.5F;

            tess.startDrawingQuads();
            tess.setColorRGBA_F((float) (r * 1.2F), (float) (g * 1.2F), (float) (b * 1.2F), a);
            tess.addVertex(-radius, yy, -radius);
            tess.addVertex(radius, yy, -radius);
            tess.addVertex(radius, yy2, radius);
            tess.addVertex(-radius, yy2, radius);
            tess.draw();
        }

        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_CULL_FACE);
        shader.stop();
    }
}
