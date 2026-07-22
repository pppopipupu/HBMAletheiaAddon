package com.pppopipupu.aletheia.mixin;

import java.util.ArrayList;
import java.util.function.Function;

import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.hbm.blocks.generic.BlockGlyphidSpawner;
import com.hbm.entity.mob.glyphid.EntityGlyphid;
import com.hbm.util.Tuple.Pair;
import com.pppopipupu.aletheia.entity.EntityGlyphidQGP;

@Mixin(value = BlockGlyphidSpawner.class, remap = false)
public abstract class MixinBlockGlyphidSpawner {

    @Shadow
    private static ArrayList<Pair<Function<World, EntityGlyphid>, int[]>> spawnMap;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void aletheia$registerQGPUltimateGlyphid(CallbackInfo ci) {
        spawnMap.add(new Pair<>(EntityGlyphidQGP::new, new int[] { -60, 50, 150 }));
    }
}
