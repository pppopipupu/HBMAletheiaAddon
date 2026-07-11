package com.pppopipupu.aletheia.mixin.machine;

import api.hbm.energymk2.IEnergyReceiverMK2;
import api.hbm.fluidmk2.IFluidReceiverMK2;
import api.hbm.fluidmk2.IFluidStandardSenderMK2;
import com.hbm.blocks.BlockDummyable;
import com.hbm.inventory.fluid.tank.FluidTank;
import com.hbm.inventory.UpgradeManagerNT;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.material.MaterialShapes;
import com.hbm.inventory.material.Mats.MaterialStack;
import com.hbm.inventory.recipes.ElectrolyserFluidRecipes;
import com.hbm.inventory.recipes.ElectrolyserFluidRecipes.ElectrolysisRecipe;
import com.hbm.inventory.recipes.ElectrolyserMetalRecipes;
import com.hbm.inventory.recipes.ElectrolyserMetalRecipes.ElectrolysisMetalRecipe;
import com.hbm.items.machine.ItemMachineUpgrade.UpgradeType;
import com.hbm.lib.Library;
import com.hbm.packet.toclient.AuxParticlePacketNT;
import com.hbm.handler.threading.PacketThreading;
import com.hbm.tileentity.TileEntityMachineBase;
import com.hbm.tileentity.machine.TileEntityElectrolyser;
import com.hbm.util.CrucibleUtil;
import com.hbm.util.fauxpointtwelve.DirPos;
import com.pppopipupu.aletheia.interfaces.IUpgradeManagerAccess;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.ForgeDirection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TileEntityElectrolyser.class, remap = false)
public abstract class MixinTileEntityElectrolyser extends TileEntityMachineBase implements IEnergyReceiverMK2, IFluidReceiverMK2, IFluidStandardSenderMK2 {

    public MixinTileEntityElectrolyser(int size) {
        super(size);
    }

    private static final int[] aletheia$OverdriveSpeeds = {1, 2, 5, 10, 20, 50, 100};

    @Shadow public long power;
    @Shadow public FluidTank[] tanks;
    @Shadow public UpgradeManagerNT upgradeManager;
    @Shadow public int maxMaterial;
    @Shadow public int usageOre;
    @Shadow public int usageFluid;
    @Shadow public int processOreTime;
    @Shadow public int processFluidTime;
    @Shadow public int progressFluid;
    @Shadow public int progressOre;
    @Shadow public MaterialStack leftStack;
    @Shadow public MaterialStack rightStack;

    @Shadow public abstract DirPos[] getConPos();

    private void aletheia$updateDuration() {
        ElectrolysisMetalRecipe metal = ElectrolyserMetalRecipes.getRecipe(slots[14]);
        ElectrolysisRecipe fluid = ElectrolyserFluidRecipes.getRecipe(tanks[0].getTankType());
        this.processOreTime = metal != null ? metal.duration : 400;
        this.processFluidTime = fluid != null ? fluid.duration : 100;
    }

