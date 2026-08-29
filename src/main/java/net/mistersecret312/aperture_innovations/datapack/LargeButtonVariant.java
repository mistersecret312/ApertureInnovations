package net.mistersecret312.aperture_innovations.datapack;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.mistersecret312.aperture_innovations.ApertureInnovations;

public class LargeButtonVariant
{
	public static final ResourceLocation LARGE_BUTTON_VARIANT_LOCATION = ResourceLocation.fromNamespaceAndPath(
			ApertureInnovations.MODID, "large_button_variant");
	public static final ResourceKey<Registry<LargeButtonVariant>> REGISTRY_KEY = ResourceKey.createRegistryKey(
			LARGE_BUTTON_VARIANT_LOCATION);
	public static final Codec<ResourceKey<LargeButtonVariant>> RESOURCE_KEY_CODEC = ResourceKey.codec(REGISTRY_KEY);

	public static final Codec<LargeButtonVariant> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ResourceLocation.CODEC.fieldOf("client_variant").forGetter(LargeButtonVariant::getClientVariant)
	).apply(instance, LargeButtonVariant::new));

	private final ResourceLocation clientVariant;
	public LargeButtonVariant(ResourceLocation clientVariant)
	{
		this.clientVariant = clientVariant;
	}

	public ResourceLocation getClientVariant()
	{
		return clientVariant;
	}
}
