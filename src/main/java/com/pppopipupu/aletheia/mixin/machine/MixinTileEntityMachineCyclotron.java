package com.pppopipupu.aletheia.mixin.machine;

import java.util.HashMap;

import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.hbm.inventory.UpgradeManagerNT;
import com.hbm.inventory.fluid.tank.FluidTank;
import com.hbm.inventory.recipes.CyclotronRecipes;
import com.hbm.items.machine.ItemMachineUpgrade.UpgradeType;
import com.hbm.lib.Library;
import com.hbm.tileentity.TileEntityMachineBase;
import com.hbm.tileentity.machine.TileEntityMachineCyclotron;
import com.pppopipupu.aletheia.interfaces.IUpgradeManagerAccess;

import api.hbm.energymk2.IEnergyReceiverMK2;

@Mixin(value = TileEntityMachineCyclotron.class, remap = false)
public abstract class MixinTileEntityMachineCyclotron extends TileEntityMachineBase implements IEnergyReceiverMK2 {

    public MixinTileEntityMachineCyclotron(int size) {
        super(size);
    }

    @Shadow(remap = false)
    public UpgradeManagerNT upgradeManager;
    @Shadow(remap = false)
    public long power;
    @Shadow(remap = false)
    public int progress;
    @Shadow(remap = false)
    public FluidTank[] tanks;

    @Shadow(remap = false)
    private void updateConnections() {}

    @Shadow(remap = false)
    private void sendFluid() {}

    @Inject(method = "getValidUpgrades", at = @At("RETURN"))
    private void aletheia$getValidUpgrades(CallbackInfoReturnable<HashMap<UpgradeType, Integer>> cir) {
        HashMap<UpgradeType, Integer> upgrades = cir.getReturnValue();
        if (upgrades != null && !upgrades.containsKey(UpgradeType.OVERDRIVE)) {
            upgrades.put(UpgradeType.OVERDRIVE, 3);
        }
    }

