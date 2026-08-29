package net.mistersecret312.aperture_innovations.init;

import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.ResourceLocation;
import net.mistersecret312.aperture_innovations.ApertureInnovations;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class EntityDataSerializerInit
{
	public static final DeferredRegister<EntityDataSerializer<?>> SERIALIZERS = DeferredRegister.create(
			NeoForgeRegistries.ENTITY_DATA_SERIALIZERS, ApertureInnovations.MODID);

	public static final DeferredHolder<EntityDataSerializer<?>, EntityDataSerializer<ResourceLocation>> RESOURCE_LOCATION =
			SERIALIZERS.register("resource_location", () -> EntityDataSerializer.forValueType(ResourceLocation.STREAM_CODEC));

	public static void register(IEventBus bus)
	{
		SERIALIZERS.register(bus);
	}
}
