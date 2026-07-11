package com.pppopipupu.aletheia.mixin.machine;

import api.hbm.energymk2.IEnergyReceiverMK2;
import api.hbm.fluidmk2.IFluidReceiverMK2;
import api.hbm.fluidmk2.IFluidStandardSenderMK2;
import com.hbm.inventory.UpgradeManagerNT;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.tank.FluidTank;
import com.hbm.inventory.recipes.MixerRecipes;
import com.hbm.inventory.recipes.MixerRecipes.MixerRecipe;
import com.hbm.items.machine.ItemMachineUpgrade.UpgradeType;
import com.hbm.lib.Library;
import com.hbm.tileentity.TileEntityMachineBase;
import com.hbm.tileentity.machine.TileEntityMachineMixer;
import com.hbm.util.fauxpointtwelve.DirPos;
import com.pppopipupu.aletheia.interfaces.IUpgradeManagerAccess;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.HashMap;

@Mixin(value = TileEntityMachineMixer.class, remap = false)
public abstract class MixinTileEntityMachineMixer extends TileEntityMachineBase implements IEnergyReceiverMK2 {

    public MixinTileEntityMachineMixer(int size) {
        super(size);
    }

    private static final int[] aletheia$overdriveSpeeds = {1, 2, 5, 10, 20, 50, 100};

    @Shadow(remap = false) public UpgradeManagerNT upgradeManager;
    @Shadow(remap = false) public long power;
    @Shadow(remap = false) public int progress;
    @Shadow(remap = false) public int processTime;
    @Shadow(remap = false) public int recipeIndex;
    @Shadow(remap = false) public FluidTank[] tanks;
    @Shadow(remap = false) private int consumption;
    @Shadow(remap = false) public boolean wasOn;
    @Shadow(remap = false) public float rotation;
    @Shadow(remap = false) public float prevRotation;
    @Shadow(remap = false) protected abstract DirPos[] getConPos();
    @Shadow(remap = false) protected void process() { }

    @Inject(method = "getValidUpgrades", at = @At("RETURN"))
    private void aletheia$getValidUpgrades(CallbackInfoReturnable<HashMap<UpgradeType, Integer>> cir) {
        HashMap<UpgradeType, Integer> upgrades = cir.getReturnValue();
        if (upgrades != null && !upgrades.containsKey(UpgradeType.OVERDRIVE)) {
            upgrades.put(UpgradeType.OVERDRIVE, 6);
        }
    }

