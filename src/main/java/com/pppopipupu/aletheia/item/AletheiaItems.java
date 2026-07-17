package com.pppopipupu.aletheia.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.hbm.inventory.FluidContainer;
import com.hbm.inventory.FluidContainerRegistry;
import com.hbm.items.machine.ItemMachineUpgrade;
import com.hbm.items.machine.ItemMachineUpgrade.UpgradeType;
import com.hbm.items.machine.ItemRBMKPellet;
import com.hbm.items.machine.ItemRBMKRod;
import com.hbm.items.machine.ItemRBMKRod.EnumBurnFunc;
import com.hbm.items.machine.ItemRBMKRod.EnumDepleteFunc;
import com.hbm.items.special.ItemChopper;
import com.hbm.items.weapon.sedna.BulletConfig;
import com.hbm.items.weapon.sedna.Crosshair;
import com.hbm.items.weapon.sedna.GunConfig;
import com.hbm.items.weapon.sedna.ItemGunBaseNT;
import com.hbm.items.weapon.sedna.ItemGunBaseNT.WeaponQuality;
import com.hbm.items.weapon.sedna.Receiver;
import com.hbm.items.weapon.sedna.factory.Lego;
import com.hbm.items.weapon.sedna.factory.Orchestras;
import com.hbm.items.weapon.sedna.factory.XFactoryEnergy;
import com.hbm.items.weapon.sedna.mags.MagazineFullReload;
import com.hbm.main.MainRegistry;
import com.hbm.tileentity.machine.rbmk.IRBMKFluxReceiver.NType;
import com.pppopipupu.aletheia.block.AletheiaBlocks;
import com.pppopipupu.aletheia.fluid.AletheiaFluids;
import com.pppopipupu.aletheia.weapon.AletheiaBullets;

import cpw.mods.fml.common.registry.GameRegistry;

public class AletheiaItems {

    public static Item bucket_qgp;
    public static Item bucket_liquid_nitrogen;
    public static Item bucket_thermal_colloid;
    public static Item bucket_modified_cold_gel;
    public static Item bucket_hot_modified_cold_gel;
    public static Item qgp_mining_bomb;
    public static Item upgrade_ultimate;
    public static Item gun_pppop;

    public static Item ams_muzzle;
    public static Item ams_focus_blank;
    public static Item ams_focus_limiter;
    public static Item ams_focus_booster;

    public static Item alien_jelly;

    public static Item bio_crystal;
    public static Item agricultural_science;

    public static Item night_vision;
    public static Item night_vision_mk2;
    public static Item chopper;
    public static Item ingot_sodium;
    public static Item ingot_strontium;
    public static Item ingot_neodymium;
    public static Item powder_sodium;
    public static Item powder_strontium;
    public static Item powder_neodymium;
    public static Item powder_neodymium_tiny;
    public static Item fragment_neodymium;
    public static Item ams_catalyst_strontium;
    public static Item pellet_rtg_strontium;

    public static Item billet_rs1;
    public static Item billet_rs2;
    public static Item billet_rs3;
    public static ItemRBMKPellet rbmk_pellet_rs1;
    public static ItemRBMKPellet rbmk_pellet_rs2;
    public static ItemRBMKPellet rbmk_pellet_rs3;
    public static ItemRBMKRod rbmk_fuel_rs1;
    public static ItemRBMKRod rbmk_fuel_rs2;
    public static ItemRBMKRod rbmk_fuel_rs3;

    public static ItemZirnoxRodAletheia rod_zirnox_digamma;
    public static ItemZirnoxRodAletheia rod_zirnox_qgp;
    public static ItemRBMKPellet rbmk_pellet_qgp_depleted;
    public static ItemRBMKRod rbmk_fuel_qgp;
    public static Item billet_qgp;
    public static Item waste_digamma;
    public static Item waste_qgp;
    public static Item spawn_maskman;
    public static Item recipe_icon;

