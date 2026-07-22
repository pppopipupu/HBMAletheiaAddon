package com.pppopipupu.aletheia.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.hbm.entity.mob.glyphid.EntityGlyphid;
import com.hbm.main.ResourceManager;
import com.hbm.render.entity.mob.RenderGlyphid;
import com.hbm.render.shader.Shader;
import com.pppopipupu.aletheia.Aletheia;

public class RenderGlyphidQGP extends RenderGlyphid {

    private static Shader qgpShader = null;
    private static Shader distortionShader = null;

    public RenderGlyphidQGP() {
        super();
        this.mainModel = new ModelGlyphidQGP();
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return super.getEntityTexture((EntityGlyphid) entity);
    }

    @Override
    public void doRender(EntityLivingBase entity, double x, double y, double z, float yaw, float partialTicks) {
        super.doRender(entity, x, y, z, yaw, partialTicks);
    }

    public static class ModelGlyphidQGP extends RenderGlyphid.ModelGlyphid {

        @Override
        public void renderModel(Entity entity, float limbSwing) {
            Minecraft mc = Minecraft.getMinecraft();
            boolean hasFbo = OpenGlHelper.isFramebufferEnabled() && mc.getFramebuffer() != null;
            int screenTex = hasFbo ? mc.getFramebuffer().framebufferTexture : 0;
            float screenWidth = hasFbo ? mc.getFramebuffer().framebufferTextureWidth : mc.displayWidth;
            float screenHeight = hasFbo ? mc.getFramebuffer().framebufferTextureHeight : mc.displayHeight;

            if (hasFbo) {
                OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit + 1);
                GL11.glEnable(GL11.GL_TEXTURE_2D);
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, screenTex);
                OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
            }

            if (qgpShader == null) {
                qgpShader = new Shader(
                    new ResourceLocation(Aletheia.MODID, "shaders/qgp.vert"),
                    new ResourceLocation(Aletheia.MODID, "shaders/qgp_glyphid.frag"));
            }

            if (distortionShader == null) {
                distortionShader = new Shader(
                    new ResourceLocation(Aletheia.MODID, "shaders/default.vert"),
                    new ResourceLocation(Aletheia.MODID, "shaders/qgp_distortion.frag"));
            }

            float currentTime = (System.currentTimeMillis() % 100000) / 1000.0F;

            qgpShader.use();
            qgpShader.setUniform1f("iTime", currentTime);
            qgpShader.setUniform1f("time", currentTime);
            qgpShader.setUniform1i("texture", 0);
            qgpShader.setUniform1i("u_screenTexture", 1);
            qgpShader.setUniform1f("u_screenWidth", screenWidth);
            qgpShader.setUniform1f("u_screenHeight", screenHeight);
            qgpShader.setUniform1i("u_hasFbo", hasFbo ? 1 : 0);

            super.renderModel(entity, limbSwing);

            qgpShader.stop();

            EntityGlyphid glyphid = (EntityGlyphid) entity;
            double biteProgress = glyphid.getSwingProgress(1.0F);
            double biteAngle = Math.sin(biteProgress * Math.PI) * 30.0;

            GL11.glPushMatrix();
            double s = glyphid.getScale();
            GL11.glScaled(s, s, s);

            GL11.glPushMatrix();
            GL11.glTranslated(0, 0.5, 0.25);

            GL11.glPushMatrix();
            GL11.glTranslated(0, 0.5, 0.25);
            GL11.glRotated(-biteAngle, 1, 0, 0);
            GL11.glTranslated(0, -0.5, -0.25);
            ResourceManager.glyphid.renderPart("JawTop");
            GL11.glPopMatrix();

            GL11.glPushMatrix();
            GL11.glTranslated(0, 0.5, 0.25);
            GL11.glRotated(biteAngle, 0, 1, 0);
            GL11.glRotated(biteAngle, 1, 0, 0);
            GL11.glTranslated(0, -0.5, -0.25);
            ResourceManager.glyphid.renderPart("JawLeft");
            GL11.glPopMatrix();

            GL11.glPushMatrix();
            GL11.glTranslated(0, 0.5, 0.25);
            GL11.glRotated(-biteAngle, 0, 1, 0);
            GL11.glRotated(biteAngle, 1, 0, 0);
            GL11.glTranslated(0, -0.5, -0.25);
            ResourceManager.glyphid.renderPart("JawRight");
            GL11.glPopMatrix();

            GL11.glPopMatrix();
            GL11.glPopMatrix();

            if (hasFbo) {
                distortionShader.use();
                distortionShader.setUniform1f("iTime", currentTime);
                distortionShader.setUniform1f("time", currentTime);
                distortionShader.setUniform1i("texture", 0);
                distortionShader.setUniform1i("u_screenTexture", 1);
                distortionShader.setUniform1f("u_screenWidth", screenWidth);
                distortionShader.setUniform1f("u_screenHeight", screenHeight);
                distortionShader.setUniform1i("u_hasFbo", 1);

                GL11.glPushMatrix();
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                GL11.glDepthMask(false);

                float quadSize = 7.0F;
                Tessellator tess = Tessellator.instance;

                tess.startDrawingQuads();
                tess.addVertexWithUV(-quadSize, -quadSize, 0.0, 0.0, 1.0);
                tess.addVertexWithUV(quadSize, -quadSize, 0.0, 1.0, 1.0);
                tess.addVertexWithUV(quadSize, quadSize, 0.0, 1.0, 0.0);
                tess.addVertexWithUV(-quadSize, quadSize, 0.0, 0.0, 0.0);
                tess.draw();

                GL11.glDepthMask(true);
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glPopMatrix();

                distortionShader.stop();

                OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit + 1);
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
            }
        }
    }
}
