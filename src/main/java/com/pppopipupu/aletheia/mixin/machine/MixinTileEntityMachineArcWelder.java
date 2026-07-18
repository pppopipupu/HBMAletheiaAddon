package com.pppopipupu.aletheia.mixin.machine;

import java.util.HashMap;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.hbm.handler.threading.PacketThreading;
import com.hbm.inventory.UpgradeManagerNT;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.recipes.ArcWelderRecipes;
import com.hbm.inventory.recipes.ArcWelderRecipes.ArcWelderRecipe;
import com.hbm.items.machine.ItemMachineUpgrade.UpgradeType;
import com.hbm.lib.Library;
import com.hbm.packet.toclient.AuxParticlePacketNT;
import com.hbm.tileentity.TileEntityMachineBase;
import com.hbm.tileentity.machine.TileEntityMachineArcWelder;
import com.hbm.util.fauxpointtwelve.DirPos;
import com.pppopipupu.aletheia.interfaces.IUpgradeManagerAccess;

import api.hbm.energymk2.IEnergyReceiverMK2;
import api.hbm.fluidmk2.IFluidReceiverMK2;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;

@Mixin(value = TileEntityMachineArcWelder.class, remap = false)
public abstract class MixinTileEntityMachineArcWelder extends TileEntityMachineBase implements IEnergyReceiverMK2 {

    public MixinTileEntityMachineArcWelder(int size) {
        super(size);
    }

    private static final int[] aletheia$overdriveSpeeds = { 1, 2, 5, 10, 20, 50, 100 };

    @Shadow(remap = false)
    public UpgradeManagerNT upgradeManager;
    @Shadow(remap = false)
    public long power;
    @Shadow(remap = false)
    public long maxPower;
    @Shadow(remap = false)
    public long consumption;
    @Shadow(remap = false)
    public int progress;
    @Shadow(remap = false)
    public int processTime;

    @Shadow(remap = false)
    protected abstract DirPos[] getConPos();

    @Inject(method = "getValidUpgrades", at = @At("RETURN"))
    private void aletheia$getValidUpgrades(CallbackInfoReturnable<HashMap<UpgradeType, Integer>> cir) {
        HashMap<UpgradeType, Integer> upgrades = cir.getReturnValue();
        if (upgrades != null && !upgrades.containsKey(UpgradeType.OVERDRIVE)) {
            upgrades.put(UpgradeType.OVERDRIVE, 3);
        }
    }

