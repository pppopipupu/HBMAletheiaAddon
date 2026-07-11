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
import com.hbm.inventory.recipes.ExposureChamberRecipes.ExposureChamberRecipe;
import com.hbm.items.machine.ItemMachineUpgrade.UpgradeType;
import com.hbm.lib.Library;
import com.hbm.tileentity.TileEntityMachineBase;
import com.hbm.tileentity.machine.TileEntityMachineExposureChamber;
import com.hbm.util.fauxpointtwelve.DirPos;
import com.pppopipupu.aletheia.interfaces.IUpgradeManagerAccess;

import api.hbm.energymk2.IEnergyReceiverMK2;

@Mixin(value = TileEntityMachineExposureChamber.class, remap = false)
public abstract class MixinTileEntityMachineExposureChamber extends TileEntityMachineBase
    implements IEnergyReceiverMK2 {

    public MixinTileEntityMachineExposureChamber(int size) {
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
    public int consumption;
    @Shadow(remap = false)
    public int savedParticles;
    @Shadow(remap = false)
    public boolean isOn;
    @Shadow(remap = false)
    public float rotation;
    @Shadow(remap = false)
    public float prevRotation;

    @Inject(method = "getValidUpgrades", at = @At("RETURN"))
    private void aletheia$getValidUpgrades(CallbackInfoReturnable<HashMap<UpgradeType, Integer>> cir) {
        HashMap<UpgradeType, Integer> upgrades = cir.getReturnValue();
        if (upgrades != null && !upgrades.containsKey(UpgradeType.OVERDRIVE)) {
            upgrades.put(UpgradeType.OVERDRIVE, 3);
        }
    }

    @Inject(method = "updateEntity", at = @At("HEAD"), cancellable = true)
    private void aletheia$updateEntity(CallbackInfo ci) {
        if (!worldObj.isRemote) {
            upgradeManager.checkSlots(slots, 6, 7);
            int uCount = ((IUpgradeManagerAccess) upgradeManager).aletheia$getUltimateCount();
            if (uCount > 0) {
                TileEntityMachineExposureChamber te = (TileEntityMachineExposureChamber) (Object) this;
                isOn = false;
                power = Library.chargeTEFromItems(slots, 5, power, TileEntityMachineExposureChamber.maxPower);
                if (worldObj.getTotalWorldTime() % 20 == 0) {
                    for (DirPos pos : te.getConPos())
                        te.trySubscribe(worldObj, pos.getX(), pos.getY(), pos.getZ(), pos.getDir());
                }
                int speedLevel = upgradeManager.getLevel(UpgradeType.SPEED);
                int powerLevel = upgradeManager.getLevel(UpgradeType.POWER);
                int overdrive = aletheia$overdriveSpeeds[upgradeManager.getLevel(UpgradeType.OVERDRIVE)];
                consumption = TileEntityMachineExposureChamber.consumptionBase;
                processTime = TileEntityMachineExposureChamber.processTimeBase
                    - TileEntityMachineExposureChamber.processTimeBase / 4 * speedLevel;
                consumption *= (speedLevel / 2 + 1);
                processTime *= (powerLevel / 2 + 1);
                consumption /= (powerLevel + 1);
                processTime /= overdrive;
                consumption *= overdrive;
                consumption = (int) (consumption * Math.pow(0.5D, uCount));
                if (slots[1] == null && slots[0] != null && slots[3] != null && savedParticles <= 0) {
                    ExposureChamberRecipe recipe = te.getRecipe(slots[0], slots[3]);
                    if (recipe != null) {
                        ItemStack container = slots[0].getItem()
                            .getContainerItem(slots[0]);
                        boolean canStore = false;
                        if (container == null) {
                            canStore = true;
                        } else if (slots[2] == null) {
                            slots[2] = container.copy();
                            canStore = true;
                        } else if (slots[2].getItem() == container.getItem()
                            && slots[2].getItemDamage() == container.getItemDamage()
                            && slots[2].stackSize < slots[2].getMaxStackSize()) {
                                slots[2].stackSize++;
                                canStore = true;
                            }
                        if (canStore) {
                            slots[1] = slots[0].copy();
                            slots[1].stackSize = 0;
                            te.decrStackSize(0, 1);
                            savedParticles = TileEntityMachineExposureChamber.maxParticles;
                        }
                    }
                }
                if (slots[1] != null && savedParticles > 0 && power >= consumption) {
                    int speedFactor = 1 + uCount * 4;
                    for (int i = 0; i < speedFactor; i++) {
                        ExposureChamberRecipe recipe = te.getRecipe(slots[1], slots[3]);
                        int mult = 1 << uCount;
                        if (power >= consumption && recipe != null
                            && (slots[4] == null || (slots[4].getItem() == recipe.output.getItem()
                                && slots[4].getItemDamage() == recipe.output.getItemDamage()
                                && slots[4].stackSize + recipe.output.stackSize * mult
                                    <= slots[4].getMaxStackSize()))) {
                            progress++;
                            power -= consumption;
                            isOn = true;
                            if (progress >= processTime) {
                                progress = 0;
                                savedParticles--;
                                te.decrStackSize(3, 1);
                                if (slots[4] == null) {
                                    slots[4] = recipe.output.copy();
                                    slots[4].stackSize *= mult;
                                } else {
                                    slots[4].stackSize += recipe.output.stackSize * mult;
                                }
                            }
                        } else {
                            progress = 0;
                            break;
                        }
                    }
                } else {
                    progress = 0;
                }
                if (savedParticles <= 0) {
                    slots[1] = null;
                }
                te.networkPackNT(50);
                ci.cancel();
            }
        }
    }
}
