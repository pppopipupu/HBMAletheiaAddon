package com.pppopipupu.aletheia.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.hbm.inventory.recipes.loader.SerializableRecipe;
import com.pppopipupu.aletheia.machine.agrichemplant.AgriChemicalPlantRecipes;
import com.pppopipupu.aletheia.recipe.AletheiaRecipes;

@Mixin(value = SerializableRecipe.class, remap = false)
public class MixinSerializableRecipe {

    @Inject(method = "registerAllHandlers", at = @At("RETURN"))
    private static void aletheia$registerAgriChemPlantHandler(CallbackInfo ci) {
        List<SerializableRecipe> handlers = SerializableRecipe.recipeHandlers;
        for (SerializableRecipe handler : handlers) {
            if (handler instanceof AgriChemicalPlantRecipes) {
                return;
            }
        }
        handlers.add(AgriChemicalPlantRecipes.INSTANCE);
    }

    @Inject(method = "initialize", at = @At("RETURN"))
    private static void aletheia$onRecipesInitialize(CallbackInfo ci) {
        AletheiaRecipes.registerHBMRecipes();
    }
}
