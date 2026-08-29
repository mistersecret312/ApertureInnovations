package net.mistersecret312.aperture_innovations.client.resourcepack;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.mistersecret312.aperture_innovations.ApertureInnovations;

import java.util.Optional;

public record ClientCubeVariant(ResourceLocation modelPath, ResourceLocation hullTexture,
								Optional<ResourceLocation> idleTexture, Optional<ResourceLocation> activeTexture,
								Optional<ResourceLocation> genericTexture)
{
	public static final String MODEL = "model";
	public static final String HULL = "hull_texture";
	public static final String IDLE = "idle_glow_texture";
	public static final String ACTIVE = "active_glow_texture";
	public static final String GENERIC = "coloring_glow_texture";

	public static final Codec<ClientCubeVariant> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ResourceLocation.CODEC.fieldOf(MODEL).forGetter(ClientCubeVariant::modelPath),
			ResourceLocation.CODEC.fieldOf(HULL).forGetter(ClientCubeVariant::hullTexture),
			ResourceLocation.CODEC.lenientOptionalFieldOf(IDLE).forGetter(ClientCubeVariant::idleTexture),
			ResourceLocation.CODEC.lenientOptionalFieldOf(ACTIVE).forGetter(ClientCubeVariant::activeTexture),
			ResourceLocation.CODEC.lenientOptionalFieldOf(GENERIC).forGetter(ClientCubeVariant::genericTexture)
	).apply(instance, ClientCubeVariant::new));


	public static final ClientCubeVariant DEFAULT_VARIANT = new ClientCubeVariant(
			ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID, "geo/entity/weighted_cube.geo.json"),
			ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID, "textures/entity/weighted_cube.png"),
			Optional.of(ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID, "textures/entity/weighted_cube_inactive.png")),
			Optional.of(ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID, "textures/entity/weighted_cube_active.png")),
			Optional.of(ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID, "textures/entity/weighted_cube_generic.png"))
	);

}
