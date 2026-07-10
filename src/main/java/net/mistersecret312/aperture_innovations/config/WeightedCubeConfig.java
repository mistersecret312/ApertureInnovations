package net.mistersecret312.aperture_innovations.config;


import net.neoforged.neoforge.common.ModConfigSpec;

public class WeightedCubeConfig
{
	public static ModConfigSpec.BooleanValue disable_cube_inventory;

	public static void init(ModConfigSpec.Builder server)
	{
		disable_cube_inventory = server
			.comment("If true, the Weighted Cubes will not have an inventory (This will only disable the UI, stored items will not be lost)")
			.define("disable_cube_inventory", false);
	}
}
