package net.mistersecret312.aperture_innovations.client.resourcepack;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import io.netty.handler.codec.DecoderException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.mistersecret312.aperture_innovations.ApertureInnovations;
import net.mistersecret312.aperture_innovations.datapack.CubeVariant;
import net.mistersecret312.aperture_innovations.datapack.MultiToolVariant;
import net.mistersecret312.aperture_innovations.datapack.PortalGunVariant;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;

import java.util.Map;

public class ResourcePackReloadListener
{
	public static final String PATH = ApertureInnovations.MODID;

	public static final String GUN_VARIANT = "portal_gun_variant";
	public static final String CUBE_VARIANT = "cube_variant";
	public static final String PEDESTAL_BUTTON_VARIANT = "pedestal_button_variant";
	public static final String MULTI_TOOL_VARIANT = "multi_tool_variant";

	private static Minecraft minecraft = Minecraft.getInstance();

	@EventBusSubscriber(modid = ApertureInnovations.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
	public static class ReloadListener extends SimpleJsonResourceReloadListener
	{
		public ReloadListener()
		{
			super(new GsonBuilder().create(), PATH);
		}

		@Override
		protected void apply(Map<ResourceLocation, JsonElement> jsonMap, ResourceManager manager, ProfilerFiller filler)
		{
			ClientPortalGunVariants.clear();
			ClientCubeVariants.clear();
			ClientPedestalButtonVariants.clear();

			ClientMultiToolVariants.clear();

			for(Map.Entry<ResourceLocation, JsonElement> jsonEntry : jsonMap.entrySet())
			{
				ResourceLocation location = jsonEntry.getKey();
				JsonElement element = jsonEntry.getValue();

				if(canShortenPath(location, GUN_VARIANT))
				{
					location = shortenPath(location, GUN_VARIANT);
					addPortalGunVariant(location, element);
				}
				if(canShortenPath(location, CUBE_VARIANT))
				{
					location = shortenPath(location, CUBE_VARIANT);
					addCubeVariant(location, element);
				}
				if(canShortenPath(location, PEDESTAL_BUTTON_VARIANT))
				{
					location = shortenPath(location, PEDESTAL_BUTTON_VARIANT);
					addPedestalButtonVariant(location, element);
				}
				if(canShortenPath(location, MULTI_TOOL_VARIANT))
				{
					location = shortenPath(location, MULTI_TOOL_VARIANT);
					addMultiToolVariant(location, element);
				}
			}
		}

		private static void addPortalGunVariant(ResourceLocation location, JsonElement element)
		{
			try
			{
				JsonObject json = GsonHelper.convertToJsonObject(element, GUN_VARIANT);
				ClientPortalGunVariant stargateVariant = ClientPortalGunVariant.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow(msg -> new DecoderException("Failed to parse Portal Gun Variant "+ msg));

				ClientPortalGunVariants.addPortalGunVariant(location, stargateVariant);
			}
			catch(RuntimeException e)
			{
				ApertureInnovations.LOGGER.error("Could not load Portal Gun Variant: " + location.toString());
				ApertureInnovations.LOGGER.error(e.getMessage());
			}
		}

		private static void addCubeVariant(ResourceLocation location, JsonElement element)
		{
			try
			{
				JsonObject json = GsonHelper.convertToJsonObject(element, CUBE_VARIANT);
				ClientCubeVariant cubeVariant = ClientCubeVariant.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow(msg -> new DecoderException("Failed to parse Cube Variant "+ msg));

				ClientCubeVariants.addCubeVariant(location, cubeVariant);
			}
			catch(RuntimeException e)
			{
				ApertureInnovations.LOGGER.error("Could not load Cube Variant: " + location.toString());
				ApertureInnovations.LOGGER.error(e.getMessage());
			}
		}

		private static void addPedestalButtonVariant(ResourceLocation location, JsonElement element)
		{
			try
			{
				JsonObject json = GsonHelper.convertToJsonObject(element, PEDESTAL_BUTTON_VARIANT);
				ClientPedestalButtonVariant pedestalButtonVariant = ClientPedestalButtonVariant.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow(msg -> new DecoderException("Failed to parse Pedestal Button Variant "+ msg));

				ClientPedestalButtonVariants.addButtonVariant(location, pedestalButtonVariant);
			}
			catch(RuntimeException e)
			{
				ApertureInnovations.LOGGER.error("Could not load Pedestal Button Variant: " + location.toString());
				ApertureInnovations.LOGGER.error(e.getMessage());
			}
		}

		private static void addMultiToolVariant(ResourceLocation location, JsonElement element)
		{
			try
			{
				JsonObject json = GsonHelper.convertToJsonObject(element, CUBE_VARIANT);
				ClientMultiToolVariant multiToolVariant = ClientMultiToolVariant.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow(msg -> new DecoderException("Failed to parse Multi Tool Variant "+ msg));

				ClientMultiToolVariants.addMultiToolVariant(location, multiToolVariant);
			}
			catch(RuntimeException e)
			{
				ApertureInnovations.LOGGER.error("Could not load Multi Tool Variant: " + location.toString());
				ApertureInnovations.LOGGER.error(e.getMessage());
			}
		}

		@SubscribeEvent
		public static void registerReloadListener(RegisterClientReloadListenersEvent event)
		{
			event.registerReloadListener(new ReloadListener());
		}

		private static boolean canShortenPath(ResourceLocation location, String shortenBy)
		{
			return location.getPath().startsWith(shortenBy) && location.getPath().length() > shortenBy.length(); // If it starts with the string and isn't empty after getting shortened
		}

		private static ResourceLocation shortenPath(ResourceLocation location, String shortenBy)
		{
			return location.withPath(location.getPath().substring(shortenBy.length() + 1)); // Magical 1 because there's also the / symbol
		}
	}
}
