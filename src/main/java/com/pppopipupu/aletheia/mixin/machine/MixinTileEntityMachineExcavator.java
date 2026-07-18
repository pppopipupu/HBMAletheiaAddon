package com.pppopipupu.aletheia.mixin.machine;

import java.util.HashMap;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.hbm.inventory.UpgradeManagerNT;
import com.hbm.inventory.fluid.tank.FluidTank;
import com.hbm.items.machine.ItemMachineUpgrade.UpgradeType;
import com.hbm.lib.Library;
import com.hbm.tileentity.TileEntityMachineBase;
import com.hbm.tileentity.machine.TileEntityMachineExcavator;
import com.hbm.util.fauxpointtwelve.DirPos;
import com.pppopipupu.aletheia.interfaces.IUpgradeManagerAccess;

import api.hbm.energymk2.IEnergyReceiverMK2;
import api.hbm.fluidmk2.IFluidReceiverMK2;

@Mixin(value = TileEntityMachineExcavator.class, remap = false)
public abstract class MixinTileEntityMachineExcavator extends TileEntityMachineBase implements IEnergyReceiverMK2 {

    public MixinTileEntityMachineExcavator(int size) {
        super(size);
    }

    private static final int[] aletheia$overdriveSpeeds = { 1, 2, 5, 10, 20, 50, 100 };

    @Shadow(remap = false)
    public UpgradeManagerNT upgradeManager;
    @Shadow(remap = false)
    public long power;
    @Shadow(remap = false)
    public long consumption;
    @Shadow(remap = false)
    public double speed;
    @Shadow(remap = false)
    public FluidTank tank;
    @Shadow(remap = false)
    public boolean operational;
    @Shadow(remap = false)
    protected boolean bedrockDrilling;
    @Shadow(remap = false)
    public boolean enableDrill;
    @Shadow(remap = false)
    public boolean enableCrusher;
    @Shadow(remap = false)
    public boolean enableWalling;
    @Shadow(remap = false)
    public boolean enableVeinMiner;
    @Shadow(remap = false)
    public boolean enableSilkTouch;
    @Shadow(remap = false)
    public int targetDepth;
    @Shadow(remap = false)
    public float drillRotation;
    @Shadow(remap = false)
    public float prevDrillRotation;
    @Shadow(remap = false)
    public float drillExtension;
    @Shadow(remap = false)
    public float prevDrillExtension;
    @Shadow(remap = false)
    public float crusherRotation;
    @Shadow(remap = false)
    public float prevCrusherRotation;
    @Shadow(remap = false)
    public int chuteTimer;
    @Shadow(remap = false)
    public long baseConsumption;
    @Shadow(remap = false)
    public static long maxPower;

    @Shadow(remap = false)
    protected abstract void tryEjectBuffer();

    @Shadow(remap = false)
    protected abstract DirPos[] getConPos();

    @Shadow(remap = false)
    protected abstract boolean tryDrill(int radius);

    @Inject(method = "getValidUpgrades", at = @At("RETURN"))
    private void aletheia$getValidUpgrades(CallbackInfoReturnable<HashMap<UpgradeType, Integer>> cir) {
        HashMap<UpgradeType, Integer> upgrades = cir.getReturnValue();
        if (upgrades != null && !upgrades.containsKey(UpgradeType.OVERDRIVE)) {
            upgrades.put(UpgradeType.OVERDRIVE, 3);
        }
    }

    private int aletheia$lastSlotHash;

    private int aletheia$prevChuteTimer;

    @Inject(method = "updateEntity", at = @At("HEAD"), cancellable = true, remap = true)
    private void aletheia$updateEntity(CallbackInfo ci) {
        upgradeManager.checkSlots(slots, 2, 3);
        int uCount = ((IUpgradeManagerAccess) upgradeManager).aletheia$getUltimateCount();
        if (uCount > 0) {
            TileEntityMachineExcavator te = (TileEntityMachineExcavator) (Object) this;
            int speedLevel = upgradeManager.getLevel(UpgradeType.SPEED);
            int powerLevel = upgradeManager.getLevel(UpgradeType.POWER);
            int over = aletheia$overdriveSpeeds[upgradeManager.getLevel(UpgradeType.OVERDRIVE)];
            consumption = baseConsumption * (1 + speedLevel);
            consumption /= (1 + powerLevel);
            consumption = (int) (consumption * ((IUpgradeManagerAccess) upgradeManager).aletheia$getPowerMult());
            if (!worldObj.isRemote) {
                tank.setType(1, slots);
                if (worldObj.getTotalWorldTime() % 20 == 0) {
                    tryEjectBuffer();
                    for (DirPos pos : getConPos()) {
                        this.trySubscribe(worldObj, pos.getX(), pos.getY(), pos.getZ(), pos.getDir());
                        ((IFluidReceiverMK2) this).trySubscribe(
                            tank.getTankType(),
                            worldObj,
                            pos.getX(),
                            pos.getY(),
                            pos.getZ(),
                            pos.getDir());
                    }
                }
                if (chuteTimer > 0) chuteTimer--;
                power = Library.chargeTEFromItems(slots, 0, this.getPower(), this.getMaxPower());
                operational = false;
                int radiusLevel = upgradeManager.getLevel(UpgradeType.EFFECT);
                if (enableDrill && te.getInstalledDrill() != null && power >= te.getPowerConsumption()) {
                    operational = true;
                    power -= te.getPowerConsumption();
                    speed = te.getInstalledDrill().speed;
                    speed *= (1 + speedLevel / 2D) * over;
                    int speedFactor = ((IUpgradeManagerAccess) upgradeManager).aletheia$getSpeedMult();
                    speed = speed * speedFactor;
                    int maxDepth = yCoord - 4;
                    if ((bedrockDrilling || targetDepth <= maxDepth) && tryDrill(1 + radiusLevel * 2)) {
                        targetDepth++;
                        if (targetDepth > maxDepth) {
                            enableDrill = false;
                        }
                    }
                } else {
                    targetDepth = 0;
                }
                if (chuteTimer > aletheia$prevChuteTimer) {
                    int mult = ((IUpgradeManagerAccess) upgradeManager).aletheia$getProductionMult();
                    if (mult > 1) {
                        for (int s = 5; s <= 13; s++) {
                            if (slots[s] != null) {
                                slots[s].stackSize *= mult;
                            }
                        }
                    }
                }
                aletheia$prevChuteTimer = chuteTimer;
                networkPackNT(150);
            } else {
                prevDrillExtension = drillExtension;
                if (drillExtension != targetDepth) {
                    float diff = Math.abs(drillExtension - targetDepth);
                    float spd = Math.max(0.15F, diff / 10F);
                    if (diff <= spd) {
                        drillExtension = targetDepth;
                    } else {
                        float sig = Math.signum(drillExtension - targetDepth);
                        drillExtension -= sig * spd;
                    }
                }
                prevDrillRotation = drillRotation;
                prevCrusherRotation = crusherRotation;
                if (operational) {
                    drillRotation += 10F * (speedLevel / 2F + 1);
                    drillRotation += 10F * (4 * uCount);
                    if (enableCrusher) {
                        crusherRotation += 10F;
                    }
                }
                if (drillRotation >= 360F) {
                    drillRotation -= 360F;
                    prevDrillRotation -= 360F;
                }
                if (crusherRotation >= 360F) {
                    crusherRotation -= 360F;
                    prevCrusherRotation -= 360F;
                }
            }
            ci.cancel();
        }
    }
}
