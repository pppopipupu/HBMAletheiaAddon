package com.pppopipupu.aletheia.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

import com.hbm.blocks.BlockDummyable;
import com.hbm.inventory.FluidStack;
import com.hbm.inventory.recipes.loader.GenericRecipe;
import com.hbm.render.item.ItemRenderBase;
import com.hbm.render.loader.HFRWavefrontObject;
import com.hbm.render.shader.Shader;
import com.hbm.render.tileentity.IItemRendererProvider;
import com.pppopipupu.aletheia.Aletheia;
import com.pppopipupu.aletheia.block.AletheiaBlocks;
import com.pppopipupu.aletheia.machine.agrichemplant.TileEntityMachineAgriChemicalPlant;

public class RenderAgriChemicalPlant extends TileEntitySpecialRenderer implements IItemRendererProvider {

    private static final ResourceLocation bodyTex = new ResourceLocation(
        Aletheia.MODID,
        "textures/models/machines/chemplant_base_new.png");
    private static final ResourceLocation spinnerTex = new ResourceLocation(
        Aletheia.MODID,
        "textures/models/machines/chemplant_spinner_new.png");
    private static final ResourceLocation pistonTex = new ResourceLocation(
        Aletheia.MODID,
        "textures/models/machines/chemplant_piston_new.png");

    private static final IModelCustom body = new HFRWavefrontObject(
        new ResourceLocation(Aletheia.MODID, "models/machines/chemplant_new_body.obj")).asVBO();
    private static final IModelCustom spinner = AdvancedModelLoader
        .loadModel(new ResourceLocation(Aletheia.MODID, "models/machines/chemplant_new_spinner.obj"));
    private static final IModelCustom piston = AdvancedModelLoader
        .loadModel(new ResourceLocation(Aletheia.MODID, "models/machines/chemplant_new_piston.obj"));

    private static IModelCustom fluidModel = null;
    static {
        try {
            fluidModel = AdvancedModelLoader
                .loadModel(new ResourceLocation(Aletheia.MODID, "models/machines/chemplant_new_fluid.hmf"));
        } catch (Exception ex) {
            fluidModel = null;
        }
    }

    private static Shader agriShader = null;
    private static final ResourceLocation agriVert = new ResourceLocation(Aletheia.MODID, "shaders/agri_plant.vert");
    private static final ResourceLocation agriFrag = new ResourceLocation(
        Aletheia.MODID,
        "shaders/agri_plant_green.frag");

    private static void ensureShader() {
        if (agriShader == null) agriShader = new Shader(agriVert, agriFrag);
    }

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float interp) {
        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5, y, z + 0.5);
        GL11.glRotated(90, 0, 1, 0);
        GL11.glShadeModel(GL11.GL_SMOOTH);

        switch (tileEntity.getBlockMetadata() - BlockDummyable.offset) {
            case 2:
                GL11.glRotatef(0, 0F, 1F, 0F);
                break;
            case 4:
                GL11.glRotatef(90, 0F, 1F, 0F);
                break;
            case 3:
                GL11.glRotatef(180, 0F, 1F, 0F);
                break;
            case 5:
                GL11.glRotatef(270, 0F, 1F, 0F);
                break;
        }

        TileEntityMachineAgriChemicalPlant chemplant = (TileEntityMachineAgriChemicalPlant) tileEntity;
        float anim = chemplant.prevAnim + (chemplant.anim - chemplant.prevAnim) * interp;
        GenericRecipe recipe = chemplant.chemplantModule.getRecipe();

        ensureShader();
        float time = (System.currentTimeMillis() % 100000) / 1000.0F;

        bindTexture(bodyTex);
        agriShader.use();
        agriShader.setUniform1f("iTime", time);
        body.renderAll();
        agriShader.stop();

        GL11.glPushMatrix();
        GL11.glTranslated(0.5, 0, 0.5);
        GL11.glRotated((anim * 15) % 360D, 0, 1, 0);
        GL11.glTranslated(-0.5, 0, -0.5);
        bindTexture(spinnerTex);
        agriShader.use();
        agriShader.setUniform1f("iTime", time);
        spinner.renderAll();
        agriShader.stop();
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        double bob = chemplant.didProcess ? (Math.sin(anim * 0.2) * 0.1 - 0.1) : 0;
        GL11.glTranslated(0, bob, 0);
        bindTexture(pistonTex);
        agriShader.use();
        agriShader.setUniform1f("iTime", time);
        piston.renderAll();
        agriShader.stop();
        GL11.glPopMatrix();

        if (chemplant.didProcess && fluidModel != null && recipe != null) {
            int r = 60, g = 255, b = 120;
            if (recipe.outputFluid != null) for (FluidStack stack : recipe.outputFluid) {
                java.awt.Color color = new java.awt.Color(stack.type.getColor());
                r += color.getRed();
                g += color.getGreen();
                b += color.getBlue();
            }
            else if (recipe.inputFluid != null) for (FluidStack stack : recipe.inputFluid) {
                java.awt.Color color = new java.awt.Color(stack.type.getColor());
                r += color.getRed();
                g += color.getGreen();
                b += color.getBlue();
            }

            GL11.glPushMatrix();
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            GL11.glColor4f(r / 255F, g / 255F, b / 255F, 0.5F);
            GL11.glDepthMask(false);
            bindTexture(bodyTex);
            agriShader.use();
            agriShader.setUniform1f("iTime", time);
            fluidModel.renderAll();
            agriShader.stop();
            GL11.glDepthMask(true);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glPopMatrix();
        }

        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glPopMatrix();
    }

    @Override
    public Item getItemForRenderer() {
        return Item.getItemFromBlock(AletheiaBlocks.machine_agri_chem_plant);
    }

    @Override
    public IItemRenderer getRenderer() {

        return new ItemRenderBase() {

            public void renderInventory() {
                GL11.glTranslated(0, -2.75, 0);
                GL11.glScaled(4.5, 4.5, 4.5);
            }

            public void renderCommonWithStack(ItemStack item) {
                GL11.glRotated(90, 0, 1, 0);
                GL11.glScaled(0.75, 0.75, 0.75);
                GL11.glShadeModel(GL11.GL_SMOOTH);
                float time = (System.currentTimeMillis() % 100000) / 1000.0F;
                ensureShader();

                Minecraft.getMinecraft()
                    .getTextureManager()
                    .bindTexture(bodyTex);
                agriShader.use();
                agriShader.setUniform1f("iTime", time);
                body.renderAll();
                spinner.renderAll();
                piston.renderAll();
                agriShader.stop();

                GL11.glShadeModel(GL11.GL_FLAT);
            }
        };
    }
}
