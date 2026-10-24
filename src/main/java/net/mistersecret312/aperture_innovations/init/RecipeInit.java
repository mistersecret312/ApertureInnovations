package net.mistersecret312.aperture_innovations.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.mistersecret312.aperture_innovations.ApertureInnovations;
import net.mistersecret312.aperture_innovations.recipes.PortalGunPiercingRecipe;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RecipeInit
{
	public static final DeferredRegister<RecipeSerializer<?>> RECIPES = DeferredRegister.create(
			Registries.RECIPE_SERIALIZER,
			ApertureInnovations.MODID);

	public static DeferredHolder<RecipeSerializer<?>, RecipeSerializer<PortalGunPiercingRecipe>> GUN_PIERCING = RECIPES.register("gun_piercing",
			() -> new SimpleCraftingRecipeSerializer<>(PortalGunPiercingRecipe::new));

	public static void register(IEventBus bus)
	{
		RECIPES.register(bus);
	}
}
