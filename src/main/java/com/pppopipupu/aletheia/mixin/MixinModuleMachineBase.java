package com.pppopipupu.aletheia.mixin;

import java.lang.reflect.Field;
import java.util.List;

import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.hbm.inventory.RecipesCommon.AStack;
import com.hbm.inventory.fluid.tank.FluidTank;
import com.hbm.inventory.recipes.loader.GenericRecipe;
import com.hbm.inventory.recipes.loader.GenericRecipes;
import com.hbm.inventory.recipes.loader.GenericRecipes.IOutput;
import com.hbm.module.machine.ModuleMachineBase;
import com.pppopipupu.aletheia.interfaces.IModuleMachineAccess;
import com.pppopipupu.aletheia.interfaces.IUpgradeManagerAccess;

import api.hbm.energymk2.IEnergyHandlerMK2;

@Mixin(value = ModuleMachineBase.class, remap = false)
public abstract class MixinModuleMachineBase implements IModuleMachineAccess {

    @Shadow
    public IEnergyHandlerMK2 battery;

    @Shadow
    public ItemStack[] slots;
    @Shadow
    public int[] inputSlots;
    @Shadow
    public int[] outputSlots;
    @Shadow
    public FluidTank[] inputTanks;
    @Shadow
    public FluidTank[] outputTanks;
    @Shadow
    protected String recipe;
    @Shadow
    public double progress;
    @Shadow
    public boolean markDirty;
    @Shadow
    public boolean restrictedMode;

    @Shadow
    public abstract GenericRecipes getRecipeSet();

    @Shadow
    public abstract void setupTanks(GenericRecipe recipe);

    @Shadow
    public abstract boolean canProcess(GenericRecipe recipe, double speed, double power);

    public int ultimateCount = 0;
    public int aletheia$productionMult = 1;
    public int aletheia$speedMult = 1;
    public double aletheia$powerMult = 1.0D;

    @Override
    public void aletheia$setUltimateCount(int count) {
        this.ultimateCount = count;
    }

    @Override
    public void aletheia$setProductionMult(int mult) {
        this.aletheia$productionMult = mult;
    }

    @Override
    public void aletheia$setSpeedMult(int mult) {
        this.aletheia$speedMult = mult;
    }

    @Override
    public void aletheia$setPowerMult(double mult) {
        this.aletheia$powerMult = mult;
    }

    @Override
    public int aletheia$getUltimateCount() {
        return this.ultimateCount;
    }

    @Override
    public int aletheia$getProductionMult() {
        return this.aletheia$productionMult;
    }

    @Override
    public int aletheia$getSpeedMult() {
        return this.aletheia$speedMult;
    }

    @Override
    public double aletheia$getPowerMult() {
        return this.aletheia$powerMult;
    }

    private Field aletheia$cachedField = null;

    private int aletheia$getUltimateCountFromBattery() {
        try {
            if (aletheia$cachedField == null) {
                aletheia$cachedField = this.battery.getClass()
                    .getField("upgradeManager");
            }
            Object upgradeManager = aletheia$cachedField.get(this.battery);
            if (upgradeManager != null) {
                return ((IUpgradeManagerAccess) upgradeManager).aletheia$getUltimateCount();
            }
        } catch (Exception e) {}
        return 0;
    }

    private int aletheia$processCount = 0;

    private Object aletheia$getUpgradeManager() {
        try {
            if (aletheia$cachedField == null) {
                aletheia$cachedField = this.battery.getClass()
                    .getField("upgradeManager");
            }
            return aletheia$cachedField.get(this.battery);
        } catch (Exception e) {}
        return null;
    }

    private int aletheia$findMultiplier(GenericRecipe recipe) {
        int count = 50;
        if (recipe.inputItem != null) {
            for (int i = 0; i < Math.min(recipe.inputItem.length, this.inputSlots.length); i++) {
                AStack aStack = recipe.inputItem[i];
                ItemStack slotStack = this.slots[this.inputSlots[i]];
                if (slotStack == null) return 0;
                if (aStack.matchesRecipe(slotStack, true)) {
                    count = Math.min(count, slotStack.stackSize / aStack.stacksize);
                } else {
                    return 0;
                }
                if (count == 0) return 0;
            }
        }
        if (recipe.inputFluid != null) {
            for (int i = 0; i < Math.min(recipe.inputFluid.length, this.inputTanks.length); i++) {
                if (recipe.inputFluid[i].fill == 0) continue;
                count = Math.min(count, this.inputTanks[i].getFill() / recipe.inputFluid[i].fill);
                if (count == 0) return 0;
            }
        }
        return count;
    }