    @Inject(method = "canProcess", at = @At("HEAD"), cancellable = true)
    private void aletheia$canProcess(ArcWelderRecipe recipe, CallbackInfoReturnable<Boolean> cir) {
        int uCount = ((IUpgradeManagerAccess) upgradeManager).aletheia$getUltimateCount();
        if (uCount > 0) {
            TileEntityMachineArcWelder te = (TileEntityMachineArcWelder) (Object) this;
            if (te.power < te.consumption) {
                cir.setReturnValue(false);
                return;
            }
            if (recipe.fluid != null) {
                if (te.tank.getTankType() != recipe.fluid.type) {
                    cir.setReturnValue(false);
                    return;
                }
                if (te.tank.getFill() < recipe.fluid.fill) {
                    cir.setReturnValue(false);
                    return;
                }
            }
            int mult = ((IUpgradeManagerAccess) upgradeManager).aletheia$getProductionMult();
            if (slots[3] != null) {
                if (slots[3].getItem() != recipe.output.getItem()) {
                    cir.setReturnValue(false);
                    return;
                }
                if (slots[3].getItemDamage() != recipe.output.getItemDamage()) {
                    cir.setReturnValue(false);
                    return;
                }
                if (slots[3].stackSize + recipe.output.stackSize * mult > slots[3].getMaxStackSize()) {
                    cir.setReturnValue(false);
                    return;
                }
            }
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "updateEntity", at = @At("HEAD"), cancellable = true, remap = true)
    private void aletheia$updateEntity(CallbackInfo ci) {
        TileEntityMachineArcWelder te = (TileEntityMachineArcWelder) (Object) this;
        if (!te.getWorldObj().isRemote) {
            te.power = Library.chargeTEFromItems(slots, 4, te.getPower(), te.getMaxPower());
            te.tank.setType(5, slots);
            if (worldObj.getTotalWorldTime() % 20 == 0) {
                for (DirPos pos : getConPos()) {
                    te.trySubscribe(worldObj, pos.getX(), pos.getY(), pos.getZ(), pos.getDir());
                    if (te.tank.getTankType() != Fluids.NONE) ((IFluidReceiverMK2) this).trySubscribe(
                        te.tank.getTankType(),
                        worldObj,
                        pos.getX(),
                        pos.getY(),
                        pos.getZ(),
                        pos.getDir());
                }
            }
            ArcWelderRecipe recipe = ArcWelderRecipes.getRecipe(slots[0], slots[1], slots[2]);
            long intendedMaxPower;
            upgradeManager.checkSlots(slots, 6, 7);
            int uCount = ((IUpgradeManagerAccess) upgradeManager).aletheia$getUltimateCount();
            if (uCount > 0) {
                int redLevel = upgradeManager.getLevel(UpgradeType.SPEED);
                int blueLevel = upgradeManager.getLevel(UpgradeType.POWER);
                int black = aletheia$overdriveSpeeds[upgradeManager.getLevel(UpgradeType.OVERDRIVE)];
                if (recipe != null) {
                    processTime = recipe.duration * (4 - redLevel) / 4 / black;
                    consumption = recipe.consumption * (4 - blueLevel) / 4 * (redLevel + 1) * black;
                    intendedMaxPower = recipe.consumption * 20 * black;
                    double powerFactor = ((IUpgradeManagerAccess) upgradeManager).aletheia$getPowerMult();
                    consumption = (long) (consumption * powerFactor);
                    intendedMaxPower = intendedMaxPower
                        * (((IUpgradeManagerAccess) upgradeManager).aletheia$getSpeedMult());
                    int speedFactor = ((IUpgradeManagerAccess) upgradeManager).aletheia$getSpeedMult();
                    for (int i = 0; i < speedFactor; i++) {
                        if (te.canProcess(recipe)) {
                            progress++;
                            power -= consumption;
                            if (progress >= processTime) {
                                progress = 0;
                                te.consumeItems(recipe);
                                int mult = ((IUpgradeManagerAccess) upgradeManager).aletheia$getProductionMult();
                                if (slots[3] == null) {
                                    slots[3] = recipe.output.copy();
                                    slots[3].stackSize *= mult;
                                } else {
                                    slots[3].stackSize += recipe.output.stackSize * mult;
                                }
                                te.markDirty();
                            }
                            if (worldObj.getTotalWorldTime() % 2 == 0) {
                                ForgeDirection dir = ForgeDirection.getOrientation(te.getBlockMetadata() - 10);
                                NBTTagCompound dPart = new NBTTagCompound();
                                dPart.setString("type", worldObj.getTotalWorldTime() % 20 == 0 ? "tau" : "hadron");
                                dPart.setByte("count", (byte) 5);
                                PacketThreading.createAllAroundThreadedPacket(
                                    new AuxParticlePacketNT(
                                        dPart,
                                        xCoord + 0.5 - dir.offsetX * 0.5,
                                        yCoord + 1.25,
                                        zCoord + 0.5 - dir.offsetZ * 0.5),
                                    new TargetPoint(worldObj.provider.dimensionId, xCoord, yCoord, zCoord, 25));
                            }
                        } else {
                            progress = 0;
                            break;
                        }
                    }
                } else {
                    progress = 0;
                    consumption = 100;
                    intendedMaxPower = 2000;
                }
                maxPower = Math.max(intendedMaxPower, power);
                te.networkPackNT(25);
                ci.cancel();
            }
        }
    }
}
