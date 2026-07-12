package com.pppopipupu.aletheia.mixin;

import java.util.HashMap;

import net.minecraft.block.Block;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.hbm.blocks.ModBlocks;
import com.hbm.tileentity.machine.TileEntityPWRController;
import com.hbm.util.fauxpointtwelve.BlockPos;

@Mixin(value = TileEntityPWRController.class, remap = false)
public class MixinTileEntityPWRController {

    @Shadow(remap = false)
    public int heatsinkCount;
    @Shadow(remap = false)
    public long coreHeatCapacity;

    @Inject(method = "setup", at = @At("RETURN"))
    private void aletheia$fixHeatsinkCount(HashMap<BlockPos, Block> partMap, HashMap<BlockPos, Block> rodMap,
        CallbackInfo ci) {
        int realCount = 0;
        for (Block block : partMap.values()) {
            if (block == ModBlocks.pwr_heatsink) realCount++;
        }
        this.heatsinkCount = realCount;
        this.coreHeatCapacity = TileEntityPWRController.coreHeatCapacityBase
            + this.heatsinkCount * (TileEntityPWRController.coreHeatCapacityBase / 20);
    }
}