    @Inject(method = "updateEntity", at = @At("HEAD"), cancellable = true)
    private void aletheia$updateEntity(CallbackInfo ci) {
        TileEntityElectrolyser te = (TileEntityElectrolyser)(Object)this;
        if(!te.getWorldObj().isRemote) {
            
            upgradeManager.checkSlots(slots, 1, 2);
            int uCount = ((IUpgradeManagerAccess) upgradeManager).aletheia$getUltimateCount();

            if (uCount > 0) {
                this.power = Library.chargeTEFromItems(slots, 0, power, TileEntityElectrolyser.maxPower);
                this.tanks[0].setType(3, 4, slots);
                this.tanks[0].loadTank(5, 6, slots);
                this.tanks[1].unloadTank(7, 8, slots);
                this.tanks[2].unloadTank(9, 10, slots);

                if(te.getWorldObj().getTotalWorldTime() % 20 == 0) {
                    for(DirPos pos : this.getConPos()) {
                        this.trySubscribe(te.getWorldObj(), pos.getX(), pos.getY(), pos.getZ(), pos.getDir());
                        this.trySubscribe(tanks[0].getTankType(), te.getWorldObj(), pos.getX(), pos.getY(), pos.getZ(), pos.getDir());
                        this.trySubscribe(tanks[3].getTankType(), te.getWorldObj(), pos.getX(), pos.getY(), pos.getZ(), pos.getDir());

                        if(tanks[1].getFill() > 0) this.tryProvide(tanks[1], te.getWorldObj(), pos.getX(), pos.getY(), pos.getZ(), pos.getDir());
                        if(tanks[2].getFill() > 0) this.tryProvide(tanks[2], te.getWorldObj(), pos.getX(), pos.getY(), pos.getZ(), pos.getDir());
                    }
                }

                this.aletheia$updateDuration();

                int speedLevel = upgradeManager.getLevel(UpgradeType.SPEED);
                int powerLevel = upgradeManager.getLevel(UpgradeType.POWER);
                int overLevel = upgradeManager.getLevel(UpgradeType.OVERDRIVE);

                usageOre = TileEntityElectrolyser.usageOreBase * (4 - powerLevel) / 4 * (speedLevel + 1);
                usageFluid = TileEntityElectrolyser.usageFluidBase * (4 - powerLevel) / 4 * (speedLevel + 1);
                this.processOreTime = this.processOreTime * ((4 - speedLevel) / 4);
                this.processFluidTime = this.processFluidTime * ((4 - speedLevel) / 4);

                int count = aletheia$OverdriveSpeeds[overLevel];
                usageOre = (int)(usageOre * Math.pow(0.5D, uCount));
                usageFluid = (int)(usageFluid * Math.pow(0.5D, uCount));
                this.aletheia$updateDuration();
                count = count == 1 ? (1 + uCount * 4) : (count + uCount * 4);

                for(int i = 0; i < count; i++) {
                    if (te.canProcessFluid()) {
                        this.progressFluid++;
                        this.power -= this.usageFluid;

                        if (this.progressFluid >= this.processFluidTime) {
                            ElectrolysisRecipe recipe = ElectrolyserFluidRecipes.recipes.get(tanks[0].getTankType());
                            tanks[0].setFill(tanks[0].getFill() - recipe.amount);
                            tanks[1].setTankType(recipe.output1.type);
                            tanks[2].setTankType(recipe.output2.type);
                            int mult = 1 << uCount;
                            tanks[1].setFill(tanks[1].getFill() + recipe.output1.fill * mult);
                            tanks[2].setFill(tanks[2].getFill() + recipe.output2.fill * mult);

                            if(recipe.byproduct != null) {
                                for(int j = 0; j < recipe.byproduct.length; j++) {
                                    ItemStack slot = slots[11 + j];
                                    ItemStack byproduct = recipe.byproduct[j].copy();
                                    byproduct.stackSize *= mult;
                                    if(slot == null) {
                                        slots[11 + j] = byproduct;
                                    } else {
                                        slots[11 + j].stackSize += byproduct.stackSize;
                                    }
                                }
                            }
                            this.progressFluid = 0;
                            this.markChanged();
                        }
                    }

                    if (te.canProcessMetal()) {
                        this.progressOre++;
                        this.power -= this.usageOre;

                        if (this.progressOre >= this.processOreTime) {
                            ElectrolysisMetalRecipe recipe = ElectrolyserMetalRecipes.getRecipe(slots[14]);
                            int mult = 1 << uCount;
                            if(recipe.output1 != null) {
                                if(leftStack == null) {
                                    leftStack = new MaterialStack(recipe.output1.material, recipe.output1.amount * mult);
                                } else {
                                    leftStack.amount += recipe.output1.amount * mult;
                                }
                            }

                            if(recipe.output2 != null) {
                                if(rightStack == null) {
                                    rightStack = new MaterialStack(recipe.output2.material, recipe.output2.amount * mult);
                                } else {
                                    rightStack.amount += recipe.output2.amount * mult;
                                }
                            }

                            tanks[3].setFill(tanks[3].getFill() - 100);

                            if(recipe.byproduct != null) {
                                for(int j = 0; j < recipe.byproduct.length; j++) {
                                    ItemStack slot = slots[15 + j];
                                    ItemStack byproduct = recipe.byproduct[j].copy();
                                    byproduct.stackSize *= mult;
                                    if(slot == null) {
                                        slots[15 + j] = byproduct;
                                    } else {
                                        slots[15 + j].stackSize += byproduct.stackSize;
                                    }
                                }
                            }

                            if(slots[14].stackSize <= 1) {
                                slots[14] = null;
                            } else {
                                slots[14].stackSize--;
                            }
                            this.progressOre = 0;
                            this.markChanged();
                        }
                    }
                }    

                if(this.leftStack != null) {
                    ForgeDirection dir = ForgeDirection.getOrientation(te.getBlockMetadata() - BlockDummyable.offset).getOpposite();
                    List<MaterialStack> toCast = new ArrayList();
                    toCast.add(this.leftStack);

                    Vec3 impact = Vec3.createVectorHelper(0, 0, 0);
                    MaterialStack didPour = CrucibleUtil.pourFullStack(te.getWorldObj(), te.xCoord + 0.5D + dir.offsetX * 5.875D, te.yCoord + 2D, te.zCoord + 0.5D + dir.offsetZ * 5.875D, 6, true, toCast, MaterialShapes.INGOT.q(overLevel > 0 ? overLevel * 3 : 1), impact);

                    if(didPour != null) {
                        NBTTagCompound data = new NBTTagCompound();
                        data.setString("type", "foundry");
                        data.setInteger("color", didPour.material.moltenColor);
                        data.setByte("dir", (byte) dir.ordinal());
                        data.setFloat("off", 0.625F);
                        data.setFloat("base", 0.625F);
                        data.setFloat("len", Math.max(1F, te.yCoord - (float) (Math.ceil(impact.yCoord) - 0.875) + 2));
                        PacketThreading.createAllAroundThreadedPacket(new AuxParticlePacketNT(data, te.xCoord + 0.5D + dir.offsetX * 5.875D, te.yCoord + 2, te.zCoord + 0.5D + dir.offsetZ * 5.875D), new TargetPoint(te.getWorldObj().provider.dimensionId, te.xCoord + 0.5, te.yCoord + 1, te.zCoord + 0.5, 50));

                        if(this.leftStack.amount <= 0) this.leftStack = null;
                    }
                }

                if(this.rightStack != null) {
                    ForgeDirection dir = ForgeDirection.getOrientation(te.getBlockMetadata() - BlockDummyable.offset);
                    List<MaterialStack> toCast = new ArrayList();
                    toCast.add(this.rightStack);

                    Vec3 impact = Vec3.createVectorHelper(0, 0, 0);
                    MaterialStack didPour = CrucibleUtil.pourFullStack(te.getWorldObj(), te.xCoord + 0.5D + dir.offsetX * 5.875D, te.yCoord + 2D, te.zCoord + 0.5D + dir.offsetZ * 5.875D, 6, true, toCast, MaterialShapes.INGOT.q(overLevel > 0 ? overLevel * 3 : 1), impact);

                    if(didPour != null) {
                        NBTTagCompound data = new NBTTagCompound();
                        data.setString("type", "foundry");
                        data.setInteger("color", didPour.material.moltenColor);
                        data.setByte("dir", (byte) dir.ordinal());
                        data.setFloat("off", 0.625F);
                        data.setFloat("base", 0.625F);
                        data.setFloat("len", Math.max(1F, te.yCoord - (float) (Math.ceil(impact.yCoord) - 0.875) + 2));
                        PacketThreading.createAllAroundThreadedPacket(new AuxParticlePacketNT(data, te.xCoord + 0.5D + dir.offsetX * 5.875D, te.yCoord + 2, te.zCoord + 0.5D + dir.offsetZ * 5.875D), new TargetPoint(te.getWorldObj().provider.dimensionId, te.xCoord + 0.5, te.yCoord + 1, te.zCoord + 0.5, 50));

                        if(this.rightStack.amount <= 0) this.rightStack = null;
                    }
                }

                this.networkPackNT(50);
                ci.cancel();
            }
        }
    }

