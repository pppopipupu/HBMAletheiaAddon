package com.pppopipupu.aletheia.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.hbm.inventory.FluidContainer;
import com.hbm.inventory.FluidContainerRegistry;
import com.hbm.items.machine.ItemMachineUpgrade;
import com.hbm.items.machine.ItemMachineUpgrade.UpgradeType;
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
import com.pppopipupu.aletheia.Aletheia;
import com.pppopipupu.aletheia.block.AletheiaBlocks;

import cpw.mods.fml.common.registry.GameRegistry;

public class AletheiaItems {

    public static Item bucket_qgp;
    public static Item qgp_mining_bomb;
    public static Item upgrade_ultimate;
    public static Item gun_pppop;

    public static Item ams_muzzle;
    public static Item ams_focus_blank;
    public static Item ams_focus_limiter;
    public static Item ams_focus_booster;

    public static Item alien_jelly;
    public static Item disperser_canister_empty;
    public static Item disperser_canister;
    public static Item glyphid_gland_empty;
    public static Item glyphid_gland;

    public static void init() {
        bucket_qgp = new ItemQGPBucket(AletheiaBlocks.qgp_block).setUnlocalizedName("bucket_qgp")
            .setContainerItem(Items.bucket);
        GameRegistry.registerItem(bucket_qgp, "bucket_qgp");
        FluidContainerRegistry.registerContainer(
            new FluidContainer(new ItemStack(bucket_qgp), new ItemStack(Items.bucket), Aletheia.fluid_qgp, 1000));

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
                                .addConfigs(Aletheia.energy_pppop, Aletheia.energy_pppop_steel))
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
                            String snd = (currentBullet == Aletheia.energy_pppop) ? "aletheia:weapon.zomgShoot"
                                : "aletheia:weapon.osiprShoot";
                            entity.worldObj.playSoundEffect(entity.posX, entity.posY, entity.posZ, snd, 1.0F, 1.0F);
                        })
                        .recoil(XFactoryEnergy.LAMBDA_RECOIL_ENERGY))
                .setupStandardConfiguration()
                .anim(XFactoryEnergy.LAMBDA_LASER_PISTOL)
                .orchestra(Orchestras.ORCHESTRA_LASER_PISTOL)).setUnlocalizedName("gun_pppop")
                    .setTextureName("aletheia:gun_pppop");
        GameRegistry.registerItem(gun_pppop, "gun_pppop");

        disperser_canister_empty = new Item().setUnlocalizedName("disperser_canister_empty")
            .setCreativeTab(MainRegistry.weaponTab)
            .setTextureName("hbm:disperser_canister");
        GameRegistry.registerItem(disperser_canister_empty, "disperser_canister_empty");

        disperser_canister = new ItemDisperser().setUnlocalizedName("disperser_canister")
            .setContainerItem(disperser_canister_empty)
            .setCreativeTab(MainRegistry.weaponTab)
            .setTextureName("hbm:disperser_canister");
        GameRegistry.registerItem(disperser_canister, "disperser_canister");

        glyphid_gland_empty = new Item().setUnlocalizedName("glyphid_gland_empty")
            .setCreativeTab(MainRegistry.weaponTab)
            .setTextureName("hbm:glyphid_gland");
        GameRegistry.registerItem(glyphid_gland_empty, "glyphid_gland_empty");

        glyphid_gland = new ItemDisperser().setUnlocalizedName("glyphid_gland")
            .setContainerItem(glyphid_gland_empty)
            .setCreativeTab(MainRegistry.weaponTab)
            .setTextureName("hbm:glyphid_gland");
        GameRegistry.registerItem(glyphid_gland, "glyphid_gland");
    }
}
