package net.mistersecret312.aperture_innovations.items;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.mistersecret312.aperture_innovations.ApertureInnovations;
import net.mistersecret312.aperture_innovations.block_entities.multiblock.MasterBlockEntity;
import net.mistersecret312.aperture_innovations.blocks.multiblock.DummyBlock;
import net.mistersecret312.aperture_innovations.client.renderer.item.CubeItemRenderer;
import net.mistersecret312.aperture_innovations.client.renderer.item.MultiTooltemRenderer;
import net.mistersecret312.aperture_innovations.client.resourcepack.ClientMultiToolVariant;
import net.mistersecret312.aperture_innovations.client.resourcepack.ClientMultiToolVariants;
import net.mistersecret312.aperture_innovations.client.screen.MultiToolScreen;
import net.mistersecret312.aperture_innovations.client.screen.renderers.BlockEntityPreviewRenderer;
import net.mistersecret312.aperture_innovations.client.screen.renderers.ItemPreviewRenderer;
import net.mistersecret312.aperture_innovations.client.screen.renderers.PreviewRenderer;
import net.mistersecret312.aperture_innovations.datapack.MultiToolVariant;
import net.mistersecret312.aperture_innovations.datapack.PortalGunVariant;
import net.mistersecret312.aperture_innovations.init.DataComponentInit;
import net.mistersecret312.aperture_innovations.init.ItemInit;
import net.mistersecret312.aperture_innovations.init.ItemTabInit;
import net.mistersecret312.aperture_innovations.init.MultiToolConfigTypeInit;
import net.mistersecret312.aperture_innovations.multitool.ConfigurationProperty;
import net.mistersecret312.aperture_innovations.multitool.IHaveConfiguration;
import net.mistersecret312.aperture_innovations.multitool.IItemConfiguration;
import net.mistersecret312.aperture_innovations.multitool.InteractionType;
import net.mistersecret312.aperture_innovations.network.ClientboundOpenMutliToolBlockScreenPacket;
import net.mistersecret312.aperture_innovations.network.ClientboundOpenMutliToolScreenPacket;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class MultiToolItem extends Item implements GeoItem, IItemConfiguration
{
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	public MultiToolItem(Properties properties)
	{
		super(properties);
		SingletonGeoAnimatable.registerSyncedAnimatable(this);
	}

	public static ItemStack createTool(ResourceLocation variant, int hullColor, int glowColor)
	{
		MultiToolItem item = ItemInit.MULTI_TOOL.get();
		ItemStack stack = item.getDefaultInstance();
		item.setVariantKey(stack, variant);
		item.setHullColor(stack, hullColor);
		item.setGlowColor(stack, glowColor);

		return stack;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> components,
								TooltipFlag tooltipFlag)
	{
		super.appendHoverText(stack, context, components, tooltipFlag);

		components.add(Component.translatable("tooltip.aperture_innovations.multi_tool").withStyle(ChatFormatting.DARK_PURPLE));
		components.add(Component.translatable("tooltip.aperture_innovations.multi_tool_2").withStyle(ChatFormatting.DARK_PURPLE));

		Level level = context.level();
		if(level != null)
		{
			Color hsbColor = Color.getHSBColor(level.getTimeOfDay(1f)*50, 1f, 1f);
			components.add(Component.translatable("tooltip.aperture_innovations.is_configurable").withStyle((style -> style.withColor(
					hsbColor.getRGB()))));
		}
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand)
	{
		InteractionHand otherHand;
		if(usedHand == InteractionHand.MAIN_HAND)
			otherHand = InteractionHand.OFF_HAND;
		else otherHand = InteractionHand.MAIN_HAND;

		ItemStack otherStack = player.getItemInHand(otherHand);
		if(otherStack.isEmpty() && player.isShiftKeyDown() && usedHand == InteractionHand.OFF_HAND)
		{
			otherStack = player.getItemInHand(usedHand);
			otherHand = usedHand;
		}
		if(!level.isClientSide() && !otherStack.isEmpty())
		{
			ItemStack stack = player.getItemInHand(usedHand);
			PacketDistributor.sendToPlayer((ServerPlayer) player, new ClientboundOpenMutliToolScreenPacket(
					usedHand.equals(InteractionHand.MAIN_HAND),
					otherHand.equals(InteractionHand.MAIN_HAND)));

			return InteractionResultHolder.success(stack);
		}

		return super.use(level, player, usedHand);
	}

	@Override
	public @NotNull InteractionResult useOn(@NotNull UseOnContext context)
	{
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();

		if(!level.isClientSide())
		{
			PacketDistributor.sendToPlayer((ServerPlayer) context.getPlayer(), new ClientboundOpenMutliToolBlockScreenPacket(pos,
					context.getHand().equals(InteractionHand.MAIN_HAND)));

			return InteractionResult.SUCCESS;
		}

		return super.useOn(context);
	}

	@Override
	public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
		consumer.accept(new GeoRenderProvider() {
			private MultiTooltemRenderer renderer;

			@Override
			public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
				if (this.renderer == null)
					this.renderer = new MultiTooltemRenderer();

				return this.renderer;
			}
		});
	}

	public ResourceLocation getVariantKey(ItemStack stack)
	{
		return stack.getOrDefault(DataComponentInit.VARIANT,
				ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID, "multi_tool"));
	}

	public void setVariantKey(ItemStack stack, ResourceLocation variant)
	{
		stack.set(DataComponentInit.VARIANT, variant);
	}

	public int getHullColor(ItemStack stack)
	{
		return stack.getOrDefault(DataComponentInit.HULL_COLOR, -1);
	}

	public int getGlowColor(ItemStack stack)
	{
		return stack.getOrDefault(DataComponentInit.ACTIVE_COLOR, -1);
	}

	public void setHullColor(ItemStack stack, int color)
	{
		stack.set(DataComponentInit.HULL_COLOR, color);
	}

	public void setGlowColor(ItemStack stack, int color)
	{
		stack.set(DataComponentInit.ACTIVE_COLOR, color);
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers)
	{

	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache()
	{
		return cache;
	}

	@Override
	public List<ConfigurationProperty<?>> getConfigurationProperties(ItemStack stack, RegistryAccess registryAccess)
	{
		List<ConfigurationProperty<?>> properties = new ArrayList<>();

		properties.add(new ConfigurationProperty<>("hull_color",
				"color", "multi_tool.aperture_innovations.multi_tool.hull_color",
				MultiToolConfigTypeInit.COLOR.get(),
				new InteractionType.RGBColorPicker(),
				clr -> setHullColor(stack, clr.packagedInt()),
				() -> net.mistersecret312.aperture_innovations.multitool.Color.fromInt(getHullColor(stack))));

		properties.add(new ConfigurationProperty<>("glow_color",
				"color", "multi_tool.aperture_innovations.multi_tool.glow_color",
				MultiToolConfigTypeInit.COLOR.get(),
				new InteractionType.RGBColorPicker(),
				clr -> setGlowColor(stack, clr.packagedInt()),
				() -> net.mistersecret312.aperture_innovations.multitool.Color.fromInt(getGlowColor(stack))));

		List<String> variants = new ArrayList<>();
		for(Map.Entry<ResourceKey<MultiToolVariant>, MultiToolVariant> entry : registryAccess
																					   .registryOrThrow(MultiToolVariant.REGISTRY_KEY)
																					   .entrySet())
		{
			variants.add(entry.getKey().location().toString());
		}

		properties.add(new ConfigurationProperty<>("variant", "variant",
				"multi_tool.aperture_innovations.variant",
				MultiToolConfigTypeInit.RESOURCE_LOCATION.get(),
				new InteractionType.ListChoice(variants, getVariantKey(stack).toString()),
				variant -> setVariantKey(stack, variant),
				() -> getVariantKey(stack)));

		return properties;
	}
}
