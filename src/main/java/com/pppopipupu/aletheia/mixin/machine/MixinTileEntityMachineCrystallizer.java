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
import com.hbm.inventory.recipes.CrystallizerRecipes;
import com.hbm.inventory.recipes.CrystallizerRecipes.CrystallizerRecipe;
import com.hbm.items.machine.ItemMachineUpgrade.UpgradeType;
import com.hbm.lib.Library;
import com.hbm.tileentity.TileEntityMachineBase;
import com.hbm.tileentity.machine.TileEntityMachineCrystallizer;
import com.pppopipupu.aletheia.interfaces.IUpgradeManagerAccess;

import api.hbm.energymk2.IEnergyReceiverMK2;

@Mixin(value = TileEntityMachineCrystallizer.class, remap = false)
public abstract class MixinTileEntityMachineCrystallizer extends TileEntityMachineBase implements IEnergyReceiverMK2 {

    public MixinTileEntityMachineCrystallizer(int size) {
        super(size);
    }

    private static final int[] aletheia$overdriveSpeeds = { 1, 2, 5, 10, 20, 50, 100 };

    @Shadow(remap = false)
    public UpgradeManagerNT upgradeManager;
    @Shadow(remap = false)
    public long power;
    @Shadow(remap = false)
    public short progress;
    @Shadow(remap = false)
    public short duration;
    @Shadow(remap = false)
    public boolean isOn;
    @Shadow(remap = false)
    public FluidTank tank;

    @Shadow(remap = false)
    private void updateConnections() {}

    @Shadow(remap = false)
    private void processItem() {}

    @Shadow(remap = false)
    private boolean canProcess() {
        return false;
    }

    @Shadow(remap = false)
    public float getCycleCount() {
        return 0;
    }

    @Shadow(remap = false)
    public int getPowerRequired() {
        return 0;
    }

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
            TileEntityMachineCrystallizer te = (TileEntityMachineCrystallizer) (Object) this;
            if (slots[0] == null) {
                cir.setReturnValue(false);
                return;
            }
            if (power < te.getPowerRequired()) {
                cir.setReturnValue(false);
                return;
            }
            CrystallizerRecipe result = CrystallizerRecipes.getOutput(slots[0], tank.getTankType());
            if (result == null) {
                cir.setReturnValue(false);
                return;
            }
            if (slots[0].stackSize < result.itemAmount) {
                cir.setReturnValue(false);
                return;
            }
            int mult = ((IUpgradeManagerAccess) upgradeManager).aletheia$getProductionMult();
            if (slots[2] != null && (slots[2].getItem() != result.output.getItem()
                || slots[2].getItemDamage() != result.output.getItemDamage())) {
                cir.setReturnValue(false);
                return;
            }
            if (slots[2] != null && slots[2].stackSize + result.output.stackSize * mult > slots[2].getMaxStackSize()) {
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
            CrystallizerRecipe result = CrystallizerRecipes.getOutput(slots[0], tank.getTankType());
            if (result == null) return;
            ItemStack stack = result.output.copy();
            int mult = ((IUpgradeManagerAccess) upgradeManager).aletheia$getProductionMult();
            stack.stackSize *= mult;
            if (slots[2] == null) {
                slots[2] = stack;
            } else if (slots[2].stackSize + stack.stackSize <= slots[2].getMaxStackSize()) {
                slots[2].stackSize += stack.stackSize;
            }
            tank.setFill(tank.getFill() - result.acidAmount);
            if (((TileEntityMachineCrystallizer) (Object) this).getFreeChance(result) == 0
                || ((TileEntityMachineCrystallizer) (Object) this).getFreeChance(result) < worldObj.rand.nextFloat()) {
                ((TileEntityMachineCrystallizer) (Object) this).decrStackSize(0, result.itemAmount);
            }
            ci.cancel();
        }
    }

    @Inject(method = "updateEntity", at = @At("HEAD"), cancellable = true, remap = true)
    private void aletheia$updateEntity(CallbackInfo ci) {
        if (!worldObj.isRemote) {
            upgradeManager.checkSlots(slots, 5, 6);
            int uCount = ((IUpgradeManagerAccess) upgradeManager).aletheia$getUltimateCount();
            if (uCount > 0) {
                TileEntityMachineCrystallizer te = (TileEntityMachineCrystallizer) (Object) this;
                isOn = false;
                updateConnections();
                power = Library.chargeTEFromItems(slots, 1, power, TileEntityMachineCrystallizer.maxPower);
                tank.setType(7, slots);
                tank.loadTank(3, 4, slots);
                int cycles = (int) te.getCycleCount();
                int speedMult = ((IUpgradeManagerAccess) upgradeManager).aletheia$getSpeedMult();
                cycles = speedMult == 1 ? 1 : speedMult + (cycles - 1);
                int powerReq = te.getPowerRequired();
                powerReq = (int) (powerReq * ((IUpgradeManagerAccess) upgradeManager).aletheia$getPowerMult());
                duration = te.getDuration();
                for (int i = 0; i < cycles; i++) {
                    if (canProcess()) {
                        progress++;
                        power -= powerReq;
                        isOn = true;
                        if (progress >= duration) {
                            progress = 0;
                            processItem();
                            markDirty();
                        }
                    } else {
                        progress = 0;
                    }
                }
                networkPackNT(25);
                ci.cancel();
            }
        }
    }
}
