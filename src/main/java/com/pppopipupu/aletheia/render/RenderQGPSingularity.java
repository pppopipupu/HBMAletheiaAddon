package com.pppopipupu.aletheia.render;

import java.util.Random;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.hbm.render.shader.Shader;
import com.pppopipupu.aletheia.Aletheia;
import com.pppopipupu.aletheia.entity.EntityQGPSingularity;

public class RenderQGPSingularity extends Render {

    private static final ResourceLocation texture = new ResourceLocation(
        "aletheia:textures/items/qgp_singularity_core.png");
    private static Shader shader = null;
    private static Shader ringShader = null;

    public RenderQGPSingularity() {
        this.shadowSize = 0.0F;
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTicks) {
        EntityQGPSingularity singularity = (EntityQGPSingularity) entity;

        GL11.glPushMatrix();
        GL11.glTranslated(x, y + 2.0D, z);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        if (shader == null) {
            shader = new Shader(new ResourceLocation(Aletheia.MODID, "shaders/qgp_singularity.frag"));
        }

        float time = (System.currentTimeMillis() % 100000) / 1000.0F;

        if (shader != null) {
            shader.use();
            shader.setUniform1f("time", time);
            shader.setUniform1i("state", singularity.state);
        }

        bindTexture(getEntityTexture(entity));

        GL11.glRotatef(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);

        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(-7.0D, -7.0D, 0.0D, 0.0D, 1.0D);
        tessellator.addVertexWithUV(7.0D, -7.0D, 0.0D, 1.0D, 1.0D);
        tessellator.addVertexWithUV(7.0D, 7.0D, 0.0D, 1.0D, 0.0D);
        tessellator.addVertexWithUV(-7.0D, 7.0D, 0.0D, 0.0D, 0.0D);
        tessellator.draw();

        if (shader != null) {
            shader.stop();
        }

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glTranslated(x, y + 2.0D, z);

        renderVolumetricAccretionRings(singularity, time);
        renderQuantumFractalArcs(singularity, time);

        GL11.glPopMatrix();
    }

    private void renderVolumetricAccretionRings(EntityQGPSingularity singularity, float time) {
        if (ringShader == null) {
            ringShader = new Shader(
                new ResourceLocation(Aletheia.MODID, "shaders/qgp.vert"),
                new ResourceLocation(Aletheia.MODID, "shaders/qgp_accretion_ring.frag"));
        }

        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        GL11.glDisable(GL11.GL_CULL_FACE);

        ringShader.use();
        ringShader.setUniform1f("time", time);

        renderBandRing(8.0D, 12.5D, time * 50.0F, 25.0F, 0.0F);
        renderBandRing(13.0D, 19.5D, -time * 35.0F, -45.0F, 30.0F);

        ringShader.stop();

        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();
    }

    private void renderBandRing(double innerR, double outerR, float rotY, float rotX, float rotZ) {
        GL11.glPushMatrix();
        GL11.glRotatef(rotY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(rotX, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(rotZ, 0.0F, 0.0F, 1.0F);

        Tessellator tess = Tessellator.instance;
        tess.startDrawing(GL11.GL_QUAD_STRIP);

        int segments = 64;
        for (int i = 0; i <= segments; i++) {
            double angle = (i * Math.PI * 2.0D) / segments;
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);

            double u = (double) i / segments;

            tess.addVertexWithUV(cos * innerR, 0.0D, sin * innerR, u, 0.0D);
            tess.addVertexWithUV(cos * outerR, 0.0D, sin * outerR, u, 1.0D);
        }
        tess.draw();

        GL11.glPopMatrix();
    }

    private void renderQuantumFractalArcs(EntityQGPSingularity singularity, float time) {
        long worldTime = singularity.ticksExisted;

        int count = (singularity.state == EntityQGPSingularity.STATE_SUPERCRITICAL) ? 12 : 8;
        Random rand = new Random((long) singularity.getEntityId() * 31L + (worldTime / 2L) * 17L);

        for (int i = 0; i < count; i++) {
            double angleA = rand.nextDouble() * Math.PI * 2.0D;
            double angleB = (rand.nextDouble() - 0.5D) * Math.PI;
            double dist = 18.0D + rand.nextDouble() * 24.0D;

            double tx = Math.cos(angleA) * Math.cos(angleB) * dist;
            double ty = Math.sin(angleB) * dist;
            double tz = Math.sin(angleA) * Math.cos(angleB) * dist;

            long arcSeed = (long) (singularity.getEntityId() * 100 + i * 37 + worldTime * 3);
            QGPFractalBeamRenderer.renderQuantumArc(0.0D, 0.0D, 0.0D, tx, ty, tz, arcSeed, 0.5F, time);
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return texture;
    }
}
