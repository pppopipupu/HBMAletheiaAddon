package com.pppopipupu.aletheia.mixin;

import java.util.List;
import java.util.Locale;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.hbm.items.ModItems;
import com.hbm.items.machine.ItemRBMKRod;
import com.hbm.items.machine.ItemRBMKRod.EnumBurnFunc;
import com.hbm.tileentity.machine.rbmk.IRBMKFluxReceiver.NType;
import com.hbm.util.i18n.I18nUtil;
import com.pppopipupu.aletheia.item.ItemRBMKFuelQGP;

@Mixin(value = ItemRBMKRod.class, remap = false)
public class MixinItemRBMKRod {

    @Inject(method = "reactivityFunc", at = @At("HEAD"), cancellable = true)
    private void aletheia$reactivityFunc(double in, double enrichment, CallbackInfoReturnable<Double> cir) {
        if ((Object) this instanceof ItemRBMKFuelQGP) {
            double flux = in * reactivityModByEnrichment(enrichment);
            double result = (Math.pow(1.01, flux) / 2.0D) / 100.0D * reactivity;
            cir.setReturnValue(result);
        }
    }

    @Shadow(remap = false)
    public double heat;
    @Shadow(remap = false)
    public double diffusion;
    @Shadow(remap = false)
    public double meltingPoint;
    @Shadow(remap = false)
    public double yield;
    @Shadow(remap = false)
    public double xGen;
    @Shadow(remap = false)
    public double xBurn;
    @Shadow(remap = false)
    public double selfRate;
    @Shadow(remap = false)
    public double reactivity;
    @Shadow(remap = false)
    public String fullName;
    @Shadow(remap = false)
    public EnumBurnFunc function;
    @Shadow(remap = false)
    public NType nType;
    @Shadow(remap = false)
    public NType rType;

    @Shadow(remap = false)
    public static double getHullHeat(ItemStack stack) {
        return 0;
    }

    @Shadow(remap = false)
    public static double getCoreHeat(ItemStack stack) {
        return 0;
    }

    @Shadow(remap = false)
    public static double getYield(ItemStack stack) {
        return 0;
    }

    @Shadow(remap = false)
    public static double getPoison(ItemStack stack) {
        return 0;
    }

    @Shadow(remap = false)
    public static double getEnrichment(ItemStack stack) {
        return 0;
    }

    @Shadow(remap = false)
    public double reactivityModByEnrichment(double enrichment) {
        return 0;
    }