    @Inject(method = "canProcess", at = @At("HEAD"), cancellable = true)
    private void aletheia$canProcess(CallbackInfoReturnable<Boolean> cir) {
        int uCount = ((IUpgradeManagerAccess) upgradeManager).aletheia$getUltimateCount();
        if (uCount > 0) {
            TileEntityMachineMixer te = (TileEntityMachineMixer)(Object)this;
            MixerRecipe[] recipes = MixerRecipes.getOutput(tanks[2].getTankType());
            if (recipes == null || recipes.length <= 0) {
                recipeIndex = 0;
                cir.setReturnValue(false);
                return;
            }
            recipeIndex = recipeIndex % recipes.length;
            MixerRecipe recipe = recipes[recipeIndex];
            if (recipe == null) {
                recipeIndex = 0;
                cir.setReturnValue(false);
                return;
            }
            tanks[0].setTankType(recipe.input1 != null ? recipe.input1.type : Fluids.NONE);
            tanks[1].setTankType(recipe.input2 != null ? recipe.input2.type : Fluids.NONE);
            if (recipe.input1 != null && tanks[0].getFill() < recipe.input1.fill) {
                cir.setReturnValue(false);
                return;
            }
            if (recipe.input2 != null && tanks[1].getFill() < recipe.input2.fill) {
                cir.setReturnValue(false);
                return;
            }
            if (power < te.getConsumption()) {
                cir.setReturnValue(false);
                return;
            }
            int mult = 1 << uCount;
            if (recipe.output * mult + tanks[2].getFill() > tanks[2].getMaxFill()) {
                cir.setReturnValue(false);
                return;
            }
            if (recipe.solidInput != null) {
                if (slots[1] == null) {
                    cir.setReturnValue(false);
                    return;
                }
                if (!recipe.solidInput.matchesRecipe(slots[1], true) || recipe.solidInput.stacksize > slots[1].stackSize) {
                    cir.setReturnValue(false);
                    return;
                }
            }
            processTime = recipe.processTime;
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "process", at = @At("HEAD"), cancellable = true)
    private void aletheia$process(CallbackInfo ci) {
        int uCount = ((IUpgradeManagerAccess) upgradeManager).aletheia$getUltimateCount();
        if (uCount > 0) {
            MixerRecipe[] recipes = MixerRecipes.getOutput(tanks[2].getTankType());
            MixerRecipe recipe = recipes[recipeIndex % recipes.length];
            if (recipe.input1 != null) tanks[0].setFill(tanks[0].getFill() - recipe.input1.fill);
            if (recipe.input2 != null) tanks[1].setFill(tanks[1].getFill() - recipe.input2.fill);
            if (recipe.solidInput != null) ((TileEntityMachineMixer)(Object)this).decrStackSize(1, recipe.solidInput.stacksize);
            int mult = 1 << uCount;
            tanks[2].setFill(tanks[2].getFill() + recipe.output * mult);
            ci.cancel();
        }
    }

    @Inject(method = "updateEntity", at = @At("HEAD"), cancellable = true)
    private void aletheia$updateEntity(CallbackInfo ci) {
        if (!worldObj.isRemote) {
            upgradeManager.checkSlots(slots, 3, 4);
            int uCount = ((IUpgradeManagerAccess) upgradeManager).aletheia$getUltimateCount();
            if (uCount > 0) {
                power = Library.chargeTEFromItems(slots, 0, power, TileEntityMachineMixer.maxPower);
                tanks[2].setType(2, slots);
                int speedLevel = upgradeManager.getLevel(UpgradeType.SPEED);
                int powerLevel = upgradeManager.getLevel(UpgradeType.POWER);
                int over = aletheia$overdriveSpeeds[upgradeManager.getLevel(UpgradeType.OVERDRIVE)];
                this.consumption = 50;
                this.consumption += speedLevel * 150;
                this.consumption -= this.consumption * powerLevel / 4;
                this.consumption *= over;
                this.consumption = (int)(this.consumption * Math.pow(0.5D, uCount));
                for (DirPos pos : getConPos()) {
                    this.trySubscribe(worldObj, pos.getX(), pos.getY(), pos.getZ(), pos.getDir());
                    if (tanks[0].getTankType() != Fluids.NONE) ((IFluidReceiverMK2) this).trySubscribe(tanks[0].getTankType(), worldObj, pos.getX(), pos.getY(), pos.getZ(), pos.getDir());
                    if (tanks[1].getTankType() != Fluids.NONE) ((IFluidReceiverMK2) this).trySubscribe(tanks[1].getTankType(), worldObj, pos.getX(), pos.getY(), pos.getZ(), pos.getDir());
                }
                wasOn = ((TileEntityMachineMixer)(Object)this).canProcess();
                if (wasOn) {
                    int speedFactor = 1 + uCount * 4;
                    for (int i = 0; i < speedFactor; i++) {
                        if (((TileEntityMachineMixer)(Object)this).canProcess()) {
                            progress++;
                            power -= ((TileEntityMachineMixer)(Object)this).getConsumption();
                            if (progress >= processTime) {
                                progress = 0;
                                MixerRecipe[] recipes = MixerRecipes.getOutput(tanks[2].getTankType());
                                MixerRecipe recipe = recipes[recipeIndex % recipes.length];
                                if (recipe.input1 != null) tanks[0].setFill(tanks[0].getFill() - recipe.input1.fill);
                                if (recipe.input2 != null) tanks[1].setFill(tanks[1].getFill() - recipe.input2.fill);
                                if (recipe.solidInput != null) ((TileEntityMachineMixer)(Object)this).decrStackSize(1, recipe.solidInput.stacksize);
                                int mult = 1 << uCount;
                                tanks[2].setFill(tanks[2].getFill() + recipe.output * mult);
                                markDirty();
                            }
                        } else {
                            progress = 0;
                            break;
                        }
                    }
                } else {
                    progress = 0;
                }
                for (DirPos pos : getConPos()) {
                    if (tanks[2].getFill() > 0) ((IFluidStandardSenderMK2) this).tryProvide(tanks[2], worldObj, pos.getX(), pos.getY(), pos.getZ(), pos.getDir());
                }
                NBTTagCompound data = new NBTTagCompound();
                data.setLong("power", power);
                data.setInteger("processTime", processTime);
                data.setInteger("progress", progress);
                data.setInteger("recipe", recipeIndex);
                data.setBoolean("wasOn", wasOn);
                for (int i = 0; i < 3; i++) {
                    tanks[i].writeToNBT(data, i + "");
                }
                networkPackNT(50);
                ci.cancel();
            }
        }
    }
}
