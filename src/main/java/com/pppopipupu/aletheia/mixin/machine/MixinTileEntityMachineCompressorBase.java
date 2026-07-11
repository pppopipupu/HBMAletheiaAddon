package com.pppopipupu.aletheia.mixin.machine;

import api.hbm.energymk2.IEnergyReceiverMK2;
import api.hbm.fluidmk2.IFluidReceiverMK2;
import api.hbm.fluidmk2.IFluidStandardSenderMK2;
import com.hbm.inventory.UpgradeManagerNT;
import com.hbm.inventory.fluid.tank.FluidTank;
import com.hbm.inventory.recipes.CompressorRecipes;
import com.hbm.inventory.recipes.CompressorRecipes.CompressorRecipe;
import com.hbm.items.machine.ItemMachineUpgrade.UpgradeType;
import com.hbm.lib.Library;
import com.hbm.tileentity.TileEntityMachineBase;
import com.hbm.tileentity.machine.TileEntityMachineCompressorBase;
import com.hbm.util.Tuple.Pair;
import com.hbm.util.fauxpointtwelve.DirPos;
import com.pppopipupu.aletheia.interfaces.IUpgradeManagerAccess;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.HashMap;

@Mixin(value = TileEntityMachineCompressorBase.class, remap = false)
public abstract class MixinTileEntityMachineCompressorBase extends TileEntityMachineBase implements IEnergyReceiverMK2, IFluidReceiverMK2, IFluidStandardSenderMK2 {

    private static final int[] aletheia$overdriveSpeeds = {1, 2, 5, 10, 20, 50, 100};

    public MixinTileEntityMachineCompressorBase(int size) {
        super(size);
    }

    @Shadow(remap = false) public UpgradeManagerNT upgradeManager;
    @Shadow(remap = false) public long power;
    @Shadow(remap = false) public int progress;
    @Shadow(remap = false) public int processTime;
    @Shadow(remap = false) public int powerRequirement;
    @Shadow(remap = false) public FluidTank[] tanks;
    @Shadow(remap = false) public boolean isOn;
    @Shadow(remap = false) protected abstract void updateConnections();
    @Shadow(remap = false) protected abstract void setupTanks();
    @Shadow(remap = false) public abstract DirPos[] getConPos();

    @Inject(method = "getValidUpgrades", at = @At("RETURN"))
    private void aletheia$getValidUpgrades(CallbackInfoReturnable<HashMap<UpgradeType, Integer>> cir) {
        HashMap<UpgradeType, Integer> upgrades = cir.getReturnValue();
        if (upgrades != null && !upgrades.containsKey(UpgradeType.OVERDRIVE)) {
            upgrades.put(UpgradeType.OVERDRIVE, 9);
        }
    }

    @Inject(method = "canProcess", at = @At("HEAD"), cancellable = true)
    private void aletheia$canProcess(CallbackInfoReturnable<Boolean> cir) {
        int uCount = ((IUpgradeManagerAccess) upgradeManager).aletheia$getUltimateCount();
        if (uCount > 0) {
            TileEntityMachineCompressorBase te = (TileEntityMachineCompressorBase)(Object)this;
            if (te.power <= powerRequirement) {
                cir.setReturnValue(false);
                return;
            }
            CompressorRecipe recipe = CompressorRecipes.recipes.get(new Pair(tanks[0].getTankType(), tanks[0].getPressure()));
            int mult = 1 << uCount;
            if (recipe == null) {
                cir.setReturnValue(tanks[0].getFill() >= 1000 && tanks[1].getFill() + 1000 * mult <= tanks[1].getMaxFill());
                return;
            }
            cir.setReturnValue(tanks[0].getFill() >= recipe.inputAmount && tanks[1].getFill() + recipe.output.fill * mult <= tanks[1].getMaxFill());
        }
    }

    @Inject(method = "process", at = @At("HEAD"), cancellable = true)
    private void aletheia$process(CallbackInfo ci) {
        int uCount = ((IUpgradeManagerAccess) upgradeManager).aletheia$getUltimateCount();
        if (uCount > 0) {
            CompressorRecipe recipe = CompressorRecipes.recipes.get(new Pair(tanks[0].getTankType(), tanks[0].getPressure()));
            int mult = 1 << uCount;
            if (recipe == null) {
                tanks[0].setFill(tanks[0].getFill() - 1000);
                tanks[1].setFill(tanks[1].getFill() + 1000 * mult);
            } else {
                tanks[0].setFill(tanks[0].getFill() - recipe.inputAmount);
                tanks[1].setFill(tanks[1].getFill() + recipe.output.fill * mult);
            }
            ci.cancel();
        }
    }

    @Inject(method = "updateEntity", at = @At("HEAD"), cancellable = true)
    private void aletheia$updateEntity(CallbackInfo ci) {
        TileEntityMachineCompressorBase te = (TileEntityMachineCompressorBase)(Object)this;
        if (!worldObj.isRemote) {
            upgradeManager.checkSlots(slots, 1, 3);
            int uCount = ((IUpgradeManagerAccess) upgradeManager).aletheia$getUltimateCount();
            if (uCount > 0) {
                if (worldObj.getTotalWorldTime() % 20 == 0) {
                    updateConnections();
                }
                power = Library.chargeTEFromItems(slots, 1, power, TileEntityMachineCompressorBase.maxPower);
                tanks[0].setType(0, slots);
                setupTanks();
                int speed = aletheia$overdriveSpeeds[upgradeManager.getLevel(UpgradeType.SPEED)];
                int powerLevel = upgradeManager.getLevel(UpgradeType.POWER);
                int over = aletheia$overdriveSpeeds[upgradeManager.getLevel(UpgradeType.OVERDRIVE)];
                CompressorRecipe rec = CompressorRecipes.recipes.get(new Pair(tanks[0].getTankType(), tanks[0].getPressure()));
                int timeBase = TileEntityMachineCompressorBase.processTimeBase;
                if (rec != null) timeBase = rec.duration;
                processTime = timeBase / speed / over;
                powerRequirement = TileEntityMachineCompressorBase.powerRequirementBase / (powerLevel + 1);
                powerRequirement = powerRequirement * speed * over;
                if (uCount > 0) {
                    int speedFactor = 1 + uCount * 4;
                    processTime = Math.max(processTime / speedFactor, 1);
                    powerRequirement = (int)(powerRequirement * Math.pow(0.5D, uCount));
                }
                if (processTime <= 0) processTime = 1;
                if (te.canProcess()) {
                    progress++;
                    isOn = true;
                    power -= powerRequirement;
                    if (progress >= processTime) {
                        progress = 0;
                        te.process();
                        te.markChanged();
                    }
                } else {
                    progress = 0;
                    isOn = false;
                }
                for (DirPos pos : getConPos()) {
                    te.tryProvide(tanks[1], worldObj, pos.getX(), pos.getY(), pos.getZ(), pos.getDir());
                }
                te.networkPackNT(100);
                ci.cancel();
            }
        }
    }
}
