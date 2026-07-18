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
import com.hbm.inventory.recipes.SolidificationRecipes;
import com.hbm.items.machine.ItemMachineUpgrade.UpgradeType;
import com.hbm.lib.Library;
import com.hbm.tileentity.TileEntityMachineBase;
import com.hbm.tileentity.machine.oil.TileEntityMachineSolidifier;
import com.hbm.util.Tuple.Pair;
import com.hbm.util.fauxpointtwelve.DirPos;
import com.pppopipupu.aletheia.interfaces.IUpgradeManagerAccess;

import api.hbm.energymk2.IEnergyReceiverMK2;
import api.hbm.fluidmk2.IFluidReceiverMK2;

@Mixin(value = TileEntityMachineSolidifier.class, remap = false)
public abstract class MixinTileEntityMachineSolidifier extends TileEntityMachineBase implements IEnergyReceiverMK2 {

    public MixinTileEntityMachineSolidifier(int size) {
        super(size);
    }

    private static final int[] aletheia$overdriveSpeeds = { 1, 2, 5, 10, 20, 50, 100 };

    @Shadow(remap = false)
    public UpgradeManagerNT upgradeManager;
    @Shadow(remap = false)
    public long power;
    @Shadow(remap = false)
    public int progress;
    @Shadow(remap = false)
    public int processTime;
    @Shadow(remap = false)
    public int usage;
    @Shadow(remap = false)
    public FluidTank tank;

    @Shadow(remap = false)
    private DirPos[] getConPos() {
        return null;
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
            if (power < usage) {
                cir.setReturnValue(false);
                return;
            }
            Pair<Integer, ItemStack> out = SolidificationRecipes.getOutput(tank.getTankType());
            if (out == null) {
                cir.setReturnValue(false);
                return;
            }
            int req = out.getKey();
            ItemStack stack = out.getValue();
            if (req > tank.getFill()) {
                cir.setReturnValue(false);
                return;
            }
            if (slots[0] != null) {
                if (slots[0].getItem() != stack.getItem()) {
                    cir.setReturnValue(false);
                    return;
                }
                if (slots[0].getItemDamage() != stack.getItemDamage()) {
                    cir.setReturnValue(false);
                    return;
                }
                int mult = ((IUpgradeManagerAccess) upgradeManager).aletheia$getProductionMult();
                if (slots[0].stackSize + stack.stackSize * mult > slots[0].getMaxStackSize()) {
                    cir.setReturnValue(false);
                    return;
                }
            }
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "process", at = @At("HEAD"), cancellable = true)
    private void aletheia$process(CallbackInfo ci) {
        int uCount = ((IUpgradeManagerAccess) upgradeManager).aletheia$getUltimateCount();
        if (uCount > 0) {
            power -= usage;
            progress++;
            if (progress >= processTime) {
                Pair<Integer, ItemStack> out = SolidificationRecipes.getOutput(tank.getTankType());
                int req = out.getKey();
                ItemStack stack = out.getValue();
                tank.setFill(tank.getFill() - req);
                int mult = ((IUpgradeManagerAccess) upgradeManager).aletheia$getProductionMult();
                if (slots[0] == null) {
                    slots[0] = stack.copy();
                    slots[0].stackSize *= mult;
                } else {
                    slots[0].stackSize += stack.stackSize * mult;
                }
                progress = 0;
                ((TileEntityMachineSolidifier) (Object) this).markDirty();
            }
            ci.cancel();
        }
    }

    @Inject(method = "updateEntity", at = @At("HEAD"), cancellable = true, remap = true)
    private void aletheia$updateEntity(CallbackInfo ci) {
        if (!worldObj.isRemote) {
            upgradeManager.checkSlots(slots, 2, 3);
            int uCount = ((IUpgradeManagerAccess) upgradeManager).aletheia$getUltimateCount();
            if (uCount > 0) {
                power = Library.chargeTEFromItems(slots, 1, power, TileEntityMachineSolidifier.maxPower);
                tank.setType(4, slots);
                for (DirPos pos : getConPos()) {
                    this.trySubscribe(worldObj, pos.getX(), pos.getY(), pos.getZ(), pos.getDir());
                    ((IFluidReceiverMK2) this)
                        .trySubscribe(tank.getTankType(), worldObj, pos.getX(), pos.getY(), pos.getZ(), pos.getDir());
                }
                int speed = upgradeManager.getLevel(UpgradeType.SPEED);
                int powerLvl = upgradeManager.getLevel(UpgradeType.POWER);
                int over = aletheia$overdriveSpeeds[upgradeManager.getLevel(UpgradeType.OVERDRIVE)];
                processTime = TileEntityMachineSolidifier.processTimeBase * (4 - speed) / 4 / over;
                usage = TileEntityMachineSolidifier.usageBase * (speed + 1) * over / (powerLvl + 1);
                usage = (int) (usage * ((IUpgradeManagerAccess) upgradeManager).aletheia$getPowerMult());
                int speedFactor = ((IUpgradeManagerAccess) upgradeManager).aletheia$getSpeedMult();
                for (int i = 0; i < speedFactor; i++) {
                    if (((TileEntityMachineSolidifier) (Object) this).canProcess()) {
                        progress++;
                        power -= usage;
                        if (progress >= processTime) {
                            progress = 0;
                            Pair<Integer, ItemStack> out = SolidificationRecipes.getOutput(tank.getTankType());
                            int req = out.getKey();
                            ItemStack stack = out.getValue();
                            tank.setFill(tank.getFill() - req);
                            int mult = ((IUpgradeManagerAccess) upgradeManager).aletheia$getProductionMult();
                            if (slots[0] == null) {
                                slots[0] = stack.copy();
                                slots[0].stackSize *= mult;
                            } else {
                                slots[0].stackSize += stack.stackSize * mult;
                            }
                            markDirty();
                        }
                    } else {
                        progress = 0;
                        break;
                    }
                }
                networkPackNT(50);
                ci.cancel();
            }
        }
    }
}
