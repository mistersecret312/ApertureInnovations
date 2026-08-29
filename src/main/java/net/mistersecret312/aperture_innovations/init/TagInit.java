package net.mistersecret312.aperture_innovations.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.mistersecret312.aperture_innovations.ApertureInnovations;

public class TagInit
{
	public class Blocks
	{
		public static final TagKey<Block> SHOOT_THROUGH = TagKey.create(
				BuiltInRegistries.BLOCK.key(), ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID, "shoot_through"));
		public static final TagKey<Block> IMPORTALABLE = TagKey.create(
				BuiltInRegistries.BLOCK.key(), ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID, "importalable"));
		public static final TagKey<Block> PORTALABLE = TagKey.create(
				BuiltInRegistries.BLOCK.key(), ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID, "portalable"));

		public static final TagKey<Block> CONNECTS_TO_ANTLINE = TagKey.create(
				BuiltInRegistries.BLOCK.key(), ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID, "connects_to_antline"));
	}

	public class Entities
	{
		public static final TagKey<EntityType<?>> BUTTON_IGNORE = TagKey.create(
				BuiltInRegistries.ENTITY_TYPE.key(), ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID, "button_ignore"));
	}
}
