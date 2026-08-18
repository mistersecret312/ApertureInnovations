package net.mistersecret312.aperture_innovations;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.logging.LogUtils;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.mistersecret312.aperture_innovations.client.Layers;
import net.mistersecret312.aperture_innovations.client.TintBakedModelWrapper;
import net.mistersecret312.aperture_innovations.client.overlay.CrosshairOverlay;
import net.mistersecret312.aperture_innovations.client.renderer.*;
import net.mistersecret312.aperture_innovations.client.renderer.antline.AntlineOutputRenderer;
import net.mistersecret312.aperture_innovations.client.renderer.antline.AntlineRenderer;
import net.mistersecret312.aperture_innovations.client.renderer.antline.AntlineTimerRenderer;
import net.mistersecret312.aperture_innovations.client.renderer.block.LargeButtonRenderer;
import net.mistersecret312.aperture_innovations.client.renderer.block.PedestalButtonRenderer;
import net.mistersecret312.aperture_innovations.client.renderer.block.VitalApparatusVentRenderer;
import net.mistersecret312.aperture_innovations.client.renderer.entity.CubeRenderer;
import net.mistersecret312.aperture_innovations.client.resourcepack.ResourcePackReloadListener;
import net.mistersecret312.aperture_innovations.datapack.CubeVariant;
import net.mistersecret312.aperture_innovations.datapack.PedestalButtonVariant;
import net.mistersecret312.aperture_innovations.datapack.PortalGunVariant;
import net.mistersecret312.aperture_innovations.init.*;
import net.mistersecret312.aperture_innovations.items.*;
import net.mistersecret312.aperture_innovations.mixin.BlockColorAccessor;
import net.mistersecret312.aperture_innovations.multitool.ConfigurationType;
import net.mistersecret312.aperture_innovations.utilities.WorldColoringUtils;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyModifier;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.registries.*;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.Map;

import static net.neoforged.fml.loading.FMLEnvironment.dist;

@Mod(ApertureInnovations.MODID)
public class ApertureInnovations
{
	public static final String MODID = "aperture_innovations";
	public static final Logger LOGGER = LogUtils.getLogger();

	public static ResourceLocation of(String path) {
		return ResourceLocation.fromNamespaceAndPath(MODID, path);
	}

