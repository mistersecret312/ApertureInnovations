package net.mistersecret312.aperture_innovations.init;

import net.minecraft.core.Registry;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.mistersecret312.aperture_innovations.ApertureInnovations;
import net.mistersecret312.aperture_innovations.multitool.Color;
import net.mistersecret312.aperture_innovations.multitool.ConfigurationType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;

public class MultiToolConfigTypeInit
{
	public static final ResourceKey<Registry<ConfigurationType<?>>> REGISTRY_KEY =
			ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID,
					"multitool_config_type"));
	public static final Registry<ConfigurationType<?>> REGISTRY = new RegistryBuilder<>(REGISTRY_KEY).sync(true).create();

	public static final DeferredRegister<ConfigurationType<?>> TYPES = DeferredRegister.create(REGISTRY, ApertureInnovations.MODID);

	public static final DeferredHolder<ConfigurationType<?>, ConfigurationType<Integer>> INT =
			TYPES.register("integer", () -> new ConfigurationType<>(ByteBufCodecs.INT));

	public static final DeferredHolder<ConfigurationType<?>, ConfigurationType<Float>> FLOAT =
			TYPES.register("float", () -> new ConfigurationType<>(ByteBufCodecs.FLOAT));

	public static final DeferredHolder<ConfigurationType<?>, ConfigurationType<Double>> DOUBLE =
			TYPES.register("double", () -> new ConfigurationType<>(ByteBufCodecs.DOUBLE));

	public static final DeferredHolder<ConfigurationType<?>, ConfigurationType<Long>> LONG =
			TYPES.register("long", () -> new ConfigurationType<>(ByteBufCodecs.VAR_LONG));

	public static final DeferredHolder<ConfigurationType<?>, ConfigurationType<Color>> COLOR =
			TYPES.register("color", () -> new ConfigurationType<>(Color.STREAM_CODEC));

	public static final DeferredHolder<ConfigurationType<?>, ConfigurationType<ResourceLocation>> RESOURCE_LOCATION =
			TYPES.register("resource_location", () -> new ConfigurationType<>(ResourceLocation.STREAM_CODEC));

	public static void register(IEventBus bus)
	{
		TYPES.register(bus);
	}
}
