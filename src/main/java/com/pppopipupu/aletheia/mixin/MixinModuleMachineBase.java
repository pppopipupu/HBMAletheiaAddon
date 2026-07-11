package com.pppopipupu.aletheia.mixin;

import java.lang.reflect.Field;

import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.hbm.inventory.recipes.loader.GenericRecipe;
import com.hbm.inventory.recipes.loader.GenericRecipes.IOutput;
import com.hbm.module.machine.ModuleMachineBase;
import com.pppopipupu.aletheia.interfaces.IModuleMachineAccess;
import com.pppopipupu.aletheia.interfaces.IUpgradeManagerAccess;

import api.hbm.energymk2.IEnergyHandlerMK2;

@Mixin(value = ModuleMachineBase.class, remap = false)
public abstract class MixinModuleMachineBase implements IModuleMachineAccess {

    @Shadow
    public IEnergyHandlerMK2 battery;

    public boolean hasUltimate = false;
    public int ultimateCount = 0;

    @Override
    public void aletheia$setUltimateCount(int count) {
        this.ultimateCount = count;
    }

    @Override
    public void aletheia$setHasUltimate(boolean hasUltimate) {
        this.hasUltimate = hasUltimate;
    }

    @Override
    public int aletheia$getUltimateCount() {
        return this.ultimateCount;
    }

    @Override
    public boolean aletheia$hasUltimate() {
        return this.hasUltimate;
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

    @Inject(method = "process", at = @At("HEAD"))
    private void aletheia$beforeProcess(GenericRecipe recipe, double speed, double power, CallbackInfo ci) {
        int uCount = aletheia$getUltimateCountFromBattery();
        this.ultimateCount = uCount;
        this.hasUltimate = uCount > 0;
    }

    @ModifyVariable(method = "process", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private double aletheia$modifyProcessSpeed(double speed) {
        int uCount = aletheia$getUltimateCountFromBattery();
        if (uCount > 0) {
            String name = this.battery.getClass()
                .getSimpleName();
            if (name.contains("Factory")) {
                speed = speed * (1.0D + uCount * 1.5D);
            } else {
                speed = speed * (1.0D + uCount * 4.0D);
            }
        }
        return speed;
    }

    @ModifyVariable(method = "process", at = @At("HEAD"), ordinal = 1, argsOnly = true)
    private double aletheia$modifyProcessPower(double power) {
        int uCount = aletheia$getUltimateCountFromBattery();
        if (uCount > 0) {
            String name = this.battery.getClass()
                .getSimpleName();
            if (name.contains("Factory")) {
                power = power * Math.pow(0.25D, uCount);
            } else {
                power = power * Math.pow(0.5D, uCount);
            }
        }
        return power;
    }

    @Redirect(
        method = "fitOutput",
        at = @At(value = "FIELD", target = "Lcom/hbm/module/machine/ModuleMachineBase;hasUltimate:Z"))
    private boolean aletheia$fitOutputRedirectHasUltimate(ModuleMachineBase instance) {
        return false;
    }

    @ModifyVariable(method = "fitOutput", at = @At("STORE"), ordinal = 0)
    private int aletheia$modifyFitOutputFactor(int baseFactor) {
        return 1 << this.ultimateCount;
    }

    @Redirect(
        method = "produceItem",
        at = @At(value = "FIELD", target = "Lcom/hbm/module/machine/ModuleMachineBase;hasUltimate:Z"))
    private boolean aletheia$produceItemRedirectHasUltimate(ModuleMachineBase instance) {
        return false;
    }

    @ModifyVariable(method = "produceItem", at = @At("STORE"), ordinal = 0)
    private int aletheia$modifyProduceItemMult(int baseMult, GenericRecipe recipe, int multi) {
        return multi * (1 << this.ultimateCount);
    }

    @Redirect(
        method = "produceItem",
        at = @At(
            value = "INVOKE",
            target = "Lcom/hbm/inventory/recipes/loader/IOutput;collapse()Lnet/minecraft/item/ItemStack;"))
    private ItemStack aletheia$redirectProduceItemCollapse(IOutput output) {
        ItemStack stack = output.collapse();
        if (stack != null) {
            stack.stackSize *= (1 << this.ultimateCount);
        }
        return stack;
    }
}
