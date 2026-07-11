package com.pppopipupu.aletheia.mixin;

import java.util.HashMap;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;

@Mixin(value = Fluids.class, remap = false)
public abstract class MixinFluids {

    @Shadow
    public static HashMap<Integer, FluidType> idMapping;
    @Shadow
    public static List<FluidType> registerOrder;

    @Inject(method = "getInOrder", at = @At("HEAD"), cancellable = true)
    private static void aletheia$getInOrder(boolean nice, CallbackInfoReturnable<FluidType[]> cir) {
        int size = idMapping.size();
        FluidType[] all = new FluidType[size];
        for (int i = 0; i < size; i++) {
            FluidType type = null;
            if (nice) {
                if (i < Fluids.metaOrder.size()) {
                    type = Fluids.metaOrder.get(i);
                }
            } else {
                if (i < registerOrder.size()) {
                    type = registerOrder.get(i);
                }
            }
            if (type == null) {
                type = Fluids.NONE;
            }
            all[i] = type;
        }
        cir.setReturnValue(all);
    }
}