    @Inject(method = "addInformation", at = @At("HEAD"), cancellable = true, remap = true)
    private void aletheia$addInformation(ItemStack stack, EntityPlayer player, List list, boolean bool,
        CallbackInfo ci) {
        if ((Object) this != ModItems.rbmk_fuel_drx) return;

        list.add(EnumChatFormatting.ITALIC + this.fullName);

        if (getHullHeat(stack) >= 50 || getCoreHeat(stack) >= 50) {
            list.add(EnumChatFormatting.GOLD + I18nUtil.resolveKey("desc.item.wasteCooling"));
        }

        if (selfRate > 0 || this.function == EnumBurnFunc.SIGMOID) {
            list.add(EnumChatFormatting.RED + I18nUtil.resolveKey("trait.rbmx.source"));
        }

        list.add(
            EnumChatFormatting.GREEN + I18nUtil.resolveKey(
                "trait.rbmx.depletion",
                ((int) (((yield - getYield(stack)) / yield) * 100000)) / 1000D + "%"));
        list.add(
            EnumChatFormatting.DARK_PURPLE
                + I18nUtil.resolveKey("trait.rbmx.xenon", ((int) (getPoison(stack) * 1000D) / 1000D) + "%"));
        list.add(
            EnumChatFormatting.BLUE
                + I18nUtil.resolveKey("trait.rbmx.splitsWith", I18nUtil.resolveKey(nType.unlocalized + ".x")));
        list.add(
            EnumChatFormatting.BLUE
                + I18nUtil.resolveKey("trait.rbmx.splitsInto", I18nUtil.resolveKey(rType.unlocalized + ".x")));
        list.add(
            EnumChatFormatting.YELLOW + I18nUtil
                .resolveKey("trait.rbmx.fluxFunc", EnumChatFormatting.WHITE + aletheia$getFuncDescription(stack)));
        if ((Object) this instanceof ItemRBMKFuelQGP) {
            list.add(
                EnumChatFormatting.YELLOW
                    + I18nUtil.resolveKey("trait.rbmx.funcType", EnumChatFormatting.RED + "EXPONENTIAL"));
        } else {
            list.add(EnumChatFormatting.YELLOW + I18nUtil.resolveKey("trait.rbmx.funcType", this.function.title));
        }
        list.add(
            EnumChatFormatting.YELLOW
                + I18nUtil.resolveKey("trait.rbmx.xenonGen", EnumChatFormatting.WHITE + "x * " + xGen));
        list.add(
            EnumChatFormatting.YELLOW
                + I18nUtil.resolveKey("trait.rbmx.xenonBurn", EnumChatFormatting.WHITE + "x\u00B2 / " + xBurn));
        list.add(EnumChatFormatting.GOLD + I18nUtil.resolveKey("trait.rbmx.heat", heat + "\u00B0C"));
        list.add(EnumChatFormatting.GOLD + I18nUtil.resolveKey("trait.rbmx.diffusion", diffusion + "\u00B9/\u2082"));
        list.add(
            EnumChatFormatting.RED
                + I18nUtil.resolveKey("trait.rbmx.skinTemp", ((int) (getHullHeat(stack) * 10D) / 10D) + "m"));
        list.add(
            EnumChatFormatting.RED
                + I18nUtil.resolveKey("trait.rbmx.coreTemp", ((int) (getCoreHeat(stack) * 10D) / 10D) + "m"));
        list.add(EnumChatFormatting.DARK_RED + I18nUtil.resolveKey("trait.rbmx.melt", meltingPoint + "m"));

        ci.cancel();
    }

