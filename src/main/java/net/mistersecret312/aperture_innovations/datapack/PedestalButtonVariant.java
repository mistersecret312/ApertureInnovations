package net.mistersecret312.aperture_innovations.datapack;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.mistersecret312.aperture_innovations.ApertureInnovations;

public class PedestalButtonVariant
{
	public static final ResourceLocation PEDESTAL_BUTTON_VARIANT_LOCATION = ResourceLocation.fromNamespaceAndPath(
			ApertureInnovations.MODID, "pedestal_button_variant");
	public static final ResourceKey<Registry<PedestalButtonVariant>> REGISTRY_KEY = ResourceKey.createRegistryKey(
			PEDESTAL_BUTTON_VARIANT_LOCATION);
	public static final Codec<ResourceKey<PedestalButtonVariant>> RESOURCE_KEY_CODEC = ResourceKey.codec(REGISTRY_KEY);

	public static final Codec<PedestalButtonVariant> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ResourceLocation.CODEC.fieldOf("client_variant").forGetter(PedestalButtonVariant::getClientVariant),
			Codec.INT.optionalFieldOf("press_time", 25).forGetter(PedestalButtonVariant::getPressTime)
	).apply(instance, PedestalButtonVariant::new));

	private final ResourceLocation clientVariant;
	private final int pressTime;

	public PedestalButtonVariant(ResourceLocation clientVariant, int pressTime)
	{
		this.clientVariant = clientVariant;
		this.pressTime = pressTime;
	}

	public ResourceLocation getClientVariant()
	{
		return clientVariant;
	}

	public int getPressTime()
	{
		return pressTime;
	}
}
