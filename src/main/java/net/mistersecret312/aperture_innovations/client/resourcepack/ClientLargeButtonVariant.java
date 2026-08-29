package net.mistersecret312.aperture_innovations.client.resourcepack;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.mistersecret312.aperture_innovations.ApertureInnovations;

import java.util.Optional;

public record ClientLargeButtonVariant(ResourceLocation modelPath, ResourceLocation animationPath, ResourceLocation hullTexture,
									   Optional<ResourceLocation> buttonTexture, Optional<ResourceLocation> genericButtonTexture,
									   Optional<ResourceLocation> activeLinesTexture, Optional<ResourceLocation> inactiveLinesTexture,
									   Optional<ResourceLocation> genericLinesTexture)
{
	public static final String MODEL = "model";
	public static final String ANIMATION = "animation";
	public static final String HULL = "hull_texture";
	public static final String BUTTON = "button_texture";
	public static final String GENERIC_BUTTON = "coloring_button_texture";
	public static final String ACTIVE_LINES = "active_lines_texture";
	public static final String INACTIVE_LINES = "inactive_lines_texture";
	public static final String GENERIC_LINES = "coloring_lines_texture";

	public static final Codec<ClientLargeButtonVariant> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ResourceLocation.CODEC.fieldOf(MODEL).forGetter(ClientLargeButtonVariant::modelPath),
			ResourceLocation.CODEC.fieldOf(ANIMATION).forGetter(ClientLargeButtonVariant::animationPath),
			ResourceLocation.CODEC.fieldOf(HULL).forGetter(ClientLargeButtonVariant::hullTexture),
			ResourceLocation.CODEC.lenientOptionalFieldOf(BUTTON).forGetter(ClientLargeButtonVariant::buttonTexture),
			ResourceLocation.CODEC.lenientOptionalFieldOf(GENERIC_BUTTON).forGetter(ClientLargeButtonVariant::genericButtonTexture),
			ResourceLocation.CODEC.lenientOptionalFieldOf(ACTIVE_LINES).forGetter(ClientLargeButtonVariant::activeLinesTexture),
			ResourceLocation.CODEC.lenientOptionalFieldOf(INACTIVE_LINES).forGetter(ClientLargeButtonVariant::inactiveLinesTexture),
			ResourceLocation.CODEC.lenientOptionalFieldOf(GENERIC_LINES).forGetter(ClientLargeButtonVariant::genericLinesTexture)
	).apply(instance, ClientLargeButtonVariant::new));


	public static final ClientLargeButtonVariant DEFAULT_VARIANT = new ClientLargeButtonVariant(
			ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID, "geo/block/large_button.geo.json"),
			ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID, "animations/block/large_button.animation.json"),
			ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID, "textures/block/large_button/large_button.png"),
			Optional.of(ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID, "textures/block/large_button/large_button_button.png")),
			Optional.of(ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID, "textures/block/large_button/large_button_button_generic.png")),
			Optional.of(ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID, "textures/block/large_button/large_button_lines_active.png")),
			Optional.of(ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID, "textures/block/large_button/large_button_lines_inactive.png")),
			Optional.of(ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID, "textures/block/large_button/large_button_lines_generic.png"))
	);

}