	public ApertureInnovations(IEventBus modEventBus, ModContainer modContainer)
	{
		ItemInit.register(modEventBus);
		BlockInit.register(modEventBus);
		BlockEntityInit.register(modEventBus);
		EntityInit.register(modEventBus);
		ItemTabInit.register(modEventBus);
		SoundInit.register(modEventBus);
		StatisticsInit.register(modEventBus);
		RecipeInit.register(modEventBus);
		AdvancementInit.register(modEventBus);
		AttachmentTypeInit.register(modEventBus);
		DataComponentInit.register(modEventBus);
		EntityDataSerializerInit.register(modEventBus);
		MultiToolConfigTypeInit.register(modEventBus);

		modEventBus.addListener(Layers::registerLayers);
		modEventBus.addListener(NetworkInit::registerPackets);
		modEventBus.addListener(ApertureInnovations::registerCapabilities);
		modEventBus.addListener(ApertureInnovations::commonSetup);
		modEventBus.addListener(ApertureInnovations::registerRegistry);

		modEventBus.addListener((DataPackRegistryEvent.NewRegistry event) ->
			{
				event.dataPackRegistry(PortalGunVariant.REGISTRY_KEY, PortalGunVariant.CODEC, PortalGunVariant.CODEC);
				event.dataPackRegistry(CubeVariant.REGISTRY_KEY, CubeVariant.CODEC, CubeVariant.CODEC);
				event.dataPackRegistry(PedestalButtonVariant.REGISTRY_KEY, PedestalButtonVariant.CODEC, PedestalButtonVariant.CODEC);
			});

		modContainer.registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG, "aperture_innovations-common.toml");
		if(dist.isClient())
			modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);

		NeoForge.EVENT_BUS.addListener(ApertureInnovations::registerCommands);
	}

	public static void registerRegistry(NewRegistryEvent event)
	{
		event.register(MultiToolConfigTypeInit.REGISTRY);
	}

	public static void commonSetup(FMLCommonSetupEvent event)
	{
		event.enqueueWork(() ->
			{
				DispenserBlock.registerBehavior(ItemInit.CUBE.get(), CubeItem.getDispenserBehaviour());
			});
	}

	public static void registerCommands(RegisterCommandsEvent event)
	{
		CommandInit.register(event.getDispatcher());
	}

	public static void registerCapabilities(RegisterCapabilitiesEvent event)
	{
		event.registerItem(Capabilities.EnergyStorage.ITEM, (stack, context) -> new PortalGunItem.Energy(stack), ItemInit.PORTAL_GUN);
		event.registerItem(Capabilities.EnergyStorage.ITEM, (stack, context) -> new LongFallBootsItem.Energy(stack), ItemInit.LONG_FALL_BOOTS);
	}

	public static boolean isIrisLoaded()
	{
		return ModList.get().isLoaded("iris");
	}

	@EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
	public static class ClientModEvents
	{
		public static ShaderInstance portalCorridorShaderInstance;
		public static ShaderInstance portalFizzleShaderInstance;

		public static final Lazy<KeyMapping> RESET_PORTAL_GUN = Lazy.of(() -> new KeyMapping("aperture_innovations.portal_gun.reset",
				KeyConflictContext.IN_GAME, KeyModifier.NONE, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R, "key.category.aperture_innovations"));

		public static final Lazy<KeyMapping> PICK_UP = Lazy.of(() -> new KeyMapping("aperture_innovations.portal_gun.pick_up",
				KeyConflictContext.IN_GAME, KeyModifier.NONE, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_G, "key.category.aperture_innovations"));

		public static final Lazy<KeyMapping> PRIMARY_FIRE = Lazy.of(() -> new KeyMapping("aperture_innovations.portal_gun.primary_fire",
				KeyConflictContext.IN_GAME, KeyModifier.NONE, InputConstants.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_LEFT, "key.category.aperture_innovations"));
		public static final Lazy<KeyMapping> SECONDARY_FIRE = Lazy.of(() -> new KeyMapping("aperture_innovations.portal_gun.secondary_fire",
				KeyConflictContext.IN_GAME, KeyModifier.NONE, InputConstants.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_RIGHT, "key.category.aperture_innovations"));


		@SubscribeEvent
		public static void onClientSetup(FMLClientSetupEvent event)
		{
			event.enqueueWork(
					() -> {
					ItemProperties.register(ItemInit.COLORFUL_GEL.get(),
							ResourceLocation.fromNamespaceAndPath(MODID, "colored"), new ColorfulGelItemProperty());
					});
		}

		@SubscribeEvent
		public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event)
		{
			event.registerBlockEntityRenderer(BlockEntityInit.ANTLINE.get(), AntlineRenderer::new);
			event.registerBlockEntityRenderer(BlockEntityInit.CHECKMARK.get(), AntlineOutputRenderer::new);
			event.registerBlockEntityRenderer(BlockEntityInit.TIMER.get(), AntlineTimerRenderer::new);

			event.registerBlockEntityRenderer(BlockEntityInit.PEDESTAL_BUTTON.get(), PedestalButtonRenderer::new);
			event.registerBlockEntityRenderer(BlockEntityInit.LARGE_BUTTON.get(), LargeButtonRenderer::new);

			event.registerBlockEntityRenderer(BlockEntityInit.VITAL_APPARATUS_VENT.get(), VitalApparatusVentRenderer::new);

			event.registerEntityRenderer(EntityInit.CUBE.get(), CubeRenderer::new);
		}

		@SubscribeEvent
		public static void registerClientExtensions(RegisterClientExtensionsEvent event)
		{
			event.registerItem(LongFallBootsRenderProperties.INSTANCE, ItemInit.LONG_FALL_BOOTS);
			event.registerItem(PortalGunRenderProperties.INSTANCE, ItemInit.PORTAL_GUN);
		}

		@SubscribeEvent
		public static void registerShaders(RegisterShadersEvent event) throws IOException
		{
//			event.registerShader(new ShaderInstance(event.getResourceProvider(),
//					ResourceLocation.fromNamespaceAndPath(MODID, "portal_corridor"),
//					DefaultVertexFormat.POSITION_TEX_COLOR),
//					shaderInstance -> portalCorridorShaderInstance = shaderInstance);

			event.registerShader(new ShaderInstance(event.getResourceProvider(),
					ResourceLocation.fromNamespaceAndPath(MODID, "portal_fizzler"),
					DefaultVertexFormat.POSITION_TEX_COLOR),
					shaderInstance -> portalFizzleShaderInstance = shaderInstance);
		}

		@SubscribeEvent
		public static void registerClientReload(RegisterClientReloadListenersEvent event)
		{
			ResourcePackReloadListener.ReloadListener.registerReloadListener(event);
		}

		@SubscribeEvent
		public static void onModifyBakingResult(ModelEvent.ModifyBakingResult event) {
			for (Map.Entry<ModelResourceLocation, BakedModel> entry : event.getModels().entrySet())
			{
				ResourceLocation keyLoc = ResourceLocation.fromNamespaceAndPath(entry.getKey().id().getNamespace(),
						entry.getKey().id().getPath().split("#")[0]);
				Block block = BuiltInRegistries.BLOCK.get(keyLoc);
				if(!WorldColoringUtils.isBlockAlreadyTinted(block))
				{
					BakedModel newModel = new TintBakedModelWrapper(entry.getValue());
					event.getModels().put(entry.getKey(), newModel);
				}
			}
		}

		@SubscribeEvent
		public static void registerItemColors(RegisterColorHandlersEvent.Item event)
		{
			ColorfulGelItem gelItem = ItemInit.COLORFUL_GEL.get();
			event.register(((stack, color) -> color != 1 ? -1 : FastColor.ARGB32.opaque(gelItem.getColor(stack))), ItemInit.COLORFUL_GEL.get());
		}

		@SubscribeEvent
		public static void registerBlockColors(RegisterColorHandlersEvent.Block event)
		{
			BuiltInRegistries.BLOCK.forEach(block ->
				{
					boolean contains = ((BlockColorAccessor) event.getBlockColors()).getBlockColors().containsKey(block);

					if(!contains)
						event.register((blockState, level, pos, tint) -> WorldColoringUtils.getColor(event.getBlockColors(), blockState, level, pos, tint), block);
				});
		}

		@SubscribeEvent
		public static void registerKeybindings(RegisterKeyMappingsEvent event)
		{
			event.register(RESET_PORTAL_GUN.get());
			event.register(PICK_UP.get());
			event.register(PRIMARY_FIRE.get());
			event.register(SECONDARY_FIRE.get());
		}

		@SubscribeEvent
		public static void registerOverlays(RegisterGuiLayersEvent event)
		{
			event.registerAboveAll(ResourceLocation.fromNamespaceAndPath(MODID, "portal_crosshair"),
					CrosshairOverlay.OVERLAY);
		}
	}
}
