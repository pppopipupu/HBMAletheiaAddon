package com.pppopipupu.aletheia.mixin;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.hbm.entity.grenade.EntityDisperserCanister;
import com.hbm.entity.grenade.EntityGrenadeBouncyGeneric;
import com.hbm.entity.grenade.IGenericGrenade;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.render.entity.projectile.RenderGenericGrenade;
import com.hbm.render.shader.Shader;

@Mixin(value = RenderGenericGrenade.class, remap = false)
public abstract class MixinRenderGenericGrenade extends Render {

    private static Shader aletheia$qgpShader;
    private static Shader aletheia$rainbowTntShader;

    @Inject(method = "getEntityTexture", at = @At("HEAD"), cancellable = true, remap = true)
    private void aletheia$getEntityTexture(Entity entity, CallbackInfoReturnable<ResourceLocation> cir) {
        if (entity instanceof EntityGrenadeBouncyGeneric) {
            EntityGrenadeBouncyGeneric bouncy = (EntityGrenadeBouncyGeneric) entity;
            Item grenadeItem = bouncy.getGrenade();
            if (grenadeItem != null && grenadeItem.getUnlocalizedName() != null
                && grenadeItem.getUnlocalizedName()
                    .contains("qgp_mining_bomb")) {
                cir.setReturnValue(TextureMap.locationBlocksTexture);
            }
        }
    }

    @Inject(method = "doRender", at = @At("HEAD"), cancellable = true, remap = true)
    private void aletheia$doRender(Entity entity, double x, double y, double z, float f0, float f1, CallbackInfo ci) {
        boolean disperser = entity instanceof EntityDisperserCanister;
        boolean isMiningBomb = false;

        if (entity instanceof EntityGrenadeBouncyGeneric) {
            EntityGrenadeBouncyGeneric bouncy = (EntityGrenadeBouncyGeneric) entity;
            Item grenadeItem = bouncy.getGrenade();
            if (grenadeItem != null && grenadeItem.getUnlocalizedName() != null
                && grenadeItem.getUnlocalizedName()
                    .contains("qgp_mining_bomb")) {
                isMiningBomb = true;
            }
        }

        if (disperser || isMiningBomb) {
            for (int i = 0; i < (disperser ? 2 : 1); i++) {
                IIcon iicon = null;
                if (disperser) {
                    EntityDisperserCanister canister = (EntityDisperserCanister) entity;
                    FluidType fluid = canister.getFluid();
                    if (fluid != null) {
                        iicon = canister.getType()
                            .getIconFromDamageForRenderPass(fluid.getID(), i);
                        if (i == 1) {
                            int hex = fluid.getColor();
                            int r = (hex & 0xFF0000) >> 16;
                            int g = (hex & 0xFF00) >> 8;
                            int b = (hex & 0xFF);
                            GL11.glColor3b((byte) (r / 2), (byte) (g / 2), (byte) (b / 2));
                        }
                    }
                } else {
                    IGenericGrenade grenade = (IGenericGrenade) entity;
                    Item grenadeItem = grenade.getGrenade();
                    if (grenadeItem != null) {
                        iicon = Blocks.tnt.getIcon(2, 0);
                    }
                }

                if (iicon != null) {
                    boolean isQGP = false;
                    boolean isRainbowTNT = false;

                    if (disperser && i == 1) {
                        EntityDisperserCanister canister = (EntityDisperserCanister) entity;
                        FluidType fl = canister.getFluid();
                        if (fl != null && "QGP".equals(fl.getName())) {
                            isQGP = true;
                        }
                    }

                    if (isMiningBomb) {
                        isRainbowTNT = true;
                    }

                    if (isQGP) {
                        if (aletheia$qgpShader == null) {
                            aletheia$qgpShader = new Shader(
                                new ResourceLocation("aletheia", "shaders/qgp.vert"),
                                new ResourceLocation("aletheia", "shaders/qgp.frag"));
                        }
                        aletheia$qgpShader.use();
                        aletheia$qgpShader.setUniform1f("iTime", (System.currentTimeMillis() % 100000) / 1000.0F);
                    }

                    if (isRainbowTNT) {
                        if (aletheia$rainbowTntShader == null) {
                            aletheia$rainbowTntShader = new Shader(
                                new ResourceLocation("aletheia", "shaders/qgp.vert"),
                                new ResourceLocation("aletheia", "shaders/rainbow_tnt.frag"));
                        }
                        aletheia$rainbowTntShader.use();
                        aletheia$rainbowTntShader
                            .setUniform1f("iTime", (System.currentTimeMillis() % 100000) / 1000.0F);
                    }

                    GL11.glPushMatrix();
                    GL11.glTranslatef((float) x, (float) y, (float) z);
                    GL11.glEnable(GL12.GL_RESCALE_NORMAL);
                    GL11.glScalef(0.5F, 0.5F, 0.5F);
                    this.bindEntityTexture(entity);
                    Tessellator tessellator = Tessellator.instance;

                    aletheia$renderItemHelper(tessellator, iicon);
                    GL11.glDisable(GL12.GL_RESCALE_NORMAL);
                    GL11.glPopMatrix();

                    if (isQGP && aletheia$qgpShader != null) {
                        aletheia$qgpShader.stop();
                    }
                    if (isRainbowTNT && aletheia$rainbowTntShader != null) {
                        aletheia$rainbowTntShader.stop();
                    }
                }

                GL11.glColor3f(1F, 1F, 1F);
            }
            ci.cancel();
        }
    }

    private void aletheia$renderItemHelper(Tessellator tess, IIcon icon) {
        float minU = icon.getMinU();
        float maxU = icon.getMaxU();
        float minV = icon.getMinV();
        float maxV = icon.getMaxV();
        float max = 1.0F;
        float offX = 0.5F;
        float offY = 0.25F;

        GL11.glRotatef(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);

        tess.startDrawingQuads();
        tess.setNormal(0.0F, 1.0F, 0.0F);
        tess.addVertexWithUV((double) (0.0F - offX), (double) (0.0F - offY), 0.0D, (double) minU, (double) maxV);
        tess.addVertexWithUV((double) (max - offX), (double) (0.0F - offY), 0.0D, (double) maxU, (double) maxV);
        tess.addVertexWithUV((double) (max - offX), (double) (max - offY), 0.0D, (double) maxU, (double) minV);
        tess.addVertexWithUV((double) (0.0F - offX), (double) (max - offY), 0.0D, (double) minU, (double) minV);
        tess.draw();
    }
}
