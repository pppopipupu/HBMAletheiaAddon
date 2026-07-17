package com.pppopipupu.aletheia.mixin;

import java.util.HashMap;

import net.minecraft.block.Block;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.hbm.blocks.ModBlocks;
import com.hbm.items.machine.ItemPWRFuel.EnumPWRFuel;
import com.hbm.tileentity.machine.TileEntityPWRController;
import com.hbm.util.fauxpointtwelve.BlockPos;
import com.hbm.util.function.Function;
import com.hbm.util.function.Function.FunctionSqrt;

@Mixin(value = TileEntityPWRController.class, remap = false)
public class MixinTileEntityPWRController {

    @Shadow(remap = false)
    public int heatsinkCount;
    @Shadow(remap = false)
    public long coreHeatCapacity;
    @Shadow(remap = false)
    public int typeLoaded;

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

    @Redirect(
        method = "updateEntity",
        at = @At(
            value = "FIELD",
            target = "Lcom/hbm/items/machine/ItemPWRFuel$EnumPWRFuel;function:Lcom/hbm/util/function/Function;",
            opcode = org.objectweb.asm.Opcodes.GETFIELD))
    private Function aletheia$pwrFunction(EnumPWRFuel fuel) {
        if (this.typeLoaded == 99) {
            return new FunctionSqrt(50.0);
        }
        return fuel.function;
    }

    @Redirect(
        method = "updateEntity",
        at = @At(
            value = "FIELD",
            target = "Lcom/hbm/items/machine/ItemPWRFuel$EnumPWRFuel;heatEmission:D",
            opcode = org.objectweb.asm.Opcodes.GETFIELD))
    private double aletheia$pwrHeatEmission(EnumPWRFuel fuel) {
        if (this.typeLoaded == 99) {
            return 30.0D;
        }
        return fuel.heatEmission;
    }

    @Redirect(
        method = "updateEntity",
        at = @At(
            value = "FIELD",
            target = "Lcom/hbm/items/machine/ItemPWRFuel$EnumPWRFuel;yield:D",
            opcode = org.objectweb.asm.Opcodes.GETFIELD))
    private double aletheia$pwrYield(EnumPWRFuel fuel) {
        if (this.typeLoaded == 99) {
            return 1500000000.0D;
        }
        return fuel.yield;
    }
}