    public static void init() {
        bucket_qgp = new ItemQGPBucket(AletheiaBlocks.qgp_block).setUnlocalizedName("bucket_qgp")
            .setContainerItem(Items.bucket);
        GameRegistry.registerItem(bucket_qgp, "bucket_qgp");
        FluidContainerRegistry.registerContainer(
            new FluidContainer(new ItemStack(bucket_qgp), new ItemStack(Items.bucket), AletheiaFluids.fluid_qgp, 1000));

        bucket_liquid_nitrogen = new Item().setUnlocalizedName("bucket_liquid_nitrogen")
            .setContainerItem(Items.bucket)
            .setCreativeTab(MainRegistry.controlTab)
            .setTextureName("aletheia:bucket_liquid_nitrogen");
        GameRegistry.registerItem(bucket_liquid_nitrogen, "bucket_liquid_nitrogen");

        bucket_thermal_colloid = new Item().setUnlocalizedName("bucket_thermal_colloid")
            .setContainerItem(Items.bucket)
            .setCreativeTab(MainRegistry.controlTab)
            .setTextureName("aletheia:bucket_thermal_colloid");
        GameRegistry.registerItem(bucket_thermal_colloid, "bucket_thermal_colloid");

        bucket_modified_cold_gel = new Item().setUnlocalizedName("bucket_modified_cold_gel")
            .setContainerItem(Items.bucket)
            .setCreativeTab(MainRegistry.controlTab)
            .setTextureName("aletheia:bucket_modified_cold_gel");
        GameRegistry.registerItem(bucket_modified_cold_gel, "bucket_modified_cold_gel");

        bucket_hot_modified_cold_gel = new Item().setUnlocalizedName("bucket_hot_modified_cold_gel")
            .setContainerItem(Items.bucket)
            .setCreativeTab(MainRegistry.controlTab)
            .setTextureName("aletheia:bucket_hot_modified_cold_gel");
        GameRegistry.registerItem(bucket_hot_modified_cold_gel, "bucket_hot_modified_cold_gel");

        qgp_mining_bomb = new ItemQGPMiningBomb(4).setUnlocalizedName("qgp_mining_bomb")
            .setTextureName("tnt_side");
        GameRegistry.registerItem(qgp_mining_bomb, "qgp_mining_bomb");

        upgrade_ultimate = new ItemMachineUpgrade(UpgradeType.OVERDRIVE, 2).setUnlocalizedName("upgrade_ultimate")
            .setTextureName("hbm:upgrade_overdrive_3");
        GameRegistry.registerItem(upgrade_ultimate, "upgrade_ultimate");

        ams_muzzle = new Item().setUnlocalizedName("ams_muzzle")
            .setTextureName("aletheia:ams_muzzle");
        GameRegistry.registerItem(ams_muzzle, "ams_muzzle");

        ams_focus_blank = new Item().setUnlocalizedName("ams_focus_blank")
            .setTextureName("aletheia:ams_focus_blank");
        GameRegistry.registerItem(ams_focus_blank, "ams_focus_blank");

        ams_focus_limiter = new Item().setUnlocalizedName("ams_focus_limiter")
            .setTextureName("aletheia:ams_focus_limiter");
        GameRegistry.registerItem(ams_focus_limiter, "ams_focus_limiter");

        ams_focus_booster = new Item().setUnlocalizedName("ams_focus_booster")
            .setTextureName("aletheia:ams_focus_booster");
        GameRegistry.registerItem(ams_focus_booster, "ams_focus_booster");

        alien_jelly = new ItemAlienJelly().setUnlocalizedName("alien_jelly")
            .setTextureName("aletheia:alien_jelly")
            .setCreativeTab(MainRegistry.consumableTab);
        GameRegistry.registerItem(alien_jelly, "alien_jelly");

        bio_crystal = new Item().setUnlocalizedName("bio_crystal")
            .setCreativeTab(MainRegistry.partsTab)
            .setTextureName("aletheia:bioflux");
        GameRegistry.registerItem(bio_crystal, "bio_crystal");

        agricultural_science = new Item().setUnlocalizedName("agricultural_science")
            .setCreativeTab(MainRegistry.controlTab)
            .setTextureName("aletheia:agricultural_science");
        GameRegistry.registerItem(agricultural_science, "agricultural_science");

        gun_pppop = new ItemGunBaseNT(
            WeaponQuality.LEGENDARY,
            new GunConfig().dura(100_000)
                .draw(10)
                .inspect(26)
                .crosshair(Crosshair.L_ARROWS)
                .rec(
                    new Receiver(0).dmg(1.0F)
                        .delay(1)
                        .spread(0.05F)
                        .spreadHipfire(0.05F)
                        .reload(10)
                        .jam(0)
                        .auto(true)
                        .mag(
                            new MagazineFullReload(0, 250)
                                .addConfigs(AletheiaBullets.energy_pppop, AletheiaBullets.energy_pppop_steel))
                        .offset(0.75, -0.0625 * 1.5, -0.1875)
                        .canFire(Lego.LAMBDA_STANDARD_CAN_FIRE)
                        .fire((itemStack, lambdaContext) -> {
                            Lego.LAMBDA_STANDARD_FIRE.accept(itemStack, lambdaContext);
                            EntityLivingBase entity = lambdaContext.entity;
                            BulletConfig currentBullet = null;
                            if (lambdaContext.config.getReceivers(itemStack).length > 0) {
                                currentBullet = (BulletConfig) lambdaContext.config.getReceivers(itemStack)[0]
                                    .getMagazine(itemStack)
                                    .getType(itemStack, lambdaContext.inventory);
                            }
                            String snd = (currentBullet == AletheiaBullets.energy_pppop) ? "aletheia:weapon.zomgShoot"
                                : "aletheia:weapon.osiprShoot";
                            entity.worldObj.playSoundEffect(entity.posX, entity.posY, entity.posZ, snd, 1.0F, 1.0F);
                        })
                        .recoil(XFactoryEnergy.LAMBDA_RECOIL_ENERGY))
                .setupStandardConfiguration()
                .anim(XFactoryEnergy.LAMBDA_LASER_PISTOL)
                .orchestra(Orchestras.ORCHESTRA_LASER_PISTOL)).setUnlocalizedName("gun_pppop")
                    .setTextureName("aletheia:gun_pppop");
        GameRegistry.registerItem(gun_pppop, "gun_pppop");

        night_vision = new Item().setUnlocalizedName("night_vision")
            .setCreativeTab(MainRegistry.weaponTab)
            .setTextureName("aletheia:night_vision");
        GameRegistry.registerItem(night_vision, "night_vision");

        night_vision_mk2 = new Item().setUnlocalizedName("night_vision_mk2")
            .setCreativeTab(MainRegistry.weaponTab)
            .setTextureName("aletheia:night_vision_mk2");
        GameRegistry.registerItem(night_vision_mk2, "night_vision_mk2");

        chopper = new Item().setUnlocalizedName("chopper")
            .setCreativeTab(MainRegistry.weaponTab)
            .setTextureName("aletheia:chopper");
        GameRegistry.registerItem(chopper, "chopper");

        ingot_sodium = new Item().setUnlocalizedName("ingot_sodium")
            .setCreativeTab(MainRegistry.partsTab)
            .setTextureName("aletheia:ingot_sodium");
        GameRegistry.registerItem(ingot_sodium, "ingot_sodium");

        ingot_strontium = new Item().setUnlocalizedName("ingot_strontium")
            .setCreativeTab(MainRegistry.partsTab)
            .setTextureName("aletheia:ingot_strontium");
        GameRegistry.registerItem(ingot_strontium, "ingot_strontium");

        ingot_neodymium = new Item().setUnlocalizedName("ingot_neodymium")
            .setCreativeTab(MainRegistry.partsTab)
            .setTextureName("aletheia:ingot_neodymium");
        GameRegistry.registerItem(ingot_neodymium, "ingot_neodymium");

        powder_sodium = new Item().setUnlocalizedName("powder_sodium")
            .setCreativeTab(MainRegistry.partsTab)
            .setTextureName("aletheia:powder_sodium");
        GameRegistry.registerItem(powder_sodium, "powder_sodium");

        powder_strontium = new Item().setUnlocalizedName("powder_strontium")
            .setCreativeTab(MainRegistry.partsTab)
            .setTextureName("aletheia:powder_strontium");
        GameRegistry.registerItem(powder_strontium, "powder_strontium");

        powder_neodymium = new Item().setUnlocalizedName("powder_neodymium")
            .setCreativeTab(MainRegistry.partsTab)
            .setTextureName("aletheia:powder_neodymium");
        GameRegistry.registerItem(powder_neodymium, "powder_neodymium");

        powder_neodymium_tiny = new Item().setUnlocalizedName("powder_neodymium_tiny")
            .setCreativeTab(MainRegistry.partsTab)
            .setTextureName("aletheia:powder_neodymium_tiny");
        GameRegistry.registerItem(powder_neodymium_tiny, "powder_neodymium_tiny");

        fragment_neodymium = new Item().setUnlocalizedName("fragment_neodymium")
            .setCreativeTab(MainRegistry.partsTab)
            .setTextureName("aletheia:fragment_neodymium");
        GameRegistry.registerItem(fragment_neodymium, "fragment_neodymium");

        ams_catalyst_strontium = new Item().setUnlocalizedName("ams_catalyst_strontium")
            .setCreativeTab(MainRegistry.controlTab)
            .setMaxStackSize(1)
            .setTextureName("aletheia:ams_catalyst_strontium");
        GameRegistry.registerItem(ams_catalyst_strontium, "ams_catalyst_strontium");

        pellet_rtg_strontium = new Item().setUnlocalizedName("pellet_rtg_strontium")
            .setCreativeTab(MainRegistry.controlTab)
            .setTextureName("aletheia:pellet_rtg_strontium");
        GameRegistry.registerItem(pellet_rtg_strontium, "pellet_rtg_strontium");

        billet_rs1 = new Item().setUnlocalizedName("billet_rs1")
            .setCreativeTab(MainRegistry.partsTab)
            .setTextureName("aletheia:billet_rs1");
        GameRegistry.registerItem(billet_rs1, "billet_rs1");
        billet_rs2 = new Item().setUnlocalizedName("billet_rs2")
            .setCreativeTab(MainRegistry.partsTab)
            .setTextureName("aletheia:billet_rs2");
        GameRegistry.registerItem(billet_rs2, "billet_rs2");
        billet_rs3 = new Item().setUnlocalizedName("billet_rs3")
            .setCreativeTab(MainRegistry.partsTab)
            .setTextureName("aletheia:billet_rs3");
        GameRegistry.registerItem(billet_rs3, "billet_rs3");

        rbmk_pellet_rs1 = (ItemRBMKPellet) new ItemRBMKPellet("Research Rod Mix - Ra226Be&Pu239").disableXenon()
            .setUnlocalizedName("rbmk_pellet_rs1")
            .setTextureName("aletheia:rbmk_pellet_rs1");
        GameRegistry.registerItem(rbmk_pellet_rs1, "rbmk_pellet_rs1");
        rbmk_pellet_rs2 = (ItemRBMKPellet) new ItemRBMKPellet("Research Rod Mix - Po210Be&Pu241").disableXenon()
            .setUnlocalizedName("rbmk_pellet_rs2")
            .setTextureName("aletheia:rbmk_pellet_rs2");
        GameRegistry.registerItem(rbmk_pellet_rs2, "rbmk_pellet_rs2");
        rbmk_pellet_rs3 = (ItemRBMKPellet) new ItemRBMKPellet("Research Rod Mix - DNT&Sa327").disableXenon()
            .setUnlocalizedName("rbmk_pellet_rs3")
            .setTextureName("aletheia:rbmk_pellet_rs3");
        GameRegistry.registerItem(rbmk_pellet_rs3, "rbmk_pellet_rs3");

        rbmk_fuel_rs1 = (ItemRBMKRod) new ItemRBMKRod(rbmk_pellet_rs1).setYield(2000000D)
            .setStats(0D, 100)
            .setFunction(EnumBurnFunc.PASSIVE)
            .setDepletionFunction(EnumDepleteFunc.LINEAR)
            .setXenon(0.0D, 50D)
            .setHeat(0.75D)
            .setMeltingPoint(2750D)
            .setNeutronTypes(NType.ANY, NType.SLOW)
            .setUnlocalizedName("rbmk_fuel_rs1")
            .setTextureName("aletheia:rbmk_fuel_rs1");
        GameRegistry.registerItem(rbmk_fuel_rs1, "rbmk_fuel_rs1");
        rbmk_fuel_rs2 = (ItemRBMKRod) new ItemRBMKRod(rbmk_pellet_rs2).setYield(6000000D)
            .setStats(0D, 300)
            .setFunction(EnumBurnFunc.PASSIVE)
            .setDepletionFunction(EnumDepleteFunc.LINEAR)
            .setXenon(0.0D, 50D)
            .setHeat(0.5D)
            .setMeltingPoint(3350D)
            .setNeutronTypes(NType.ANY, NType.SLOW)
            .setUnlocalizedName("rbmk_fuel_rs2")
            .setTextureName("aletheia:rbmk_fuel_rs2");
        GameRegistry.registerItem(rbmk_fuel_rs2, "rbmk_fuel_rs2");
        rbmk_fuel_rs3 = (ItemRBMKRod) new ItemRBMKRod(rbmk_pellet_rs3).setYield(20000000D)
            .setStats(0D, 1000)
            .setFunction(EnumBurnFunc.PASSIVE)
            .setDepletionFunction(EnumDepleteFunc.LINEAR)
            .setXenon(0.0D, 50D)
            .setHeat(0.2D)
            .setMeltingPoint(6500D)
            .setNeutronTypes(NType.ANY, NType.SLOW)
            .setUnlocalizedName("rbmk_fuel_rs3")
            .setTextureName("aletheia:rbmk_fuel_rs3");
        GameRegistry.registerItem(rbmk_fuel_rs3, "rbmk_fuel_rs3");

        rod_zirnox_digamma = new ItemZirnoxRodAletheia(ItemZirnoxRodAletheia.EnumZirnoxAletheiaType.DIGAMMA);
        GameRegistry.registerItem(rod_zirnox_digamma, "rod_zirnox_digamma");

        rod_zirnox_qgp = new ItemZirnoxRodAletheia(ItemZirnoxRodAletheia.EnumZirnoxAletheiaType.QGP);
        GameRegistry.registerItem(rod_zirnox_qgp, "rod_zirnox_qgp");

        rbmk_pellet_qgp_depleted = (ItemRBMKPellet) new ItemRBMKPellet("Spent Quark-Gluon Plasma Slag").disableXenon()
            .setUnlocalizedName("rbmk_pellet_qgp_depleted")
            .setTextureName("aletheia:rbmk_pellet_qgp_depleted");
        GameRegistry.registerItem(rbmk_pellet_qgp_depleted, "rbmk_pellet_qgp_depleted");

        rbmk_fuel_qgp = (ItemRBMKRod) new ItemRBMKFuelQGP(rbmk_pellet_qgp_depleted).setYield(200000000D)
            .setStats(200D, 120D)
            .setFunction(EnumBurnFunc.EXPERIMENTAL)
            .setDepletionFunction(EnumDepleteFunc.CF_SLOPE)
            .setXenon(1.0D, 25D)
            .setHeat(8.0D)
            .setMeltingPoint(8000D)
            .setDiffusion(0.08D)
            .setNeutronTypes(NType.ANY, NType.FAST)
            .setTint(0x00e5ff)
            .setUnlocalizedName("rbmk_fuel_qgp")
            .setTextureName("aletheia:rbmk_fuel_qgp");
        GameRegistry.registerItem(rbmk_fuel_qgp, "rbmk_fuel_qgp");

        ItemZirnoxRodAletheia.rod_zirnox_digamma_depleted = new Item().setUnlocalizedName("rod_zirnox_digamma_depleted")
            .setTextureName("aletheia:rod_zirnox_digamma_depleted")
            .setCreativeTab(MainRegistry.controlTab);
        GameRegistry.registerItem(ItemZirnoxRodAletheia.rod_zirnox_digamma_depleted, "rod_zirnox_digamma_depleted");
        ItemZirnoxRodAletheia.rod_zirnox_qgp_depleted = new Item().setUnlocalizedName("rod_zirnox_qgp_depleted")
            .setTextureName("aletheia:rod_zirnox_qgp_depleted")
            .setCreativeTab(MainRegistry.controlTab);
        GameRegistry.registerItem(ItemZirnoxRodAletheia.rod_zirnox_qgp_depleted, "rod_zirnox_qgp_depleted");

        billet_qgp = new Item().setUnlocalizedName("billet_qgp")
            .setTextureName("hbm:billet_schrabidium_fuel")
            .setCreativeTab(MainRegistry.controlTab);
        GameRegistry.registerItem(billet_qgp, "billet_qgp");

        waste_digamma = new Item().setUnlocalizedName("waste_digamma")
            .setTextureName("hbm:waste_schrabidium")
            .setCreativeTab(MainRegistry.controlTab);
        GameRegistry.registerItem(waste_digamma, "waste_digamma");

        waste_qgp = new Item().setUnlocalizedName("waste_qgp")
            .setTextureName("hbm:waste_mox")
            .setCreativeTab(MainRegistry.controlTab);
        GameRegistry.registerItem(waste_qgp, "waste_qgp");

        ItemZirnoxRodAletheia.registerFuelMap();

        spawn_maskman = new ItemChopper().setUnlocalizedName("spawn_maskman")
            .setMaxStackSize(1)
            .setCreativeTab(MainRegistry.consumableTab)
            .setTextureName("aletheia:spawn_maskman");
        GameRegistry.registerItem(spawn_maskman, "spawn_maskman");

        recipe_icon = new Item().setUnlocalizedName("recipe_icon")
            .setCreativeTab(null)
            .setTextureName("aletheia:recipe_icon");
        GameRegistry.registerItem(recipe_icon, "recipe_icon");
    }
}
