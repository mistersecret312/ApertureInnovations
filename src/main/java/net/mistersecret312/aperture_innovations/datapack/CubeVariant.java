package net.mistersecret312.aperture_innovations.datapack;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.mistersecret312.aperture_innovations.ApertureInnovations;

public class CubeVariant
{
	public static final ResourceLocation CUBE_VARIANT_LOCATION = ResourceLocation.fromNamespaceAndPath(
			ApertureInnovations.MODID, "cube_variant");
	public static final ResourceKey<Registry<CubeVariant>> REGISTRY_KEY = ResourceKey.createRegistryKey(
			CUBE_VARIANT_LOCATION);
	public static final Codec<ResourceKey<CubeVariant>> RESOURCE_KEY_CODEC = ResourceKey.codec(REGISTRY_KEY);

	public static final Codec<CubeVariant> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ResourceLocation.CODEC.fieldOf("client_variant").forGetter(CubeVariant::getClientVariant),
			Codec.BOOL.optionalFieldOf("reflects", false).forGetter(CubeVariant::isReflective),
			Codec.BOOL.optionalFieldOf("has_container", false).forGetter(CubeVariant::hasContainer)
	).apply(instance, CubeVariant::new));

	private final ResourceLocation clientVariant;
	private final boolean reflects;
	private final boolean hasContainer;

	public CubeVariant(ResourceLocation clientVariant, boolean reflects, boolean hasContainer)
	{
		this.clientVariant = clientVariant;
		this.reflects = reflects;
		this.hasContainer = hasContainer;
	}

	public ResourceLocation getClientVariant()
	{
		return clientVariant;
	}

	public boolean isReflective()
	{
		return reflects;
	}

	public boolean hasContainer()
	{
		return hasContainer;
	}
}
