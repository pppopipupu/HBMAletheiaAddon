package com.pppopipupu.aletheia.stats;

import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraftforge.common.AchievementPage;

import com.hbm.items.ModItems;
import com.pppopipupu.aletheia.block.AletheiaBlocks;
import com.pppopipupu.aletheia.item.AletheiaItems;

public class AletheiaAchievements {

    public static Achievement achievementGlyphidHatch;
    public static Achievement achievementGlyphidHatchUnexpected;
    public static Achievement achievementAmsBase;
    public static Achievement achievementPPPOP;
    public static Achievement achievementQgpMeltdown;
    public static Achievement achievementQgpQuench;
    public static Achievement achievementQgpBlackhole;

    public static void init() {
        achievementGlyphidHatch = new Achievement(
            "achievement.glyphid_hatch",
            "glyphid_hatch",
            0,
            0,
            new ItemStack(AletheiaItems.alien_jelly),
            null).initIndependentStat()
                .registerStat();
        achievementGlyphidHatchUnexpected = new Achievement(
            "achievement.glyphid_hatch_unexpected",
            "glyphid_hatch_unexpected",
            2,
            0,
            ModItems.egg_glyphid,
            achievementGlyphidHatch).initIndependentStat()
                .setSpecial()
                .registerStat();
        achievementAmsBase = new Achievement(
            "achievement.amsBase",
            "amsBase",
            4,
            0,
            new ItemStack(AletheiaBlocks.ams_base),
            achievementGlyphidHatch).initIndependentStat()
                .setSpecial()
                .registerStat();
        achievementPPPOP = new Achievement(
            "achievement.pppop",
            "pppop",
            6,
            0,
            new ItemStack(AletheiaItems.gun_pppop),
            null).initIndependentStat()
                .setSpecial()
                .registerStat();

        achievementQgpMeltdown = new Achievement(
            "achievement.qgp_meltdown",
            "qgp_meltdown",
            0,
            3,
            new ItemStack(AletheiaItems.rbmk_fuel_qgp),
            null).initIndependentStat()
                .setSpecial()
                .registerStat();

        achievementQgpQuench = new Achievement(
            "achievement.qgp_quench",
            "qgp_quench",
            2,
            3,
            new ItemStack(AletheiaItems.qgp_singularity_core),
            achievementQgpMeltdown).initIndependentStat()
                .setSpecial()
                .registerStat();

        achievementQgpBlackhole = new Achievement(
            "achievement.qgp_blackhole",
            "qgp_blackhole",
            4,
            3,
            new ItemStack(AletheiaItems.quark_micro_singularity),
            achievementQgpMeltdown).initIndependentStat()
                .setSpecial()
                .registerStat();

        AchievementPage.registerAchievementPage(
            new AchievementPage(
                "Aletheia",
                achievementGlyphidHatch,
                achievementGlyphidHatchUnexpected,
                achievementAmsBase,
                achievementPPPOP,
                achievementQgpMeltdown,
                achievementQgpQuench,
                achievementQgpBlackhole));
    }
}
