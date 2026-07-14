package com.pppopipupu.aletheia;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import com.hbm.entity.mob.glyphid.EntityGlyphid;
import com.hbm.entity.mob.glyphid.EntityGlyphidBrawler;
import com.hbm.entity.mob.glyphid.EntityGlyphidScout;
import com.hbm.items.ModItems;
import com.pppopipupu.aletheia.stats.AletheiaAchievements;
import com.pppopipupu.decaylib.event.DecayEvent;
import com.pppopipupu.decaylib.event.DecayEvent.DecayContext;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class AletheiaDecayEventHandler {

    private static EntityGlyphid createRandomGlyphid(World world) {
        double r = world.rand.nextDouble();
        if (r < 0.40) {
            return new EntityGlyphid(world);
        } else if (r < 0.65) {
            return new EntityGlyphidScout(world);
        } else if (r < 0.85) {
            return new EntityGlyphidBrawler(world);
        } else {
            return new EntityGlyphid(world);
        }
    }

    @SubscribeEvent
    public void onDecay(DecayEvent event) {
        if (event.originalStack == null || event.originalStack.getItem() != ModItems.egg_glyphid) {
            return;
        }

        EntityGlyphid glyphid = createRandomGlyphid(event.world);
        event.setProductEntity(glyphid);

        if (event.context == DecayContext.PLAYER_INVENTORY) {
            if (event.carrier instanceof EntityPlayer) {
                ((EntityPlayer) event.carrier).triggerAchievement(AletheiaAchievements.achievementGlyphidHatch);
            }
        } else if (event.context == DecayContext.ENTITY_ITEM) {
            EntityPlayer nearestPlayer = event.world.getClosestPlayer(event.x, event.y, event.z, 16.0);
            if (nearestPlayer != null) {
                nearestPlayer.triggerAchievement(AletheiaAchievements.achievementGlyphidHatch);
            }
        } else if (event.context == DecayContext.TILE_ENTITY) {
            EntityPlayer nearestPlayer = event.world.getClosestPlayer(event.x, event.y, event.z, 16.0);
            if (nearestPlayer != null) {
                nearestPlayer.triggerAchievement(AletheiaAchievements.achievementGlyphidHatchUnexpected);
            }
        }
    }
}
