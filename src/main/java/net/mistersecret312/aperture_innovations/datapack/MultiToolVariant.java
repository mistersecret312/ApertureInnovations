package net.mistersecret312.aperture_innovations.datapack;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.mistersecret312.aperture_innovations.ApertureInnovations;

public class MultiToolVariant
{
	public static final ResourceLocation MULTI_TOOL_VARIANT_LOCATION = ResourceLocation.fromNamespaceAndPath(
			ApertureInnovations.MODID, "multi_tool_variant");
	public static final ResourceKey<Registry<MultiToolVariant>> REGISTRY_KEY = ResourceKey.createRegistryKey(
			MULTI_TOOL_VARIANT_LOCATION);
	public static final Codec<ResourceKey<MultiToolVariant>> RESOURCE_KEY_CODEC = ResourceKey.codec(REGISTRY_KEY);

	public static final Codec<MultiToolVariant> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ResourceLocation.CODEC.fieldOf("client_variant").forGetter(MultiToolVariant::getClientVariant)
	).apply(instance, MultiToolVariant::new));

	private final ResourceLocation clientVariant;
	public MultiToolVariant(ResourceLocation clientVariant)
	{
		this.clientVariant = clientVariant;
	}

	public ResourceLocation getClientVariant()
	{
		return clientVariant;
	}
}
