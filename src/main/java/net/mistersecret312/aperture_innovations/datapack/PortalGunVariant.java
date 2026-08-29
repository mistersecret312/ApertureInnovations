package net.mistersecret312.aperture_innovations.datapack;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.mistersecret312.aperture_innovations.ApertureInnovations;

public class PortalGunVariant
{
	public static final ResourceLocation PORTAL_GUN_VARIANT_LOCATION = ResourceLocation.fromNamespaceAndPath(
			ApertureInnovations.MODID, "portal_gun_variant");
	public static final ResourceKey<Registry<PortalGunVariant>> REGISTRY_KEY = ResourceKey.createRegistryKey(
			PORTAL_GUN_VARIANT_LOCATION);
	public static final Codec<ResourceKey<PortalGunVariant>> RESOURCE_KEY_CODEC = ResourceKey.codec(REGISTRY_KEY);

	public static final Codec<PortalGunVariant> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ResourceLocation.CODEC.fieldOf("client_variant").forGetter(PortalGunVariant::getClientVariant)
	).apply(instance, PortalGunVariant::new));

	private final ResourceLocation clientVariant;

	public PortalGunVariant(ResourceLocation clientVariant)
	{
		this.clientVariant = clientVariant;
	}

	public ResourceLocation getClientVariant()
	{
		return clientVariant;
	}
}
