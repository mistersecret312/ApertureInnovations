package net.mistersecret312.aperture_innovations.client.resourcepack;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.mistersecret312.aperture_innovations.ApertureInnovations;

import java.util.Optional;

public record ClientVitalApparatusVentVariant(ResourceLocation modelPath, ResourceLocation animationPath, ResourceLocation hullTexture,
											  ResourceLocation glassTexture,
											  Optional<ResourceLocation> inactiveTexture, Optional<ResourceLocation> activeTexture,
											  ResourceLocation glowGeneric, ResourceLocation hullGeneric)
{
	public static final String MODEL = "model";
	public static final String ANIMATION = "animation";
	public static final String HULL = "hull_texture";
	public static final String GLASS = "glass_texture";
	public static final String INACTIVE = "inactive_texture";
	public static final String ACTIVE = "active_texture";
	public static final String GENERIC_GLOW = "coloring_glow_texture";
	public static final String GENERIC_HULL = "coloring_hull_texture";

	public static final Codec<ClientVitalApparatusVentVariant> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ResourceLocation.CODEC.fieldOf(MODEL).forGetter(ClientVitalApparatusVentVariant::modelPath),
			ResourceLocation.CODEC.fieldOf(ANIMATION).forGetter(ClientVitalApparatusVentVariant::animationPath),
			ResourceLocation.CODEC.fieldOf(HULL).forGetter(ClientVitalApparatusVentVariant::hullTexture),
			ResourceLocation.CODEC.fieldOf(GLASS).forGetter(ClientVitalApparatusVentVariant::glassTexture),
			ResourceLocation.CODEC.lenientOptionalFieldOf(INACTIVE).forGetter(ClientVitalApparatusVentVariant::inactiveTexture),
			ResourceLocation.CODEC.lenientOptionalFieldOf(ACTIVE).forGetter(ClientVitalApparatusVentVariant::activeTexture),
			ResourceLocation.CODEC.fieldOf(GENERIC_GLOW).forGetter(ClientVitalApparatusVentVariant::glowGeneric),
			ResourceLocation.CODEC.fieldOf(GENERIC_HULL).forGetter(ClientVitalApparatusVentVariant::hullGeneric)
	).apply(instance, ClientVitalApparatusVentVariant::new));


	public static final ClientVitalApparatusVentVariant DEFAULT_VARIANT = new ClientVitalApparatusVentVariant(
			ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID, "geo/block/vital_apparatus_vent.geo.json"),
			ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID, "animations/block/vital_apparatus_vent.animation.json"),
			ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID, "textures/entity/vital_apparatus_vent/vital_apparatus_vent.png"),
			ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID, "textures/entity/vital_apparatus_vent/glass.png"),
			Optional.of(ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID, "textures/entity/vital_apparatus_vent/vital_apparatus_vent_inactive.png")),
			Optional.of(ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID, "textures/entity/vital_apparatus_vent/vital_apparatus_vent_active.png")),
			ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID, "textures/entity/vital_apparatus_vent/vital_apparatus_vent_generic.png"),
			ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID, "textures/entity/vital_apparatus_vent/vital_apparatus_vent_hull_generic.png")
	);

}
