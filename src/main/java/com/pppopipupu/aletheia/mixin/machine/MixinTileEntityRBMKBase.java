package com.pppopipupu.aletheia.mixin.machine;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.hbm.tileentity.machine.rbmk.TileEntityRBMKBase;
import com.pppopipupu.aletheia.AletheiaQGPMeltdownHandler;
import com.pppopipupu.aletheia.entity.EntityQGPSingularity;

@Mixin(value = TileEntityRBMKBase.class, remap = false)
public class MixinTileEntityRBMKBase {

    @Inject(method = "meltdown", at = @At("RETURN"))
    private void aletheia$onQgpMeltdown(CallbackInfo ci) {
        if (AletheiaQGPMeltdownHandler.qgpMeltdown) {
            TileEntityRBMKBase rbmk = (TileEntityRBMKBase) (Object) this;
            if (rbmk.getWorldObj() != null && !rbmk.getWorldObj().isRemote) {
                EntityQGPSingularity singularity = new EntityQGPSingularity(rbmk.getWorldObj());
                singularity.setPosition(rbmk.xCoord + 0.5D, rbmk.yCoord + 5.0D, rbmk.zCoord + 0.5D);
                rbmk.getWorldObj()
                    .spawnEntityInWorld(singularity);
            }
            AletheiaQGPMeltdownHandler.qgpMeltdown = false;
        }
    }
}
