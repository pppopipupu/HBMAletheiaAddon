package com.pppopipupu.aletheia.mixin.machine;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.hbm.inventory.UpgradeManagerNT;
import com.hbm.inventory.recipes.CentrifugeRecipes;
import com.hbm.items.machine.ItemMachineUpgrade.UpgradeType;
import com.hbm.lib.Library;
import com.hbm.tileentity.TileEntityMachineBase;
import com.hbm.tileentity.machine.TileEntityMachineCentrifuge;
import com.pppopipupu.aletheia.interfaces.IUpgradeManagerAccess;

import api.hbm.energymk2.IEnergyReceiverMK2;

@Mixin(value = TileEntityMachineCentrifuge.class, remap = false)
public abstract class MixinTileEntityMachineCentrifuge extends TileEntityMachineBase implements IEnergyReceiverMK2 {

    public MixinTileEntityMachineCentrifuge(int size) {
        super(size);
    }

    @Shadow
    public long power;
    @Shadow
    public int progress;
    @Shadow
    public boolean isProgressing;
    @Shadow
    public UpgradeManagerNT upgradeManager;

    @Shadow
    public abstract boolean hasPower();

    @Shadow
    public abstract boolean isProcessing();

    @Inject(method = "canProcess", at = @At("HEAD"), cancellable = true)
    private void aletheia$canProcess(CallbackInfoReturnable<Boolean> cir) {
        int uCount = ((IUpgradeManagerAccess) upgradeManager).aletheia$getUltimateCount();
        if (uCount > 0) {
            if (slots[0] == null) {
                cir.setReturnValue(false);
                return;
            }
            ItemStack[] out = CentrifugeRecipes.getOutput(slots[0]);
            if (out == null) {
                cir.setReturnValue(false);
                return;
            }

            int mult = ((IUpgradeManagerAccess) upgradeManager).aletheia$getProductionMult();
            for (int i = 0; i < Math.min(4, out.length); i++) {
                if (slots[i + 2] == null) continue;

                if (out[i] == null) continue;

                if (slots[i + 2].isItemEqual(out[i])
                    && slots[i + 2].stackSize + out[i].stackSize * mult <= out[i].getMaxStackSize()) continue;

                cir.setReturnValue(false);
                return;
            }
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "processItem", at = @At("HEAD"), cancellable = true)
    private void aletheia$processItem(CallbackInfo ci) {
        int uCount = ((IUpgradeManagerAccess) upgradeManager).aletheia$getUltimateCount();
        if (uCount > 0) {
            ItemStack[] out = CentrifugeRecipes.getOutput(slots[0]);
            int mult = ((IUpgradeManagerAccess) upgradeManager).aletheia$getProductionMult();

            for (int i = 0; i < Math.min(4, out.length); i++) {
                if (out[i] == null) continue;

                int sizeToAdd = out[i].stackSize * mult;
                if (slots[i + 2] == null) {
                    slots[i + 2] = out[i].copy();
                    slots[i + 2].stackSize = sizeToAdd;
                } else {
                    slots[i + 2].stackSize += sizeToAdd;
                }
            }

            ((TileEntityMachineCentrifuge) (Object) this).decrStackSize(0, 1);
            ((TileEntity) (Object) this).markDirty();
            ci.cancel();
        }
    }

    @Inject(method = "updateEntity", at = @At("HEAD"), cancellable = true, remap = true)
    private void aletheia$updateEntity(CallbackInfo ci) {
        TileEntityMachineCentrifuge te = (TileEntityMachineCentrifuge) (Object) this;
        if (!te.getWorldObj().isRemote) {
            upgradeManager.checkSlots(slots, 6, 7);
            int uCount = ((IUpgradeManagerAccess) upgradeManager).aletheia$getUltimateCount();

            if (uCount > 0) {
                for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
                    this.trySubscribe(
                        te.getWorldObj(),
                        te.xCoord + dir.offsetX,
                        te.yCoord + dir.offsetY,
                        te.zCoord + dir.offsetZ,
                        dir);
                }

                power = Library.chargeTEFromItems(slots, 1, power, TileEntityMachineCentrifuge.maxPower);

                int consumption = TileEntityMachineCentrifuge.baseConsumption;
                int speed = 1;

                speed += upgradeManager.getLevel(UpgradeType.SPEED);
                consumption += upgradeManager.getLevel(UpgradeType.SPEED) * TileEntityMachineCentrifuge.baseConsumption;

                int over = upgradeManager.getLevel(UpgradeType.OVERDRIVE);
                over += over > 0 ? 1 : 0;
                speed *= (int) Math.pow(2, over);
                consumption *= (int) Math.pow(2, over);

                consumption /= (1 + upgradeManager.getLevel(UpgradeType.POWER));

                speed = (((IUpgradeManagerAccess) upgradeManager).aletheia$getSpeedMult() + (speed - 1));
                consumption = (int) (consumption * ((IUpgradeManagerAccess) upgradeManager).aletheia$getPowerMult());

                if (hasPower() && isProcessing()) {
                    this.power -= consumption;
                    if (this.power < 0) {
                        this.power = 0;
                    }
                }

                if (hasPower() && te.canProcess()) {
                    isProgressing = true;
                } else {
                    isProgressing = false;
                }

                if (isProgressing) {
                    progress += speed;
                    if (this.progress >= TileEntityMachineCentrifuge.processingSpeed) {
                        this.progress -= TileEntityMachineCentrifuge.processingSpeed;

                        ItemStack[] out = CentrifugeRecipes.getOutput(slots[0]);
                        int mult = ((IUpgradeManagerAccess) upgradeManager).aletheia$getProductionMult();
                        for (int i = 0; i < Math.min(4, out.length); i++) {
                            if (out[i] == null) continue;

                            int sizeToAdd = out[i].stackSize * mult;
                            if (slots[i + 2] == null) {
                                slots[i + 2] = out[i].copy();
                                slots[i + 2].stackSize = sizeToAdd;
                            } else {
                                slots[i + 2].stackSize += sizeToAdd;
                            }
                        }
                        te.decrStackSize(0, 1);
                        ((TileEntity) (Object) this).markDirty();
                    }
                } else {
                    progress = 0;
                }

                this.networkPackNT(50);
                ci.cancel();
            }
        }
    }
}