    private String aletheia$getFuncDescription(ItemStack stack) {
        if ((Object) this instanceof ItemRBMKFuelQGP) {
            double enrichment = getEnrichment(stack);
            String reactivityStr = "" + this.reactivity;
            if (enrichment < 1) {
                enrichment = reactivityModByEnrichment(enrichment);
                String enrichmentMod = "" + ((int) (enrichment * 1000D) / 1000D);
                String efficiencyPer = EnumChatFormatting.GOLD + " (" + ((int) ((1.0D) * 1000D) / 10D) + "%)";
                String flux = selfRate > 0
                    ? "(x" + EnumChatFormatting.RED + " + " + selfRate + EnumChatFormatting.WHITE + ")"
                    : "x";
                return String.format(
                    Locale.US,
                    "(1.01^(%s " + EnumChatFormatting.YELLOW + "* %s" + EnumChatFormatting.WHITE + ") / 2) * %s / 100",
                    flux,
                    enrichmentMod,
                    reactivityStr)
                    .concat(efficiencyPer);
            } else {
                String flux = selfRate > 0
                    ? "(x" + EnumChatFormatting.RED + " + " + selfRate + EnumChatFormatting.WHITE + ")"
                    : "x";
                return String.format(Locale.US, "(1.01^%s / 2) * %s / 100", flux, reactivityStr);
            }
        }
        String function;

        switch (this.function) {
            case PASSIVE:
                function = EnumChatFormatting.RED + "" + selfRate;
                break;
            case LOG_TEN:
                function = "log10(%1$s + 1) * %2$s / 2";
                break;
            case PLATEU:
                function = "(1 - e^-%1$s / 25) * %2$s";
                break;
            case ARCH:
                function = "(%1$s - %1$s\u00B2 / 10000) * %2$s / 100";
                break;
            case SIGMOID:
                function = "%2$s / (1 + e^(5 - %1$s / 10)";
                break;
            case SQUARE_ROOT:
                function = "sqrt(%1$s) * %2$s / 10";
                break;
            case LINEAR:
                function = "%1$s * %2$s / 100";
                break;
            case QUADRATIC:
                function = "%1$s\u00B2 * %2$s / 10000";
                break;
            case EXPERIMENTAL:
                function = "%1$s * (sin(%1$s) + 1) * %2$s";
                break;
            case SLOW_LINEAR:
                function = "sqrt(2 * %1$s + 30) / 10 * %2$s / 2.5";
                break;
            default:
                function = "ERROR";
        }

        double enrichment = getEnrichment(stack);

        if (enrichment < 1) {
            enrichment = reactivityModByEnrichment(enrichment);
            String enrichmentMod = "" + ((int) (enrichment * 1000D) / 1000D);
            String efficiencyPer = EnumChatFormatting.GOLD + " (" + ((int) ((1.0D) * 1000D) / 10D) + "%)";

            switch (this.function) {
                case PASSIVE:
                    function = EnumChatFormatting.RED + ""
                        + selfRate
                        + EnumChatFormatting.YELLOW
                        + " * "
                        + enrichmentMod;
                    break;
                case LOG_TEN:
                    function = "log10(%1$s " + EnumChatFormatting.YELLOW
                        + "* %3$s"
                        + EnumChatFormatting.WHITE
                        + " + 1) * %2$s / 2";
                    break;
                case PLATEU:
                    function = "(1 - e^-(%1$s " + EnumChatFormatting.YELLOW
                        + "* %3$s"
                        + EnumChatFormatting.WHITE
                        + ") / 25) * %2$s";
                    break;
                case ARCH:
                    function = "(%1$s " + EnumChatFormatting.YELLOW
                        + "* %3$s"
                        + EnumChatFormatting.WHITE
                        + " - (%1$s "
                        + EnumChatFormatting.YELLOW
                        + "* %3$s"
                        + EnumChatFormatting.WHITE
                        + ")\u00B2 / 10000) * %2$s / 100";
                    break;
                case SIGMOID:
                    function = "%2$s / (1 + e^(5 - %1$s " + EnumChatFormatting.YELLOW
                        + "* %3$s"
                        + EnumChatFormatting.WHITE
                        + " / 10)";
                    break;
                case SQUARE_ROOT:
                    function = "sqrt(%1$s " + EnumChatFormatting.YELLOW
                        + "* %3$s"
                        + EnumChatFormatting.WHITE
                        + ") * %2$s / 10";
                    break;
                case LINEAR:
                    function = "%1$s " + EnumChatFormatting.YELLOW
                        + "* %3$s"
                        + EnumChatFormatting.WHITE
                        + " * %2$s / 100";
                    break;
                case QUADRATIC:
                    function = "(%1$s " + EnumChatFormatting.YELLOW
                        + "* %3$s"
                        + EnumChatFormatting.WHITE
                        + ")\u00B2 * %2$s / 10000";
                    break;
                case EXPERIMENTAL:
                    function = "%1$s " + EnumChatFormatting.YELLOW
                        + "* %3$s"
                        + EnumChatFormatting.WHITE
                        + " * (sin(%1$s "
                        + EnumChatFormatting.YELLOW
                        + "* %3$s"
                        + EnumChatFormatting.WHITE
                        + ") + 1) * %2$s";
                    break;
                default:
                    function = "ERROR";
            }

            return String
                .format(
                    Locale.US,
                    function,
                    selfRate > 0 ? "(x" + EnumChatFormatting.RED + " + " + selfRate + EnumChatFormatting.WHITE + ")"
                        : "x",
                    reactivity,
                    enrichmentMod)
                .concat(efficiencyPer);
        }

        return String.format(
            Locale.US,
            function,
            selfRate > 0 ? "(x" + EnumChatFormatting.RED + " + " + selfRate + EnumChatFormatting.WHITE + ")" : "x",
            reactivity);
    }

    @Inject(method = "getFuncDescription", at = @At("HEAD"), cancellable = true)
    private void aletheia$getFuncDescription(ItemStack stack, CallbackInfoReturnable<String> cir) {
        cir.setReturnValue(aletheia$getFuncDescription(stack));
    }
}
