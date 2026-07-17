package com.pppopipupu.aletheia.mixin.machine;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.hbm.hazard.HazardSystem;
import com.hbm.inventory.fluid.tank.FluidTank;
import com.hbm.items.machine.ItemZirnoxRod;
import com.hbm.tileentity.TileEntityMachineBase;
import com.hbm.tileentity.machine.TileEntityReactorZirnox;
import com.pppopipupu.aletheia.item.ItemZirnoxRodAletheia;
import com.pppopipupu.aletheia.tileentity.IAletheiaZirnox;

import io.netty.buffer.ByteBuf;

@Mixin(value = TileEntityReactorZirnox.class, remap = false)
public abstract class MixinTileEntityReactorZirnox extends TileEntityMachineBase implements IAletheiaZirnox {

    public MixinTileEntityReactorZirnox(int size) {
        super(size);
    }

    public int aletheia$rodMode = 0;

    @Shadow
    public int heat;
    @Shadow
    public FluidTank steam;
    @Shadow
    protected int output;

    private ItemZirnoxRodAletheia.EnumZirnoxAletheiaType aletheia$type(int id) {
        ItemStack stack = slots[id];
        if (ItemZirnoxRodAletheia.isAletheiaRod(stack)) {
            return ((ItemZirnoxRodAletheia) stack.getItem()).getType();
        }
        return null;
    }

    private int[] aletheia$countRods() {
        int digamma = 0;
        int qgp = 0;
        for (int i = 0; i < 24; i++) {
            ItemZirnoxRodAletheia.EnumZirnoxAletheiaType t = aletheia$type(i);
            if (t == ItemZirnoxRodAletheia.EnumZirnoxAletheiaType.DIGAMMA) {
                digamma++;
            } else if (t == ItemZirnoxRodAletheia.EnumZirnoxAletheiaType.QGP) {
                qgp++;
            }
        }
        return new int[] { digamma, qgp };
    }

    @Override
    public int getRodMode() {
        return aletheia$rodMode;
    }

    @Redirect(
        method = "decay",
        at = @At(
            value = "FIELD",
            target = "Lcom/hbm/items/machine/ItemZirnoxRod$EnumZirnoxType;heat:I",
            opcode = org.objectweb.asm.Opcodes.GETFIELD))
    private int aletheia$heat(ItemZirnoxRod.EnumZirnoxType num, int id) {
        ItemZirnoxRodAletheia.EnumZirnoxAletheiaType t = aletheia$type(id);
        return t != null ? t.heat : num.heat;
    }

    @Redirect(
        method = "decay",
        at = @At(
            value = "FIELD",
            target = "Lcom/hbm/items/machine/ItemZirnoxRod$EnumZirnoxType;maxLife:I",
            opcode = org.objectweb.asm.Opcodes.GETFIELD))
    private int aletheia$maxLife(ItemZirnoxRod.EnumZirnoxType num, int id) {
        ItemZirnoxRodAletheia.EnumZirnoxAletheiaType t = aletheia$type(id);
        return t != null ? t.maxLife : num.maxLife;
    }

    @Redirect(
        method = "decay",
        at = @At(
            value = "FIELD",
            target = "Lcom/hbm/items/machine/ItemZirnoxRod$EnumZirnoxType;breeding:Z",
            opcode = org.objectweb.asm.Opcodes.GETFIELD))
    private boolean aletheia$breedingDecay(ItemZirnoxRod.EnumZirnoxType num, int id) {
        ItemZirnoxRodAletheia.EnumZirnoxAletheiaType t = aletheia$type(id);
        return t != null ? false : num.breeding;
    }

    @Redirect(
        method = "hasFuelRod",
        at = @At(
            value = "FIELD",
            target = "Lcom/hbm/items/machine/ItemZirnoxRod$EnumZirnoxType;breeding:Z",
            opcode = org.objectweb.asm.Opcodes.GETFIELD))
    private boolean aletheia$breedingHasFuel(ItemZirnoxRod.EnumZirnoxType num, int id) {
        ItemZirnoxRodAletheia.EnumZirnoxAletheiaType t = aletheia$type(id);
        return t != null ? false : num.breeding;
    }

    @Inject(method = "isItemValidForSlot", at = @At("HEAD"), cancellable = true)
    private void aletheia$isItemValidForSlot(int i, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (i < 24 && ItemZirnoxRodAletheia.isAletheiaRod(stack)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "canExtractItem", at = @At("HEAD"), cancellable = true)
    private void aletheia$canExtractItem(int i, ItemStack stack, int j, CallbackInfoReturnable<Boolean> cir) {
        if (i < 24 && ItemZirnoxRodAletheia.isAletheiaRod(stack)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(
        method = "updateEntity",
        at = @At(value = "INVOKE", target = "Lcom/hbm/tileentity/machine/TileEntityReactorZirnox;checkIfMeltdown()V"))
    private void aletheia$updateEffects(CallbackInfo ci) {
        int[] counts = aletheia$countRods();
        int digamma = counts[0];
        int qgp = counts[1];

        aletheia$rodMode = (digamma > 0 ? 1 : 0) | (qgp > 0 ? 2 : 0);

        if (digamma + qgp <= 0) {
            return;
        }

        this.heat += digamma * 40 + qgp * 120;

        if (this.steam.getFill() < this.steam.getMaxFill()) {
            int bonus = qgp * 15 + digamma * 6;
            this.steam.setFill(Math.min(this.steam.getMaxFill(), this.steam.getFill() + bonus));
            this.output += bonus;
        }

        if (worldObj.getTotalWorldTime() % 20 == 0) {
            double range = 15D;
            AxisAlignedBB box = AxisAlignedBB.getBoundingBox(
                xCoord - range,
                yCoord - range,
                zCoord - range,
                xCoord + range,
                yCoord + range,
                zCoord + range);
            for (Object o : worldObj.getEntitiesWithinAABB(EntityPlayer.class, box)) {
                EntityLivingBase entity = (EntityLivingBase) o;
                if (digamma > 0 && ItemZirnoxRodAletheia.rod_zirnox_digamma_depleted != null) {
                    HazardSystem.applyHazards(new ItemStack(ItemZirnoxRodAletheia.rod_zirnox_digamma_depleted), entity);
                }
                if (qgp > 0 && ItemZirnoxRodAletheia.rod_zirnox_qgp_depleted != null) {
                    HazardSystem.applyHazards(new ItemStack(ItemZirnoxRodAletheia.rod_zirnox_qgp_depleted), entity);
                }
            }
        }
    }

    @Inject(method = "serialize", at = @At("RETURN"))
    private void aletheia$serialize(ByteBuf buf, CallbackInfo ci) {
        buf.writeByte(aletheia$rodMode);
    }

    @Inject(method = "deserialize", at = @At("RETURN"))
    private void aletheia$deserialize(ByteBuf buf, CallbackInfo ci) {
        aletheia$rodMode = buf.readByte();
    }
}
