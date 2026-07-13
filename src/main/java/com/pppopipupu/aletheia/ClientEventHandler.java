package com.pppopipupu.aletheia;

import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import com.hbm.render.shader.Shader;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class ClientEventHandler {

    private static Shader qgpFullscreenShader = null;
    private static Shader alienJellyRayShader = null;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            if (ClientProxy.qgpDistortionTicks > 0) {
                ClientProxy.qgpDistortionTicks--;
            }
            ClientProxy.tickAlienJellyRays();
        }
    }

    @SubscribeEvent
    public void onOverlayRender(RenderGameOverlayEvent.Pre event) {
        if (event.type == ElementType.CROSSHAIRS && ClientProxy.qgpDistortionTicks > 0) {
            renderQGPDistortion(event);
        }
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (ClientProxy.alienJellyRayTicksByEntity.isEmpty()) return;

        Minecraft mc = Minecraft.getMinecraft();
        RenderManager rm = RenderManager.instance;

        if (alienJellyRayShader == null) {
            alienJellyRayShader = new Shader(new ResourceLocation("aletheia", "shaders/alien_jelly_ray.frag"));
        }

        float beamHeight = 48.0F;
        float halfWidth = 0.5F;

        for (Map.Entry<Integer, Integer> entry : ClientProxy.alienJellyRayTicksByEntity.entrySet()) {
            Entity entity = mc.theWorld.getEntityByID(entry.getKey());
            if (entity == null) continue;

            int ticks = entry.getValue();
            float progress = 1.0F - (float) ticks / 100.0F;

            double dx = entity.prevPosX + (entity.posX - entity.prevPosX) * event.partialTicks;
            double dy = entity.prevPosY + (entity.posY - entity.prevPosY) * event.partialTicks;
            double dz = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * event.partialTicks;

            GL11.glPushMatrix();
            GL11.glTranslated(dx - rm.renderPosX, dy - 1.0 - rm.renderPosY, dz - rm.renderPosZ);

            alienJellyRayShader.use();
            alienJellyRayShader.setUniform1f("iTime", (System.currentTimeMillis() % 100000) / 1000.0F);
            alienJellyRayShader.setUniform1f("u_progress", progress);

            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
            GL11.glDepthMask(false);

            Tessellator tess = Tessellator.instance;

            tess.startDrawingQuads();
            tess.setBrightness(240);
            tess.setColorRGBA_F(1.0F, 1.0F, 1.0F, 1.0F);
            tess.addVertexWithUV(-halfWidth, 0, 0, 0, 0);
            tess.addVertexWithUV(halfWidth, 0, 0, 1, 0);
            tess.addVertexWithUV(halfWidth, beamHeight, 0, 1, 1);
            tess.addVertexWithUV(-halfWidth, beamHeight, 0, 0, 1);
            tess.draw();

            tess.startDrawingQuads();
            tess.setBrightness(240);
            tess.setColorRGBA_F(1.0F, 1.0F, 1.0F, 1.0F);
            tess.addVertexWithUV(0, 0, -halfWidth, 0, 0);
            tess.addVertexWithUV(0, 0, halfWidth, 1, 0);
            tess.addVertexWithUV(0, beamHeight, halfWidth, 1, 1);
            tess.addVertexWithUV(0, beamHeight, -halfWidth, 0, 1);
            tess.draw();

            GL11.glDepthMask(true);
            GL11.glEnable(GL11.GL_CULL_FACE);

            alienJellyRayShader.stop();
            GL11.glPopMatrix();
        }
    }

    private void renderQGPDistortion(RenderGameOverlayEvent.Pre event) {
        Minecraft mc = Minecraft.getMinecraft();
        boolean hasFbo = OpenGlHelper.isFramebufferEnabled() && mc.getFramebuffer() != null;
        if (hasFbo) {
            int screenTex = mc.getFramebuffer().framebufferTexture;
            float screenWidth = mc.getFramebuffer().framebufferTextureWidth;
            float screenHeight = mc.getFramebuffer().framebufferTextureHeight;
            int width = event.resolution.getScaledWidth();
            int height = event.resolution.getScaledHeight();

            GL11.glPushMatrix();

            OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit + 1);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, screenTex);
            OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);

            if (qgpFullscreenShader == null) {
                qgpFullscreenShader = new Shader(
                    new ResourceLocation("aletheia", "shaders/qgp_mining_bomb_distortion.frag"));
            }

            qgpFullscreenShader.use();
            qgpFullscreenShader.setUniform1f("iTime", (System.currentTimeMillis() % 100000) / 1000.0F);
            qgpFullscreenShader.setUniform1i("u_screenTexture", 1);
            qgpFullscreenShader.setUniform1f("u_screenWidth", screenWidth);
            qgpFullscreenShader.setUniform1f("u_screenHeight", screenHeight);
            qgpFullscreenShader.setUniform1i("u_hasFbo", 1);

            float progress = (60.0F - ClientProxy.qgpDistortionTicks) / 60.0F;
            qgpFullscreenShader.setUniform1f("u_progress", progress);

            int loc = qgpFullscreenShader.getUniformLocation("u_iconUvRange");
            GL20.glUniform4f(loc, 0.0F, 1.0F, 0.0F, 1.0F);

            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glDepthMask(false);

            Tessellator tess = Tessellator.instance;
            tess.startDrawingQuads();
            tess.addVertexWithUV(0, height, 0.0, 0.0, 0.0);
            tess.addVertexWithUV(width, height, 0.0, 1.0, 0.0);
            tess.addVertexWithUV(width, 0, 0.0, 1.0, 1.0);
            tess.addVertexWithUV(0, 0, 0.0, 0.0, 1.0);
            tess.draw();

            GL11.glDepthMask(true);
            GL11.glEnable(GL11.GL_CULL_FACE);

            qgpFullscreenShader.stop();

            OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit + 1);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);

            GL11.glPopMatrix();
        }
    }
}
