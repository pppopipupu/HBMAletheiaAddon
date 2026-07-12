package com.pppopipupu.aletheia.mixin;

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
        for (MissingMapping mapping : event.get()) {
            if (mapping.type == GameRegistry.Type.ITEM) {
                if ("hbm:bucket_qgp".equals(mapping.name)) {
                    mapping.remap(AletheiaItems.bucket_qgp);
                } else if ("hbm:qgp_mining_bomb".equals(mapping.name)) {
                    mapping.remap(AletheiaItems.qgp_mining_bomb);
                } else if ("hbm:upgrade_ultimate".equals(mapping.name)) {
                    mapping.remap(AletheiaItems.upgrade_ultimate);
                } else if ("hbm:gun_pppop".equals(mapping.name)) {
                    mapping.remap(AletheiaItems.gun_pppop);
                } else if ("hbm:ams_muzzle".equals(mapping.name)) {
                    mapping.remap(AletheiaItems.ams_muzzle);
                } else if ("hbm:ams_focus_limiter".equals(mapping.name)) {
                    mapping.remap(AletheiaItems.ams_focus_limiter);
                } else if ("hbm:ams_focus_booster".equals(mapping.name)) {
                    mapping.remap(AletheiaItems.ams_focus_booster);
                } else if ("hbm:night_vision_mk2".equals(mapping.name)) {
                    mapping.remap(AletheiaItems.night_vision_mk2);
                } else if ("hbm:billet_rs1".equals(mapping.name)) {
                    mapping.remap(AletheiaItems.billet_rs1);
                } else if ("hbm:billet_rs2".equals(mapping.name)) {
                    mapping.remap(AletheiaItems.billet_rs2);
                } else if ("hbm:billet_rs3".equals(mapping.name)) {
                    mapping.remap(AletheiaItems.billet_rs3);
                } else if ("hbm:rbmk_pellet_rs1".equals(mapping.name)) {
                    mapping.remap(AletheiaItems.rbmk_pellet_rs1);
                } else if ("hbm:rbmk_pellet_rs2".equals(mapping.name)) {
                    mapping.remap(AletheiaItems.rbmk_pellet_rs2);
                } else if ("hbm:rbmk_pellet_rs3".equals(mapping.name)) {
                    mapping.remap(AletheiaItems.rbmk_pellet_rs3);
                } else if ("hbm:rbmk_fuel_rs1".equals(mapping.name)) {
                    mapping.remap(AletheiaItems.rbmk_fuel_rs1);
                } else if ("hbm:rbmk_fuel_rs2".equals(mapping.name)) {
                    mapping.remap(AletheiaItems.rbmk_fuel_rs2);
                } else if ("hbm:rbmk_fuel_rs3".equals(mapping.name)) {
                    mapping.remap(AletheiaItems.rbmk_fuel_rs3);
                } else if ("hbm:spawn_maskman".equals(mapping.name)) {
                    mapping.remap(AletheiaItems.spawn_maskman);
                } else if ("hbm:recipe_icon".equals(mapping.name)) {
                    mapping.remap(AletheiaItems.recipe_icon);
                } else if ("hbm:item.block_sodium".equals(mapping.name)) {
                    mapping.remap(AletheiaBlocks.block_sodium);
                } else if ("hbm:item.block_strontium".equals(mapping.name)) {
                    mapping.remap(AletheiaBlocks.block_strontium);
                } else if ("hbm:item.block_neodymium".equals(mapping.name)) {
                    mapping.remap(AletheiaBlocks.block_neodymium);
                }
            } else if (mapping.type == GameRegistry.Type.BLOCK) {
                if ("hbm:qgp_block".equals(mapping.name)) {
                    mapping.remap(AletheiaBlocks.qgp_block);
                } else if ("hbm:ams_base".equals(mapping.name)) {
                    mapping.remap(AletheiaBlocks.ams_base);
                } else if ("hbm:ams_emitter".equals(mapping.name)) {
                    mapping.remap(AletheiaBlocks.ams_emitter);
                } else if ("hbm:ams_limiter".equals(mapping.name)) {
                    mapping.remap(AletheiaBlocks.ams_limiter);
                } else if ("hbm:machine_schrabidium_transmutator".equals(mapping.name)) {
                    mapping.remap(AletheiaBlocks.machine_schrabidium_transmutator);
                } else if ("hbm:tile.block_sodium".equals(mapping.name)) {
                    mapping.remap(AletheiaBlocks.block_sodium);
                } else if ("hbm:tile.block_strontium".equals(mapping.name)) {
                    mapping.remap(AletheiaBlocks.block_strontium);
                } else if ("hbm:tile.block_neodymium".equals(mapping.name)) {
                    mapping.remap(AletheiaBlocks.block_neodymium);
                }
            }
        }
    }
}
