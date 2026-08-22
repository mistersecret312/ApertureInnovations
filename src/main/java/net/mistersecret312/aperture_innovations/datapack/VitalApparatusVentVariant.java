package net.mistersecret312.aperture_innovations.datapack;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.mistersecret312.aperture_innovations.ApertureInnovations;

public class VitalApparatusVentVariant
{
	public static final ResourceLocation VITAL_APPARATUS_VENT_VARIANT_LOCATION = ResourceLocation.fromNamespaceAndPath(
			ApertureInnovations.MODID, "vital_apparatus_vent_variant");
	public static final ResourceKey<Registry<VitalApparatusVentVariant>> REGISTRY_KEY = ResourceKey.createRegistryKey(
			VITAL_APPARATUS_VENT_VARIANT_LOCATION);
	public static final Codec<ResourceKey<VitalApparatusVentVariant>> RESOURCE_KEY_CODEC = ResourceKey.codec(REGISTRY_KEY);

	public static final Codec<VitalApparatusVentVariant> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ResourceLocation.CODEC.fieldOf("client_variant").forGetter(VitalApparatusVentVariant::getClientVariant)
	).apply(instance, VitalApparatusVentVariant::new));

	private final ResourceLocation clientVariant;
	public VitalApparatusVentVariant(ResourceLocation clientVariant)
	{
		this.clientVariant = clientVariant;
	}

	public ResourceLocation getClientVariant()
	{
		return clientVariant;
	}
}