    @Inject(method = "canProcessFluid", at = @At("HEAD"), cancellable = true)
    private void aletheia$canProcessFluid(CallbackInfoReturnable<Boolean> cir) {
        int uCount = ((IUpgradeManagerAccess) upgradeManager).aletheia$getUltimateCount();
        if (uCount > 0) {
            if(this.power < usageFluid) {
                cir.setReturnValue(false);
                return;
            }
            ElectrolysisRecipe recipe = ElectrolyserFluidRecipes.recipes.get(tanks[0].getTankType());
            if(recipe == null) {
                cir.setReturnValue(false);
                return;
            }
            if(recipe.amount > tanks[0].getFill()) {
                cir.setReturnValue(false);
                return;
            }
            int mult = 1 << uCount;
            if(recipe.output1.type == tanks[1].getTankType() && recipe.output1.fill * mult + tanks[1].getFill() > tanks[1].getMaxFill()) {
                cir.setReturnValue(false);
                return;
            }
            if(recipe.output2.type == tanks[2].getTankType() && recipe.output2.fill * mult + tanks[2].getFill() > tanks[2].getMaxFill()) {
                cir.setReturnValue(false);
                return;
            }

            if(recipe.byproduct != null) {
                for(int i = 0; i < recipe.byproduct.length; i++) {
                    ItemStack slot = slots[11 + i];
                    ItemStack byproduct = recipe.byproduct[i];
                    if(slot == null) continue;
                    if(!slot.isItemEqual(byproduct)) {
                        cir.setReturnValue(false);
                        return;
                    }
                    if(slot.stackSize + byproduct.stackSize * mult > slot.getMaxStackSize()) {
                        cir.setReturnValue(false);
                        return;
                    }
                }
            }
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "canProcessMetal", at = @At("HEAD"), cancellable = true)
    private void aletheia$canProcessMetal(CallbackInfoReturnable<Boolean> cir) {
        int uCount = ((IUpgradeManagerAccess) upgradeManager).aletheia$getUltimateCount();
        if (uCount > 0) {
            if(slots[14] == null) {
                cir.setReturnValue(false);
                return;
            }
            if(this.power < usageOre) {
                cir.setReturnValue(false);
                return;
            }
            if(this.tanks[3].getFill() < 100) {
                cir.setReturnValue(false);
                return;
            }

            ElectrolysisMetalRecipe recipe = ElectrolyserMetalRecipes.getRecipe(slots[14]);
            if(recipe == null) {
                cir.setReturnValue(false);
                return;
            }

            int mult = 1 << uCount;
            if(leftStack != null && recipe.output1 != null) {
                if(recipe.output1.material != leftStack.material) {
                    cir.setReturnValue(false);
                    return;
                }
                if(recipe.output1.amount * mult + leftStack.amount > this.maxMaterial) {
                    cir.setReturnValue(false);
                    return;
                }
            }

            if(rightStack != null && recipe.output2 != null) {
                if(recipe.output2.material != rightStack.material) {
                    cir.setReturnValue(false);
                    return;
                }
                if(recipe.output2.amount * mult + rightStack.amount > this.maxMaterial) {
                    cir.setReturnValue(false);
                    return;
                }
            }

            if(recipe.byproduct != null) {
                for(int i = 0; i < recipe.byproduct.length; i++) {
                    ItemStack slot = slots[15 + i];
                    ItemStack byproduct = recipe.byproduct[i];
                    if(slot == null) continue;
                    if(!slot.isItemEqual(byproduct)) {
                        cir.setReturnValue(false);
                        return;
                    }
                    if(slot.stackSize + byproduct.stackSize * mult > slot.getMaxStackSize()) {
                        cir.setReturnValue(false);
                        return;
                    }
                }
            }
            cir.setReturnValue(true);
        }
    }
}