    private int aletheia$fitOutput(GenericRecipe recipe, int count) {
        int factor = this.aletheia$getProductionMult();
        if (recipe.outputItem != null) {
            for (int i = 0; i < Math.min(recipe.outputItem.length, this.outputSlots.length); i++) {
                ItemStack stack = this.slots[this.outputSlots[i]];
                IOutput output = recipe.outputItem[i];
                if (output.possibleMultiOutput()) return 0;
                ItemStack single = output.getSingle();
                if (stack == null) {
                    count = Math.min(count, single.getMaxStackSize() / (single.stackSize * factor));
                    continue;
                }
                if (single == null) return 0;
                if (stack.getItem() != single.getItem()) return 0;
                if (stack.getItemDamage() != single.getItemDamage()) return 0;
                count = Math.min(count, (stack.getMaxStackSize() - stack.stackSize) / (single.stackSize * factor));
                if (count == 0) return 0;
            }
        }
        if (recipe.outputFluid != null) {
            for (int i = 0; i < Math.min(recipe.outputFluid.length, this.outputTanks.length); i++) {
                count = Math.min(
                    count,
                    (this.outputTanks[i].getMaxFill() - this.outputTanks[i].getFill())
                        / (recipe.outputFluid[i].fill * factor));
                if (count == 0) return 0;
            }
        }
        return count;
    }

    private void aletheia$consumeInput(GenericRecipe recipe, int multi) {
        if (recipe.inputItem != null) {
            for (int i = 0; i < Math.min(recipe.inputItem.length, this.inputSlots.length); i++) {
                this.slots[this.inputSlots[i]].stackSize -= multi * recipe.inputItem[i].stacksize;
                if (this.slots[this.inputSlots[i]].stackSize <= 0) {
                    this.slots[this.inputSlots[i]] = null;
                }
            }
        }
        if (recipe.inputFluid != null) {
            for (int i = 0; i < Math.min(recipe.inputFluid.length, this.inputTanks.length); i++) {
                this.inputTanks[i].setFill(this.inputTanks[i].getFill() - multi * recipe.inputFluid[i].fill);
            }
        }
    }

    private void aletheia$produceItem(GenericRecipe recipe, int multi) {
        int mult = multi * this.aletheia$getProductionMult();
        if (recipe.outputItem != null) {
            for (int i = 0; i < Math.min(recipe.outputItem.length, this.outputSlots.length); i++) {
                ItemStack collapse = recipe.outputItem[i].collapse();
                if (collapse != null) {
                    collapse.stackSize *= this.aletheia$getProductionMult();
                    collapse.stackSize *= multi;
                    if (this.slots[this.outputSlots[i]] == null) {
                        this.slots[this.outputSlots[i]] = collapse;
                    } else {
                        this.slots[this.outputSlots[i]].stackSize += collapse.stackSize;
                    }
                }
            }
        }
        if (recipe.outputFluid != null) {
            for (int i = 0; i < Math.min(recipe.outputFluid.length, this.outputTanks.length); i++) {
                this.outputTanks[i].setFill(this.outputTanks[i].getFill() + mult * recipe.outputFluid[i].fill);
            }
        }
        this.markDirty = true;
    }

    @Inject(method = "canProcess", at = @At("HEAD"), cancellable = true)
    private void aletheia$canProcess(GenericRecipe recipe, double speed, double power,
        CallbackInfoReturnable<Boolean> cir) {
        if (recipe == null) {
            cir.setReturnValue(Boolean.FALSE);
            return;
        }
        if (recipe.autoSwitchGroup != null && this.inputSlots.length > 0 && this.slots[this.inputSlots[0]] != null) {
            ItemStack itemToSwitchBy = this.slots[this.inputSlots[0]];
            List<GenericRecipe> recipes = (List<GenericRecipe>) this.getRecipeSet().autoSwitchGroups
                .get(recipe.autoSwitchGroup);
            if (recipes != null) {
                for (GenericRecipe nextRec : recipes) {
                    if (nextRec.getInternalName()
                        .equals(this.recipe)) continue;
                    if (nextRec.inputItem == null) continue;
                    if (nextRec.inputItem[0].matchesRecipe(itemToSwitchBy, true)) {
                        this.recipe = nextRec.getInternalName();
                        cir.setReturnValue(Boolean.FALSE);
                        return;
                    }
                }
            }
        }
        if (power != 1 && this.battery.getPower() < recipe.power * power) {
            cir.setReturnValue(Boolean.FALSE);
            return;
        }
        if (power == 1 && this.battery.getPower() < recipe.power) {
            cir.setReturnValue(Boolean.FALSE);
            return;
        }
        int count = aletheia$findMultiplier(recipe);
        if (count == 0) {
            cir.setReturnValue(Boolean.FALSE);
            return;
        }
        count = aletheia$fitOutput(recipe, count);
        this.aletheia$processCount = count;
        cir.setReturnValue(count > 0);
    }

