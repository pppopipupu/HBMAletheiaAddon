package com.pppopipupu.aletheia.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.hbm.inventory.fluid.FluidType;
import com.hbm.render.shader.Shader;
import com.hbm.render.tileentity.RenderFluidTank;
import com.hbm.tileentity.machine.storage.TileEntityMachineFluidTank;

@Mixin(value = RenderFluidTank.class, remap = false)
public abstract class MixinRenderFluidTank extends TileEntitySpecialRenderer {

    @Unique
    private static Shader aletheia$qgpShader = null;

    @Inject(method = "renderTileEntityAt", at = @At("HEAD"))
    private void aletheia$renderTileEntityAtHead(TileEntity tileEntity, double x, double y, double z, float f,
        CallbackInfo ci) {
        if (!(tileEntity instanceof TileEntityMachineFluidTank)) return;
        TileEntityMachineFluidTank tank = (TileEntityMachineFluidTank) tileEntity;
        FluidType type = tank.tank.getTankType();
        boolean isQgp = type != null && "QGP".equals(type.getName());

        if (isQgp) {
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

            if (aletheia$qgpShader == null) {
                aletheia$qgpShader = new Shader(
                    new ResourceLocation("aletheia", "shaders/default.vert"),
                    new ResourceLocation("aletheia", "shaders/distortion.frag"));
            }

            aletheia$qgpShader.use();
            aletheia$qgpShader.setUniform1f("iTime", (System.currentTimeMillis() % 100000) / 1000.0F);
            aletheia$qgpShader.setUniform1i("u_screenTexture", 1);
            aletheia$qgpShader.setUniform1f("u_screenWidth", screenWidth);
            aletheia$qgpShader.setUniform1f("u_screenHeight", screenHeight);
            aletheia$qgpShader.setUniform1i("u_hasFbo", hasFbo ? 1 : 0);

            int loc = aletheia$qgpShader.getUniformLocation("u_iconUvRange");
            GL20.glUniform4f(loc, 0.0F, 1.0F, 0.0F, 1.0F);
        }
    }

    @Inject(method = "renderTileEntityAt", at = @At("RETURN"))
    private void aletheia$renderTileEntityAtReturn(TileEntity tileEntity, double x, double y, double z, float f,
        CallbackInfo ci) {
        if (!(tileEntity instanceof TileEntityMachineFluidTank)) return;
        TileEntityMachineFluidTank tank = (TileEntityMachineFluidTank) tileEntity;
        FluidType type = tank.tank.getTankType();
        boolean isQgp = type != null && "QGP".equals(type.getName());

        if (isQgp && aletheia$qgpShader != null) {
            aletheia$qgpShader.stop();
            Minecraft mc = Minecraft.getMinecraft();
            boolean hasFbo = OpenGlHelper.isFramebufferEnabled() && mc.getFramebuffer() != null;
            if (hasFbo) {
                OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit + 1);
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
            }
        }
    }
}
