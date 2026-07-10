package net.mistersecret312.aperture_innovations;

import net.mistersecret312.aperture_innovations.config.LongFallBootsConfig;
import net.mistersecret312.aperture_innovations.config.PortalGunConfig;
import net.mistersecret312.aperture_innovations.config.WeightedCubeConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

public class Config
{
	private static final ModConfigSpec.Builder COMMON_BUILDER = new ModConfigSpec.Builder();
	public static final ModConfigSpec COMMON_CONFIG;

	static
	{
		COMMON_BUILDER.push("portal_gun");
		PortalGunConfig.init(COMMON_BUILDER);
		COMMON_BUILDER.pop();

		COMMON_BUILDER.push("long_fall_boots");
		LongFallBootsConfig.init(COMMON_BUILDER);
		COMMON_BUILDER.pop();

		COMMON_BUILDER.push("weighted_cube");
		WeightedCubeConfig.init(COMMON_BUILDER);
		COMMON_BUILDER.pop();

		COMMON_CONFIG = COMMON_BUILDER.build();
	}
}
