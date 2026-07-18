package com.pppopipupu.aletheia.mixin;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.hbm.items.ModItems;
import com.hbm.items.machine.ItemPWRFuel;

@Mixin(value = ItemPWRFuel.class, remap = false)
public class MixinItemPWRFuel {

    @Unique
    private IIcon aletheia$iconQGP;

    @Inject(method = "addInformation", at = @At("HEAD"), cancellable = true, remap = true)
    private void aletheia$addInformation(ItemStack stack, EntityPlayer player, List list, boolean bool,
        CallbackInfo ci) {
        if (stack.getItemDamage() == 99) {
            String color = EnumChatFormatting.GOLD + "";
            String reset = EnumChatFormatting.RESET + "";
            list.add(color + "Heat per flux: " + reset + "30.0 TU");
            list.add(color + "Reaction function: " + reset + "sqrt(x * 50.0)");
            list.add(color + "Fuel type: " + reset + "VERY DANGEROUS / CRITICAL");
            ci.cancel();
        }
    }

    @Inject(method = { "registerIcons", "func_94581_a" }, at = @At("TAIL"), require = 0, expect = 0)
    private void aletheia$registerIcons(IIconRegister reg, CallbackInfo ci) {
        Item thisItem = (Item) (Object) this;
        if (thisItem == ModItems.pwr_fuel) {
            aletheia$iconQGP = reg.registerIcon("hbm:pwr_fuel.hes327");
        } else if (thisItem == ModItems.pwr_fuel_hot) {
            aletheia$iconQGP = reg.registerIcon("hbm:pwr_fuel_hot.hes327");
        } else if (thisItem == ModItems.pwr_fuel_depleted) {
            aletheia$iconQGP = reg.registerIcon("hbm:pwr_fuel_depleted.hes327");
        }
    }

    @Inject(
        method = { "getIconFromDamage", "func_77617_a" },
        at = @At("HEAD"),
        cancellable = true,
        require = 0,
        expect = 0)
    private void aletheia$getIconFromDamage(int meta, CallbackInfoReturnable<IIcon> cir) {
        if (meta == 99) {
            cir.setReturnValue(aletheia$iconQGP);
        }
    }

    @Inject(method = { "getSubItems", "func_150895_a" }, at = @At("TAIL"), require = 0, expect = 0)
    private void aletheia$getSubItems(Item item, CreativeTabs tab, List list, CallbackInfo ci) {
        list.add(new ItemStack(item, 1, 99));
    }
}
