package com.pppopipupu.aletheia;

import com.hbm.items.ModItems;
import com.pppopipupu.aletheia.item.AletheiaItems;
import com.pppopipupu.decaylib.DecayAction;
import com.pppopipupu.decaylib.DecayRegistry;
import com.pppopipupu.decaylib.DecayRule;

public class AletheiaDecayRegistry {

    public static void register() {
        DecayRule eggRule = new DecayRule();
        eggRule.input = "hbm:item.egg_glyphid";
        eggRule.decayTime = 72000L;
        eggRule.action = new DecayAction();
        eggRule.action.type = "entity";
        eggRule.action.id = "hbm:entity_glyphid";
        eggRule.action.count = 1;
        eggRule.productTooltip = "tooltip.egg_glyphid.product";
        DecayRegistry.registerDefault(ModItems.egg_glyphid, eggRule);

        DecayRule jellyRule = new DecayRule();
        jellyRule.input = "aletheia:alien_jelly";
        jellyRule.decayTime = 144000L;
        jellyRule.action = new DecayAction();
        jellyRule.action.type = "item";
        jellyRule.action.id = "hbm:item.biomass";
        jellyRule.action.count = 1;
        DecayRegistry.registerDefault(AletheiaItems.alien_jelly, jellyRule);

        DecayRule bioCrystalRule = new DecayRule();
        bioCrystalRule.input = "aletheia:bio_crystal";
        bioCrystalRule.decayTime = 54000L;
        bioCrystalRule.action = new DecayAction();
        bioCrystalRule.action.type = "entity";
        bioCrystalRule.action.id = "Silverfish";
        bioCrystalRule.action.count = 1;
        bioCrystalRule.productTooltip = "tooltip.bio_crystal.product";
        DecayRegistry.registerDefault(AletheiaItems.bio_crystal, bioCrystalRule);

        DecayRule agScienceRule = new DecayRule();
        agScienceRule.input = "aletheia:agricultural_science";
        agScienceRule.decayTime = 216000L;
        agScienceRule.action = new DecayAction();
        agScienceRule.action.type = "item";
        agScienceRule.action.id = "minecraft:glass_bottle";
        agScienceRule.action.count = 1;
        DecayRegistry.registerDefault(AletheiaItems.agricultural_science, agScienceRule);
    }
}
