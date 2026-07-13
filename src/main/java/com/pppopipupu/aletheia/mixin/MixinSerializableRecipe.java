package com.pppopipupu.aletheia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.hbm.inventory.recipes.loader.SerializableRecipe;
import com.pppopipupu.aletheia.recipe.AletheiaRecipes;

@Mixin(value = SerializableRecipe.class, remap = false)
public class MixinSerializableRecipe {

    @Inject(method = "initialize", at = @At("RETURN"))
    private static void aletheia$onRecipesInitialize(CallbackInfo ci) {
        AletheiaRecipes.registerHBMRecipes();
    }
}
