package com.pppopipupu.aletheia.mixin;

import net.minecraft.item.Item;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.hbm.main.MainRegistry;
import com.pppopipupu.aletheia.block.AletheiaBlocks;
import com.pppopipupu.aletheia.item.AletheiaItems;

import cpw.mods.fml.common.event.FMLMissingMappingsEvent;
import cpw.mods.fml.common.event.FMLMissingMappingsEvent.MissingMapping;
import cpw.mods.fml.common.registry.GameRegistry;

@Mixin(value = MainRegistry.class, remap = false)
public class MixinMainRegistry {

    @Inject(method = "handleMissingMappings", at = @At("RETURN"))
    private void aletheia$handleMissingMappings(FMLMissingMappingsEvent event, CallbackInfo ci) {
        MainRegistry.logger.info("Aletheia missing mappings remapper started processing.");
        for (MissingMapping mapping : event.get()) {
            MainRegistry.logger.info("Processing missing mapping: " + mapping.name + " of type " + mapping.type);
            if (mapping.type == GameRegistry.Type.ITEM) {
                if ("hbm:item.bucket_qgp".equals(mapping.name)) {
                    MainRegistry.logger.info("Remapping item: " + mapping.name + " to " + AletheiaItems.bucket_qgp);
                    mapping.remap(AletheiaItems.bucket_qgp);
                } else if ("hbm:item.qgp_mining_bomb".equals(mapping.name)) {
                    MainRegistry.logger
                        .info("Remapping item: " + mapping.name + " to " + AletheiaItems.qgp_mining_bomb);
                    mapping.remap(AletheiaItems.qgp_mining_bomb);
                } else if ("hbm:item.upgrade_ultimate".equals(mapping.name)) {
                    MainRegistry.logger
                        .info("Remapping item: " + mapping.name + " to " + AletheiaItems.upgrade_ultimate);
                    mapping.remap(AletheiaItems.upgrade_ultimate);
                } else if ("hbm:item.gun_pppop".equals(mapping.name)) {
                    MainRegistry.logger.info("Remapping item: " + mapping.name + " to " + AletheiaItems.gun_pppop);
                    mapping.remap(AletheiaItems.gun_pppop);
                } else if ("hbm:item.ams_muzzle".equals(mapping.name)) {
                    MainRegistry.logger.info("Remapping item: " + mapping.name + " to " + AletheiaItems.ams_muzzle);
                    mapping.remap(AletheiaItems.ams_muzzle);
                } else if ("hbm:item.ams_focus_limiter".equals(mapping.name)) {
                    MainRegistry.logger
                        .info("Remapping item: " + mapping.name + " to " + AletheiaItems.ams_focus_limiter);
                    mapping.remap(AletheiaItems.ams_focus_limiter);
                } else if ("hbm:item.ams_focus_booster".equals(mapping.name)) {
                    MainRegistry.logger
                        .info("Remapping item: " + mapping.name + " to " + AletheiaItems.ams_focus_booster);
                    mapping.remap(AletheiaItems.ams_focus_booster);
                } else if ("hbm:item.night_vision_mk2".equals(mapping.name)) {
                    MainRegistry.logger
                        .info("Remapping item: " + mapping.name + " to " + AletheiaItems.night_vision_mk2);
                    mapping.remap(AletheiaItems.night_vision_mk2);
                } else if ("hbm:item.billet_rs1".equals(mapping.name)) {
                    MainRegistry.logger.info("Remapping item: " + mapping.name + " to " + AletheiaItems.billet_rs1);
                    mapping.remap(AletheiaItems.billet_rs1);
                } else if ("hbm:item.billet_rs2".equals(mapping.name)) {
                    MainRegistry.logger.info("Remapping item: " + mapping.name + " to " + AletheiaItems.billet_rs2);
                    mapping.remap(AletheiaItems.billet_rs2);
                } else if ("hbm:item.billet_rs3".equals(mapping.name)) {
                    MainRegistry.logger.info("Remapping item: " + mapping.name + " to " + AletheiaItems.billet_rs3);
                    mapping.remap(AletheiaItems.billet_rs3);
                } else if ("hbm:item.rbmk_pellet_rs1".equals(mapping.name)) {
                    MainRegistry.logger
                        .info("Remapping item: " + mapping.name + " to " + AletheiaItems.rbmk_pellet_rs1);
                    mapping.remap(AletheiaItems.rbmk_pellet_rs1);
                } else if ("hbm:item.rbmk_pellet_rs2".equals(mapping.name)) {
                    MainRegistry.logger
                        .info("Remapping item: " + mapping.name + " to " + AletheiaItems.rbmk_pellet_rs2);
                    mapping.remap(AletheiaItems.rbmk_pellet_rs2);
                } else if ("hbm:item.rbmk_pellet_rs3".equals(mapping.name)) {
                    MainRegistry.logger
                        .info("Remapping item: " + mapping.name + " to " + AletheiaItems.rbmk_pellet_rs3);
                    mapping.remap(AletheiaItems.rbmk_pellet_rs3);
                } else if ("hbm:item.rbmk_fuel_rs1".equals(mapping.name)) {
                    MainRegistry.logger.info("Remapping item: " + mapping.name + " to " + AletheiaItems.rbmk_fuel_rs1);
                    mapping.remap(AletheiaItems.rbmk_fuel_rs1);
                } else if ("hbm:item.rbmk_fuel_rs2".equals(mapping.name)) {
                    MainRegistry.logger.info("Remapping item: " + mapping.name + " to " + AletheiaItems.rbmk_fuel_rs2);
                    mapping.remap(AletheiaItems.rbmk_fuel_rs2);
                } else if ("hbm:item.rbmk_fuel_rs3".equals(mapping.name)) {
                    MainRegistry.logger.info("Remapping item: " + mapping.name + " to " + AletheiaItems.rbmk_fuel_rs3);
                    mapping.remap(AletheiaItems.rbmk_fuel_rs3);
                } else if ("hbm:item.spawn_maskman".equals(mapping.name)) {
                    MainRegistry.logger.info("Remapping item: " + mapping.name + " to " + AletheiaItems.spawn_maskman);
                    mapping.remap(AletheiaItems.spawn_maskman);
                } else if ("hbm:item.recipe_icon".equals(mapping.name)) {
                    MainRegistry.logger.info("Remapping item: " + mapping.name + " to " + AletheiaItems.recipe_icon);
                    mapping.remap(AletheiaItems.recipe_icon);
                } else if ("hbm:item.block_sodium".equals(mapping.name)) {
                    Item targetItem = Item.getItemFromBlock(AletheiaBlocks.block_sodium);
                    MainRegistry.logger.info("Remapping item block: " + mapping.name + " to " + targetItem);
                    mapping.remap(targetItem);
                } else if ("hbm:item.block_strontium".equals(mapping.name)) {
                    Item targetItem = Item.getItemFromBlock(AletheiaBlocks.block_strontium);
                    MainRegistry.logger.info("Remapping item block: " + mapping.name + " to " + targetItem);
                    mapping.remap(targetItem);
                } else if ("hbm:item.block_neodymium".equals(mapping.name)) {
                    Item targetItem = Item.getItemFromBlock(AletheiaBlocks.block_neodymium);
                    MainRegistry.logger.info("Remapping item block: " + mapping.name + " to " + targetItem);
                    mapping.remap(targetItem);
                }
            } else if (mapping.type == GameRegistry.Type.BLOCK) {
                if ("hbm:tile.qgp_block".equals(mapping.name)) {
                    MainRegistry.logger.info("Remapping block: " + mapping.name + " to " + AletheiaBlocks.qgp_block);
                    mapping.remap(AletheiaBlocks.qgp_block);
                } else if ("hbm:tile.ams_base".equals(mapping.name)) {
                    MainRegistry.logger.info("Remapping block: " + mapping.name + " to " + AletheiaBlocks.ams_base);
                    mapping.remap(AletheiaBlocks.ams_base);
                } else if ("hbm:tile.ams_emitter".equals(mapping.name)) {
                    MainRegistry.logger.info("Remapping block: " + mapping.name + " to " + AletheiaBlocks.ams_emitter);
                    mapping.remap(AletheiaBlocks.ams_emitter);
                } else if ("hbm:tile.ams_limiter".equals(mapping.name)) {
                    MainRegistry.logger.info("Remapping block: " + mapping.name + " to " + AletheiaBlocks.ams_limiter);
                    mapping.remap(AletheiaBlocks.ams_limiter);
                } else if ("hbm:tile.machine_schrabidium_transmutator".equals(mapping.name)) {
                    MainRegistry.logger.info(
                        "Remapping block: " + mapping.name + " to " + AletheiaBlocks.machine_schrabidium_transmutator);
                    mapping.remap(AletheiaBlocks.machine_schrabidium_transmutator);
                } else if ("hbm:tile.block_sodium".equals(mapping.name)) {
                    MainRegistry.logger.info("Remapping block: " + mapping.name + " to " + AletheiaBlocks.block_sodium);
                    mapping.remap(AletheiaBlocks.block_sodium);
                } else if ("hbm:tile.block_strontium".equals(mapping.name)) {
                    MainRegistry.logger
                        .info("Remapping block: " + mapping.name + " to " + AletheiaBlocks.block_strontium);
                    mapping.remap(AletheiaBlocks.block_strontium);
                } else if ("hbm:tile.block_neodymium".equals(mapping.name)) {
                    MainRegistry.logger
                        .info("Remapping block: " + mapping.name + " to " + AletheiaBlocks.block_neodymium);
                    mapping.remap(AletheiaBlocks.block_neodymium);
                }
            }
        }
    }
}
