package com.pppopipupu.aletheia.render;

import java.util.Random;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.hbm.render.shader.Shader;
import com.pppopipupu.aletheia.Aletheia;

public class QGPFractalBeamRenderer {

    private static Shader arcShader = null;

    public static void renderQuantumArc(double sx, double sy, double sz, double ex, double ey, double ez, long seed,
        float width, float time) {
        if (arcShader == null) {
            arcShader = new Shader(
                new ResourceLocation(Aletheia.MODID, "shaders/qgp.vert"),
                new ResourceLocation(Aletheia.MODID, "shaders/qgp_quantum_arc.frag"));
        }

        Random rand = new Random(seed);
        int steps = 16;

        double[] px = new double[steps + 1];
        double[] py = new double[steps + 1];
        double[] pz = new double[steps + 1];

        px[0] = sx;
        py[0] = sy;
        pz[0] = sz;

        px[steps] = ex;
        py[steps] = ey;
        pz[steps] = ez;

        for (int i = 1; i < steps; i++) {
            double progress = (double) i / steps;
            double offsetScale = Math.sin(progress * Math.PI) * 2.2D;

            px[i] = sx + (ex - sx) * progress + (rand.nextDouble() - 0.5D) * offsetScale;
            py[i] = sy + (ey - sy) * progress + (rand.nextDouble() - 0.5D) * offsetScale;
            pz[i] = sz + (ez - sz) * progress + (rand.nextDouble() - 0.5D) * offsetScale;
        }

        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        GL11.glDisable(GL11.GL_TEXTURE_2D);

        arcShader.use();
        arcShader.setUniform1f("time", time);

        Tessellator tess = Tessellator.instance;

        for (int i = 0; i < steps; i++) {
            double x1 = px[i];
            double y1 = py[i];
            double z1 = pz[i];

            double x2 = px[i + 1];
            double y2 = py[i + 1];
            double z2 = pz[i + 1];

            double dx = x2 - x1;
            double dy = y2 - y1;
            double dz = z2 - z1;
            double len = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (len < 0.001D) continue;

            double nx = -dz / len * width;
            double ny = dy / len * width;
            double nz = dx / len * width;

            tess.startDrawingQuads();
            tess.addVertexWithUV(x1 - nx, y1 - ny, z1 - nz, 0.0D, 0.0D);
            tess.addVertexWithUV(x1 + nx, y1 + ny, z1 + nz, 0.0D, 1.0D);
            tess.addVertexWithUV(x2 + nx, y2 + ny, z2 + nz, 1.0D, 1.0D);
            tess.addVertexWithUV(x2 - nx, y2 - ny, z2 - nz, 1.0D, 0.0D);
            tess.draw();
        }

        arcShader.stop();

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();
    }
}