    @Inject(method = "canProcess", at = @At("HEAD"), cancellable = true)
    private void aletheia$canProcess(CallbackInfoReturnable<Boolean> cir) {
        int uCount = ((IUpgradeManagerAccess) upgradeManager).aletheia$getUltimateCount();
        if (uCount > 0) {
            TileEntityMachineCyclotron te = (TileEntityMachineCyclotron) (Object) this;
            if (power < te.getConsumption()) {
                cir.setReturnValue(false);
                return;
            }
            int convert = te.getCoolantConsumption();
            if (tanks[0].getFill() < convert) {
                cir.setReturnValue(false);
                return;
            }
            if (tanks[1].getFill() + convert > tanks[1].getMaxFill()) {
                cir.setReturnValue(false);
                return;
            }
            int mult = ((IUpgradeManagerAccess) upgradeManager).aletheia$getProductionMult();
            for (int i = 0; i < 3; i++) {
                Object[] res = CyclotronRecipes.getOutput(slots[i + 3], slots[i]);
                if (res == null) continue;
                ItemStack out = (ItemStack) res[0];
                if (out == null) continue;
                if (slots[i + 6] == null) {
                    cir.setReturnValue(true);
                    return;
                }
                if (slots[i + 6].getItem() == out.getItem() && slots[i + 6].getItemDamage() == out.getItemDamage()
                    && slots[i + 6].stackSize + mult <= out.getMaxStackSize()) {
                    cir.setReturnValue(true);
                    return;
                }
            }
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "process", at = @At("HEAD"), cancellable = true)
    private void aletheia$process(CallbackInfo ci) {
        int uCount = ((IUpgradeManagerAccess) upgradeManager).aletheia$getUltimateCount();
        if (uCount > 0) {
            TileEntityMachineCyclotron te = (TileEntityMachineCyclotron) (Object) this;
            int mult = ((IUpgradeManagerAccess) upgradeManager).aletheia$getProductionMult();
            for (int i = 0; i < 3; i++) {
                Object[] res = CyclotronRecipes.getOutput(slots[i + 3], slots[i]);
                if (res == null) continue;
                ItemStack out = (ItemStack) res[0];
                if (out == null) continue;
                if (slots[i + 6] == null) {
                    te.decrStackSize(i, 1);
                    te.decrStackSize(i + 3, 1);
                    slots[i + 6] = out.copy();
                    slots[i + 6].stackSize = mult;
                    tanks[2].setFill(tanks[2].getFill() + (Integer) res[1] * mult);
                    continue;
                }
                if (slots[i + 6].getItem() == out.getItem() && slots[i + 6].getItemDamage() == out.getItemDamage()
                    && slots[i + 6].stackSize + mult <= out.getMaxStackSize()) {
                    te.decrStackSize(i, 1);
                    te.decrStackSize(i + 3, 1);
                    slots[i + 6].stackSize += mult;
                    tanks[2].setFill(tanks[2].getFill() + (Integer) res[1] * mult);
                }
            }
            if (tanks[2].getFill() > tanks[2].getMaxFill()) {
                tanks[2].setFill(tanks[2].getMaxFill());
            }
            ci.cancel();
        }
    }

    @Inject(method = "getSpeed", at = @At("RETURN"), cancellable = true)
    private void aletheia$getSpeed(CallbackInfoReturnable<Integer> cir) {
        int uCount = ((IUpgradeManagerAccess) this.upgradeManager).aletheia$getUltimateCount();
        if (uCount > 0) {
            int speed = cir.getReturnValue();
            cir.setReturnValue(
                speed == 1 ? (((IUpgradeManagerAccess) upgradeManager).aletheia$getSpeedMult())
                    : (((IUpgradeManagerAccess) upgradeManager).aletheia$getSpeedMult() + (speed - 1)));
        }
    }

    @Inject(method = "getConsumption", at = @At("RETURN"), cancellable = true)
    private void aletheia$getConsumption(CallbackInfoReturnable<Integer> cir) {
        int uCount = ((IUpgradeManagerAccess) this.upgradeManager).aletheia$getUltimateCount();
        if (uCount > 0) {
            cir.setReturnValue(
                (int) (cir.getReturnValue() * ((IUpgradeManagerAccess) upgradeManager).aletheia$getPowerMult()));
        }
    }

    @Inject(method = "getCoolantConsumption", at = @At("RETURN"), cancellable = true)
    private void aletheia$getCoolantConsumption(CallbackInfoReturnable<Integer> cir) {
        int uCount = ((IUpgradeManagerAccess) this.upgradeManager).aletheia$getUltimateCount();
        if (uCount > 0) {
            cir.setReturnValue(
                (int) (cir.getReturnValue() * ((IUpgradeManagerAccess) upgradeManager).aletheia$getPowerMult()));
        }
    }

    @Inject(method = "updateEntity", at = @At("HEAD"), cancellable = true)
    private void aletheia$updateEntity(CallbackInfo ci) {
        if (!worldObj.isRemote) {
            upgradeManager.checkSlots(slots, 10, 11);
            int uCount = ((IUpgradeManagerAccess) upgradeManager).aletheia$getUltimateCount();
            if (uCount > 0) {
                updateConnections();
                power = Library.chargeTEFromItems(slots, 9, power, TileEntityMachineCyclotron.maxPower);
                if (((TileEntityMachineCyclotron) (Object) this).canProcess()) {
                    progress += ((TileEntityMachineCyclotron) (Object) this).getSpeed();
                    power -= ((TileEntityMachineCyclotron) (Object) this).getConsumption();
                    int convert = ((TileEntityMachineCyclotron) (Object) this).getCoolantConsumption();
                    tanks[0].setFill(tanks[0].getFill() - convert);
                    tanks[1].setFill(tanks[1].getFill() + convert);
                    if (progress >= TileEntityMachineCyclotron.duration) {
                        ((TileEntityMachineCyclotron) (Object) this).process();
                        progress = 0;
                        markDirty();
                    }
                } else {
                    progress = 0;
                }
                sendFluid();
                networkPackNT(25);
                ci.cancel();
            }
        }
    }
}
