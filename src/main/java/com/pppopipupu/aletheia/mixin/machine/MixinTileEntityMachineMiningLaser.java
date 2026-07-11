package com.pppopipupu.aletheia.mixin.machine;

import api.hbm.energymk2.IEnergyReceiverMK2;
import api.hbm.fluid.IFluidStandardSender;
import com.hbm.inventory.UpgradeManagerNT;
import com.hbm.inventory.fluid.tank.FluidTank;
import com.hbm.items.machine.ItemMachineUpgrade;
import com.hbm.items.machine.ItemMachineUpgrade.UpgradeType;
import com.hbm.lib.Library;
import com.hbm.tileentity.TileEntityMachineBase;
import com.hbm.tileentity.machine.TileEntityMachineMiningLaser;
import com.hbm.util.fauxpointtwelve.DirPos;
import com.pppopipupu.aletheia.interfaces.IUpgradeManagerAccess;
import net.minecraft.block.Block;
import net.minecraftforge.common.util.ForgeDirection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.HashMap;

@Mixin(value = TileEntityMachineMiningLaser.class, remap = false)
public abstract class MixinTileEntityMachineMiningLaser extends TileEntityMachineBase implements IEnergyReceiverMK2 {

    public MixinTileEntityMachineMiningLaser(int size) {
        super(size);
    }

    @Shadow(remap = false) public UpgradeManagerNT upgradeManager;
    @Shadow(remap = false) public long power;
    @Shadow(remap = false) public FluidTank tank;
    @Shadow(remap = false) public boolean isOn;
    @Shadow(remap = false) public boolean beam;
    @Shadow(remap = false) public int targetX;
    @Shadow(remap = false) public int targetY;
    @Shadow(remap = false) public int targetZ;
    @Shadow(remap = false) public int lastTargetX;
    @Shadow(remap = false) public int lastTargetY;
    @Shadow(remap = false) public int lastTargetZ;
    @Shadow(remap = false) public double breakProgress;
    @Shadow(remap = false) private void updateConnections() { }
    @Shadow(remap = false) private DirPos[] getConPos() { return null; }
    @Shadow(remap = false) private void tryFillContainer(int x, int y, int z) { }
    @Shadow(remap = false) private void breakBlock(int fortune) { }
    @Shadow(remap = false) private void buildDam() { }
    @Shadow(remap = false) private boolean canBreak(Block block, int x, int y, int z) { return false; }
    @Shadow(remap = false) public void scan(int range) { }
    @Shadow(remap = false) public double getBreakSpeed(int speed) { return 0; }

    @Inject(method = "getValidUpgrades", at = @At("RETURN"))
    private void aletheia$getValidUpgrades(CallbackInfoReturnable<HashMap<UpgradeType, Integer>> cir) {
        HashMap<UpgradeType, Integer> upgrades = cir.getReturnValue();
        if (upgrades != null && !upgrades.containsKey(UpgradeType.OVERDRIVE)) {
            upgrades.put(UpgradeType.OVERDRIVE, 9);
        }
    }

    @Inject(method = "updateEntity", at = @At("HEAD"), cancellable = true)
    private void aletheia$updateEntity(CallbackInfo ci) {
        if (!worldObj.isRemote) {
            upgradeManager.checkSlots(this, slots, 1, 8);
            int uCount = ((IUpgradeManagerAccess) upgradeManager).aletheia$getUltimateCount();
            if (uCount > 0) {
                updateConnections();
                for (DirPos pos : getConPos()) {
                    ((IFluidStandardSender) this).sendFluid(tank, worldObj, pos.getX(), pos.getY(), pos.getZ(), pos.getDir());
                }
                power = Library.chargeTEFromItems(slots, 0, power, TileEntityMachineMiningLaser.maxPower);
                if (lastTargetX != targetX || lastTargetY != targetY || lastTargetZ != targetZ) {
                    breakProgress = 0;
                }
                lastTargetX = targetX;
                lastTargetY = targetY;
                lastTargetZ = targetZ;
                boolean prevRedstone = false;
                boolean redstonePowered = false;
                for (DirPos conPos : getConPos()) {
                    if (worldObj.isBlockIndirectlyGettingPowered(conPos.getX() - conPos.getDir().offsetX, conPos.getY() - conPos.getDir().offsetY, conPos.getZ() - conPos.getDir().offsetZ)) {
                        redstonePowered = true;
                        break;
                    }
                }
                if (prevRedstone != redstonePowered) {
                    markDirty();
                }
                boolean shouldRun = isOn && !redstonePowered;
                if (shouldRun) {
                    int cycles = 1 + upgradeManager.getLevel(UpgradeType.OVERDRIVE);
                    int speed = 1 + upgradeManager.getLevel(UpgradeType.SPEED);
                    int range = 1 + upgradeManager.getLevel(UpgradeType.EFFECT) * 2;
                    int fortune = upgradeManager.getLevel(UpgradeType.FORTUNE);
                    int consumption = TileEntityMachineMiningLaser.consumption
                            - (TileEntityMachineMiningLaser.consumption * upgradeManager.getLevel(UpgradeType.POWER) / 16)
                            + (TileEntityMachineMiningLaser.consumption * upgradeManager.getLevel(UpgradeType.SPEED) / 16);
                    cycles = cycles == 1 ? (1 + uCount * 4) : (cycles + uCount * 4);
                    consumption = (int)(consumption * Math.pow(0.5D, uCount));
                    for (int i = 0; i < cycles; i++) {
                        if (power < consumption) {
                            beam = false;
                            break;
                        }
                        power -= consumption;
                        if (targetY <= 0) {
                            targetY = yCoord - 2;
                        }
                        scan(range);
                        if (beam && canBreak(worldObj.getBlock(targetX, targetY, targetZ), targetX, targetY, targetZ)) {
                            breakProgress += getBreakSpeed(speed);
                            if (breakProgress < 1) {
                                worldObj.destroyBlockInWorldPartially(-1, targetX, targetY, targetZ, (int)Math.floor(breakProgress * 10));
                            } else {
                                breakBlock(fortune);
                                buildDam();
                            }
                        }
                    }
                } else {
                    targetY = yCoord - 2;
                    beam = false;
                }
                for (DirPos pos : getConPos()) {
                    tryFillContainer(pos.getX(), pos.getY(), pos.getZ());
                }
                networkPackNT(250);
                ci.cancel();
            }
        }
    }
}
