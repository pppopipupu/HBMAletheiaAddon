package com.pppopipupu.aletheia.render;

import java.util.function.BiConsumer;

import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;

import com.hbm.entity.projectile.EntityBulletBaseMK4;
import com.pppopipupu.aletheia.weapon.AletheiaBullets;

public class RenderPPPOPProjectile {

    public static final BiConsumer<EntityBulletBaseMK4, Float> RENDERER = (bullet, interp) -> {
        boolean superuser = bullet.getBulletConfig() == AletheiaBullets.energy_pppop;

        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        GL11.glDepthMask(false);

        GL11.glScalef(1F / 3F, 1F / 5F, 1F / 5F);
        GL11.glScalef(-1, 1, 1);
        GL11.glScalef(3, 3, 3);

        Tessellator tess = Tessellator.instance;

        if (superuser) {
            float r1 = 0.1F;
            float g1 = 0.0F;
            float b1 = 0.22F;

            tess.startDrawing(4);
            tess.setColorRGBA_F(r1, g1, b1, 1.0F);
            tess.addVertex(6, 0, 0);
            tess.setColorRGBA_F(r1, g1, b1, 0.0F);
            tess.addVertex(3, -1, -1);
            tess.addVertex(3, 1, -1);
            tess.draw();

            tess.startDrawing(4);
            tess.setColorRGBA_F(r1, g1, b1, 0.0F);
            tess.addVertex(3, -1, 1);
            tess.setColorRGBA_F(r1, g1, b1, 1.0F);
            tess.addVertex(6, 0, 0);
            tess.setColorRGBA_F(r1, g1, b1, 0.0F);
            tess.addVertex(3, 1, 1);
            tess.draw();

            tess.startDrawing(4);
            tess.setColorRGBA_F(r1, g1, b1, 1.0F);
            tess.addVertex(6, 0, 0);
            tess.setColorRGBA_F(r1, g1, b1, 0.0F);
            tess.addVertex(3, 1, -1);
            tess.setColorRGBA_F(r1, g1, b1, 0.0F);
            tess.addVertex(3, 1, 1);
            tess.draw();

            tess.startDrawing(4);
            tess.setColorRGBA_F(r1, g1, b1, 1.0F);
            tess.addVertex(6, 0, 0);
            tess.addVertex(4, -0.5F, -0.5F);
            tess.addVertex(4, 0.5F, -0.5F);
            tess.draw();

            tess.startDrawing(4);
            tess.setColorRGBA_F(r1, g1, b1, 1.0F);
            tess.addVertex(4, -0.5F, 0.5F);
            tess.addVertex(6, 0, 0);
            tess.addVertex(4, 0.5F, 0.5F);
            tess.draw();

            tess.startDrawing(4);
            tess.setColorRGBA_F(r1, g1, b1, 1.0F);
            tess.addVertex(4, -0.5F, -0.5F);
            tess.addVertex(6, 0, 0);
            tess.addVertex(4, -0.5F, 0.5F);
            tess.draw();

            tess.startDrawing(4);
            tess.setColorRGBA_F(r1, g1, b1, 1.0F);
            tess.addVertex(6, 0, 0);
            tess.addVertex(4, 0.5F, -0.5F);
            tess.addVertex(4, 0.5F, 0.5F);
            tess.draw();

            tess.startDrawingQuads();
            tess.setColorRGBA_F(r1, g1, b1, 1.0F);
            tess.addVertex(4, 0.5F, -0.5F);
            tess.addVertex(4, 0.5F, 0.5F);
            tess.setColorRGBA_F(r1, g1, b1, 0.0F);
            tess.addVertex(0, 0.5F, 0.5F);
            tess.addVertex(0, 0.5F, -0.5F);
            tess.draw();

            tess.startDrawingQuads();
            tess.setColorRGBA_F(r1, g1, b1, 1.0F);
            tess.addVertex(4, -0.5F, -0.5F);
            tess.addVertex(4, -0.5F, 0.5F);
            tess.setColorRGBA_F(r1, g1, b1, 0.0F);
            tess.addVertex(0, -0.5F, 0.5F);
            tess.addVertex(0, -0.5F, -0.5F);
            tess.draw();

            tess.startDrawingQuads();
            tess.setColorRGBA_F(r1, g1, b1, 1.0F);
            tess.addVertex(4, -0.5F, 0.5F);
            tess.addVertex(4, 0.5F, 0.5F);
            tess.setColorRGBA_F(r1, g1, b1, 0.0F);
            tess.addVertex(0, 0.5F, 0.5F);
            tess.addVertex(0, -0.5F, 0.5F);
            tess.draw();

            tess.startDrawingQuads();
            tess.setColorRGBA_F(r1, g1, b1, 1.0F);
            tess.addVertex(4, -0.5F, -0.5F);
            tess.addVertex(4, 0.5F, -0.5F);
            tess.setColorRGBA_F(r1, g1, b1, 0.0F);
            tess.addVertex(0, 0.5F, -0.5F);
            tess.addVertex(0, -0.5F, -0.5F);
            tess.draw();

            GL11.glPushMatrix();
            GL11.glScalef(2.2F, 4.5F, 4.5F);
            float r2 = 0.5F;
            float g2 = 0.0F;
            float b2 = 0.7F;

            double time = (double) bullet.ticksExisted + interp;
            float pulse = 0.5F + (float) Math.sin(time * 0.8D) * 0.3F;
            float glowAlpha = 0.6F * pulse;

            tess.startDrawing(4);
            tess.setColorRGBA_F(r2, g2, b2, glowAlpha);
            tess.addVertex(6, 0, 0);
            tess.setColorRGBA_F(r2, g2, b2, 0.0F);
            tess.addVertex(3, -1, -1);
            tess.addVertex(3, 1, -1);
            tess.draw();

            tess.startDrawing(4);
            tess.setColorRGBA_F(r2, g2, b2, 0.0F);
            tess.addVertex(3, -1, 1);
            tess.setColorRGBA_F(r2, g2, b2, glowAlpha);
            tess.addVertex(6, 0, 0);
            tess.setColorRGBA_F(r2, g2, b2, 0.0F);
            tess.addVertex(3, 1, 1);
            tess.draw();

            tess.startDrawing(4);
            tess.setColorRGBA_F(r2, g2, b2, 0.0F);
            tess.addVertex(3, -1, -1);
            tess.setColorRGBA_F(r2, g2, b2, glowAlpha);
            tess.addVertex(6, 0, 0);
            tess.setColorRGBA_F(r2, g2, b2, 0.0F);
            tess.addVertex(3, -1, 1);
            tess.draw();

            tess.startDrawing(4);
            tess.setColorRGBA_F(r2, g2, b2, glowAlpha);
            tess.addVertex(6, 0, 0);
            tess.setColorRGBA_F(r2, g2, b2, 0.0F);
            tess.addVertex(3, 1, -1);
            tess.setColorRGBA_F(r2, g2, b2, 0.0F);
            tess.addVertex(3, 1, 1);
            tess.draw();

            tess.startDrawing(4);
            tess.setColorRGBA_F(r2, g2, b2, glowAlpha);
            tess.addVertex(6, 0, 0);
            tess.addVertex(4, -0.5F, -0.5F);
            tess.addVertex(4, 0.5F, -0.5F);
            tess.draw();

            tess.startDrawing(4);
            tess.setColorRGBA_F(r2, g2, b2, glowAlpha);
            tess.addVertex(4, -0.5F, 0.5F);
            tess.addVertex(6, 0, 0);
            tess.addVertex(4, 0.5F, 0.5F);
            tess.draw();

            tess.startDrawing(4);
            tess.setColorRGBA_F(r2, g2, b2, glowAlpha);
            tess.addVertex(4, -0.5F, -0.5F);
            tess.addVertex(6, 0, 0);
            tess.addVertex(4, -0.5F, 0.5F);
            tess.draw();

            tess.startDrawing(4);
            tess.setColorRGBA_F(r2, g2, b2, glowAlpha);
            tess.addVertex(6, 0, 0);
            tess.addVertex(4, 0.5F, -0.5F);
            tess.addVertex(4, 0.5F, 0.5F);
            tess.draw();

            tess.startDrawingQuads();
            tess.setColorRGBA_F(r2, g2, b2, glowAlpha);
            tess.addVertex(4, 0.5F, -0.5F);
            tess.addVertex(4, 0.5F, 0.5F);
            tess.setColorRGBA_F(r2, g2, b2, 0.0F);
            tess.addVertex(0, 0.5F, 0.5F);
            tess.addVertex(0, 0.5F, -0.5F);
            tess.draw();

            tess.startDrawingQuads();
            tess.setColorRGBA_F(r2, g2, b2, glowAlpha);
            tess.addVertex(4, -0.5F, -0.5F);
            tess.addVertex(4, -0.5F, 0.5F);
            tess.setColorRGBA_F(r2, g2, b2, 0.0F);
            tess.addVertex(0, -0.5F, 0.5F);
            tess.addVertex(0, -0.5F, -0.5F);
            tess.draw();

            tess.startDrawingQuads();
            tess.setColorRGBA_F(r2, g2, b2, glowAlpha);
            tess.addVertex(4, -0.5F, 0.5F);
            tess.addVertex(4, 0.5F, 0.5F);
            tess.setColorRGBA_F(r2, g2, b2, 0.0F);
            tess.addVertex(0, 0.5F, 0.5F);
            tess.addVertex(0, -0.5F, 0.5F);
            tess.draw();

            tess.startDrawingQuads();
            tess.setColorRGBA_F(r2, g2, b2, glowAlpha);
            tess.addVertex(4, -0.5F, -0.5F);
            tess.addVertex(4, 0.5F, -0.5F);
            tess.setColorRGBA_F(r2, g2, b2, 0.0F);
            tess.addVertex(0, 0.5F, -0.5F);
            tess.addVertex(0, -0.5F, -0.5F);
            tess.draw();
            GL11.glPopMatrix();
        } else {
            float red = 1.0F;
            float green = 1.0F;
            float blue = 1.0F;

            int eID = bullet.getEntityId();
            java.util.Random rand = new java.util.Random(eID * eID);
            red = rand.nextInt(2) * 0.6F;
            green = rand.nextInt(2) * 0.6F;
            blue = rand.nextInt(2) * 0.6F;

            tess.startDrawing(4);
            tess.setColorRGBA_F(red, green, blue, 1);
            tess.addVertex(6, 0, 0);
            tess.setColorRGBA_F(red, green, blue, 0);
            tess.addVertex(3, -1, -1);
            tess.addVertex(3, 1, -1);
            tess.draw();

            tess.startDrawing(4);
            tess.setColorRGBA_F(red, green, blue, 0);
            tess.addVertex(3, -1, 1);
            tess.setColorRGBA_F(red, green, blue, 1);
            tess.addVertex(6, 0, 0);
            tess.setColorRGBA_F(red, green, blue, 0);
            tess.addVertex(3, 1, 1);
            tess.draw();

            tess.startDrawing(4);
            tess.setColorRGBA_F(red, green, blue, 0);
            tess.addVertex(3, -1, -1);
            tess.setColorRGBA_F(red, green, blue, 1);
            tess.addVertex(6, 0, 0);
            tess.setColorRGBA_F(red, green, blue, 0);
            tess.addVertex(3, -1, 1);
            tess.draw();

            tess.startDrawing(4);
            tess.setColorRGBA_F(red, green, blue, 1);
            tess.addVertex(6, 0, 0);
            tess.setColorRGBA_F(red, green, blue, 0);
            tess.addVertex(3, 1, -1);
            tess.setColorRGBA_F(red, green, blue, 0);
            tess.addVertex(3, 1, 1);
            tess.draw();

            tess.startDrawing(4);
            tess.setColorRGBA_F(red, green, blue, 1);
            tess.addVertex(6, 0, 0);
            tess.addVertex(4, -0.5F, -0.5F);
            tess.addVertex(4, 0.5F, -0.5F);
            tess.draw();

            tess.startDrawing(4);
            tess.setColorRGBA_F(red, green, blue, 1);
            tess.addVertex(4, -0.5F, 0.5F);
            tess.addVertex(6, 0, 0);
            tess.addVertex(4, 0.5F, 0.5F);
            tess.draw();

            tess.startDrawing(4);
            tess.setColorRGBA_F(red, green, blue, 1);
            tess.addVertex(4, -0.5F, -0.5F);
            tess.addVertex(6, 0, 0);
            tess.addVertex(4, -0.5F, 0.5F);
            tess.draw();

            tess.startDrawing(4);
            tess.setColorRGBA_F(red, green, blue, 1);
            tess.addVertex(6, 0, 0);
            tess.addVertex(4, 0.5F, -0.5F);
            tess.addVertex(4, 0.5F, 0.5F);
            tess.draw();

            tess.startDrawingQuads();
            tess.setColorRGBA_F(red, green, blue, 1);
            tess.addVertex(4, 0.5F, -0.5F);
            tess.addVertex(4, 0.5F, 0.5F);
            tess.setColorRGBA_F(red, green, blue, 0);
            tess.addVertex(0, 0.5F, 0.5F);
            tess.addVertex(0, 0.5F, -0.5F);
            tess.draw();

            tess.startDrawingQuads();
            tess.setColorRGBA_F(red, green, blue, 1);
            tess.addVertex(4, -0.5F, -0.5F);
            tess.addVertex(4, -0.5F, 0.5F);
            tess.setColorRGBA_F(red, green, blue, 0);
            tess.addVertex(0, -0.5F, 0.5F);
            tess.addVertex(0, -0.5F, -0.5F);
            tess.draw();

            tess.startDrawingQuads();
            tess.setColorRGBA_F(red, green, blue, 1);
            tess.addVertex(4, -0.5F, 0.5F);
            tess.addVertex(4, 0.5F, 0.5F);
            tess.setColorRGBA_F(red, green, blue, 0);
            tess.addVertex(0, 0.5F, 0.5F);
            tess.addVertex(0, -0.5F, 0.5F);
            tess.draw();

            tess.startDrawingQuads();
            tess.setColorRGBA_F(red, green, blue, 1);
            tess.addVertex(4, -0.5F, -0.5F);
            tess.addVertex(4, 0.5F, -0.5F);
            tess.setColorRGBA_F(red, green, blue, 0);
            tess.addVertex(0, 0.5F, -0.5F);
            tess.addVertex(0, -0.5F, -0.5F);
            tess.draw();
        }

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glDepthMask(true);
        GL11.glPopMatrix();
    };
}
