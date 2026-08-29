package net.mistersecret312.aperture_innovations.init;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.mistersecret312.aperture_innovations.ApertureInnovations;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.UnaryOperator;

public class DataComponentInit
{
	public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS = DeferredRegister.createDataComponents(
			Registries.DATA_COMPONENT_TYPE, ApertureInnovations.MODID);

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> ENERGY = register("energy", builder -> builder.persistent(
			Codec.LONG));

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> LINK_ID = register("link_id", builder -> builder.persistent(
			Codec.STRING));
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> PAIR_ID = register("pair_id", builder -> builder.persistent(
		    Codec.STRING));

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> DUALITY_STATE = register("duality", builder -> builder.persistent(
			Codec.INT));
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> LAST_PORTAL = register("last_portal", builder -> builder.persistent(
			Codec.INT));

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> INITIALIZED = register("initialized", builder -> builder.persistent(
			Codec.BOOL));

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> ZAP_TICK = register("zap_tick", builder -> builder.persistent(
			Codec.INT
	));

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> ZAP_SOUND_TICK = register("zap_sound_tick", builder -> builder.persistent(
			Codec.INT
	));

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> PRIMARY_PORTAL_COLOR =
			register("primary_portal_color", builder -> builder.persistent(Codec.INT));
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> SECONDARY_PORTAL_COLOR =
			register("secondary_portal_color", builder -> builder.persistent(Codec.INT));
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> PRIMARY_STRIPE_COLOR =
			register("primary_stripe_color", builder -> builder.persistent(Codec.INT));
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> SECONDARY_STRIPE_COLOR =
			register("secondary_stripe_color", builder -> builder.persistent(Codec.INT));

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<ResourceLocation>> PORTAL_GUN_VARIANT =
			register("portal_gun_variant", builder -> builder.persistent(ResourceLocation.CODEC));
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<ResourceLocation>> VARIANT =
			register("variant", builder -> builder.persistent(ResourceLocation.CODEC));

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> COLOR =
			register("color", builder -> builder.persistent(Codec.INT));
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> ACTIVE_COLOR =
			register("active_color", builder -> builder.persistent(Codec.INT));
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> HULL_COLOR =
			register("hull_color", builder -> builder.persistent(Codec.INT));

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> HELD_ENTITY = register("held_entity", builder -> builder.persistent(
			Codec.INT
	));

	private static <T>DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(String name, UnaryOperator<DataComponentType.Builder<T>> builderOperator)
	{
		return DATA_COMPONENTS.register(name, () -> builderOperator.apply(DataComponentType.builder()).build());
	}

	public static void register(IEventBus eventBus)
	{
		DATA_COMPONENTS.register(eventBus);
	}
}
