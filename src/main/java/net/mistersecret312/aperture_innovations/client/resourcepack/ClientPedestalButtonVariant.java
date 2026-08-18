package net.mistersecret312.aperture_innovations.client.resourcepack;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.mistersecret312.aperture_innovations.ApertureInnovations;

import java.util.Optional;

public record ClientPedestalButtonVariant(ResourceLocation modelPath, ResourceLocation animationPath, ResourceLocation hullTexture,
										  Optional<ResourceLocation> buttonTexture, Optional<ResourceLocation> linesTexture,
										  Optional<ResourceLocation> genericButtonTexture, Optional<ResourceLocation> genericLinesTexture)
{
	public static final String MODEL = "model";
	public static final String ANIMATIONS = "animations";
	public static final String HULL = "hull_texture";
	public static final String BUTTON = "button_glow_texture";
	public static final String LINES = "lines_glow_texture";
	public static final String GENERIC_BUTTON = "button_coloring_glow_texture";
	public static final String GENERIC_LINES = "lines_coloring_glow_texture";

	public static final Codec<ClientPedestalButtonVariant> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ResourceLocation.CODEC.fieldOf(MODEL).forGetter(ClientPedestalButtonVariant::modelPath),
			ResourceLocation.CODEC.fieldOf(ANIMATIONS).forGetter(ClientPedestalButtonVariant::animationPath),
			ResourceLocation.CODEC.fieldOf(HULL).forGetter(ClientPedestalButtonVariant::hullTexture),
			ResourceLocation.CODEC.lenientOptionalFieldOf(BUTTON).forGetter(ClientPedestalButtonVariant::buttonTexture),
			ResourceLocation.CODEC.lenientOptionalFieldOf(LINES).forGetter(ClientPedestalButtonVariant::linesTexture),
			ResourceLocation.CODEC.lenientOptionalFieldOf(GENERIC_BUTTON).forGetter(ClientPedestalButtonVariant::genericButtonTexture),
			ResourceLocation.CODEC.lenientOptionalFieldOf(GENERIC_LINES).forGetter(ClientPedestalButtonVariant::genericLinesTexture)
	).apply(instance, ClientPedestalButtonVariant::new));


	public static final ClientPedestalButtonVariant DEFAULT_VARIANT = new ClientPedestalButtonVariant(
			ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID, "geo/block/pedestal_button.geo.json"),
			ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID, "animations/block/pedestal_button.animation.json"),
			ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID, "textures/block/pedestal_button/pedestal_button.png"),
			Optional.of(ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID, "textures/block/pedestal_button/pedestal_button_button.png")),
			Optional.of(ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID, "textures/block/pedestal_button/pedestal_button_lines.png")),
			Optional.of(ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID, "textures/block/pedestal_button/pedestal_button_button_generic.png")),
			Optional.of(ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID, "textures/block/pedestal_button/pedestal_button_lines_generic.png"))
	);

}