    @Inject(method = "process", at = @At("HEAD"), cancellable = true)
    private void aletheia$process(GenericRecipe recipe, double speed, double power, CallbackInfo ci) {
        int uCount = aletheia$getUltimateCountFromBattery();
        this.ultimateCount = uCount;
        if (uCount > 0) {
            IUpgradeManagerAccess upgrade = (IUpgradeManagerAccess) aletheia$getUpgradeManager();
            int speedMult = upgrade.aletheia$getSpeedMult();
            double powerMult = upgrade.aletheia$getPowerMult();
            String name = this.battery.getClass()
                .getSimpleName();
            if (name.contains("Factory")) {
                speed = speed * (1.0D + (speedMult - 1) * 0.375D);
                power = power * Math.pow(powerMult, 2.0D);
            } else {
                speed = speed * speedMult;
                power = power * powerMult;
            }
        }
        if (this.restrictedMode) speed *= 0.25;
        this.battery.setPower(this.battery.getPower() - (power == 1 ? recipe.power : (long) (recipe.power * power)));
        double step = speed / recipe.duration;
        this.progress += step;
        int multi = Math.min((int) this.progress, this.aletheia$processCount);
        if (multi > 0) {
            aletheia$consumeInput(recipe, multi);
            aletheia$produceItem(recipe, multi);
            if (this.canProcess(recipe, speed, power)) {
                this.progress -= multi;
            } else {
                this.progress = 0.0D;
            }
        }
        ci.cancel();
    }

    @Inject(method = "canFitOutput", at = @At("HEAD"), cancellable = true)
    private void aletheia$canFitOutput(GenericRecipe recipe, CallbackInfoReturnable<Boolean> cir) {
        int factor = this.aletheia$getProductionMult();
        if (recipe.outputItem != null) {
            for (int i = 0; i < Math.min(recipe.outputItem.length, this.outputSlots.length); i++) {
                ItemStack stack = this.slots[this.outputSlots[i]];
                IOutput output = recipe.outputItem[i];
                if (output.possibleMultiOutput()) {
                    cir.setReturnValue(Boolean.FALSE);
                    return;
                }
                ItemStack single = output.getSingle();
                if (stack == null) continue;
                if (single == null) {
                    cir.setReturnValue(Boolean.FALSE);
                    return;
                }
                if (stack.getItem() != single.getItem()) {
                    cir.setReturnValue(Boolean.FALSE);
                    return;
                }
                if (stack.getItemDamage() != single.getItemDamage()) {
                    cir.setReturnValue(Boolean.FALSE);
                    return;
                }
                if (stack.stackSize + single.stackSize * factor > stack.getMaxStackSize()) {
                    cir.setReturnValue(Boolean.FALSE);
                    return;
                }
            }
        }
        if (recipe.outputFluid != null) {
            for (int i = 0; i < Math.min(recipe.outputFluid.length, this.outputTanks.length); i++) {
                if (this.outputTanks[i].getFill() + recipe.outputFluid[i].fill * factor
                    > this.outputTanks[i].getMaxFill()) {
                    cir.setReturnValue(Boolean.FALSE);
                    return;
                }
            }
        }
        cir.setReturnValue(Boolean.TRUE);
    }

    @Inject(method = "consumeInput", at = @At("HEAD"), cancellable = true)
    private void aletheia$consumeInput(GenericRecipe recipe, CallbackInfo ci) {
        aletheia$consumeInput(recipe, 1);
        ci.cancel();
    }

    @Inject(method = "produceItem", at = @At("HEAD"), cancellable = true)
    private void aletheia$produceItem(GenericRecipe recipe, CallbackInfo ci) {
        aletheia$produceItem(recipe, 1);
        ci.cancel();
    }
}
